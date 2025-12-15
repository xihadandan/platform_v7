/*
 * @(#)2017年12月18日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.support.dataprovider;

import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.multi.org.bean.OrgNodeDto;
import com.wellsoft.pt.multi.org.bean.OrgTreeDialLogAsynParms;
import com.wellsoft.pt.multi.org.entity.MultiOrgElement;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.support.utils.ConvertOrgNode;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年12月18日.1	zyguo		2017年12月18日		Create
 * </pre>
 * @date 2017年12月18日
 */
@Component
public class MyUnitOrgTreeDeptDialogDataProvider extends AbstractOrgTreeDialogServiceImpl implements OrgTreeDialogProvider {

    public static final String TYPE = "MyUnitDept";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 442201326011028822L;
    Logger logger = LoggerFactory.getLogger(MyUnitOrgTreeDeptDialogDataProvider.class);

    @Override
    public String getType() {
        return TYPE;
    }


    private Set<String> getIgnoreIdPrefix() {
        Set<String> ignoreIdPrefix = new HashSet<>();
        ignoreIdPrefix.add(IdPrefix.JOB.getValue());
        return ignoreIdPrefix;
    }

    @Override
    public List<OrgNode> children(OrgTreeDialLogAsynParms parms) {
        List<OrgNode> treeNodes = new ArrayList<>();
        //节点Id null 根节点 组织版本查询
        if (StringUtils.isBlank(parms.getTreeNodeId())) {
            return this.getTopTreeNode(parms.getUserId(), parms.getUnitId());
        }

        Assert.hasText(parms.getOrgVersionId(), "orgVersionId 不能为空");
        List<MultiOrgElement> elements = multiOrgTreeNodeService.children(parms.getOrgVersionId(), parms.getTreeNodeId(), getIgnoreIdPrefix());
        this.addTreeNodes(parms.getOrgVersionId(), treeNodes, elements, parms.getCheckedIds());
        return treeNodes;
    }

    private void addTreeNodes(String orgVersionId, List<OrgNode> treeNodes, List<MultiOrgElement> elements, List<String> checkedIds) {
        Set<String> halfCheckSet = this.halfCheckIds(orgVersionId, checkedIds);
        for (MultiOrgElement element : elements) {
            //查询子节点数量
            int count = this.multiOrgTreeNodeService.countChildren(orgVersionId, element.getId(), getIgnoreIdPrefix());
            //转换treeNode
            OrgNode treeNode = ConvertOrgNode.convert(element, count);
            //勾选状态处理
            ConvertOrgNode.checked(treeNode, checkedIds, halfCheckSet);
            treeNodes.add(treeNode);
        }
    }


    private Map<String, OrgNode> orgNodeMap(OrgNode orgNode, OrgTreeDialLogAsynParms parms) {
        return this.getStringOrgNodeMap(orgNode, parms.getCheckedIds(), parms.getOrgVersionId(), parms.getOrgVersionId());
    }

    private Map<String, OrgNode> getStringOrgNodeMap(OrgNode orgNode, List<String> checkedIds, String orgVersionId, String eleIdPath) {
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("orgVersionId", orgVersionId);
        query.put("ignoreIdPrefix", getIgnoreIdPrefix());
        //查询所有子节点
        query.put("eleIdPath", eleIdPath + MultiOrgService.PATH_SPLIT_SYSMBOL + "%");
        StringBuilder hqlSb = new StringBuilder(
                "select a.eleId as id,b.name as name,b.shortName as shortName,b.type as type,b.code as code,a.eleIdPath as eleIdPath from MultiOrgTreeNode a,MultiOrgElement b ");
        hqlSb.append(" where a.eleId = b.id and a.orgVersionId=:orgVersionId and a.eleIdPath like :eleIdPath and b.type not in (:ignoreIdPrefix) ");
        List<OrgNodeDto> nodeDtoList = multiOrgTreeNodeService.listItemHqlQuery(hqlSb.toString(), OrgNodeDto.class, query);
        //最大长度eleIdPath
        Set<String> eleIdPathMax = new HashSet<>();
        //转为map
        Map<String, OrgNodeDto> nodeDtoMap = new HashMap<String, OrgNodeDto>((int) ((float) nodeDtoList.size() / 0.75F + 1.0F));
        for (OrgNodeDto obj : nodeDtoList) {
            nodeDtoMap.put(obj.getId(), obj);
            //替换成最大长度eleIdPath
            ConvertOrgNode.containsIndexOf(eleIdPathMax, obj.getEleIdPath());
        }

        //保存所有已处理节点
        Map<String, OrgNode> orgNodeMap = new HashMap<>();
        //遍历 最大长度eleIdPath 就能遍历所有节点
        Iterator<String> iterator = eleIdPathMax.iterator();
        while (iterator.hasNext()) {
            String idPath = iterator.next();
            //eleIdPaths V0000000424/B0000000191/O0000000425/D0000000127/J0000000089
            idPath = idPath.replace(eleIdPath + MultiOrgService.PATH_SPLIT_SYSMBOL, "");
            String[] eleIdPaths = idPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            // 从 B0000000191 开始处理
            this.addChildren(orgNode, nodeDtoMap, orgNodeMap, eleIdPaths, 0, checkedIds);
        }
        return orgNodeMap;
    }

    private void addChildren(OrgNode orgNode, Map<String, OrgNodeDto> nodeDtoMap, Map<String, OrgNode> orgNodeMap, String[] eleIdPaths, int i, List<String> checkedIds) {
        if (i >= eleIdPaths.length) {
            //结束递归
            return;
        }
        String eleId = eleIdPaths[i];
        if (eleId.startsWith(IdPrefix.JOB.getValue())) {
            return;
        }
        //处理过 该节点 累加i 处理下一个
        if (orgNodeMap.containsKey(eleId)) {
            i++;
            //以 eleId 为上级节点 递归处理子节点
            addChildren(orgNodeMap.get(eleId), nodeDtoMap, orgNodeMap, eleIdPaths, i, checkedIds);
            return;
        }
        OrgNodeDto orgNodeDto = nodeDtoMap.get(eleId);
        TreeMap<String, List<OrgNode>> treeMap = orgNode.getTreeMap();
        if (treeMap == null) {
            treeMap = new TreeMap<>();
            orgNode.setTreeMap(treeMap);
        }
        List<OrgNode> orgNodes = treeMap.get(orgNodeDto.getCode());
        if (orgNodes == null) {
            orgNodes = new ArrayList<>();
            //以code 排序
            treeMap.put(orgNodeDto.getCode(), orgNodes);
        }
        //转换
        OrgNode children = ConvertOrgNode.convert(orgNodeDto);
        //在list里 不存在则添加
        if (!orgNodes.contains(children)) {
            orgNodes.add(children);
        }
        if (checkedIds != null && checkedIds.contains(children.getId())) {
            children.setChecked(true);
        }
        //保存处理记录
        orgNodeMap.put(eleId, children);
        i++;
        addChildren(children, nodeDtoMap, orgNodeMap, eleIdPaths, i, checkedIds);
    }

    @Override
    public List<OrgNode> full(OrgTreeDialLogAsynParms parms) {
        Assert.hasText(parms.getOrgVersionId(), "orgVersionId 不能为空");
        //构造顶级节点
        OrgNode orgNode = new OrgNode();
        orgNode.setId(parms.getOrgVersionId());
        orgNode.setTreeMap(new TreeMap<String, List<OrgNode>>());

        this.orgNodeMap(orgNode, parms);

        List<OrgNode> orgNodes = new ArrayList<>();
        for (List<OrgNode> nodes : orgNode.getTreeMap().values()) {
            orgNodes.addAll(nodes);
        }
        return orgNodes;
    }

    @Override
    public List<OrgNode> search(OrgTreeDialLogAsynParms parms) {
        Assert.hasText(parms.getOrgVersionId(), "orgVersionId 不能为空");
        Assert.hasText(parms.getKeyword(), "keyword 不能为空");
        List<OrgNode> treeNodes = new ArrayList<>();
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("orgVersionId", parms.getOrgVersionId());
        query.put("ignoreIdPrefix", getIgnoreIdPrefix());
        query.put("key_eq", parms.getKeyword());
        query.put("key_right", parms.getKeyword() + "%");
        query.put("key_left", "%" + parms.getKeyword());
        query.put("key_like", "%" + parms.getKeyword() + "%");
        query.put("eleIdPath", parms.getOrgVersionId() + MultiOrgService.PATH_SPLIT_SYSMBOL + "%");

        /**
         *  子查询MultiOrgTreeNode 指定 orgVersionId ; eleIdPath like 获取下一级
         *  全模糊查询
         *  按匹配度 =**》**%》%**》%**% 排序
         */
        String hql = "from MultiOrgElement a where exists (select 1 from MultiOrgTreeNode b where b.orgVersionId=:orgVersionId and b.eleId=a.id and b.eleIdPath like :eleIdPath) "
                + "and (a.name like :key_like or a.shortName like :key_like) and a.type not in (:ignoreIdPrefix) "
                + "order by ("
                + "CASE WHEN (a.name = :key_eq OR a.shortName = :key_eq ) THEN 1 "
                + "WHEN (a.name LIKE :key_right OR a.shortName LIKE :key_right) THEN 2 "
                + "WHEN (a.name LIKE :key_left OR a.shortName LIKE :key_left ) THEN 3 "
                + "WHEN (a.name LIKE :key_like OR a.shortName LIKE :key_like ) THEN 4 " + "ELSE 5 END),a.code ";
        List<MultiOrgElement> elements = multiOrgElementService.listByHQL(hql, query);
        for (MultiOrgElement element : elements) {
            OrgNode treeNode = ConvertOrgNode.convert(element, 0);
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

}
