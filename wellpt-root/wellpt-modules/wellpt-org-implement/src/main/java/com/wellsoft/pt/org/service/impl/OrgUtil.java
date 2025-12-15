package com.wellsoft.pt.org.service.impl;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService;
import com.wellsoft.pt.org.dao.UserDao;
import com.wellsoft.pt.org.entity.Job;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.org.entity.UserJob;
import com.wellsoft.pt.org.service.JobService;
import com.wellsoft.pt.org.service.UserService;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.Role;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-8-5.1  zhengky	2014-8-5	  Create
 * </pre>
 * @date 2014-8-5
 */
public class OrgUtil {
    // TODO

    // 管理型组织
    public static final String MGR_ORG = "MgrOrg";
    // 专业型组织
    public static final String PRO_ORG = "ProOrg";

    /**
     * 构建权限树节点(部门/群组/用户/岗位)
     *
     * @param treeNode
     * @param privileges
     * @param checkedResources
     */
    public static TreeNode buildPrivilegeTree(List<Privilege> privileges, List<Privilege> checkedResources) {
        TreeNode treeNode = new TreeNode();
        treeNode.setName("权限");
        treeNode.setId(TreeNode.ROOT_ID);
        treeNode.setNocheck(true);

        List<TreeNode> children = new ArrayList<TreeNode>();
        for (Privilege privilege : privileges) {
            TreeNode child = new TreeNode();
            child.setId(privilege.getUuid());
            child.setName(privilege.getName());
            children.add(child);

            // 选中已拥有的结点
            child.setChecked(checkedResources.contains(privilege));
        }
        treeNode.getChildren().addAll(children);
        return treeNode;
    }

    /**
     * 导入时根据规则查找上级领导 3种规则.
     * '10003;赵六;立达信绿色照明股份有限公司导入测试/总经理办公室/经理/张三
     *
     * @param userService
     * @param userDao
     * @param jobDao
     * @param leadername
     * @return
     */
    public static User findLeaders(UserService userService, UserDao userDao, JobService jobService, String leadername) {
        User leader = userService.getByLoginName(leadername);
        // 根据登录名取不到，取姓名,再取不到则用部门全路径+职位+用户名取
        if (leader == null) {
            List<User> users = userService.getUserByName(leadername);
            if (users.isEmpty() || users.size() > 1) {
                if (leadername.indexOf("/") < 0) {// 如果不包含/ 则返回空.
                    return null;
                }
                // 根据部门全路径+职位+用户取 TODO
                String jobdeptPath = leadername.substring(0, leadername.lastIndexOf("/"));
                String pathName = jobdeptPath.substring(0, jobdeptPath.lastIndexOf("/"));
                String jobName = jobdeptPath.substring(jobdeptPath.lastIndexOf("/") + 1, jobdeptPath.length());
                String userName = leadername.substring(leadername.lastIndexOf("/") + 1, leadername.length());

                // 先找职位，再找用户
                List<Job> jobList = jobService.getJobByNameAndDeptName(jobName, pathName);
                if (jobList == null || jobList.size() == 0) {
                    return null;
                } else {
                    // 职位获得关联用户
                    Set<UserJob> jobusers = jobList.get(0).getJobUsers();
                    for (UserJob userJob : jobusers) {
                        if (userName.equals(userJob.getUser().getUserName())) {
                            return userService.getById(userJob.getUser().getId());
                        }
                    }
                }
            } else {
                leader = users.get(0);
            }
        }
        return leader;
    }

    /**
     * 构建分类和角色map，分类为key，角色list为value
     *
     * @param roles
     * @return
     */
    private static HashMap<String, List<Role>> getCategoryRoleMap(List<Role> roles) {
        HashMap<String, List<Role>> categoryRoleMap = new HashMap<String, List<Role>>();
        categoryRoleMap.put("null", new ArrayList<Role>());
        for (Role role : roles) {
            if (StringUtils.isEmpty(role.getCategoryUuid())) {
                categoryRoleMap.get("null").add(role);
            } else {
                if (categoryRoleMap.containsKey(role.getCategoryUuid())) {
                    categoryRoleMap.get(role.getCategoryUuid()).add(role);
                } else {
                    ArrayList<Role> rolels = new ArrayList<Role>();
                    rolels.add(role);
                    categoryRoleMap.put(role.getCategoryUuid(), rolels);
                }
            }
        }
        return categoryRoleMap;
    }

    private static List<TreeNode> createRoleTreeNodes(Set<Role> userRoles, List<Role> roles) {
        List<TreeNode> children = new ArrayList<TreeNode>();
        if (roles == null) {
            return children;
        }
        for (Role role : roles) {
            TreeNode child = new TreeNode();
            child.setId(role.getUuid());
            child.setName(role.getName());
            child.setChecked(userRoles.contains(role));
            children.add(child);
        }
        return children;
    }

    /**
     * 根据权限UUID获取相应的资源树
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.PrivilegeService#getResourceTree(java.lang.String)
     */
    public static TreeNode getCategoryRoleTree(DataDictionaryService dataDictionaryService, Set<Role> userRoles,
                                               List<Role> roles) {
        HashMap<String, List<Role>> categoryRoleMap = getCategoryRoleMap(roles);
        String prefix = Config.getValue("audit.pushResource.prefix");
        String categoryType = Config.getValue("audit.security.category.type");
        String QUERY_FOR_CATEGORYPRIVILEGE_TREE = "select cd_data_dict.uuid as uuid, cd_data_dict.name as name, cd_data_dict.code as code, cd_data_dict.parent.uuid as parentUuid from DataDictionary cd_data_dict where cd_data_dict.parent.type = '"
                + categoryType + "' order by cd_data_dict.code asc";
        DataDictionary category = dataDictionaryService.getByType(categoryType);
        if (category == null) {
            return null;
        }
        String categoryUuid = category.getUuid();// "f304594f-8c59-4aa5-bf40-e7f19764644e"
        // 构建权限资源树
        List<QueryItem> allResources = dataDictionaryService.query(QUERY_FOR_CATEGORYPRIVILEGE_TREE,
                new HashMap<String, Object>(0), QueryItem.class);
        TreeNode treeNode = new TreeNode();
        treeNode.setName("角色列表");
        treeNode.setId(TreeNode.ROOT_ID);
        treeNode.setNocheck(true);

        List<QueryItem> topResources = new ArrayList<QueryItem>();
        Map<String, List<QueryItem>> parentResourceMap = new HashMap<String, List<QueryItem>>();
        for (QueryItem queryItem : allResources) {
            String parentUuid = queryItem.getString("parentUuid");
            if (!categoryUuid.equals(parentUuid)) {
                if (!parentResourceMap.containsKey(parentUuid)) {
                    parentResourceMap.put(parentUuid, new ArrayList<QueryItem>());
                }
                parentResourceMap.get(parentUuid).add(queryItem);
            } else {
                topResources.add(queryItem);
            }
        }
        for (QueryItem queryItem : topResources) {
            TreeNode node = new TreeNode();
            String resourceUuid = queryItem.getString("uuid");
            String resourceCode = queryItem.getString("code");
            String resourceName = queryItem.getString("name");
            node.setId(resourceUuid);
            node.setName(resourceName);
            node.setNocheck(true);
            // 生成子结点
            buildChildNodes(node, parentResourceMap, userRoles, roles, categoryRoleMap);
            node.getChildren().addAll(createRoleTreeNodes(userRoles, categoryRoleMap.get(resourceCode)));

            treeNode.getChildren().add(node);
        }

        TreeNode nulltreeNode = new TreeNode();
        nulltreeNode.setName("无分类");
        nulltreeNode.setId("null");
        nulltreeNode.setNocheck(true);
        List<Role> nullCategoryroles = categoryRoleMap.get("null");
        // 一级节点
        nulltreeNode.setChildren(createRoleTreeNodes(userRoles, nullCategoryroles));
        treeNode.getChildren().add(nulltreeNode);
        return treeNode;
    }

    /**
     * @param node
     * @param parentMap
     */
    private static void buildChildNodes(TreeNode node, Map<String, List<QueryItem>> parentResourceMap,
                                        Set<Role> userRoles, List<Role> roles, HashMap<String, List<Role>> categoryRoleMap) {
        String key = node.getId();
        List<QueryItem> queryItems = parentResourceMap.get(key);
        if (queryItems == null) {
            return;
        }

        for (QueryItem queryItem : queryItems) {
            TreeNode child = new TreeNode();
            String id = queryItem.getString("uuid");
            String name = queryItem.getString("name");

            child.setId(id);
            child.setName(name);
            child.setNocheck(true);
            child.getChildren().addAll(createRoleTreeNodes(userRoles, categoryRoleMap.get(id)));
            node.getChildren().add(child);
            buildChildNodes(child, parentResourceMap, userRoles, roles, categoryRoleMap);
        }
    }

    /**
     * 是否同步AD
     *
     * @return
     */
    public static boolean isAdSync() {
        String issync = Config.getValue("org.isadsync");
        if ("true".equals(issync)) {
            return true;
        }
        return false;
    }

    /**
     * 是否同步邮件账号
     *
     * @return
     */
    public static boolean isSyncMailAccount() {
        String isSyncMailAccount = Config.getValue("org.sync.mail.account");
        if (Config.TRUE.equalsIgnoreCase(isSyncMailAccount)) {
            return true;
        }
        return false;
    }

    public static String getAdPrincipalnameSuffix() {
        return Config.getValue("org.principalname.suffix");
    }

    // public static String is

    public static String getAdBase() {
        return Config.getValue("org.ad.base");
    }

    public static String getAdBaseOu() {
        // 兼容正式机 先返回OU 后面改成取配置文件
        // return "OU";
        return Config.getValue("org.ad.baseou");
    }

    /**
     * 获取登录名的唯一性范围
     *
     * @return
     */
    public static EnumLoginNameUniqueScope getLoginNameUniqueScope() {
        String uniqueScope = Config.getValue("org.loginName.uniqueScope");
        if (uniqueScope != null && uniqueScope.trim().equals(EnumLoginNameUniqueScope.UNIQUE_IN_GLOBAL.getValue())) {
            return EnumLoginNameUniqueScope.UNIQUE_IN_GLOBAL;
        } else {
            return EnumLoginNameUniqueScope.UNIQUE_IN_TENANT;
        }
    }

    /**
     * 登录名全局唯一
     *
     * @return
     */
    public static boolean isLoginNameUniqueInGlobal() {
        return EnumLoginNameUniqueScope.UNIQUE_IN_GLOBAL == getLoginNameUniqueScope();
    }

    /**
     * 登录名租户内唯一
     *
     * @return
     */
    public static boolean isLoginNameUniqueInTenant() {
        return EnumLoginNameUniqueScope.UNIQUE_IN_TENANT == getLoginNameUniqueScope();
    }

    public static boolean isEnableMulitOrg() {
        String enableMulitOrg = Config.getValue("org.multi.enable", "false");
        if ("true".equalsIgnoreCase(enableMulitOrg)) {
            return true;
        }
        return false;
    }

    public static String getDefaultOrgId() {
        return Config.getValue("org.id.default", "O0000000001");
    }

    /**
     * @author hunt
     * 登录名的唯一性范围
     */
    public enum EnumLoginNameUniqueScope {
        UNIQUE_IN_GLOBAL("-1", "登录名全局性唯一"), UNIQUE_IN_TENANT("0", "登录名租户内唯一");
        private String value = "";
        private String remark;

        private EnumLoginNameUniqueScope(String value, String remark) {
            this.value = value;
            this.remark = remark;
        }

        public static EnumLoginNameUniqueScope value2EnumObj(String value) {
            EnumLoginNameUniqueScope enumObj = null;
            for (EnumLoginNameUniqueScope status : EnumLoginNameUniqueScope.values()) {
                if (status.getValue().equals(value)) {
                    enumObj = status;
                }
            }
            return enumObj;
        }

        public String getValue() {
            return value;
        }

        public String getRemark() {
            return remark;
        }
    }
}
