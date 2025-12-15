package com.wellsoft.pt.dms.service.impl;

import com.wellsoft.pt.dms.dao.impl.DmsDataLabelRelaDaoImpl;
import com.wellsoft.pt.dms.entity.DataLabelRelaEntity;
import com.wellsoft.pt.dms.entity.DmsDataLabelRelaEntity;
import com.wellsoft.pt.dms.service.DmsDataLabelRelaService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.ClassUtils;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Description: 标签与数据的关系服务实现类
 *
 * @author chenq
 * @date 2018/6/12
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/12    chenq		2018/6/12		Create
 * </pre>
 */
@Service
public class DmsDataLabelRelaServiceImpl extends AbstractJpaServiceImpl<DmsDataLabelRelaEntity, DmsDataLabelRelaDaoImpl, String> implements
        DmsDataLabelRelaService {

    @Transactional
    @Override
    public void saveRelaEntity(DataLabelRelaEntity labelRelaEntity) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        Calendar now = Calendar.getInstance();
        labelRelaEntity.setCreateTime(now.getTime());
        labelRelaEntity.setModifyTime(now.getTime());
        labelRelaEntity.setCreator(user.getUserId());
        labelRelaEntity.setModifier(user.getUserId());
        labelRelaEntity.setSystemUnitId(user.getSystemUnitId());
        this.dao.getSession().saveOrUpdate(labelRelaEntity);
    }

    @Transactional
    @Override
    public void deleteByDataUuids(List<String> dataUuid) {
        this.dao.deleteByDataUuids(dataUuid);
    }

    @Transactional
    @Override
    public void deleteByDataUuidAndLabelUuid(String dataUuid, String labelUuid) {
        this.dao.deleteByDataUuidAndLabelUuid(dataUuid, labelUuid);
    }

    @Override
    public List<DmsDataLabelRelaEntity> listByDataUuid(String dataUuid) {
        return this.dao.listByDataUuid(dataUuid);
    }

    @Override
    public void deleteByLabelUuids(ArrayList<String> labelUuids) {
        this.dao.deleteByLabelUuids(labelUuids);
    }

    @Override
    public List<DmsDataLabelRelaEntity> listDataRelaEntities(String dataUuid, String entityClassName) {
        return this.dao.listDataRelaEntities(dataUuid, entityClassName);
    }

    @Override
    @Transactional
    public void deleteByUuidsAndEntityClass(List<String> relaUuid, String entityClassName) {
        for (String uuid : relaUuid) {

            Object obj = this.dao.getSession().get(ClassUtils.getEntityClasses().get(StringUtils.uncapitalize(entityClassName)), uuid);
            if (obj != null)
                this.dao.getSession().delete(obj);
        }
    }
}
