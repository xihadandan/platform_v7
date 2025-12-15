package com.wellsoft.pt.dms.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.dms.bean.DataActiveDyformParam;
import com.wellsoft.pt.dms.entity.DataActiveReportEntity;
import com.wellsoft.pt.dms.facade.service.DmsDataActiveReportFacadeService;
import com.wellsoft.pt.dms.service.DmsDataActiveReportService;
import com.wellsoft.pt.jpa.util.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
public class DmsDataActiveReportFacadeServiceImpl extends AbstractApiFacade implements
        DmsDataActiveReportFacadeService {

    @Resource
    DmsDataActiveReportService dmsDataActiveReportService;


    @Override
    public void batchAddDataActiveByClass(List<String> dataValues, List<String> dataTexts,
                                          Class<? extends DataActiveReportEntity> reportClass) {
        dmsDataActiveReportService.batchAddDataActive(dataValues, dataTexts, reportClass);
    }

    @Override
    public void batchAddDataActiveByClassName(List<String> dataValues, List<String> dataTexts,
                                              String reportClassName) {
        dmsDataActiveReportService.batchAddDataActive(dataValues, dataTexts,
                (Class<? extends DataActiveReportEntity>) ClassUtils.getEntityClasses().get(
                        StringUtils.uncapitalize(reportClassName)));

    }

    @Override
    public void addDataActive(String dataValue, String dataText,
                              Class<? extends DataActiveReportEntity> reportClass) {
        dmsDataActiveReportService.addDataActive(dataValue, dataText, reportClass);
    }

    @Override
    public void removeUserDataActive(String userId,
                                     Class<? extends DataActiveReportEntity> reportClass) {
        dmsDataActiveReportService.removeUserDataActive(userId, reportClass);
    }

    @Override
    public List<Select2DataBean> loadSelectionsByActiveTimeDesc(String userId,
                                                                Class<? extends DataActiveReportEntity> reportClass) {
        List<? extends DataActiveReportEntity> reportEntities = dmsDataActiveReportService.listByActiveTimeDesc(
                userId, reportClass);
        List<Select2DataBean> select2DataBeanList = Lists.newArrayList();
        for (DataActiveReportEntity entity : reportEntities) {
            select2DataBeanList.add(
                    new Select2DataBean(entity.getDataValue(), entity.getDataText()));

        }
        return select2DataBeanList;
    }

    @Override
    public void addDataActiveByDyform(DataActiveDyformParam dyformParam) {
        dmsDataActiveReportService.addDataActiveByDyform(dyformParam);
    }

    @Override
    public void removeUserDataActiveByDyForm(String userId, String formUuid, String userIdColumn) {
        dmsDataActiveReportService.removeUserDataActiveByDyForm(userId, formUuid);

    }
}
