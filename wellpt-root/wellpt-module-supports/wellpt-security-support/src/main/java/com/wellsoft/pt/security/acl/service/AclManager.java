package com.wellsoft.pt.security.acl.service;

import com.wellsoft.pt.org.entity.Group;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.security.acl.entity.AclEntry;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lilin
 * @ClassName: AclManager
 * @Description: ACL的管理接口
 */

public interface AclManager {
    /**
     * G_ D_
     *
     * @param @param  user
     * @param @param  classId
     * @param @return 设定文件
     * @return boolean 返回类型
     * @throws
     * @Title: canRead
     * @Description: 判断用户是否具有 读权限
     */
    // FIXME：AclManager 暴露AclEntryDao？？？
    // public AclEntryDao getAclEntryDao();
    public boolean canRead(User user, String classId);

    public boolean canWrite(User user, String classId);

    public boolean canDelete(User user, String classId);

    public boolean canRead(Group group, String classId);

    public boolean canWrite(Group group, String classId);

    public boolean canDelete(Group group, String classId);

    public Set<User> getAllUser(String classId);

    public Set<User> getAllReadUser(String classId);

    public Set<User> getAllWriteUser(String classId);

    public Set<User> getAllDeleteUser(String classId);

    // public List<Role> getAllReadRole(String classId);
    //
    // public List<Role> getAllWriteRole(String classId);
    //
    // public List<Role> getAllDeleteRole(String classId);

    public void saveAcl(String sid, byte permission, String classType, String classId);

    public void saveAcl(String sid, byte permission, String classType, String classId, int type);

    public void saveAcl(String sid, byte permission, String classType, String classId, int type, String action);

    public void saveAcl(AclEntry entry);

    public void deleteAcl(User user, String classId);

    public void deleteAcl(User user);

    public void deleteAcl(String classId);

    public void deleteAcl(String classId, String classType, String action);

    // public void updateAcl(User user, byte permission, String classType,
    // String classId);

    public Map<String, List> getAllEntityByUser(User user) throws ClassNotFoundException;

    public <T> List<T> getEntityByUser(User user, Class T);

    public List<Map> getEntityByUser(User user, String tableName);

    public List<AclEntry> getAcls(String id, String classtype, int type, String action);

    public List<AclEntry> getAcls(String hql, final Object... values);
}
