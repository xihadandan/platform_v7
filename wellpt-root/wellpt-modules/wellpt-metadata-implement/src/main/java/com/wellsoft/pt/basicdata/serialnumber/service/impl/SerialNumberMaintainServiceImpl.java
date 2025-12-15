package com.wellsoft.pt.basicdata.serialnumber.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.basicdata.serialnumber.bean.SerialNumberMaintainBean;
import com.wellsoft.pt.basicdata.serialnumber.dao.SerialNumberMaintainDao;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumber;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumberMaintain;
import com.wellsoft.pt.basicdata.serialnumber.service.SerialNumberMaintainService;
import com.wellsoft.pt.basicdata.serialnumber.service.SerialNumberService;
import com.wellsoft.pt.basicdata.serialnumber.support.SerialNumberBuildParams;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.entity.MultiOrgElement;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.acl.entity.AclSid;
import com.wellsoft.pt.security.acl.service.AclService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 流水号维护服务层实现类
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-6.1	zhouyq		2013-3-6		Create
 * </pre>
 * @date 2013-3-6
 */
@Service
public class SerialNumberMaintainServiceImpl extends
        AbstractJpaServiceImpl<SerialNumberMaintain, SerialNumberMaintainDao, String> implements
        SerialNumberMaintainService {
    @Autowired
    private SerialNumberService serialNumberService;

    @Autowired
    private AclService aclService;

    @Autowired
    private OrgApiFacade orgApiFacade;
    @Autowired
    private DyFormFacade dyFormApiFacade;

    /**
     * 根据指定id来获取流水号维护集合
     */
    @Override
    public List<SerialNumberMaintain> getByDesignatedId(String designatedId, String isOverride) {
        Integer incremental = 0;
        SerialNumber serialNumber = serialNumberService.getById(designatedId);
        incremental = serialNumber.getIncremental();
        List<SerialNumberMaintain> maintainList = dao.listByFieldEqValue("id", designatedId);
        dao.getSession().clear();// 清除缓存
        List<SerialNumberMaintain> maintains = BeanUtils.convertCollection(maintainList, SerialNumberMaintain.class);
        for (SerialNumberMaintain s : maintains) {
            s.setPointer(String.valueOf(Integer.valueOf(s.getPointer()) + incremental));
            // maintainList.add(serialNumberMaintain);
        }
        return maintains;
    }

    @Override
    public SerialNumberMaintain getBySerialNumberIdAndKeyPart(String serialNumberId) {
        Integer incremental = 0;
        String initialValue = "";
        String keyPart = "";
        Boolean isFillPosition = false;
        SerialNumber serialNumber = serialNumberService.getById(serialNumberId);
        if (serialNumber != null) {
            incremental = serialNumber.getIncremental();
            initialValue = serialNumber.getInitialValue();
            Boolean fillPosition = serialNumber.getIsFillPosition();
            if (fillPosition) {
                isFillPosition = true;
            }
            Map<String, String> parts = serialNumberService.explainSerialNumberParts(serialNumber);
            keyPart = parts.get("keyPart");
            SerialNumberMaintain serialNumberMaintain = getByIdAndKeyPart(serialNumberId, keyPart);
            String result = serialNumberMaintain.getPointer();// 旧指针
            String newPointer = String.valueOf(Integer.valueOf(serialNumberMaintain.getPointer()) + incremental);// 未补位新指针
            // 补位处理
            if (isFillPosition) {
                int length = initialValue.length();
                // 0 代表前面补充0
                // 4 代表长度为4
                // d 代表参数为正数型
                String formatContent = "%0" + String.valueOf(length) + "d";
                result = String.format(formatContent, Integer.valueOf(result));
                newPointer = String.format(formatContent, Integer.valueOf(newPointer));
            }
            serialNumberMaintain.setPointer(newPointer);
            return serialNumberMaintain;
        }

        return null;
    }

    /**
     * 根据ID来获取所有的流水号维护集合
     */
    public List<SerialNumberMaintain> getById(String id) {
        List<SerialNumberMaintain> maintains = Lists.newArrayList();
        SerialNumber sList = serialNumberService.getById(id);
        if (null != sList) {
            SerialNumberBuildParams params = new SerialNumberBuildParams();
            params.setOccupied(false);
            params.setSerialNumberId(sList.getId());
            SerialNumberMaintain serialNumberMaintain = serialNumberService.generateSerialNumber2(params);
            if (null != serialNumberMaintain) {
                maintains.add(serialNumberMaintain);
            }
        }
        return maintains;
    }

    /**
     * 根据名称来获取所有的流水号维护集合
     */
    @Deprecated
    // bug#47286:流程签批的流水号的年度时间不会自动更新过来
    @Override
    public List<SerialNumberMaintain> getByName(String name) {
        Integer incremental = 0;
        String initialValue = "";
        Boolean isFillPosition = false;
        List<SerialNumber> sList = serialNumberService.getAllByName(name);
        for (SerialNumber serialNumber : sList) {
            incremental = serialNumber.getIncremental();
            initialValue = serialNumber.getInitialValue();
            Boolean fillPosition = serialNumber.getIsFillPosition();
            if (fillPosition) {
                isFillPosition = true;
            }
        }

        List<SerialNumberMaintain> maintainList = dao.listByFieldEqValue("name", name);
        dao.getSession().clear();// 清除缓存
        List<SerialNumberMaintain> maintains = BeanUtils.convertCollection(maintainList, SerialNumberMaintain.class);
        for (SerialNumberMaintain s : maintains) {
            String result = s.getPointer();// 旧指针
            String newPointer = String.valueOf(Integer.valueOf(s.getPointer()) + incremental);// 未补位新指针
            // 补位处理
            if (isFillPosition) {
                int length = initialValue.length();
                // 0 代表前面补充0
                // 4 代表长度为4
                // d 代表参数为正数型
                String formatContent = "%0" + String.valueOf(length) + "d";
                result = String.format(formatContent, Integer.valueOf(result));
                newPointer = String.format(formatContent, Integer.valueOf(newPointer));
            }
            s.setPointer(newPointer);
        }
        return maintains;
    }

    /**
     * 通过uuid获取流水号维护
     *
     * @param uuid
     * @return
     */
    @Override
    public SerialNumberMaintain get(String uuid) {
        return dao.getOne(uuid);
    }

    /**
     * 通过uuid获取流水号维护VO对象
     *
     * @param uuid
     * @return
     */
    @Override
    public SerialNumberMaintainBean getBeanByUuid(String uuid) {
        SerialNumberMaintain serialNumberMaintain = this.dao.getOne(uuid);
        SerialNumberMaintainBean bean = new SerialNumberMaintainBean();
        BeanUtils.copyProperties(serialNumberMaintain, bean);

        // 设置流水号维护使用人
        List<AclSid> aclSids = aclService.getSid(serialNumberMaintain);
        List<String> sids = new ArrayList<String>();
        for (AclSid sid : aclSids) {
            if (ACL_SID.equals(sid.getSid())) {
                continue;
            }
            sids.add(sid.getSid());
        }
        StringBuilder ownerIds = new StringBuilder();
        StringBuilder ownerNames = new StringBuilder();
        Iterator<String> it = sids.iterator();
        while (it.hasNext()) {
            String sid = it.next();
            if (sid.startsWith(IdPrefix.USER.getValue())) {
                MultiOrgUserAccount user = orgApiFacade.getAccountByUserId(sid);
                ownerIds.append(user.getId());
                ownerNames.append(user.getUserName());
            } else if (sid.startsWith(IdPrefix.DEPARTMENT.getValue())) {
                MultiOrgElement department = orgApiFacade.getOrgElementById(sid);
                ownerIds.append(department.getId());
                ownerNames.append(department.getName());
            }
            if (it.hasNext()) {
                ownerIds.append(Separator.SEMICOLON.getValue());
                ownerNames.append(Separator.SEMICOLON.getValue());
            }
        }
        bean.setOwnerIds(ownerIds.toString());
        bean.setOwnerNames(ownerNames.toString());

        return bean;
    }

    /**
     * 保存流水号维护bean
     *
     * @see com.wellsoft.pt.message.service.SerialNumberMaintainService#saveBean(com.wellsoft.pt.message.bean.SerialNumberMaintainBean)
     */
    @Override
    @Transactional
    public void saveBean(SerialNumberMaintainBean bean) {
        SerialNumberMaintain serialNumberMaintain = new SerialNumberMaintain();
        // 保存新serialNumberMaintain 设置id值
        if (StringUtils.isBlank(bean.getUuid())) {
            bean.setUuid(null);
        } else {
            serialNumberMaintain = this.dao.getOne(bean.getUuid());
        }
        org.springframework.beans.BeanUtils.copyProperties(bean, serialNumberMaintain, "systemUnitId");

        // 设置所有者
        if (StringUtils.isNotBlank(bean.getOwnerIds())) {
            String[] ownerIds = StringUtils.split(bean.getOwnerIds(), Separator.SEMICOLON.getValue());
            serialNumberMaintain.setOwners(Arrays.asList(ownerIds));
        }
        this.dao.save(serialNumberMaintain);
        // ACL行数据权限保存
        this.saveAcl(serialNumberMaintain);
    }

    /**
     * 删除流水号维护
     *
     * @see com.wellsoft.pt.message.service.SerialNumberMaintainService#remove(java.lang.String)
     */
    @Override
    @Transactional
    public void remove(String uuid) {
        this.dao.delete(uuid);
    }

    /**
     * 批量删除
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.serialnumber.service.SerialNumberMaintainService#deleteAllById(java.lang.String[])
     */
    @Override
    @Transactional
    public void deleteAllById(String[] ids) {
        for (String id : ids) {
            SerialNumberMaintain entity = dao.getById(id);
            if (entity != null) {
                dao.delete(entity);
            }
        }
    }

    @Override
    @Transactional
    public void deleteAllByUuids(List<String> uuids) {
        dao.deleteByUuids(uuids);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.service.SerialNumberMaintainService#query(com.wellsoft.pt.common.component.jqgrid.JqGridQueryInfo)
     */
    @Override
    public JqGridQueryData query(JqGridQueryInfo queryInfo) {
        PagingInfo page = new PagingInfo(queryInfo.getPage(), queryInfo.getRows(), true);
        List<SerialNumberMaintain> serialNumberMaintains = dao.listAllByOrderPage(page, null);
        List<SerialNumberMaintain> jqUsers = new ArrayList<SerialNumberMaintain>();
        for (SerialNumberMaintain serialNumberMaintain : serialNumberMaintains) {
            SerialNumberMaintain jqSerialNumberMaintain = new SerialNumberMaintain();
            BeanUtils.copyProperties(serialNumberMaintain, jqSerialNumberMaintain);
            jqUsers.add(jqSerialNumberMaintain);
        }
        JqGridQueryData queryData = new JqGridQueryData();
        queryData.setCurrentPage(queryInfo.getPage());
        queryData.setDataList(jqUsers);
        queryData.setRepeatitems(false);
        queryData.setTotalPages(page.getTotalPages());
        queryData.setTotalRows(page.getTotalCount());
        return queryData;
    }

    @Override
    @Transactional
    public SerialNumberMaintain saveAcl(SerialNumberMaintain serialNumberMaintain) {
        List<AclSid> aclSids = aclService.getSid(serialNumberMaintain);
        List<String> existSids = new ArrayList<String>();
        for (AclSid aclSid : aclSids) {
            existSids.add(aclSid.getSid());
        }
        List<String> sids = getAclSid(serialNumberMaintain);
        // 新的SID
        List<String> newSids = new ArrayList<String>();
        for (String newSid : sids) {
            if (!existSids.contains(newSid)) {
                newSids.add(newSid);
            }
        }
        // 要删除的SID
        List<String> delSids = new ArrayList<String>();
        for (String newSid : existSids) {
            if (!sids.contains(newSid)) {
                delSids.add(newSid);
            }
        }

        // 删除
        for (String sid : delSids) {
            aclService.removePermission(serialNumberMaintain, BasePermission.ADMINISTRATION, sid);
        }
        // 新增
        /*
         * if (serialNumberMaintain.getParent() != null) {
         * aclService.save(serialNumberMaintain,
         * serialNumberMaintain.getParent(), sids.get(0),
         * BasePermission.ADMINISTRATION); }
         */
        for (String sid : sids) {
            aclService.addPermission(serialNumberMaintain, BasePermission.ADMINISTRATION, sid);
        }
        return aclService
                .get(SerialNumberMaintain.class, serialNumberMaintain.getUuid(), BasePermission.ADMINISTRATION);
    }

    /**
     * 返回流水号维护使用者在ACL中的SID
     *
     * @param serialNumberMaintain
     * @return
     */
    private List<String> getAclSid(SerialNumberMaintain serialNumberMaintain) {
        if (serialNumberMaintain.getOwners().isEmpty()) {
            // "ROLE_SERIAL_NUMBER"
            serialNumberMaintain.getOwners().add(ACL_SID);
            return serialNumberMaintain.getOwners();
        }
        // 返回组织部门中选择的角色作为SID
        return serialNumberMaintain.getOwners();
    }

    /**
     * 判断当前登录用户是否在指定的组织部门中
     *
     * @param serialNumberMaintain
     * @param sid
     */
    private Boolean hasPermission(SerialNumberMaintain serialNumberMaintain) {
        Boolean hasPermission = false;
        // 获取该流水号维护的所有SID，判断是否有访问权限
        List<AclSid> aclSids = aclService.getSid(serialNumberMaintain);
        for (AclSid aclSid : aclSids) {
            String sid = aclSid.getSid();
            // 如果所有者是默认的则有权限
            if (sid.equals(ACL_SID)) {
                hasPermission = true;
                break;
            } else {// 由组织部门提供接口，判断当前登录用户是否在指定的SID(组织部门)中
                if (sid.startsWith(IdPrefix.USER.getValue())) {
                    if (StringUtils.equals(((UserDetails) SpringSecurityUtils.getCurrentUser()).getUserId(), sid)) {
                        hasPermission = true;
                        break;
                    }
                } else {
                    hasPermission = false;
                }
            }
        }
        return hasPermission;
    }

    /**
     * 获得所有可编辑流水号
     *
     * @param uuid
     * @return
     */
    @Override
    public List<SerialNumberMaintain> getByIsEditor(Boolean isEditor) {
        List<SerialNumberMaintain> serialList = null;
        if (isEditor) {
            serialList = dao.listByFieldEqValue("isEditor", isEditor);
        }
        return serialList;

    }

    /**
     * 保存可编辑流水号的指针
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.serialnumber.service.SerialNumberMaintainService#savePointer(java.lang.String)
     */
    @Override
    @Transactional
    public boolean savePointer(String serial, String headPart, String lastPart, String serialNoDefId, String serialVal, String isOverride, String newPointer,
                               String uuid) {
        SerialNumberMaintain s = null;
        if (StringUtils.isNotBlank(uuid)) {
            s = dao.getOne(uuid);
        } else {
            SerialNumberMaintain queryMaintain = new SerialNumberMaintain();
            if (StringUtils.isNotBlank(headPart)) {
                queryMaintain.setHeadPart(headPart);
            }
            if (StringUtils.isNotBlank(lastPart)) {
                queryMaintain.setLastPart(lastPart);
            }
            queryMaintain.setId(serialNoDefId);
            List<SerialNumberMaintain> maintainList = this.listByEntity(queryMaintain);
            s = maintainList.get(0);
        }
        if (s != null) {
            String id = s.getId();
            SerialNumber num = serialNumberService.getById(id);
            Boolean isFillPosition = num.getIsFillPosition();
            String initialValue = num.getInitialValue();
            Integer increment = num.getIncremental();

            String hPart = s.getHeadPart();
            String lPart = s.getLastPart();
            String result = serial;
            if (StringUtils.isNotBlank(hPart)) {
                result = result.replace(hPart, "");
            }
            if (StringUtils.isNotBlank(headPart)) {
                result = result.replace(headPart, "");
            }
            if (StringUtils.isNotBlank(lPart)) {
                result = result.replaceAll(lPart, "");
            }

            // 补位处理
            if (isFillPosition) {
                int length = initialValue.length();
                // 0 代表前面补充0
                // 4 代表长度为4
                // d 代表参数为正数型
                String formatContent = "%0" + String.valueOf(length) + "d";
                result = String.format(formatContent, Integer.valueOf(result));
                newPointer = String.format(formatContent, Integer.valueOf(newPointer));
            }
            // 判断是否强制覆盖指针
            if ("1".equals(isOverride)) {
                if (Integer.valueOf(newPointer) > Integer.valueOf(s.getPointer())) {
                    s.setPointer(newPointer);
                    dao.save(s);
                    return true;
                }
            } else if ("2".equals(isOverride)) {
                s.setPointer(newPointer);
                dao.save(s);
                return true;
            }
        } else {
            SerialNumber num = serialNumberService.getOneByName(serialVal);
            // String nameId = num.getName();
            String numberId = num.getId();
            // 进行解析，不存入数据库
            SerialNumber num1 = serialNumberService.getFormatedSerialNumberByName(serialVal);
            String headPart1 = num1.getHeadPart();
            String lastPart1 = num1.getLastPart();
            String keyPart = num1.getKeyPart();
            Boolean isFillPosition = num.getIsFillPosition();
            String initialValue = num.getInitialValue();
            SerialNumberMaintain newS = new SerialNumberMaintain();
            newS.setName(serialVal);
            newS.setId(numberId);
            newS.setKeyPart(keyPart);
            newS.setHeadPart(headPart1);
            newS.setLastPart(lastPart1);
            String point = serial;
            if (StringUtils.isNotBlank(headPart1)) {
                point = point.replace(headPart1, "");
            }
            if (StringUtils.isNotBlank(headPart)) {
                point = point.replace(headPart, "");
            }
            if (StringUtils.isNotBlank(lastPart1)) {
                point = point.replaceAll(lastPart1, "");
            }
            // 补位处理
            if (isFillPosition) {
                int length = initialValue.length();
                // 0 代表前面补充0
                // 4 代表长度为4
                // d 代表参数为正数型
                String formatContent = "%0" + String.valueOf(length) + "d";
                point = String.format(formatContent, Integer.valueOf(point));
            }

            newS.setPointer(point);
            newS.setIsEditor(true);
            dao.save(newS);
            return true;
        }
        return false;
    }

    /**
     * 检测当前流水号是否已被占用
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.serialnumber.service.SerialNumberMaintainService#checkIsOccupied()
     */
    @Override
    public Boolean checkIsOccupied(String formUuid, String projection, String lastSerialNumber) {
        try {
            String tableName = dyFormApiFacade.getFormDefinition(formUuid).getTableName();
            if (dyFormApiFacade.queryFormDataExists(tableName, projection, lastSerialNumber)) {//未使用该流水号，则返回该未使用的流水号
                return true;
            }
        } catch (Exception e) {
            logger.error("检测当前流水号{}是否被占用异常：", lastSerialNumber, e);
        }
        return false;

    }

    @Override
    public SerialNumberMaintain getByIdAndKeyPart(String id, String keyPart) {
        return dao.getByIdAndKeyPart(id, keyPart);
    }

    @Override
    public List<SerialNumberMaintain> getByIdAndKeyParts(String id, String keyPart) {
        return dao.getByIdAndKeyParts(id, keyPart);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveByNewTransaction(SerialNumberMaintain entity) {
        super.save(entity);
    }

    @Override
    @Transactional
    public void saveByDefaultTransaction(SerialNumberMaintain entity) {
        super.save(entity);
    }
}
