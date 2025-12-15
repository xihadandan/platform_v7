/*
 * @(#)2017年12月18日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.collection.List2GroupMap;
import com.wellsoft.context.util.collection.List2Map;
import com.wellsoft.context.util.collection.TreeNode4AddNodeFromGroupMap;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.jpa.util.HqlUtils;
import com.wellsoft.pt.multi.org.bean.*;
import com.wellsoft.pt.multi.org.entity.*;
import com.wellsoft.pt.multi.org.facade.service.*;
import com.wellsoft.pt.multi.org.service.*;
import com.wellsoft.pt.multi.org.support.dataprovider.OrgTreeAllUserProvider;
import com.wellsoft.pt.multi.org.support.dataprovider.OrgTreeDialogDataProvider;
import com.wellsoft.pt.multi.org.support.dataprovider.OrgTreeDialogProvider;
import com.wellsoft.pt.multi.org.support.utils.ConvertOrgNode;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
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
@Service
@Transactional(readOnly = true)
public class MultiOrgTreeDialogServiceImpl implements MultiOrgTreeDialogService, InitializingBean {

    @Autowired
    private MultiOrgService multiOrgService;
    @Autowired
    private MultiOrgTypeService multiOrgTypeService;
    @Autowired
    private MultiOrgUserService multiOrgUserService;
    @Autowired
    private MultiOrgVersionFacade multiOrgVersionService;
    @Autowired
    private MultiOrgTreeNodeService multiOrgTreeNodeService;
    @Autowired
    private MultiOrgElementService multiOrgElementService;
    @Autowired
    private MultiOrgUserAccountService multiOrgUserAccountService;
    @Autowired
    private OrgApiFacade orgApiFacade;
    @Autowired
    private List<OrgTreeDialogDataProvider> dataProviderList;
    private Map<String, OrgTreeDialogDataProvider> dataProviderMap;

    @Autowired
    private List<OrgTreeDialogProvider> dialogDataAsynProviders;
    private Map<String, OrgTreeDialogProvider> dialogDataAsynProviderMap;

    @Autowired
    private List<OrgTreeAllUserProvider> orgTreeAllUserProviders;
    private Map<String, OrgTreeAllUserProvider> orgTreeAllUserProviderMap;

    @Autowired
    private MultiOrgUserTreeNodeService multiOrgUserTreeNodeService;

    // 类型转换
    public static OrgTreeNodeDto OrgUserTreeNodeDto2OrgTreeNodeDto(OrgUserTreeNodeDto userDto, TreeNode parentNode) {
        OrgTreeNodeDto orgDto = new OrgTreeNodeDto();
        orgDto.setUuid(userDto.getUuid());
        orgDto.setName(userDto.getName());
        orgDto.setCode(userDto.getCode());
        orgDto.setEleId(userDto.getUserId());
        String eleIdPath = parentNode.getPath() + MultiOrgService.PATH_SPLIT_SYSMBOL + userDto.getUserId();
        orgDto.setEleIdPath(eleIdPath);
        orgDto.setShortName(userDto.getShortName());
        if ("1".equals(userDto.getSex())) {
            orgDto.setRemark("man");
        } else if ("0".equals(userDto.getSex())) {
            orgDto.setRemark("women");
        }
        orgDto.setType(IdPrefix.USER.getValue());
        orgDto.setEleNamePath(userDto.getName());
        orgDto.setUserNamePy(userDto.getUserNamePy());
        orgDto.setOrgVersionId(userDto.getOrgVersionId());
        return orgDto;
    }

    @Override
    public List<MultiOrgType> getCurrentUnitOrgType() {
        String currentSystemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        return multiOrgTypeService.queryOrgTypeListBySystemUnitId(currentSystemUnitId);
    }

    @Override
    public List<TreeNode> queryUnitTreeDialogDataByType(String type, OrgTreeDialogParams params) {
        OrgTreeDialogDataProvider dataProvider = dataProviderMap.get(type);
        if (dataProvider == null) {
            throw new RuntimeException("请先实现 type=" + type + "的dataProvider");
        }
        return dataProvider.provideData(params);
    }

    private void excludeOrgNode(List<OrgNode> treeNodes, List<String> excludeValues) {
        Iterator<OrgNode> iterator = treeNodes.iterator();
        while (iterator.hasNext()) {
            OrgNode orgNode = iterator.next();
            if (isExcludeOrgNode(orgNode, excludeValues)) {
                iterator.remove();
            } else {
                if (!CollectionUtils.isEmpty(orgNode.getChildren())) {
                    excludeOrgNode(orgNode.getChildren(), excludeValues);
                }
            }
        }
    }

    private boolean isExcludeOrgNode(OrgNode orgNode, List<String> excludeValues) {
        for (String excludeValue : excludeValues) {
            if (orgNode.getId().equals(excludeValue)) {
                return true;
            }
            if (StringUtils.isNotBlank(orgNode.getIdPath()) && orgNode.getIdPath().indexOf(excludeValue) > -1) {
                return true;
            }
        }
        return false;
    }

    private void excludeUserNode(List<UserNode> userNodes, List<String> excludeValues) {
        Iterator<UserNode> iterator = userNodes.iterator();
        while (iterator.hasNext()) {
            UserNode userNode = iterator.next();
            if (isExcludeUserNode(userNode, excludeValues)) {
                iterator.remove();
            }
        }
    }

    private boolean isExcludeUserNode(UserNode userNode, List<String> excludeValues) {
        for (String excludeValue : excludeValues) {
            if (userNode.getId().equals(excludeValue)) {
                return true;
            }
            if (userNode.getIdPath().indexOf(excludeValue) > -1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<OrgNode> children(String type, OrgTreeDialLogAsynParms params) {
        OrgTreeDialogProvider dataProvider = dialogDataAsynProviderMap.get(type);
        if (dataProvider == null) {
            throw new RuntimeException("请先实现 type=" + type + "的OrgTreeDialogProvider");
        }

        List<OrgNode> orgNodeList = dataProvider.children(params);
        if (!CollectionUtils.isEmpty(params.getExcludeValues())) {
            this.excludeOrgNode(orgNodeList, params.getExcludeValues());
        }
        return orgNodeList;
    }

    @Override
    public List<OrgNode> full(String type, OrgTreeDialLogAsynParms params) {
        OrgTreeDialogProvider dataProvider = dialogDataAsynProviderMap.get(type);
        if (dataProvider == null) {
            throw new RuntimeException("请先实现 type=" + type + "的OrgTreeDialogProvider");
        }
        List<OrgNode> orgNodeList = dataProvider.full(params);
        if (!CollectionUtils.isEmpty(params.getExcludeValues())) {
            this.excludeOrgNode(orgNodeList, params.getExcludeValues());
        }
        return orgNodeList;
    }

    @Override
    public List<OrgNode> search(String type, OrgTreeDialLogAsynParms params) {
        OrgTreeDialogProvider dataProvider = dialogDataAsynProviderMap.get(type);
        if (dataProvider == null) {
            throw new RuntimeException("请先实现 type=" + type + "的OrgTreeDialogProvider");
        }
        List<OrgNode> orgNodeList = dataProvider.search(params);
        if (type.equals("DocExchangeContactBook") || type.equals("MailContactBook")) {
            return orgNodeList;
        }
        List<String> userIdList = new ArrayList<>();
        List<String> eleIdList = new ArrayList<>();
        for (OrgNode orgNode : orgNodeList) {
            if (orgNode.getType().equals(IdPrefix.USER.getValue())) {
                userIdList.add(orgNode.getId());
            } else {
                eleIdList.add(orgNode.getId());
            }
        }
        Map<String, String[]> mapNamePath = this.fullNameMap(params.getOrgVersionId(), eleIdList);
        for (OrgNode orgNode : orgNodeList) {
            if (mapNamePath.containsKey(orgNode.getId())) {
                orgNode.setIdPath(mapNamePath.get(orgNode.getId())[1]);
                orgNode.setNamePath(mapNamePath.get(orgNode.getId())[0]);
            }
        }

        Map<String, UserJob> map = multiOrgUserTreeNodeService.gerUserJob(params.getOrgVersionId(), userIdList);
        List<OrgNode> orgNodes = new ArrayList<>();
        for (OrgNode orgNode : orgNodeList) {
            if (orgNode.getType().equals(IdPrefix.USER.getValue())) {
                UserJob userJob = map.get(orgNode.getId());
                if (userJob == null) {
                    continue;
                }
                List<OrgElementVo> orgElementVos = new ArrayList<>();
                if (userJob.getMainJobs() != null) {
                    orgElementVos.addAll(userJob.getMainJobs());
                }
                if (userJob.getOtherJobs() != null) {
                    orgElementVos.addAll(userJob.getOtherJobs());
                }
                if (orgElementVos.size() > 0) {
                    for (OrgElementVo orgElementVo : orgElementVos) {
                        OrgNode userNode = new OrgNode();
                        userNode.setId(orgNode.getId());
                        userNode.setName(orgNode.getName());
                        userNode.setIconSkin(orgNode.getIconSkin());
                        userNode.setType(orgNode.getType());
                        OrgPathVo orgPathVo = ConvertOrgNode.convert(orgElementVo);
                        userNode.setIdPath(orgPathVo.getIdPath());
                        userNode.setNamePath(orgPathVo.getNamePath());
                        orgNodes.add(userNode);
                    }
                } else {
                    orgNodes.add(orgNode);
                }
            } else {
                orgNodes.add(orgNode);
            }
        }
        if (!CollectionUtils.isEmpty(params.getExcludeValues())) {
            this.excludeOrgNode(orgNodes, params.getExcludeValues());
        }
        return orgNodes;
    }

    @Override
    public UserNodePy allUserSearch(String type, OrgTreeDialLogAsynParms params) {
        OrgTreeAllUserProvider provider = orgTreeAllUserProviderMap.get(type);
        if (provider == null) {
            throw new RuntimeException("请先实现 type=" + type + "的OrgTreeAllUserProvider");
        }
        UserNodePy userNodePy = provider.allUserSearch(params);

        if (type.equals("DocExchangeContactBook") || type.equals("MailContactBook")) {
            return userNodePy;
        }

        List<String> userIdList = new ArrayList<>();
        for (UserNode userNode : userNodePy.getNodes()) {
            userIdList.add(userNode.getId());
        }
        Map<String, UserJob> map = multiOrgUserTreeNodeService.gerUserJob(params.getOrgVersionId(), userIdList);
        if (map.size() == 0) {
            return userNodePy;
        }
        List<UserNode> userNodeList = new ArrayList<>();
        for (UserNode userNode : userNodePy.getNodes()) {
            UserJob userJob = map.get(userNode.getId());
            if (userJob == null) {
                continue;
            }
            List<OrgElementVo> orgElementVos = new ArrayList<>();
            if (userJob.getMainJobs() != null) {
                orgElementVos.addAll(userJob.getMainJobs());
            }
            if (userJob.getOtherJobs() != null) {
                orgElementVos.addAll(userJob.getOtherJobs());
            }
            if (orgElementVos.size() > 0) {
                for (OrgElementVo orgElementVo : orgElementVos) {
                    UserNode user = new UserNode();
                    user.setId(userNode.getId());
                    user.setName(userNode.getName());
                    user.setNamePy(userNode.getNamePy());
                    user.setIconSkin(userNode.getIconSkin());
                    user.setOrgVersionId(userNode.getOrgVersionId());
                    OrgPathVo orgPathVo = ConvertOrgNode.convert(orgElementVo);
                    user.setIdPath(orgPathVo.getIdPath());
                    user.setNamePath(orgPathVo.getNamePath());
                    userNodeList.add(user);
                }
            } else {
                userNodeList.add(userNode);
            }
        }

        // 按人员编号升序
        List<UserNode> userNodeSortListNew = getUserNodeSortList(userNodeList);

        if (!CollectionUtils.isEmpty(params.getExcludeValues())) {
            this.excludeUserNode(userNodeSortListNew, params.getExcludeValues());
        }
        if (org.apache.commons.lang.StringUtils.isNotEmpty(params.getSort()) && params.getSort().equals("code")) {
            userNodePy.setNodes(userNodeSortListNew);
            return userNodePy;
        }
        userNodePy = ConvertOrgNode.convertUserNodePy(userNodeSortListNew);
        return userNodePy;
    }

    /**
     * // 按人员编号升序
     *
     * @param userNodeList
     * @return 返回按人员编号升序的用户节点信息
     **/
    private List<UserNode> getUserNodeSortList(List<UserNode> userNodeList) {
        Set<String> userIds = Sets.newHashSet();
        for (UserNode userNode : userNodeList) {
            userIds.add(userNode.getId());
        }

        List<String> userIdsList = new ArrayList<>(userIds);
        List<MultiOrgUserAccount> accounts = orgApiFacade.getAccountsByUserIds(userIdsList);
        // key=userId
        Map<String, MultiOrgUserAccount> orgUserAccountMap = Maps.newHashMap();
        for (MultiOrgUserAccount account : accounts) {
            orgUserAccountMap.put(account.getId(), account);
        }
        List<UserNodeSortDto> userNodeSortList = BeanUtils.copyCollection(userNodeList, UserNodeSortDto.class);
        for (UserNodeSortDto userNodeSortDto : userNodeSortList) {
            if (orgUserAccountMap.get(userNodeSortDto.getId()) != null) {
                userNodeSortDto.setCode(orgUserAccountMap.get(userNodeSortDto.getId()).getCode());
            }
        }
        Collections.sort(userNodeSortList, new Comparator<UserNodeSortDto>() {
            @Override
            public int compare(UserNodeSortDto o1, UserNodeSortDto o2) {
                String code1 = o1.getCode();
                String code2 = o2.getCode();
                if (code1 != null && code2 != null) {
                    return code1.compareTo(code2);
                }
                return 0;
            }
        });
        userNodeList = Lists.newArrayList();
        List<UserNode> userNodeSortListNew = BeanUtils.copyCollection(userNodeSortList, UserNode.class);
        return userNodeSortListNew;
    }

    private Map<String, String[]> fullNameMap(String orgVersionId, List<String> eleIds) {
        Map<String, String[]> map = new HashMap<>();
        Map<String, String[]> oMap = new HashMap<>();
        Set<String> userIdSet = new HashSet<>();
        Set<String> eleIdSet = new HashSet<>();
        for (String eleId : eleIds) {
            if (eleId.startsWith(IdPrefix.GROUP.getValue()) || IdPrefix.startsWithExternal(eleId)
                    || eleId.startsWith(IdPrefix.DUTY.getValue()) || eleId.startsWith(IdPrefix.ROLE.getValue())) {
                oMap.put(eleId, new String[]{orgApiFacade.getNameByOrgEleId(eleId), eleId});
            } else if (eleId.startsWith(IdPrefix.USER.getValue())) {
                oMap.put(eleId, new String[]{orgApiFacade.getNameByOrgEleId(eleId), eleId});
                userIdSet.add(eleId);
            } else {
                eleIdSet.add(eleId);
            }
        }
        Set<String> eleIdPathSet = new HashSet<>();
        if (userIdSet.size() > 0) {
            HashMap<String, Object> query = new HashMap<String, Object>();
            StringBuilder hql = new StringBuilder(
                    "select a.userId as userId,c.eleIdPath as eleIdPath from MultiOrgUserTreeNode a,MultiOrgVersion b,MultiOrgTreeNode c where "
                            + "a.orgVersionId = b.id and c.orgVersionId = b.id and a.eleId = c.eleId "
                            + "and b.status=1 and a.isMain = 1 and ");
            HqlUtils.appendSql("a.userId", query, hql, Sets.<Serializable>newHashSet(userIdSet));
            if (StringUtils.isNotBlank(orgVersionId)) {
                hql.append(" and b.id=:orgVersionId");
                query.put("orgVersionId", orgVersionId);
            }
            List<QueryItem> queryItemList = this.multiOrgUserTreeNodeService.listItemHqlQuery(hql.toString(),
                    QueryItem.class, query);
            for (QueryItem queryItem : queryItemList) {
                oMap.remove(queryItem.getString("userId"));
                map.put(queryItem.getString("userId"), new String[]{queryItem.getString("eleIdPath"), null});
                eleIdPathSet.add(queryItem.getString("eleIdPath"));
            }
        }
        if (eleIdSet.size() > 0) {
            HashMap<String, Object> query = new HashMap<>();
            StringBuilder hqlSb = new StringBuilder("select b from MultiOrgVersion a,MultiOrgTreeNode b where "
                    + "a.id = b.orgVersionId and a.status=1 and ");
            HqlUtils.appendSql("b.eleId", query, hqlSb, Sets.<Serializable>newHashSet(eleIdSet));
            if (StringUtils.isNotBlank(orgVersionId)) {
                hqlSb.append(" and a.id=:orgVersionId");
                query.put("orgVersionId", orgVersionId);
            }
            List<MultiOrgTreeNode> multiOrgTreeNodeList = this.multiOrgTreeNodeService.listByHQL(hqlSb.toString(),
                    query);
            for (MultiOrgTreeNode treeNode : multiOrgTreeNodeList) {
                map.put(treeNode.getEleId(), new String[]{null, treeNode.getEleIdPath()});
                eleIdPathSet.add(treeNode.getEleIdPath());
            }
        }

        if (eleIdPathSet.size() > 0) {
            Set<String> eIdSet = new HashSet<>();
            for (String eleIdPath : eleIdPathSet) {
                String[] eleIdStrs = eleIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                for (String eleIdStr : eleIdStrs) {
                    eIdSet.add(eleIdStr);
                }
            }
            HashMap<String, Object> query = new HashMap<String, Object>();
            StringBuilder hql = new StringBuilder("from MultiOrgElement a where ");
            HqlUtils.appendSql("a.id", query, hql, Sets.<Serializable>newHashSet(eIdSet));
            List<MultiOrgElement> multiOrgElementList = this.multiOrgElementService.listByHQL(hql.toString(), query);
            Map<String, MultiOrgElement> multiOrgElementMap = new HashMap<>();
            for (MultiOrgElement multiOrgElement : multiOrgElementList) {
                multiOrgElementMap.put(multiOrgElement.getId(), multiOrgElement);
            }
            for (String key : map.keySet()) {
                String[] val = map.get(key);
                String[] eleIdStrs = val[1].split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                String[] eleNameStrs = new String[eleIdStrs.length];
                for (int i = 0; i < eleIdStrs.length; i++) {
                    eleNameStrs[i] = multiOrgElementMap.get(eleIdStrs[i]).getName();
                }
                val[1] = StringUtils.join(eleNameStrs, MultiOrgService.PATH_SPLIT_SYSMBOL);
            }
        }
        map.putAll(oMap);
        return map;
    }

    @Override
    public String fullNamePath(String orgVersionId, String eleId) {
        List<String> eleIdList = new ArrayList<>();
        eleIdList.add(eleId);
        Map<String, String[]> map = this.fullNameMap(orgVersionId, eleIdList);
        if (null != map.get(eleId)) {
            return map.get(eleId)[0];
        }
        return "";
    }

    @Override
    public Map<String, OrgNode> smartName(int nameDisplayMethod, List<String> nodeIds, List<String> nodeNames) {
        return this.smartName(nodeIds, nodeNames);
    }

    @Override
    public Map<String, OrgNode> smartName(List<String> nodeIds, List<String> nodeNames) {
        Map<String, OrgNode> smartNameMap = new LinkedHashMap<>();
        if (nodeIds == null || nodeIds.size() == 0) {
            return smartNameMap;
        }
        Set<String> userIdSet = new HashSet<>();
        Set<String> nodeIdSet = new HashSet<>();
        Map<String, OrgNode> nodeMap = Maps.newHashMap();
        Set<String> ids = Sets.newHashSet();
        for (int i = 0; i < nodeIds.size(); i++) {
            String nodeId = nodeIds.get(i);
            OrgNode orgNode = new OrgNode();
            if (nodeId.startsWith(IdPrefix.ORG_VERSION.getValue())
                    && nodeId.contains(MultiOrgService.PATH_SPLIT_SYSMBOL)) {
                String[] eleIds = nodeId.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                nodeId = eleIds[eleIds.length - 1];
            }
            nodeMap.put(nodeId, orgNode);
            if (IdPrefix.startsDocExc(nodeId) || IdPrefix.startsContactBook(nodeId)) {
                if (nodeNames != null && nodeNames.size() == nodeIds.size()) {
                    orgNode.setName(nodeNames.get(i));
                } else {
                    orgNode.setName(nodeId);
                }
                smartNameMap.put(nodeId, orgNode);
                continue;
            }
            if (nodeId.startsWith(IdPrefix.GROUP.getValue()) || IdPrefix.startsWithExternal(nodeId)
                    || nodeId.startsWith(IdPrefix.DUTY.getValue()) || nodeId.startsWith(IdPrefix.USER.getValue())
                    || nodeId.startsWith(IdPrefix.ROLE.getValue())) {
                ids.add(nodeId);
                if (nodeId.startsWith(IdPrefix.USER.getValue())) {
                    userIdSet.add(nodeId);
                }
            } else if (nodeId.startsWith(IdPrefix.MULTI_GROUP_CATEGORY.getValue()) || nodeId.startsWith(IdPrefix.MULTI_GROUP.getValue())) {
                orgNode.setName(nodeNames.get(i));
                smartNameMap.put(nodeId, orgNode);
            } else {
                nodeIdSet.add(nodeId);
            }
            smartNameMap.put(nodeId, orgNode);
        }

        Map<String, String> idNames = orgApiFacade.getNamesByOrgEleIds(ids);
        for (String i : ids) {
            if (idNames.containsKey(i)) {
                nodeMap.get(i).setName(idNames.get(i));
            }
        }

        Set<String> eleIdSet = new HashSet<>();
        Map<String, List<String>> eleIdStrsMap = new HashMap<>();
        if (userIdSet.size() > 0) {
            HashMap<String, Object> query = new HashMap<String, Object>();
            StringBuilder hql = new StringBuilder(
                    "select a.userId as userId,c.eleIdPath as eleIdPath from MultiOrgUserTreeNode a,MultiOrgVersion b,MultiOrgTreeNode c where "
                            + "a.orgVersionId = b.id and c.orgVersionId = b.id and a.eleId = c.eleId "
                            + "and b.status=1 and a.isMain = 1 and ");
            HqlUtils.appendSql("a.userId", query, hql, Sets.<Serializable>newHashSet(userIdSet));
            List<QueryItem> queryItemList = this.multiOrgUserTreeNodeService.listItemHqlQuery(hql.toString(),
                    QueryItem.class, query);
            for (QueryItem queryItem : queryItemList) {
                String[] elePathIds = queryItem.getString("eleIdPath").split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                List<String> eldIdList = new ArrayList<>();
                for (String eldIdStr : elePathIds) {
                    eldIdList.add(eldIdStr);
                    eleIdSet.add(eldIdStr);
                }
                eleIdStrsMap.put(queryItem.getString("userId"), eldIdList);
                smartNameMap.get(queryItem.getString("userId"))
                        .setIdPath(StringUtils.join(eldIdList, MultiOrgService.PATH_SPLIT_SYSMBOL));
                smartNameMap.get(queryItem.getString("userId")).setId(queryItem.getString("userId"));
            }
        }
        if (nodeIdSet.size() > 0) {
            HashMap<String, Object> query = new HashMap<>();
            StringBuilder hqlSb = new StringBuilder("select b from MultiOrgVersion a,MultiOrgTreeNode b where "
                    + "a.id = b.orgVersionId and a.status=1 and ");
            HqlUtils.appendSql("b.eleId", query, hqlSb, Sets.<Serializable>newHashSet(nodeIdSet));
            List<MultiOrgTreeNode> multiOrgTreeNodeList = this.multiOrgTreeNodeService.listByHQL(hqlSb.toString(),
                    query);
            for (MultiOrgTreeNode treeNode : multiOrgTreeNodeList) {
                String[] eldIdStrs = treeNode.getEleIdPath().split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                List<String> eldIdList = new ArrayList<>();
                for (String eldIdStr : eldIdStrs) {
                    eldIdList.add(eldIdStr);
                    eleIdSet.add(eldIdStr);
                }
                eleIdStrsMap.put(treeNode.getEleId(), eldIdList);
                smartNameMap.get(treeNode.getEleId())
                        .setIdPath(StringUtils.join(eldIdList, MultiOrgService.PATH_SPLIT_SYSMBOL));
                smartNameMap.get(treeNode.getEleId()).setId(treeNode.getEleId());
            }
        }
        if (eleIdSet.size() == 0) {
            return smartNameMap;
        }
        HashMap<String, Object> query = new HashMap<String, Object>();
        StringBuilder hql = new StringBuilder("from MultiOrgElement a where ");
        HqlUtils.appendSql("a.id", query, hql, Sets.<Serializable>newHashSet(eleIdSet));
        List<MultiOrgElement> multiOrgElementList = this.multiOrgElementService.listByHQL(hql.toString(), query);
        Map<String, MultiOrgElement> multiOrgElementMap = new HashMap<>();
        for (MultiOrgElement multiOrgElement : multiOrgElementList) {
            multiOrgElementMap.put(multiOrgElement.getId(), multiOrgElement);
        }

        for (String key : eleIdStrsMap.keySet()) {
            List<String> list = eleIdStrsMap.get(key);
            for (int i = 0; i < list.size(); i++) {
                String eleId = list.get(i);
                String name = multiOrgElementMap.get(eleId).getName();
                list.set(i, name);
            }
            OrgNode orgNode = smartNameMap.get(key);
            if (orgNode != null) {
                orgNode.setNamePath(StringUtils.join(list, MultiOrgService.PATH_SPLIT_SYSMBOL));
            }
        }

        this.eliminateDuplicateNames(eleIdStrsMap, 0);

        for (String eleId : smartNameMap.keySet()) {
            OrgNode orgNode = smartNameMap.get(eleId);
            if (orgNode.getName() == null && !multiOrgElementMap.containsKey(eleId)) {
                continue;
            }
            orgNode.setName(orgNode.getName() != null ? orgNode.getName() : multiOrgElementMap.get(eleId).getName());
            orgNode.setShortName(
                    multiOrgElementMap.get(eleId) != null ? multiOrgElementMap.get(eleId).getShortName() : null);
            orgNode.setSmartNamePath(StringUtils.join(eleIdStrsMap.get(eleId), MultiOrgService.PATH_SPLIT_SYSMBOL));
        }

        return smartNameMap;
    }

    // 消除重复的名字 最多只截取前两级
    @Override
    public void eliminateDuplicateNames(Map<String, List<String>> eleIdStrsMap, int i) {
        if (i > 1) {
            return;
        }
        Set<String> nameSet = new HashSet<>();
        for (String key : eleIdStrsMap.keySet()) {
            List<String> list = eleIdStrsMap.get(key);
            if (list.size() > 1) {
                nameSet.add(list.get(0));
            } else {
                return;
            }
        }
        if (nameSet.size() == 1) {
            for (String key : eleIdStrsMap.keySet()) {
                List<String> list = eleIdStrsMap.get(key);
                list.remove(0);
            }
            eliminateDuplicateNames(eleIdStrsMap, ++i);
        }
        return;
    }

    @Override
    public OrgTreeNode getTreeNodeByVerisonAndEleIdPath(String eleIdPath, String orgVersionId, int isNeedUser,
                                                        boolean isInMyUnit) {
        OrgTreeNode orgTree = this.multiOrgService.getOrgAsTreeByEleIdPath(orgVersionId, eleIdPath, isInMyUnit);
        if (isNeedUser == 1) { // 需要用户
            // 获取本单位的用户
            List<OrgUserTreeNodeDto> userList = this.multiOrgUserService.queryUserByOrgVersion(orgVersionId);
            // 因为可能会包含其他的子单位，所以需要递归算出一共需要哪几个单位的用户
            HashSet<String> otherVerSet = this.computeOtherVersionIdFromTreeNode(orgTree, orgVersionId);
            if (!CollectionUtils.isEmpty(otherVerSet)) {
                // 为提升查询，去除重复的，如果有
                for (String versionId : otherVerSet) {
                    List<OrgUserTreeNodeDto> otherUserList = this.multiOrgUserService.queryUserByOrgVersion(versionId);
                    if (!CollectionUtils.isEmpty(otherUserList)) {
                        userList.addAll(otherUserList);
                    }
                }
            }

            // 将用户挂进组织树中去
            addUserListToOrgTree(orgTree, userList);
        }
        // 如果根节点是个组织类型，则需要变更名字为fullName,以便区别
        if (orgTree.getType().equals(IdPrefix.ORG_VERSION.getValue())) {
            MultiOrgVersion ver = this.multiOrgVersionService.getOrgVersionById(orgVersionId);
            orgTree.setName(ver.getFullName());
        }
        return orgTree;
    }

    private HashSet<String> computeOtherVersionIdFromTreeNode(OrgTreeNode orgTree, String versionId) {
        HashSet<String> set = new HashSet<String>();
        if (!versionId.equals(orgTree.getOrgVersionId())) { // 不是同一个组织版本，需要对应的用户
            set.add(orgTree.getOrgVersionId());
        }
        if (!CollectionUtils.isEmpty(orgTree.getChildren())) {
            for (TreeNode child : orgTree.getChildren()) {
                HashSet<String> childList = this.computeOtherVersionIdFromTreeNode((OrgTreeNode) child, versionId);
                if (!CollectionUtils.isEmpty(childList)) {
                    set.addAll(childList);
                }
            }
        }
        return set;
    }

    // 将用户挂进组织树对应的位置中去
    private void addUserListToOrgTree(TreeNode orgTree, List<OrgUserTreeNodeDto> userList) {
        if (!CollectionUtils.isEmpty(userList)) {
            // 将用户列表按所在的组织树位置进行分组
            Map<String, List<OrgUserTreeNodeDto>> userMap = new List2GroupMap<OrgUserTreeNodeDto>() {
                @Override
                protected String getGroupUuid(OrgUserTreeNodeDto obj) {
                    return obj.getEleId();
                }
            }.convert(userList);
            // 将用户挂进组织树中去
            new TreeNode4AddNodeFromGroupMap<OrgUserTreeNodeDto>() {
                @Override
                protected String getGroupMapKeyFromTreeNode(TreeNode node) {
                    OrgTreeNodeDto data = (OrgTreeNodeDto) node.getData();
                    return data.getEleId();
                }

                @Override
                protected TreeNode obj2TreeNode(OrgUserTreeNodeDto obj, TreeNode parentNode) {
                    OrgTreeNodeDto orgTreeNodeDto = OrgUserTreeNodeDto2OrgTreeNodeDto(obj, parentNode);
                    return orgTreeNodeDto.convert2TreeNode();
                }
            }.addNode(orgTree, userMap);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        dataProviderMap = new List2Map<OrgTreeDialogDataProvider>() {
            @Override
            protected String getMapKey(OrgTreeDialogDataProvider obj) {
                return obj.getType();
            }
        }.convert(dataProviderList);

        dialogDataAsynProviderMap = new List2Map<OrgTreeDialogProvider>() {
            @Override
            protected String getMapKey(OrgTreeDialogProvider obj) {
                return obj.getType();
            }
        }.convert(dialogDataAsynProviders);

        orgTreeAllUserProviderMap = new List2Map<OrgTreeAllUserProvider>() {
            @Override
            protected String getMapKey(OrgTreeAllUserProvider obj) {
                return obj.getType();
            }
        }.convert(orgTreeAllUserProviders);
    }
}
