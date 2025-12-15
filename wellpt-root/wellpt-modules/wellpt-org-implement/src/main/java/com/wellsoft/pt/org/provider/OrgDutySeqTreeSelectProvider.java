package com.wellsoft.pt.org.provider;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.multi.org.support.dataprovider.OrgSelectProvider;
import com.wellsoft.pt.org.service.OrgElementService;
import com.wellsoft.pt.org.service.OrgVersionService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 我的组织
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年12月12日   chenq	 Create
 * </pre>
 */
@Component
public class OrgDutySeqTreeSelectProvider implements OrgSelectProvider {
    @Resource
    OrgElementService orgElementService;

    @Resource
    OrgVersionService orgVersionService;


    @Override
    public String type() {
        return "OrgDutySeq";
    }


    @Override
    public List<Node> fetch(Params params) {
        StringBuilder sql = new StringBuilder("SELECT SEQ.UUID, SEQ.DUTY_SEQ_NAME,SEQ.PARENT_UUID , SEQ.DUTY_SEQ_CODE FROM ORG_DUTY_SEQ SEQ WHERE SEQ.TENANT = :tenant");
        Map<String, Object> sqlParams = Maps.newHashMap();
        sqlParams.put("tenant", SpringSecurityUtils.getCurrentTenantId());
        List<QueryItem> queryItems = orgElementService.listQueryItemBySQL(sql.toString(), sqlParams, null);
        if (CollectionUtils.isEmpty(queryItems)) {
            return null;
        }
        Map<String, Node> nodeMap = Maps.newHashMap();
        for (QueryItem item : queryItems) {
            Node n = new Node();
            n.setKey(item.getString("uuid"));
            n.setParentKey("0".equals(item.getString("parentUuid")) ? null : item.getString("parentUuid"));
            n.setTitle(item.getString("dutySeqName"));
            n.setIsLeaf(false);
            n.setCheckable(false);
            nodeMap.put(n.getKey(), n);
        }

        // 构建父子层级
        Set<String> keys = nodeMap.keySet();
        List<Node> nodes = Lists.newArrayList();
        for (String key : keys) {
            Node n = nodeMap.get(key);
            if (StringUtils.isNotBlank(n.getParentKey())) {
                String pid = n.getParentKey();
                Node parent = nodeMap.get(pid);
                if (CollectionUtils.isEmpty(parent.getChildren())) {
                    parent.setChildren(Lists.newArrayList(n));
                    continue;
                }
                parent.getChildren().add(n);
            } else {
                nodes.add(n);
            }
        }

        // 查询序列下的职务
        sql = new StringBuilder("select M.DUTY_SEQ_UUID,M.CODE,M.ID,M.NAME from MULTI_ORG_DUTY M WHERE EXISTS (\n" +
                "\tSELECT 1 FROM ORG_DUTY_SEQ SEQ WHERE ( SEQ.UUID = M.DUTY_SEQ_UUID OR SEQ.PARENT_UUID = M.DUTY_SEQ_UUID ) AND SEQ.TENANT = :tenant\n" +
                ")");
        queryItems = orgElementService.listQueryItemBySQL(sql.toString(), sqlParams, null);
        if (CollectionUtils.isNotEmpty(queryItems)) {
            for (QueryItem item : queryItems) {
                Node n = new Node();
                n.setKey(item.getString("id"));
                n.setTitle(item.getString("name"));
                n.setIsLeaf(true);
                String pid = item.getString("dutySeqUuid");
                if (nodeMap.containsKey(pid)) {
                    Node parent = nodeMap.get(pid);
                    if (CollectionUtils.isEmpty(parent.getChildren())) {
                        parent.setChildren(Lists.newArrayList(n));
                        continue;
                    }
                    parent.getChildren().add(n);
                }
            }
        }
        return nodes;
    }

    @Override
    public List<Node> fetchByKeys(Params params) {
        List<Node> nodes = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(params.getKeys())) {

            StringBuilder sql = new StringBuilder("select M.DUTY_SEQ_UUID,M.CODE,M.ID,M.NAME  from MULTI_ORG_DUTY M  where M.ID IN (:ids)");
            Map<String, Object> sqlParams = Maps.newHashMap();

            Set<String> keys = Sets.newHashSetWithExpectedSize(100);
            for (int i = 0; i < params.getKeys().size(); i++) {
                keys.add(params.getKeys().get(i));
                if ((i + 1) % 100 == 0 || i == params.getKeys().size() - 1) {
                    sqlParams.put("ids", keys);
                    List<QueryItem> queryItems = orgElementService.getDao().listQueryItemBySQL(sql.toString(), sqlParams, null);
                    keys.clear();
                    for (QueryItem item : queryItems) {
                        Node n = new Node();
                        n.setKey(item.getString("id"));
                        n.setTitle(item.getString("name"));
                        n.setIsLeaf(true);
                        nodes.add(n);
                    }
                }
            }

        }

        return nodes;
    }

    @Override
    public PageNode fetchUser(Params params) {
        return null;
    }

    @Override
    public List<Node> fetchByTitles(Params params) {
        return null;
    }


}
