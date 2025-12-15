package com.wellsoft.pt.org.provider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.pt.common.i18n.AppCodeI18nMessageSource;
import com.wellsoft.pt.org.entity.OrgUserEntity;
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

/**
 * Description: 我的下属
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年12月12日   chenq	 Create
 * </pre>
 */
@Component
public class MyUnderlingProvider extends MyOrgSelectProvider {
    @Resource
    OrgElementService orgElementService;

    @Resource
    OrgVersionService orgVersionService;


    @Override
    public String type() {
        return "MyUnderling";
    }


    @Override
    public List<Node> fetch(Params params) {
        OrgVersionEntity version = orgVersionService.getByStateAndOrgUuid(OrgVersionEntity.State.PUBLISHED, params.getOrgUuid());
        Long orgVersionUuid = version.getUuid();
        Map<String, Object> sqlParams = Maps.newHashMap();
        sqlParams.put("orgVersionUuid", version.getUuid());
        sqlParams.put("userId", SpringSecurityUtils.getCurrentUserId());
//        sqlParams.put("userId", "U_802152029467176960");
        List<Node> nodes = Lists.newArrayList();
        Node directUnderlingNode = new Node();
        directUnderlingNode.setKey("1");
        directUnderlingNode.setTitle(AppCodeI18nMessageSource.getMessage("organization.directUnderlingNodeTitle", "pt-org", LocaleContextHolder.getLocale().toString(), "直接下属"));
        directUnderlingNode.setType("directUnderling");
        directUnderlingNode.setIsLeaf(false);
        directUnderlingNode.setCheckable(false);
        directUnderlingNode.setChildren(Lists.newArrayList());
        nodes.add(directUnderlingNode);
        Node branchUnderlingNode = new Node();
        branchUnderlingNode.setKey("2");
        branchUnderlingNode.setTitle(AppCodeI18nMessageSource.getMessage("organization.branchUnderlingNodeTitle", "pt-org", LocaleContextHolder.getLocale().toString(), "分管下属"));
        branchUnderlingNode.setType("brachUnderling");
        branchUnderlingNode.setIsLeaf(false);
        branchUnderlingNode.setCheckable(false);
        branchUnderlingNode.setChildren(Lists.newArrayList());
        nodes.add(branchUnderlingNode);
        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
            sqlParams.put("locale", LocaleContextHolder.getLocale().toString());
        }
        // 1. 查询我所在部门的负责人、分管领导
        List<QueryItem> queryItems = this.orgElementService.getDao().listQueryItemBySQL("SELECT U.UUID, U.LOGIN_NAME,U.USER_ID,U.AVATAR,U.GENDER,U.USER_NAME, U.PIN_YIN,U.USER_NO, U.CEIL_PHONE_NUMBER, U.MAIL \n" +
                (sqlParams.containsKey("locale") ? ", I.USER_NAME AS I_USER_NAME " : "") +
                "FROM\n" +
                "\tUSER_INFO U \n" +
                (sqlParams.containsKey("locale") ? " LEFT JOIN USER_NAME_I18N I ON I.USER_ID = U.USER_ID AND I.LOCALE=:locale " : "") +
                "WHERE\n" +
                "\tEXISTS (\n" +
                "\tSELECT\n" +
                "\t\t1 \n" +
                "\tFROM\n" +
                "\t\tORG_ELEMENT_MANAGEMENT M,\n" +
                "\t\tORG_USER RELA \n" +
                "\tWHERE\n" +
                "\t\tM.DIRECTOR = :userId AND M.ORG_VERSION_UUID =:orgVersionUuid \n" +
                "\t\tAND RELA.ORG_ELEMENT_ID = M.ORG_ELEMENT_ID \n" +
                "\t\tAND RELA.TYPE = 0 \n" +
                "\t\tAND U.USER_ID = RELA.USER_ID \n" +
                "\t) \n" +
                "\tOR EXISTS (\n" +
                "\tSELECT\n" +
                "\t\t1 \n" +
                "\tFROM\n" +
                "\t\tORG_ELEMENT_MANAGEMENT M,\n" +
                "\t\tORG_ELEMENT_PATH_CHAIN C,\n" +
                "\t\tORG_USER RELA \n" +
                "\tWHERE\n" +
                "\t\tM.DIRECTOR = :userId AND M.ORG_VERSION_UUID =:orgVersionUuid \n" +
                "\t\tAND M.ORG_ELEMENT_ID = C.ORG_ELEMENT_ID \n" +
                "\t\tAND C.SUB_ORG_ELEMENT_TYPE = 'job' \n" +
                "\t\tAND C.\"LEVEL\" = 1 \n" +
                "\t\tAND RELA.ORG_ELEMENT_ID = C.SUB_ORG_ELEMENT_ID \n" +
                "\t\tAND RELA.TYPE IN ( 1, 2 ) \n" +
                "\t\tAND RELA.USER_ID =U.USER_ID\n" +
                "\t) \n" +
                "\tORDER BY U.USER_NO ASC", sqlParams, null);
        Map<String, QueryItem> userUuidMap = Maps.newHashMap();
        Map<String, List<Node>> userUuidNodes = Maps.newHashMap();
        Map<String, List<Node>> userIdNodes = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(queryItems)) {
            for (QueryItem item : queryItems) {
                if (sqlParams.get("userId").equals(item.getString("userId"))) {
                    continue;
                }
                Node n = new Node();
                item.put("type", "user");
                n.setKey(item.getString("userId"));
                n.setTitle(StringUtils.defaultString(item.getString("iUserName"), item.getString("userName")));
                n.setType("user");
                n.setData(item);
                if (!userIdNodes.containsKey(n.getKey())) {
                    userIdNodes.put(n.getKey(), Lists.newArrayList());
                }
                userIdNodes.get(n.getKey()).add(n);

                if (!userUuidNodes.containsKey(item.getString("uuid"))) {
                    userUuidNodes.put(item.getString("uuid"), Lists.newArrayList());
                }
                userUuidNodes.get(item.getString("uuid")).add(n);

                n.setIsLeaf(true);
                directUnderlingNode.getChildren().add(n);
                if (!userUuidNodes.containsKey(item.getString("uuid"))) {
                    userUuidNodes.put(item.getString("uuid"), Lists.newArrayList());
                }
            }
        }

        // 2. 查询我的职位归属的部门领导
        queryItems = this.orgElementService.getDao().listQueryItemBySQL("SELECT U.UUID, U.LOGIN_NAME,U.USER_ID,U.AVATAR,U.GENDER,U.USER_NAME, U.PIN_YIN,U.USER_NO, U.CEIL_PHONE_NUMBER, U.MAIL \n" +
                (sqlParams.containsKey("locale") ? ", I.USER_NAME AS I_USER_NAME " : "") +
                "FROM\n" +
                "\tUSER_INFO U \n" +
                (sqlParams.containsKey("locale") ? " LEFT JOIN USER_NAME_I18N I ON I.USER_ID = U.USER_ID AND I.LOCALE=:locale " : "") +
                "WHERE\n" +
                "\tEXISTS (\n" +
                "\tSELECT\n" +
                "\t\t1 \n" +
                "\tFROM\n" +
                "\t\tORG_ELEMENT_MANAGEMENT M,\n" +
                "\t\tORG_USER RELA \n" +
                "\tWHERE\n" +
                "\t\tM.LEADER = :userId AND M.ORG_VERSION_UUID =:orgVersionUuid \n" +
                "\t\tAND RELA.ORG_ELEMENT_ID = M.ORG_ELEMENT_ID \n" +
                "\t\tAND RELA.TYPE = 0 \n" +
                "\t\tAND U.USER_ID = RELA.USER_ID \n" +
                "\t) \n" +
                "\tOR EXISTS (\n" +
                "\tSELECT\n" +
                "\t\t1 \n" +
                "\tFROM\n" +
                "\t\tORG_ELEMENT_MANAGEMENT M,\n" +
                "\t\tORG_ELEMENT_PATH_CHAIN C,\n" +
                "\t\tORG_USER RELA \n" +
                "\tWHERE\n" +
                "\t\tM.LEADER = :userId AND M.ORG_VERSION_UUID =:orgVersionUuid \n" +
                "\t\tAND M.ORG_ELEMENT_ID = C.ORG_ELEMENT_ID \n" +
                " \t\tAND C.\"LEVEL\" = 1 \n" +
                "\t\tAND RELA.ORG_ELEMENT_ID = C.SUB_ORG_ELEMENT_ID \n" +
                "\t\tAND RELA.USER_ID =U.USER_ID\n" +
                "\t) \tORDER BY U.USER_NO ASC", sqlParams, null);

        if (CollectionUtils.isNotEmpty(queryItems)) {
            for (QueryItem item : queryItems) {
                if (sqlParams.get("userId").equals(item.getString("userId"))) {
                    continue;
                }
                Node n = new Node();
                item.put("type", "user");
                n.setKey(item.getString("userId"));
                n.setTitle(StringUtils.defaultString(item.getString("iUserName"), item.getString("userName")));
                n.setType("user");
                n.setData(item);
                userUuidMap.put(item.getString("uuid"), item);
                n.setIsLeaf(true);
                if (!userIdNodes.containsKey(n.getKey())) {
                    userIdNodes.put(n.getKey(), Lists.newArrayList());
                }
                userIdNodes.get(n.getKey()).add(n);

                if (!userUuidNodes.containsKey(item.getString("uuid"))) {
                    userUuidNodes.put(item.getString("uuid"), Lists.newArrayList());
                }
                userUuidNodes.get(item.getString("uuid")).add(n);
                branchUnderlingNode.getChildren().add(n);
                if (!userUuidNodes.containsKey(item.getString("uuid"))) {
                    userUuidNodes.put(item.getString("uuid"), Lists.newArrayList());
                }
            }
        }
//        for (Node n : nodes) {
//            if (CollectionUtils.isEmpty(n.getChildren())) {
//                n.setIsLeaf(true);
//            }
//        }

        // 查询用户在该版本下职位、部门归属
        if (MapUtils.isNotEmpty(userIdNodes)) {
            ListUtils.handleSubList(Lists.newArrayList(userIdNodes.keySet()), 200, list -> {
                List<QueryItem> orgElements = orgElementService.getDao().listQueryItemBySQL(
                        "SELECT OU.USER_ID, OU.TYPE,P.CN_PATH,P.ID_PATH FROM ORG_USER OU INNER JOIN  ORG_ELEMENT_PATH P ON P.ORG_ELEMENT_ID = OU.ORG_ELEMENT_ID AND OU.ORG_VERSION_UUID = P.ORG_VERSION_UUID  WHERE OU.TYPE =1 AND  OU.USER_ID IN (:userIds) AND OU.ORG_VERSION_UUID=:orgVersionUuid",
                        ImmutableMap.<String, Object>builder().put("userIds", list).put("orgVersionUuid", orgVersionUuid).build(), null);
                if (CollectionUtils.isNotEmpty(orgElements)) {
                    for (QueryItem q : orgElements) {
                        if (q.getInt("type") == OrgUserEntity.Type.PRIMARY_JOB_USER.ordinal()) {
                            List<Node> userNodes = userIdNodes.get(q.getString("userId"));
                            for (Node n : userNodes) {
                                QueryItem item = (QueryItem) n.getData();
                                if (StringUtils.isNotBlank(q.getString("idPath"))) {
                                    String[] keys = q.getString("idPath").split(Separator.SLASH.getValue());
                                    String[] titles = q.getString("cnPath").split(Separator.SLASH.getValue());
                                    for (int i = keys.length - 1; i >= 0; i--) {
                                        if (keys[i].startsWith(IdPrefix.JOB.getValue() + Separator.UNDERLINE.getValue())) {
                                            item.put("job_id", keys[i]);
                                            item.put("job_name", titles[i]);
                                        } else if (keys[i].startsWith(IdPrefix.DEPARTMENT.getValue() + Separator.UNDERLINE.getValue())) {
                                            item.put("dept_id", keys[i]);
                                            item.put("dept_name", titles[i]);
                                        } else if (keys[i].startsWith(IdPrefix.SYSTEM_UNIT.getValue() + Separator.UNDERLINE.getValue())) {
                                            item.put("unit_id", keys[i]);
                                            item.put("unit_name", titles[i]);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }
        // 查询用户扩展信息
        String userAttrSql = "select attr_key,attr_value,user_uuid from USER_INFO_EXT where user_uuid in :userUuid ";
        if (MapUtils.isNotEmpty(userUuidMap)) {
            ListUtils.handleSubList(Lists.newArrayList(userUuidMap.keySet()), 200, list -> {
                List<QueryItem> userAttrsQueryItems = orgElementService.getDao().listQueryItemBySQL(userAttrSql,
                        ImmutableMap.<String, Object>builder().put("userUuid", list).build(), null);
                if (CollectionUtils.isNotEmpty(userAttrsQueryItems)) {
                    for (QueryItem q : userAttrsQueryItems) {
                        if (userUuidNodes.containsKey(q.getString("userUuid"))) {
                            for (Node n : userUuidNodes.get(q.getString("userUuid"))) {
                                ((QueryItem) n.getData()).put(q.getString("attrKey"), q.getString("attrValue"), false);
                            }
                        }
                    }
                }
            });
        }
        return nodes;
    }


}
