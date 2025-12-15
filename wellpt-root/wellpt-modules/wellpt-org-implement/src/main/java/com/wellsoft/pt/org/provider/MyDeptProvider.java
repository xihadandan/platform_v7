package com.wellsoft.pt.org.provider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.pt.org.entity.OrgVersionEntity;
import com.wellsoft.pt.org.service.OrgElementService;
import com.wellsoft.pt.org.service.OrgVersionService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年12月12日   chenq	 Create
 * </pre>
 */
@Component
public class MyDeptProvider extends MyOrgSelectProvider {
    @Resource
    OrgElementService orgElementService;

    @Resource
    OrgVersionService orgVersionService;


    @Override
    public String type() {
        return "MyDept";
    }

    @Override
    public List<Node> fetch(Params params) {
        List<Node> root = Lists.newArrayList();

        Map<String, Object> sqlParams = Maps.newHashMap();
        sqlParams.put("userId", SpringSecurityUtils.getCurrentUserId());
//        sqlParams.put("userId", "U_800129455358996480");
        if (!params.async()) {
            List<Node> nodes = Lists.newArrayList();
            // 查询全部
            OrgVersionEntity version = orgVersionService.getByStateAndOrgUuid(OrgVersionEntity.State.PUBLISHED, params.getOrgUuid());
            sqlParams.put("orgVersionUuid", version.getUuid());
            if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                sqlParams.put("locale", LocaleContextHolder.getLocale().toString());
            }
            List<QueryItem> queryItems = orgElementService.listQueryItemByNameSQLQuery("myDeptQuery", sqlParams, null);
            if (CollectionUtils.isEmpty(queryItems)) {
                return null;
            }
            Map<String, Node> nodeMap = Maps.newLinkedHashMap();
            Map<String, String> uuidIdMap = Maps.newHashMap();
            Map<String, String> idI18ns = Maps.newHashMap();
            for (QueryItem item : queryItems) {
                Node n = new Node();
                item.put("uuid", item.getLong("uuid").toString());
                String uuid = item.getString("uuid");
                String id = item.getString("id");
                n.setKey(id);
                n.setVersion(version.getId());
                n.setTitle(StringUtils.defaultString(item.getString("nameI18n"), item.getString("name")));
                if (StringUtils.isNotBlank(item.getString("nameI18n"))) {
                    idI18ns.put(id, item.getString("nameI18n"));
                }
                n.setType(item.getString("type"));
                n.setShortTitle(item.getString("shortName"));
                n.setKeyPath(item.getString("idPath"));
                n.setTitlePath(item.getString("cnPath"));
                n.setData(item);
                n.setIsLeaf(false);
                n.setParentKey(item.getLong("parentUuid") != null ? item.getLong("parentUuid").toString() : null);
                nodeMap.put(id, n);
                uuidIdMap.put(uuid, id);
                nodes.add(n);
            }

            // 更新国际化路径
            if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                for (Node n : nodes) {
                    String keyPath = n.getKeyPath();
                    if (StringUtils.isNotBlank(keyPath)) {
                        String[] keys = keyPath.split(Separator.SLASH.getValue());
                        String[] titlePaths = n.getTitlePath().split(Separator.SLASH.getValue());
                        for (int i = 0, len = keys.length; i < len; i++) {
                            if (idI18ns.containsKey(keys[i])) {
                                titlePaths[i] = idI18ns.get(keys[i]);
                            }
                        }
                        n.setTitlePath(StringUtils.join(titlePaths, Separator.SLASH.getValue()));
                    }
                }
            }


            // 构建父子层级
            Set<String> keys = nodeMap.keySet();
            Set<String> uuids = uuidIdMap.keySet();
            for (String key : keys) {
                Node n = nodeMap.get(key);
                if (n.getParentKey() == null || !uuids.contains(n.getParentKey())) {
                    // 表示为父级
                    root.add(n);
                } else {
                    String pid = uuidIdMap.get(n.getParentKey());
                    if (StringUtils.isNotBlank(pid)) {
                        n.setParentKey(pid); // 父级uuid转为父级id
                    }
                    pid = n.getParentKey();
                    Node parent = nodeMap.get(pid);

                    if (CollectionUtils.isEmpty(parent.getChildren())) {
                        parent.setChildren(Lists.newArrayList(n));
                        continue;
                    }
                    parent.getChildren().add(n);


                }

            }

            List<String> ids = Lists.newArrayList(uuidIdMap.values());

            // 查询节点下的用户
            StringBuilder sql = new StringBuilder(" SELECT U.UUID, U.LOGIN_NAME,U.USER_ID,U.AVATAR,U.GENDER,U.USER_NAME, U.PIN_YIN, U.USER_NO ,U.CEIL_PHONE_NUMBER , U.MAIL , RELA.ORG_ELEMENT_ID"
            );
            if (sqlParams.containsKey("locale")) {
                sql.append(", I.USER_NAME AS I_USER_NAME ");
            }
            sql.append(" FROM USER_INFO U INNER JOIN ORG_USER RELA ON  RELA.USER_ID=U.USER_ID ");
            if (sqlParams.containsKey("locale")) {
                sql.append(" LEFT JOIN USER_NAME_I18N I ON I.USER_ID=U.USER_ID AND I.LOCALE=:locale ");
            }
            sql.append(" WHERE RELA.ORG_VERSION_UUID=:orgVersionUuid AND ORG_ELEMENT_ID IN (:ids) ORDER BY U.USER_NO ASC");
            Set<String> idParams = Sets.newHashSetWithExpectedSize(100);
            Map<String, QueryItem> userUuidMap = Maps.newHashMap();
            for (int i = 0, len = ids.size(); i < len; i++) {
                idParams.add(ids.get(i));
                if ((i + 1) % 100 == 0 || i == len - 1) {
                    sqlParams.put("ids", idParams);
                    List<QueryItem> userQueryItems = orgElementService.getDao().listQueryItemBySQL(sql.toString(), sqlParams, null);
                    for (QueryItem item : userQueryItems) {
                        Node parent = nodeMap.get(item.getString("orgElementId"));
                        if (parent == null) {
                            continue;
                        }
                        Node n = new Node();
                        item.put("type", "user");
                        n.setKey(item.getString("userId"));
                        n.setTitle(StringUtils.defaultString(item.getString("iUserName"), item.getString("userName")));
                        n.setShortTitle(n.getTitle());
                        n.setVersion(version.getId());
                        n.setType("user");
                        n.setData(item);
                        n.setIsLeaf(true);
                        userUuidMap.put(item.getString("uuid"), item);
                        n.setParentKey(parent.getKey());
                        if (CollectionUtils.isEmpty(parent.getChildren())) {
                            parent.setChildren(Lists.newArrayList());
                            parent.setIsLeaf(false);
                        }
                        n.setTitlePath(StringUtils.join(new String[]{parent.getTitlePath(), n.getTitle()}, Separator.SLASH.getValue()));
                        n.setKeyPath(StringUtils.join(new String[]{parent.getKeyPath(), n.getKey()}, Separator.SLASH.getValue()));
                        parent.getChildren().add(n);

                        // 获取上级职位、部门、职级
                        if (StringUtils.isNotBlank(n.getKeyPath())) {
                            String[] tempKeys = n.getKeyPath().split(Separator.SLASH.getValue());
                            String[] titles = n.getTitlePath().split(Separator.SLASH.getValue());
                            for (int j = tempKeys.length - 1; j >= 0; j--) {
                                if (tempKeys[j].startsWith(IdPrefix.JOB.getValue() + Separator.UNDERLINE.getValue()) && !item.containsKey("jobId")) {
                                    item.put("job_id", tempKeys[j]);
                                    item.put("job_name", titles[j]);
                                } else if (tempKeys[j].startsWith(IdPrefix.DEPARTMENT.getValue() + Separator.UNDERLINE.getValue()) && !item.containsKey("deptId")) {
                                    item.put("dept_id", tempKeys[j]);
                                    item.put("dept_name", titles[j]);
                                } else if (tempKeys[j].startsWith(IdPrefix.SYSTEM_UNIT.getValue() + Separator.UNDERLINE.getValue()) && !item.containsKey("unitId")) {
                                    item.put("unit_id", tempKeys[j]);
                                    item.put("unit_name", titles[j]);
                                }
                            }
                        }

                    }

                }
            }

            // 查询用户扩展信息
            String userAttrSql = "select attr_key,attr_value,user_uuid from USER_INFO_EXT where user_uuid in :userUuid ";
            if (MapUtils.isNotEmpty(userUuidMap)) {
                ListUtils.handleSubList(Lists.newArrayList(userUuidMap.keySet()), 200, list -> {
                    List<QueryItem> userAttrsQueryItems = orgElementService.getDao().listQueryItemBySQL(userAttrSql,
                            ImmutableMap.<String, Object>builder().put("userUuid", list).build(), null);
                    if (CollectionUtils.isNotEmpty(userAttrsQueryItems)) {
                        for (QueryItem q : userAttrsQueryItems) {
                            if (userUuidMap.containsKey(q.getString("userUuid"))) {
                                userUuidMap.get(q.getString("userUuid")).put(q.getString("attrKey"), q.getString("attrValue"), false);
                            }
                        }
                    }
                });
            }

            nodes.forEach(node -> {
                node.setIsLeaf(CollectionUtils.isEmpty(node.getChildren()));
            });
        }
        return root;
    }

}
