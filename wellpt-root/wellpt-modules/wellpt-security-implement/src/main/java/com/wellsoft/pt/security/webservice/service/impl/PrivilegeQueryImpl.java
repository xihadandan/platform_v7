package com.wellsoft.pt.security.webservice.service.impl;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.org.service.UserService;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.Resource;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.audit.service.ResourceService;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * 2014-8-8.1  zhengky	2014-8-8	  Create
 * </pre>
 * @date 2014-8-8
 */
@Service
public class PrivilegeQueryImpl implements com.wellsoft.pt.security.audit.webservice.PrivilegeQueryService {

    @Autowired
    private UserService userService;
    @Autowired
    private ResourceService resourceService;

    private Logger logger = LoggerFactory.getLogger(PrivilegeQueryImpl.class);

    /**
     * 根据登录名获得用户所有的权限信息
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.core.userdetails.UserDetailsServiceProvider#getUserPrivileges(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public JSONObject getUserPrivileges(String loginName, String[] moduleCodes) {
        User user = userService.getByLoginNameIgnoreCase(loginName, null);
        long time1 = System.currentTimeMillis();
        if (user == null) {
            JSONObject privilegejson = new JSONObject();
            privilegejson.put("error", "查不到的用户信息!");
            return privilegejson;
        }

        HashMap<String, String> kmap = new HashMap<String, String>();
        for (String code : moduleCodes) {
            List<Resource> resources = resourceService.getByCode(code);
            if (resources.size() > 0) {
                kmap.put(resources.get(0).getUuid(), resources.get(0).getUuid());
            }
        }

        List<QueryItem> itemList = this.resourceService.listQueryItemBySQL(
                "select a.uuid,a.code,a.parent_uuid from audit_resource a", null, null);
        Map<String, List<QueryItem>> parentResourceMap = new HashMap<String, List<QueryItem>>();
        List<QueryItem> topResources = new ArrayList<QueryItem>();
        for (QueryItem item : itemList) {
            String parentUuid = item.getString("parentUuid");
            String code = item.getString("code");
            String uuid = item.getString("uuid");
            if (StringUtils.isNotBlank(parentUuid)) {
                if (kmap.containsKey(parentUuid)) {
                }
                if (!parentResourceMap.containsKey(parentUuid)) {
                    parentResourceMap.put(parentUuid, new ArrayList<QueryItem>());
                }
                parentResourceMap.get(parentUuid).add(item);
            }
            if (kmap.containsKey(uuid)) {
                topResources.add(item);
            }
        }

        ArrayList<QueryItem> resourcels = new ArrayList<QueryItem>();
        Map<String, String> allResourceMap = new HashMap<String, String>();
        for (QueryItem object : topResources) {
            resourcels.add(object);
            getAllChildrenByTop(object.getString("uuid"), resourcels, parentResourceMap);
        }

        for (QueryItem objects : resourcels) {
            allResourceMap.put(objects.getString("code"), objects.getString("code"));
        }

        HashSet<Privilege> privilegeSet = new HashSet<Privilege>();

        // 2.获得所有角色 根据所有角色再获得权限
        HashSet<Role> roleSet = (HashSet<Role>) getUserRoles(user);
        for (Role role : roleSet) {
            Set<Privilege> Privileges = role.getPrivileges();
            for (Privilege privilege : Privileges) {
                privilegeSet.add(privilege);
            }
        }

        // 构建权限资源json
        JSONObject rsjson = new JSONObject();
        for (Privilege privilege : privilegeSet) {
            JSONObject privilegejson = new JSONObject();
            privilegejson.put("name", privilege.getName());

            Set<Resource> resourceset = privilege.getResources();
            JSONObject resourcejson = new JSONObject();
            for (Resource resource : resourceset) {
                // 如果资源在查询的资源范围，再加入返回的json里
                if (allResourceMap.containsKey(resource.getCode())) {
                    resourcejson.put(resource.getCode(), resource.getName());
                }
            }
            // 只返回查询范围内的权限
            if (!resourcejson.isEmpty()) {
                privilegejson.put("resources", resourcejson);
                rsjson.put(privilege.getCode(), privilegejson);
            }
        }

        long time2 = System.currentTimeMillis();
        logger.info("[getUserPrivileges] userName:[" + loginName + "]and Data spent " + (time2 - time1) / 1000.0 + "s");
        return rsjson;
    }

    /**
     * 获得用户拥有的角色
     *
     * @param user
     * @return
     */
    private Set<Role> getUserRoles(User user) {
        return userService.getUserOrgRoles(user);
    }

    private void getAllChildrenByTop(String topuuid, ArrayList<QueryItem> resourcels,
                                     Map<String, List<QueryItem>> parentResourceMap) {
        List<QueryItem> queryItems = parentResourceMap.get(topuuid);
        if (queryItems == null || queryItems.size() == 0) {
            return;
        }
        for (QueryItem rs : queryItems) {
            resourcels.add(rs);
            String parentuuid = rs.getString("parentUuid");
            getAllChildrenByTop(parentuuid, resourcels, parentResourceMap);
        }

    }

}
