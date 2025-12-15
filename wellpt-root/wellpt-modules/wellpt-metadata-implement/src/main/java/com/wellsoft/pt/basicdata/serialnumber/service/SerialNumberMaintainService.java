package com.wellsoft.pt.basicdata.serialnumber.service;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.pt.basicdata.serialnumber.bean.SerialNumberMaintainBean;
import com.wellsoft.pt.basicdata.serialnumber.dao.SerialNumberMaintainDao;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumberMaintain;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 流水号维护服务层接口
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-13.1	zhouyq		2013-3-13		Create
 * </pre>
 * @date 2013-3-13
 */

public interface SerialNumberMaintainService extends
        JpaService<SerialNumberMaintain, SerialNumberMaintainDao, String> {
    public static final String ACL_SID = "ROLE_SERIAL_NUMBER_MAINTAIN";

    /**
     * 根据指定id来获取流水号维护集合
     *
     * @param id
     * @return
     */
    public List<SerialNumberMaintain> getByDesignatedId(String designatedId, String isOverride);

    SerialNumberMaintain getBySerialNumberIdAndKeyPart(String serialNumberId);

    public List<SerialNumberMaintain> getById(String id);

    /**
     * 根据名称来获取所有的流水号维护集合
     */
    public List<SerialNumberMaintain> getByName(String name);

    /**
     * 使用acl接口存放流水号维护使用人
     *
     * @param serialNumberMaintain
     * @return
     */
    SerialNumberMaintain saveAcl(SerialNumberMaintain serialNumberMaintain);

    /**
     * 通过uuid获取流水号维护
     *
     * @param uuid
     * @return
     */
    public SerialNumberMaintain get(String uuid);

    /**
     * 通过uuid获取流水号维护VO对象
     *
     * @param uuid
     * @return
     */
    public SerialNumberMaintainBean getBeanByUuid(String uuid);

    /**
     * 获得所有可编辑流水号
     *
     * @param uuid
     * @return
     */
    public List<SerialNumberMaintain> getByIsEditor(Boolean isEditor);

    /**
     * 保存指针
     */
    public boolean savePointer(String serial, String headPart, String lastPart, String serialNoDefId, String serialVal, String isOverride,
                               String newPointer, String uuid);


    /**
     * 保存流水号维护
     *
     * @param uuid
     * @return
     */
    public void saveBean(SerialNumberMaintainBean bean);

    /**
     * 通过UUID删除流水号维护
     *
     * @param uuid
     * @return
     */
    public void remove(String uuid);

    /**
     * 批量删除
     *
     * @param uuids
     */
    public void deleteAllById(String[] ids);

    void deleteAllByUuids(List<String> uuids);

    /**
     * 流水号维护列表查询
     *
     * @param queryInfo
     * @return
     */
    public JqGridQueryData query(JqGridQueryInfo queryInfo);

    /**
     * 检测当前流水号是否已被占用
     *
     * @return
     */
    public Boolean checkIsOccupied(String formUuid, String projection, String lastSerialNumber);

    /**
     * @param id
     * @param keyPart
     * @return
     */
    public SerialNumberMaintain getByIdAndKeyPart(String id, String keyPart);

    public List<SerialNumberMaintain> getByIdAndKeyParts(String id, String keyPart);

    void saveByNewTransaction(SerialNumberMaintain entity);

    /**
     * 默认事务
     *
     * @param entity
     */
    void saveByDefaultTransaction(SerialNumberMaintain entity);

}
