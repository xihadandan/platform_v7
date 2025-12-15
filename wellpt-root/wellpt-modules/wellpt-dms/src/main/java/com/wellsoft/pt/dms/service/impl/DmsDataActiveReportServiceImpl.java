package com.wellsoft.pt.dms.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.dms.bean.DataActiveDyformParam;
import com.wellsoft.pt.dms.entity.DataActiveReportEntity;
import com.wellsoft.pt.dms.service.DmsDataActiveReportService;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.data.service.FormDataService;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumSystemField;
import com.wellsoft.pt.jpa.dao.UniversalDao;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/7/3
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/7/3    chenq		2018/7/3		Create
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class DmsDataActiveReportServiceImpl implements DmsDataActiveReportService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Resource
    UniversalDao universalDao;

    @Resource
    DyFormFacade dyFormFacade;

    @Resource
    FormDataService formDataService;

    @Override
    @Transactional
    public void batchAddDataActive(List<String> dataValues, List<String> dataTexts,
                                   Class<? extends DataActiveReportEntity> reportClass) {
        if (CollectionUtils.isEmpty(dataValues)) {
            return;
        }
        try {
            for (int i = 0, len = dataValues.size(); i < len; i++) {
                addDataActive(dataValues.get(i),
                        CollectionUtils.isNotEmpty(dataTexts) ? dataTexts.get(i) : null,
                        reportClass);
            }
        } catch (Exception e) {
            LOGGER.error("批量保存数据活动异常：", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    @Transactional
    public void addDataActive(String dataValue, String dataText,
                              Class<? extends DataActiveReportEntity> reportClass) {
        try {
            List<? extends DataActiveReportEntity> exists = listByDataValue(dataValue,
                    reportClass);
            if (CollectionUtils.isNotEmpty(exists)) {
                exists.get(0).setActiveCount(exists.get(0).getActiveCount() + 1);
                exists.get(0).setLastActiveTime(new Date());
                universalDao.save(exists.get(0));
                return;
            }

            DataActiveReportEntity entity = (DataActiveReportEntity) reportClass.newInstance();
            entity.setDataText(dataText);
            entity.setLastActiveTime(new Date());
            entity.setDataValue(dataValue);
            universalDao.save(entity);
        } catch (Exception e) {
            LOGGER.error("保存数据活动异常：", e);
            throw new RuntimeException(e);
        }
    }


    @Override
    @Transactional
    public void addDataActiveByDyform(DataActiveDyformParam dyformParam) {
        try {
            if (dyformParam != null && CollectionUtils.isNotEmpty(dyformParam.getDataValues())) {
                for (int i = 0; i < dyformParam.getDataValues().size(); i++) {
                    Map<String, Object> updateDataMap = Maps.newHashMap();
                    DyFormFormDefinition formFormDefinition = dyFormFacade.getFormDefinition(
                            dyformParam.getFormUuid());
                    String tableName = formFormDefinition.getTableName();
                    if (formDataService.queryFormDataExists(tableName,
                            dyformParam.getDataValueColumn(),
                            dyformParam.getDataValues().get(i))) {
                        Map<String, Object> param = Maps.newHashMap();
                        param.put("userId", SpringSecurityUtils.getCurrentUserId());
                        param.put("dataValue", dyformParam.getDataValues().get(i));
                        String[] columns = new String[]{"UUID", dyformParam.getActiveCntColumn() + " as cnt "};
                        List<QueryItem> queryItemList = formDataService.query(tableName, false,
                                columns,
                                " " + EnumSystemField.creator.getName() + "=:userId  " +
                                        "and " + dyformParam.getDataValueColumn() + "=:dataValue ",
                                param,
                                null, null, null, 0, 1);
                        String uuid = queryItemList.get(0).getString("UUID");
                        int activeCnt = queryItemList.get(0).getInt("cnt");
                        updateDataMap.put("uuid", uuid);
                        updateDataMap.put(dyformParam.getActiveCntColumn(), activeCnt + 1);//更新活动次数
                        updateDataMap.put(dyformParam.getLastActiveTimeColumn(),
                                new Date());//更新最后一次活动时间
                        formDataService.updateFormData(dyformParam.getFormUuid(), updateDataMap);
                    } else {
                        updateDataMap.put(dyformParam.getDataValueColumn(),
                                dyformParam.getDataValues().get(0));
                        updateDataMap.put(dyformParam.getDataTextColumn(),
                                CollectionUtils.isNotEmpty(
                                        dyformParam.getDataTexts()) ? dyformParam.getDataTexts().get(
                                        0) : null);
                        updateDataMap.put(dyformParam.getActiveCntColumn(), 1);//更新活动次数
                        updateDataMap.put(dyformParam.getLastActiveTimeColumn(),
                                new Date());//更新最后一次活动时间
                    }
                    formDataService.updateFormData(dyformParam.getFormUuid(), updateDataMap);

                }
            }
        } catch (Exception e) {
            LOGGER.error("保存数据活动异常：", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    @Transactional
    public void removeUserDataActiveByDyForm(String userId, String formUuid) {
        try {
            DyFormFormDefinition formFormDefinition = dyFormFacade.getFormDefinition(formUuid);
            String tableName = formFormDefinition.getTableName();
            if (formDataService.queryFormDataExists(tableName, EnumSystemField.creator.getName(),
                    userId)) {
                Map<String, Object> param = Maps.newHashMap();
                param.put("userId", SpringSecurityUtils.getCurrentUserId());
                String[] columns = new String[]{"UUID"};
                List<QueryItem> queryItemList = formDataService.query(tableName, false, columns,
                        " " + EnumSystemField.creator.getName() + "=:userId ",
                        param,
                        null, null, null, 1, 1);
                String uuid = queryItemList.get(0).getString("UUID");

                formDataService.delFormData(formUuid, uuid);
            }
        } catch (Exception e) {
            LOGGER.error("根据表单删除数据活动异常：", e);
            throw new RuntimeException(e);
        }
    }


    @Override
    @Transactional
    public void removeUserDataActive(String userId,
                                     Class<? extends DataActiveReportEntity> reportClass) {
        universalDao.deleteAll(listByUserId(userId, reportClass));
    }

    @Override
    public List<? extends DataActiveReportEntity> listByActiveTimeDesc(String userId,
                                                                       Class<? extends DataActiveReportEntity> reportClass) {
        return this.listDataActives(userId, reportClass, "lastActiveTime desc");
    }

    @Override
    public List<? extends DataActiveReportEntity> listByCntDescAndActiveTimeDesc(String userId,
                                                                                 Class<? extends DataActiveReportEntity> reportClass) {
        return this.listDataActives(userId, reportClass, "activeCount desc,lastActiveTime desc");
    }


    @Override
    public List<? extends DataActiveReportEntity> listDataActives(String userId,
                                                                  Class<? extends DataActiveReportEntity> reportClass,
                                                                  String orderBy) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        return universalDao.query("from " + ClassUtils.getShortClassName(
                reportClass) + " where creator=:userId order by lastActiveTime desc", params);
    }


    public List<? extends DataActiveReportEntity> listByDataValue(String dataValue,
                                                                  Class<? extends DataActiveReportEntity> reportClass) {
        return universalDao.findBy(reportClass, "dataValue", dataValue);
    }


    public List<? extends DataActiveReportEntity> listByUserId(String userId,
                                                               Class<? extends DataActiveReportEntity> reportClass) {
        return universalDao.findBy(reportClass, "creator", userId);
    }

}
