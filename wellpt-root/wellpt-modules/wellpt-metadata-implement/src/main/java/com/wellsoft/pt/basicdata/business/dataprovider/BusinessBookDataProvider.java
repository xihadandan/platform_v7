package com.wellsoft.pt.basicdata.business.dataprovider;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.basicdata.business.entity.BusinessCategoryEntity;
import com.wellsoft.pt.basicdata.business.entity.BusinessCategoryOrgEntity;
import com.wellsoft.pt.basicdata.business.entity.BusinessRoleOrgUserEntity;
import com.wellsoft.pt.basicdata.business.service.BusinessCategoryOrgService;
import com.wellsoft.pt.basicdata.business.service.BusinessCategoryService;
import com.wellsoft.pt.basicdata.business.service.BusinessRoleOrgUserService;
import com.wellsoft.pt.jpa.util.HqlUtils;
import com.wellsoft.pt.multi.org.bean.*;
import com.wellsoft.pt.multi.org.support.dataprovider.OrgTreeAllUserProvider;
import com.wellsoft.pt.multi.org.support.dataprovider.OrgTreeDialogDataProvider;
import com.wellsoft.pt.multi.org.support.dataprovider.OrgTreeDialogProvider;
import com.wellsoft.pt.multi.org.support.utils.ConvertOrgNode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;

@Component
public class BusinessBookDataProvider implements OrgTreeDialogDataProvider, OrgTreeDialogProvider,
        OrgTreeAllUserProvider {

    public static final String TYPE = "BusinessBook";
    private static final long serialVersionUID = -1126965706191270580L;
    @Autowired
    private BusinessCategoryService businessCategoryService;

    @Autowired
    private BusinessCategoryOrgService businessCategoryOrgService;

    @Autowired
    private BusinessRoleOrgUserService businessRoleOrgUserService;

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public List<TreeNode> provideData(OrgTreeDialogParams p) {
        Map<String, String> otherParams = p.getOtherParams();
        String categoryId = otherParams.get("categoryId");
        if (StringUtils.isBlank(categoryId)) {
            throw new RuntimeException("没有传入分类参数");
        }
        Map<String, Object> params = Maps.newHashMap();
        params.putAll(otherParams);
        String hql = "from BusinessCategoryEntity where id=:categoryId";
        List<BusinessCategoryEntity> businessCategoryEntity = businessCategoryService.listByHQL(hql, params);
        if (businessCategoryEntity.isEmpty() == false) {
            categoryId = businessCategoryEntity.get(0).getUuid();
        }
        String showOrgUser = otherParams.get("showOrgUser");
        List<TreeNode> nodes = new ArrayList<TreeNode>();
        getOrgByRecursive("-1", categoryId, nodes, StringUtils.equals(showOrgUser, "true"));
        return nodes;
    }

    protected void getOrgByRecursive(String parentUuid, String categoryId, List<TreeNode> nodes, boolean showOrgUser) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("parentUuid", parentUuid);
        paramMap.put("categoryId", categoryId);
        String hql = "from BusinessCategoryOrgEntity where businessCategoryUuid = :categoryId and parentUuid = :parentUuid order by code";
        List<BusinessCategoryOrgEntity> entitys = businessCategoryOrgService.listByHQL(hql, paramMap);
        for (BusinessCategoryOrgEntity entity : entitys) {
            TreeNode node = new TreeNode();
            node.setId(entity.getId());
            node.setData(entity.getUuid());
            node.setName(entity.getName());
            node.setType(entity.getId().substring(0, 1));
            nodes.add(node);
            if (showOrgUser) {
                // 显示分组下的用户信息
                getOrgUser(node);
            }
            getOrgByRecursive((String) node.getData(), categoryId, node.getChildren(), showOrgUser);
        }
    }

    protected void getOrgUser(TreeNode node) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("businessCategoryOrgUuid", node.getData());

        List<String> userIdList = new ArrayList<String>();
        for (TreeNode children : node.getChildren()) {
            if (IdPrefix.USER.getValue().equals(children.getType())) {
                userIdList.add((String) children.getData());
            }
        }

        String hql = "from BusinessRoleOrgUserEntity where businessCategoryOrgUuid = :businessCategoryOrgUuid";
        List<BusinessRoleOrgUserEntity> entitys = businessRoleOrgUserService.listByHQL(hql, paramMap);
        for (BusinessRoleOrgUserEntity entity : entitys) {
            if (StringUtils.isNotBlank(entity.getUsers())) {
                String[] userIds = entity.getUsers().split(";");
                String[] userValues = entity.getUsersValue().split(";");
                if (userIds.length == userValues.length) { //健壮性判断，担心前端js会有bug
                    for (int i = 0; i < userIds.length; i++) {
                        if (!userIdList.contains(userIds[i])) {
                            userIdList.add(userIds[i]);
                            TreeNode children = new TreeNode();
                            children.setId(userIds[i]);
                            children.setName(userValues[i]);
                            children.setType(IdPrefix.USER.getValue());
                            node.getChildren().add(children);
                        }
                    }
                }
            }
        }
    }

    private List<BusinessCategoryEntity> getBusinessCategoryEntityList(String categoryId) {
        BusinessCategoryEntity categoryEntity = new BusinessCategoryEntity();
        categoryEntity.setId(categoryId);
        List<BusinessCategoryEntity> businessCategoryEntityList = businessCategoryService.listByEntity(categoryEntity);
        if (businessCategoryEntityList.size() == 0) {
            BusinessCategoryEntity businessCategoryEntity = businessCategoryService.getOne(categoryId);
            if (businessCategoryEntity != null) {
                businessCategoryEntityList.add(businessCategoryEntity);
            }
        }
        return businessCategoryEntityList;
    }

    @Override
    public List<OrgNode> children(OrgTreeDialLogAsynParms parms) {
        List<OrgNode> orgNodes = new ArrayList<>();
        //节点Id null 根节点
        Map<String, String> otherParams = parms.getOtherParams();
        List<String> checkedIds = parms.getCheckedIds();
        Set<String> halfCheckSet = this.getHalfCheckSet(checkedIds);

        // 默认读取可选节点配置
        boolean showUserNodes = false;//是否展示用户
        if (otherParams.containsKey("showOrgUser")) {
            parms.setIsNeedUser(StringUtils.equals(otherParams.get("showOrgUser"), "true") ? 1 : 0);
            showUserNodes = parms.getIsNeedUser() == 1;
        }
        String ids = otherParams.get("ids");
        String operator = otherParams.get("operator");
        Set<String> idSet = Sets.newHashSet();
        if (StringUtils.isNotBlank(ids)) {
            idSet.addAll(Arrays.asList(ids.split(",|;|，|；")));
        }
        if (StringUtils.isBlank(parms.getTreeNodeId())) {
            String categoryIdStr = otherParams.get("categoryId");
            if (StringUtils.isBlank(categoryIdStr)) {
                throw new RuntimeException("没有传入分类参数");
            }
            String[] categoryIds = categoryIdStr.split(",|;|，|；");
            for (String categoryId : categoryIds) {
                //参照旧接口categoryId
                List<BusinessCategoryEntity> businessCategoryEntityList = this.getBusinessCategoryEntityList(categoryId);
                if (businessCategoryEntityList.size() == 0) {
                    continue;
                }
                String uuId = businessCategoryEntityList.get(0).getUuid();
                //查询下一级
                List<BusinessCategoryOrgEntity> categoryOrgEntityList = filterBusinessCategoryOrgEntity(uuId, "-1", idSet, operator);
                addOrgNodes(orgNodes, categoryOrgEntityList, operator, idSet, checkedIds, halfCheckSet, showUserNodes);
            }
            return orgNodes;
        }
        BusinessCategoryOrgEntity orgEntity = businessCategoryOrgService.getBusinessById(parms.getTreeNodeId());
        //查询下一级
        List<BusinessCategoryOrgEntity> categoryOrgEntityList = this.listCategoryOrg(null, orgEntity.getUuid());
        addOrgNodes(orgNodes, categoryOrgEntityList, operator, idSet, checkedIds, halfCheckSet, showUserNodes);
        if (showUserNodes) {
            List<OrgNode> userOrgNodes = this.userOrgNodes(orgEntity.getUuid(), parms.getCheckedIds());
            if (userOrgNodes != null) {
                orgNodes.addAll(userOrgNodes);
            }
        }
        return orgNodes;
    }

    private void addOrgNodes(List<OrgNode> orgNodes, List<BusinessCategoryOrgEntity> categoryOrgEntityList, String operator, Set<String> idSet, List<String> checkedIds, Set<String> halfCheckSet, boolean showUserNodes) {
        for (BusinessCategoryOrgEntity businessCategoryOrgEntity : categoryOrgEntityList) {
            if ("not in".equalsIgnoreCase(operator) && idSet.contains(businessCategoryOrgEntity.getId())) {
                continue;
            }
            //二级 id前缀 2_
            OrgNode orgNode = this.convert(businessCategoryOrgEntity);
            ConvertOrgNode.checked(orgNode, checkedIds, halfCheckSet);
            //查询下一级数量
            long count = 0L;
            if ("not in".equalsIgnoreCase(operator) && CollectionUtils.isNotEmpty(idSet)) {
                Map<String, Object> params = Maps.newHashMap();
                params.put("parentUuid", businessCategoryOrgEntity.getUuid());
                params.put("ids", idSet);
                count = businessCategoryOrgService.getDao().countByHQL("from BusinessCategoryOrgEntity where parentUuid=:parentUuid and id not in (:ids)", params);
            } else {
                count = this.countCategoryOrg(null, businessCategoryOrgEntity.getUuid());
            }
            //count=0 需要用户数据
            if (count == 0l && showUserNodes) {
                //查询用户数量
                count = this.countUser(businessCategoryOrgEntity.getUuid());
            }
            if (count > 0l) {
                orgNode.setIsParent(true);
            }
            orgNodes.add(orgNode);
        }
    }

    private Set<String> getHalfCheckSet(List<String> checkedIds) {
        Set<String> set = new HashSet<>();
        if (checkedIds == null) {
            return set;
        }
        for (String checkedId : checkedIds) {
            this.addHalfCheckSet(set, checkedId);
        }
        return set;
    }

    private void addHalfCheckSet(Set<String> set, String checkedId) {
        BusinessCategoryOrgEntity businessCategoryOrgEntity = businessCategoryOrgService.get(checkedId);
        if (businessCategoryOrgEntity != null && !businessCategoryOrgEntity.getParentUuid().equals("-1")) {
            set.add(businessCategoryOrgEntity.getParentUuid());
            this.addHalfCheckSet(set, businessCategoryOrgEntity.getParentUuid());
        }
    }

    private List<OrgNode> userOrgNodes(String businessCategoryOrgUuid, List<String> checkedIds) {
        List<BusinessRoleOrgUserEntity> userEntityList = this.listUser(businessCategoryOrgUuid);
        Set<String> userIdSet = new HashSet<>();
        for (BusinessRoleOrgUserEntity userEntity : userEntityList) {
            if (StringUtils.isNotBlank(userEntity.getUsers())) {
                String[] userIds = userEntity.getUsers().split(";");
                for (String idStr : userIds) {
                    userIdSet.add(idStr);
                }
            }
        }

        if (userIdSet.size() > 0) {
            List<OrgNode> list = new ArrayList<>();
            HashMap<String, Object> query = new HashMap<String, Object>();
            StringBuilder uhqlSb = new StringBuilder(
                    "select a.id as id,a.userName as name,b.sex as sex,a.code as code from MultiOrgUserAccount a,MultiOrgUserInfo b ");
            uhqlSb.append(" where a.id = b.userId and a.isForbidden = 0 and ");
            HqlUtils.appendSql("a.id", query, uhqlSb, Sets.<Serializable>newHashSet(userIdSet));
            uhqlSb.append(" order by a.code ");
            List<OrgNodeUserDto> userNodes = businessRoleOrgUserService.listItemHqlQuery(uhqlSb.toString(),
                    OrgNodeUserDto.class, query);
            for (OrgNodeUserDto userNode : userNodes) {
                //转换
                OrgNode orgNode = ConvertOrgNode.convert(userNode);
                ConvertOrgNode.checked(orgNode, checkedIds, null);
                list.add(orgNode);
            }
            return list;
        }
        return null;
    }

    private long countUser(String businessCategoryOrgUuid) {
        //查询用户数量
        BusinessRoleOrgUserEntity businessRoleOrgUserEntity = new BusinessRoleOrgUserEntity();
        businessRoleOrgUserEntity.setBusinessCategoryOrgUuid(businessCategoryOrgUuid);
        long count = businessRoleOrgUserService.getDao().countByEntity(businessRoleOrgUserEntity);
        return count;
    }

    private List<BusinessRoleOrgUserEntity> listUser(String businessCategoryOrgUuid) {
        //查询用户
        BusinessRoleOrgUserEntity businessRoleOrgUserEntity = new BusinessRoleOrgUserEntity();
        businessRoleOrgUserEntity.setBusinessCategoryOrgUuid(businessCategoryOrgUuid);
        List<BusinessRoleOrgUserEntity> userEntityList = businessRoleOrgUserService
                .listByEntity(businessRoleOrgUserEntity);
        return userEntityList;
    }

    private long countCategoryOrg(String businessCategoryUuid, String parentUuid) {
        BusinessCategoryOrgEntity categoryOrgEntity = new BusinessCategoryOrgEntity();
        categoryOrgEntity.setBusinessCategoryUuid(businessCategoryUuid);
        categoryOrgEntity.setParentUuid(parentUuid);
        long count = businessCategoryOrgService.getDao().countByEntity(categoryOrgEntity);
        return count;
    }

    private List<BusinessCategoryOrgEntity> listCategoryOrg(String businessCategoryUuid, String parentUuid) {
        BusinessCategoryOrgEntity categoryOrgEntity = new BusinessCategoryOrgEntity();
        categoryOrgEntity.setBusinessCategoryUuid(businessCategoryUuid);
        categoryOrgEntity.setParentUuid(parentUuid);
        List<BusinessCategoryOrgEntity> categoryOrgEntityList = businessCategoryOrgService
                .listAllByPage(categoryOrgEntity, null, "code");
        return categoryOrgEntityList;
    }


    private OrgNode convert(BusinessCategoryOrgEntity businessCategoryOrgEntity) {
        OrgNode orgNode = new OrgNode();
        //二级 id前缀
        orgNode.setId(businessCategoryOrgEntity.getId());
        orgNode.setName(businessCategoryOrgEntity.getName());
        orgNode.setType(businessCategoryOrgEntity.getId().substring(0, 1));
        orgNode.setIconSkin(orgNode.getType());
        return orgNode;
    }

    @Override
    public List<OrgNode> full(OrgTreeDialLogAsynParms parms) {
        String categoryIdStr = parms.getOtherParams().get("categoryId");
        if (StringUtils.isBlank(categoryIdStr)) {
            throw new RuntimeException("没有传入分类参数");
        }
        List<OrgNode> orgNodes = new ArrayList<>();
        String[] categoryIds = categoryIdStr.split(",|;|，|；");
        String ids = parms.getOtherParams().get("ids");
        String operator = parms.getOtherParams().get("operator");
        Set<String> idSet = Sets.newHashSet();
        if (StringUtils.isNotBlank(ids)) {
            idSet.addAll(Arrays.asList(ids.split(",|;|，|；")));
        }
        if (parms.getOtherParams().containsKey("showOrgUser")) {
            parms.setIsNeedUser(StringUtils.equals(parms.getOtherParams().get("showOrgUser"), "true") ? 1 : 0);
        }
        for (String categoryId : categoryIds) {
            //参照旧接口categoryId
            List<BusinessCategoryEntity> businessCategoryEntityList = this.getBusinessCategoryEntityList(categoryId);
            if (businessCategoryEntityList.size() == 0) {
                continue;
            }
            String uuId = businessCategoryEntityList.get(0).getUuid();
            orgNodes.addAll(this.getOrgNodes(uuId, "-1", parms.getIsNeedUser(), parms.getCheckedIds(), idSet, operator));
        }
        return orgNodes;
    }

    private List<BusinessCategoryOrgEntity> filterBusinessCategoryOrgEntity(String businessCategoryUuid, String parentUuid, Set<String> idSet, String operator) {
        //查询下一级
        List<BusinessCategoryOrgEntity> categoryOrgEntityList = Lists.newArrayList();

        if ("in".equalsIgnoreCase(operator) && !idSet.isEmpty()) {// 仅包含的情况下，要判断出父节点
            Map<String, Object> idParams = Maps.newHashMap();
            idParams.put("ids", idSet);
            idParams.put("businessCategoryUuid", businessCategoryUuid);
            List<BusinessCategoryOrgEntity> entities = businessCategoryOrgService.listByHQL("from BusinessCategoryOrgEntity where id in (:ids) and businessCategoryUuid=:businessCategoryUuid", idParams);
            if (CollectionUtils.isNotEmpty(entities)) {
                Set<String> parentUuids = Sets.newHashSet();
                for (BusinessCategoryOrgEntity entity : entities) {
                    if ("-1".equals(entity.getParentUuid())) {// 父节点，直接加入展示节点的集合
                        categoryOrgEntityList.add(entity);
                        parentUuids.add(entity.getUuid());
                    } else {// 叶子节点，需要判断是否其父节点已经加入展示节点的集合
                        boolean exclude = false;
                        // 往上追溯其父节点是否已经在包含的节点里面，如果不包含，则展示节点加入，否则排除
                        BusinessCategoryOrgEntity parent = businessCategoryOrgService.getOne(entity.getParentUuid());
                        if (idSet.contains(parent.getId())) {
                            exclude = true;
                        }
                        if (!exclude && !parentUuids.contains(entity.getParentUuid())) {
                            parent = businessCategoryOrgService.getOne(entity.getParentUuid());
                            if (idSet.contains(parent.getId())) {
                                exclude = true;
                            }
                            if (!exclude && !"-1".equals(parent.getParentUuid())) {//还有父级
                                parent = businessCategoryOrgService.getOne(parent.getParentUuid());
                                if (idSet.contains(parent.getId())) {
                                    exclude = true;
                                }
                                while (!exclude && parent != null) {
                                    if (parentUuids.contains(parent.getUuid())) {
                                        exclude = true;
                                        break;
                                    }
                                    parent = businessCategoryOrgService.getOne(parent.getParentUuid());
                                }
                            }
                        }
                        if (!exclude) {
                            parentUuids.add(entity.getUuid());
                            categoryOrgEntityList.add(entity);
                        }
                    }
                }
            }
        } else {
            categoryOrgEntityList = this.listCategoryOrg(businessCategoryUuid, parentUuid);
        }
        return categoryOrgEntityList;
    }

    private List<OrgNode> getOrgNodes(String businessCategoryUuid, String parentUuid, int isNeedUser,
                                      List<String> checkedIds, Set<String> idSet, String operator) {
        //查询下一级
        List<BusinessCategoryOrgEntity> categoryOrgEntityList = filterBusinessCategoryOrgEntity(businessCategoryUuid, parentUuid, idSet, operator);
        if (categoryOrgEntityList.size() == 0) {
            return null;
        }
        List<OrgNode> orgNodes = new ArrayList<>();
        for (BusinessCategoryOrgEntity categoryOrgEntity : categoryOrgEntityList) {
            if ("not in".equalsIgnoreCase(operator) && idSet.contains(categoryOrgEntity.getId())) {
                continue;
            }
            OrgNode orgNode = this.convert(categoryOrgEntity);
            ConvertOrgNode.checked(orgNode, checkedIds, null);
            List<OrgNode> children = this.getOrgNodes(null, categoryOrgEntity.getUuid(), isNeedUser, checkedIds, idSet, "in".equalsIgnoreCase(operator) ? null : operator);
            if (isNeedUser == 1) {
                List<OrgNode> userOrgNodes = this.userOrgNodes(categoryOrgEntity.getUuid(), checkedIds);
                if (children == null) {
                    children = userOrgNodes;
                } else if (userOrgNodes != null) {
                    children.addAll(userOrgNodes);
                }
            }
            orgNode.setChildren(children);
            orgNodes.add(orgNode);
        }
        return orgNodes;
    }

    @Override
    public List<OrgNode> search(OrgTreeDialLogAsynParms parms) {
        Assert.hasText(parms.getKeyword(), "keyword 不能为空");
        String categoryIdStr = parms.getOtherParams().get("categoryId");
        String[] categoryIds = categoryIdStr.split(",|;|，|；");
        String ids = parms.getOtherParams().get("ids");
        String operator = parms.getOtherParams().get("operator");
        Set<String> idSet = Sets.newHashSet();
        if (StringUtils.isNotBlank(ids)) {
            idSet.addAll(Arrays.asList(ids.split(",|;|，|；")));
        }
        if (parms.getOtherParams().containsKey("showOrgUser")) {
            parms.setIsNeedUser(StringUtils.equals(parms.getOtherParams().get("showOrgUser"), "true") ? 1 : 0);
        }
        List<OrgNode> orgNodes = new ArrayList<>();
        for (String categoryId : categoryIds) {
            //参照旧接口categoryId
            List<BusinessCategoryEntity> businessCategoryEntityList = this.getBusinessCategoryEntityList(parms.getOtherParams().get("categoryId"));
            Set<String> businessCategoryUuidSet = new HashSet<>();
            for (BusinessCategoryEntity businessCategoryEntity : businessCategoryEntityList) {
                businessCategoryUuidSet.add(businessCategoryEntity.getUuid());
            }
            if (businessCategoryUuidSet.size() == 0) {
                continue;
            }
            HashMap<String, Object> query = new HashMap<String, Object>();
            query.put("name", "%" + parms.getKeyword() + "%");
            StringBuilder uhqlSb = new StringBuilder("from BusinessCategoryOrgEntity where name like :name and ");
            if (StringUtils.isNotBlank(operator) && CollectionUtils.isNotEmpty(idSet)) {
                query.put("ids", idSet);
                uhqlSb.append(" id " + operator + " (:ids) and ");
            }
            HqlUtils.appendSql("businessCategoryUuid", query, uhqlSb, Sets.<Serializable>newHashSet(businessCategoryUuidSet));
            List<BusinessCategoryOrgEntity> categoryOrgEntityList = businessCategoryOrgService.listByHQL(uhqlSb.toString(),
                    query);
            Set<String> categoryOrgUUIDSet = new HashSet<>();
            for (BusinessCategoryOrgEntity categoryOrgEntity : categoryOrgEntityList) {
                OrgNode orgNode = this.convert(categoryOrgEntity);
                orgNodes.add(orgNode);
                categoryOrgUUIDSet.add(categoryOrgEntity.getUuid());
            }
            if (parms.getIsNeedUser() == 0) {
                continue;
            }
            Set<String> userIdSet = getUserIdSet(categoryOrgUUIDSet);
            if (userIdSet.size() > 0) {
                List<OrgNode> list = new ArrayList<>();
                List<OrgNodeUserDto> userNodes = getOrgNodeUserDtos(parms.getKeyword(), userIdSet);
                for (OrgNodeUserDto userNode : userNodes) {
                    //转换
                    OrgNode orgNode = ConvertOrgNode.convert(userNode);
                    list.add(orgNode);
                }
                orgNodes.addAll(list);
            }
        }

        return orgNodes;
    }

    private List<OrgNodeUserDto> getOrgNodeUserDtos(String keyword, Set<String> userIdSet) {
        HashMap<String, Object> userQuery = new HashMap<String, Object>();
        userQuery.put("key_like", "%" + keyword + "%");
        StringBuilder userHql = new StringBuilder(
                "select a.id as id,a.userName as name,b.sex as sex,a.code as code from MultiOrgUserAccount a,MultiOrgUserInfo b ");
        userHql.append(" where a.id = b.userId and a.isForbidden = 0 "
                + "and (a.loginName like :key_like or a.loginNameLowerCase like :key_like or a.userName like :key_like or a.userNamePy like :key_like) and ");
        HqlUtils.appendSql("a.id", userQuery, userHql, Sets.<Serializable>newHashSet(userIdSet));
        userHql.append(" order by a.code ");
        return businessRoleOrgUserService.listItemHqlQuery(userHql.toString(), OrgNodeUserDto.class, userQuery);
    }

    private Set<String> getUserIdSet(Set<String> categoryOrgUUIDSet) {
        if (categoryOrgUUIDSet.size() == 0) {
            return new HashSet<>();
        }
        HashMap<String, Object> roleOrgUserQuery = new HashMap<String, Object>();
        StringBuilder hql = new StringBuilder("from BusinessRoleOrgUserEntity where ");
        HqlUtils.appendSql("businessCategoryOrgUuid", roleOrgUserQuery, hql, Sets.<Serializable>newHashSet(categoryOrgUUIDSet));
        List<BusinessRoleOrgUserEntity> userEntityList = businessRoleOrgUserService.listByHQL(hql.toString(),
                roleOrgUserQuery);
        Set<String> userIdSet = new HashSet<>();
        for (BusinessRoleOrgUserEntity userEntity : userEntityList) {
            if (StringUtils.isNotBlank(userEntity.getUsers())) {
                String[] userIds = userEntity.getUsers().split(";");
                for (String idStr : userIds) {
                    userIdSet.add(idStr);
                }
            }
        }
        return userIdSet;
    }

    @Override
    public UserNodePy allUserSearch(OrgTreeDialLogAsynParms parms) {
        Assert.hasText(parms.getTreeNodeId(), "treeNodeId 不能为空");

        Set<String> categoryOrgUUIDSet = new HashSet<>();
        BusinessCategoryOrgEntity orgEntity = businessCategoryOrgService.getBusinessById(parms.getTreeNodeId());
        categoryOrgUUIDSet.add(orgEntity.getUuid());
        Set<String> userIdSet = getUserIdSet(categoryOrgUUIDSet);
        UserNodePy userNodePy = new UserNodePy();
        if (userIdSet.size() > 0) {
            HashMap<String, Object> query = new HashMap<String, Object>();
            StringBuilder hqlSb = new StringBuilder(
                    "select distinct a.id as id,a.userName as name,a.userNamePy as namePy,b.sex as iconSkin from MultiOrgUserAccount a,MultiOrgUserInfo b ");
            hqlSb.append(" where a.id = b.userId and a.isForbidden = 0 and ");
            HqlUtils.appendSql("a.id", query, hqlSb, Sets.<Serializable>newHashSet(userIdSet));
            if (StringUtils.isNotBlank(parms.getKeyword())) {
                query.put("key_eq", parms.getKeyword());
                query.put("key_right", parms.getKeyword() + "%");
                query.put("key_left", "%" + parms.getKeyword());
                query.put("key_like", "%" + parms.getKeyword() + "%");
                hqlSb.append(" and (a.userName like :key_like or a.userNamePy like :key_like) "
                        + "order by a.userNamePy ");
            } else {
                hqlSb.append(" order by a.userNamePy ");
            }
            List<UserNode> userNodes = businessCategoryOrgService.listItemHqlQuery(hqlSb.toString(), UserNode.class,
                    query);
            List<String> py = new ArrayList<>();
            for (UserNode userNode : userNodes) {
                py.add(userNode.getNamePy().substring(0, 1));
                ConvertOrgNode.setUserNode(userNode);
            }
            userNodePy.setNodes(userNodes);
            userNodePy.setPy(py);
        }
        return userNodePy;
    }
}
