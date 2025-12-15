package com.wellsoft.pt.security.acl.service;

import org.springframework.stereotype.Service;

@Service
// @Transactional
public class DefaultAclManager {
    //	@Autowired
    //	private AclEntryDao aclEntryDao;
    //	@Autowired
    //	private UserManager userManager;
    //
    //	@Override
    //	public boolean canRead(User user, String classId) {
    //
    //		List<AclEntry> entryList = aclEntryDao.getAclEntryByTable(
    //				user.getUuid(), classId);
    //		for (AclEntry acl : entryList) {
    //			if (this.parsePermissionRead(acl.getPermission())) {
    //				return true;
    //			}
    //		}
    //		Set<Group> goupList = userManager.getAllGroupByUser(user.getUuid());
    //		for (Group group : goupList) {
    //			List<AclEntry> groupentryList = aclEntryDao.getAclEntryByTable(
    //					group.getUuid(), classId);
    //			for (AclEntry acl : groupentryList) {
    //				if (this.parsePermissionRead(acl.getPermission())) {
    //					return true;
    //				}
    //			}
    //		}
    //		return false;
    //	}
    //
    //	@Override
    //	public boolean canWrite(User user, String classId) {
    //
    //		List<AclEntry> entryList = aclEntryDao.getAclEntryByTable(
    //				user.getUuid(), classId);
    //		for (AclEntry acl : entryList) {
    //			if (this.parsePermissionWrite(acl.getPermission())) {
    //				return true;
    //			}
    //		}
    //
    //		Set<Group> goupList = userManager.getAllGroupByUser(user.getUuid());
    //		for (Group group : goupList) {
    //			List<AclEntry> groupentryList = aclEntryDao.getAclEntryByTable(
    //					group.getUuid(), classId);
    //			for (AclEntry acl : groupentryList) {
    //				if (this.parsePermissionWrite(acl.getPermission())) {
    //					return true;
    //				}
    //			}
    //		}
    //		return false;
    //
    //	}
    //
    //	@Override
    //	public boolean canDelete(User user, String classId) {
    //		List<AclEntry> entryList = aclEntryDao.getAclEntryByTable(
    //				user.getUuid(), classId);
    //		for (AclEntry acl : entryList) {
    //			if (this.parsePermissionDelete(acl.getPermission())) {
    //				return true;
    //			}
    //		}
    //		Set<Group> goupList = userManager.getAllGroupByUser(user.getUuid());
    //		for (Group group : goupList) {
    //			List<AclEntry> groupentryList = aclEntryDao.getAclEntryByTable(
    //					group.getUuid(), classId);
    //			for (AclEntry acl : groupentryList) {
    //				if (this.parsePermissionDelete(acl.getPermission())) {
    //					return true;
    //				}
    //			}
    //		}
    //		return false;
    //	}
    //
    //	@Override
    //	public boolean canRead(Group group, String classId) {
    //		// TODO Auto-generated method stub
    //		return false;
    //	}
    //
    //	@Override
    //	public boolean canWrite(Group group, String classId) {
    //		// TODO Auto-generated method stub
    //		return false;
    //	}
    //
    //	@Override
    //	public boolean canDelete(Group group, String classId) {
    //		// TODO Auto-generated method stub
    //		return false;
    //	}
    //
    //	/**
    //	 *
    //	 * @Title: getClassByUser
    //	 * @Description:获取某用户，某个表所有能够控制的记录
    //	 * @param @param user
    //	 * @param @param T
    //	 * @param @return 设定文件
    //	 * @return List<T> 返回类型
    //	 * @throws
    //	 */
    //	@Override
    //	public <T> List<T> getEntityByUser(User user, Class T) {
    //		List<AclEntry> aclList = aclEntryDao.getAclEntryByClass(user,
    //				T.getSimpleName());
    //		HibernateDao hibernatedao = new HibernateDao(
    //				aclEntryDao.getSessionFactory(), T);
    //		List ids = Lists.newArrayList();
    //		for (AclEntry acl : aclList) {
    //			ids.add(acl.getClassId());
    //		}
    //		return hibernatedao.get(ids);
    //	}
    //
    //	@Override
    //	public List<Map> getEntityByUser(User user, String tableName) {
    //		List<AclEntry> aclList = aclEntryDao
    //				.getAclEntryByClass(user, tableName);
    //		// DynamicFormHibernateDao dao = new DynamicFormHibernateDao();
    //		// List ids = Lists.newArrayList();
    //		// List<Map> entityMap = Lists.newArrayList();
    //		// for (AclEntry acl : aclList) {
    //		// entityMap.add(dao.getById(tableName, acl.getClassId()));
    //		// }
    //		// return entityMap;
    //		return null;
    //	}
    //
    //	/**
    //	 *
    //	 * <p>
    //	 * Title: getAllFormByUser
    //	 * </p>
    //	 * <p>
    //	 * Description:
    //	 * </p>
    //	 * 获取用户所有能够访问的资源
    //	 *
    //	 * @param user
    //	 * @return
    //	 * @throws ClassNotFoundException
    //	 * @throws IllegalAccessException
    //	 * @throws InstantiationException
    //	 * @see com.wellsoft.pt.security.acl.service.AclManager#getAllFormByUser(com.wellsoft.pt.core.user.entity.User)
    //	 */
    //	// @Override
    //	public Map<String, List> getAllEntityByUser(User user)
    //			throws ClassNotFoundException {
    //		// TODO Auto-generated method stub
    //		List<AclEntry> aclList = aclEntryDao.getAclEntryByUser(user);
    //		Map<String, List> entityMap = new HashMap();
    //		for (AclEntry acl : aclList) {
    //			String classId = acl.getClassId();
    //			String classType = acl.getClassType();
    //			// Object entity = Class.forName(classType).newInstance();
    //			String className = Class.forName(classType).getSimpleName();
    //			HibernateDao hibernatedao = new HibernateDao(
    //					aclEntryDao.getSessionFactory(), Class.forName(classType));
    //			if (entityMap.containsKey(className)) {
    //				((List) entityMap.get(className))
    //						.add(hibernatedao.get(classId));
    //			} else {
    //				List entityList = Lists.newArrayList();
    //				entityList.add(hibernatedao.get(classId));
    //				entityMap.put(className, entityList);
    //			}
    //		}
    //		return entityMap;
    //	}
    //
    //	@Override
    //	public Set<User> getAllReadUser(String classId) {
    //		// TODO Auto-generated method stub
    //		return null;
    //	}
    //
    //	@Override
    //	public Set<User> getAllWriteUser(String classId) {
    //		// TODO Auto-generated method stub
    //		return null;
    //	}
    //
    //	@Override
    //	public Set<User> getAllDeleteUser(String classId) {
    //		// TODO Auto-generated method stub
    //		return null;
    //	}
    //
    //	@Override
    //	public List<AclEntry> getAcls(String id, String classtype, int type,
    //			String action) {
    //		return aclEntryDao.getAcls(id, classtype, type, action);
    //	}
    //
    //	@Override
    //	public List<AclEntry> getAcls(String hql, final Object... values) {
    //		return aclEntryDao.getAcls(hql, values);
    //	}
    //
    //	@Override
    //	public Set<User> getAllUser(String classId) {
    //		List<AclEntry> acllist = this.aclEntryDao.findBy("classId", classId);
    //		Set<User> userset = Sets.newHashSet();
    //		for (AclEntry acl : acllist) {
    //			if (acl.getType() == AclConstants.TYPE_USER) {
    //				userset.add(userManager.getUserByUUID(acl.getUsid()));
    //			} else if (acl.getType() == AclConstants.TYPE_GROUP) {
    //				List<User> userlist = userManager.getGroupByUUID(acl.getUsid())
    //						.getUserList();
    //				for (User user : userlist) {
    //					userset.add(user);
    //				}
    //			}
    //		}
    //		return userset;
    //	}
    //
    //	@Override
    //	public void saveAcl(String sid, byte permission, String classType,
    //			String classId) {
    //		// TODO Auto-generated method stub
    //		AclEntry entry = new AclEntry();
    //		entry.setClassType(classType);
    //		entry.setClassId(classId);
    //		entry.setPermission(permission);
    //		entry.setUsid(sid);
    //		this.aclEntryDao.save(entry);
    //
    //	}
    //
    //	@Override
    //	public void saveAcl(String sid, byte permission, String classType,
    //			String classId, int type) {
    //		// TODO Auto-generated method stub
    //		AclEntry entry = new AclEntry();
    //		entry.setClassType(classType);
    //		entry.setClassId(classId);
    //		entry.setPermission(permission);
    //		entry.setUsid(sid);
    //		entry.setType(type);
    //		this.aclEntryDao.save(entry);
    //
    //	}
    //
    //	@Override
    //	public void saveAcl(String sid, byte permission, String classType,
    //			String classId, int type, String action) {
    //		// TODO Auto-generated method stub
    //		AclEntry entry = new AclEntry();
    //		entry.setClassType(classType);
    //		entry.setClassId(classId);
    //		entry.setPermission(permission);
    //		entry.setUsid(sid);
    //		entry.setType(type);
    //		entry.setAction(action);
    //		this.aclEntryDao.save(entry);
    //
    //	}
    //
    //	@Override
    //	public void saveAcl(AclEntry entry) {
    //		this.aclEntryDao.save(entry);
    //	}
    //
    //	@Override
    //	public void deleteAcl(User user, String classId) {
    //		// TODO Auto-generated method stub
    //		List<AclEntry> acllist = aclEntryDao.getAclEntryByTable(user.getUuid(),
    //				classId);
    //		for (AclEntry acl : acllist) {
    //			aclEntryDao.delete(acl);
    //		}
    //	}
    //
    //	public void deleteAcl(User user) {
    //		List<AclEntry> acllist = this.aclEntryDao.findBy("sid", user.getUuid());
    //		for (AclEntry acl : acllist) {
    //			aclEntryDao.delete(acl);
    //		}
    //	}
    //
    //	public void deleteAcl(String classId) {
    //		List<AclEntry> acllist = this.aclEntryDao.findBy("classId", classId);
    //		for (AclEntry acl : acllist) {
    //			aclEntryDao.delete(acl);
    //		}
    //	}
    //
    //	private boolean parsePermissionRead(byte permission) {
    //		if ((permission & (byte) 0x1) == 1) {
    //			return true;
    //		} else {
    //			return false;
    //		}
    //	}
    //
    //	private boolean parsePermissionWrite(byte permission) {
    //		if ((permission & (byte) 0x2) == 2) {
    //			return true;
    //		} else {
    //			return false;
    //		}
    //	}
    //
    //	private boolean parsePermissionDelete(byte permission) {
    //		if ((permission & (byte) 0x4) == 4) {
    //			return true;
    //		} else {
    //			return false;
    //		}
    //	}
    //
    //	public static void main(String[] args) {
    //		DefaultAclManager aclManager = new DefaultAclManager();
    //		// aclManager.getTableName(AclEntry.class);
    //		aclManager.parsePermissionRead(Byte.parseByte("100"));
    //		// System.out.println(aclManager.parsePermissionRead(Byte.parseByte("100")));
    //		// System.out.println(aclManager.parsePermissionWrite(Byte.parseByte("100")));
    //		// System.out.println(aclManager.parsePermissionWrite(Byte.parseByte("010")));
    //		// System.out.println(aclManager.parsePermissionWrite(Byte.parseByte("101")));
    //		System.out.println(aclManager.parsePermissionDelete(Byte
    //				.parseByte("100")));
    //		System.out.println(aclManager.parsePermissionDelete(Byte
    //				.parseByte("010")));
    //		System.out.println(aclManager.parsePermissionDelete(Byte
    //				.parseByte("101")));
    //	}
    //
    //	@Override
    //	public AclEntryDao getAclEntryDao() {
    //		// TODO Auto-generated method stub
    //		return this.aclEntryDao;
    //	}
    //
    //	@Override
    //	public void deleteAcl(String classId, String classType, String action) {
    //		this.aclEntryDao.deleteAcl(classId, classType, action);
    //	}

}
