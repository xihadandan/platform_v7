package com.wellsoft.pt.dms.facade.service.impl;

import com.wellsoft.context.util.excel.ExcelImportDataReport;
import com.wellsoft.context.util.excel.ExcelRowDataAnalysedResult;
import com.wellsoft.pt.dms.bean.DmsDataImportBean;
import com.wellsoft.pt.dms.enums.DataImportStrategyEnum;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.data.enums.EnumFormFilterCondition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/12/20
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/12/20    chenq		2018/12/20		Create
 * </pre>
 */
@Service
public class DmsDataImportSaveServiceImpl {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DyFormFacade dyFormFacade;

    public void save(Map<Integer, DyFormData> dyFormDatas, DmsDataImportBean dmsDataImportBean,
                     Map<String/* 数据 uuid */, Map<String/* 表单字段 */, Object/* 表单字段值 */>> uuidFieldKeyValues,
                     ExcelImportDataReport dataReport) {
        String uuid = null;
        try {
            Set<Map.Entry<Integer, DyFormData>> entrySet = dyFormDatas.entrySet();
            for (Map.Entry<Integer, DyFormData> entry : entrySet) {
                try {
                    DyFormData dyFormData = entry.getValue();
                    uuid = dyFormData.getDataUuid();
                    Map<String/* 表单字段 */, Object/* 表单字段值 */> fieldKeyValues = uuidFieldKeyValues.get(
                            uuid);
                    if (MapUtils.isNotEmpty(fieldKeyValues)) {
                        List<String> list = dyFormFacade.queryUniqueForFields(
                                dyFormData.getFormUuid(),
                                fieldKeyValues,
                                EnumFormFilterCondition.ORG.getValue());
                        if (CollectionUtils.isNotEmpty(list)) {
                            if (StringUtils.isNotBlank(dmsDataImportBean.getStrategy())) {
                                if (StringUtils.equals(DataImportStrategyEnum.ERROR.getType(),
                                        dmsDataImportBean.getStrategy())) {
                                    throw new RuntimeException("唯一性校验错误");
                                } else if (StringUtils.equals(
                                        DataImportStrategyEnum.OVERLAP.getType(),
                                        dmsDataImportBean.getStrategy())) {
                                    DyFormData srcDyFormData = dyFormFacade
                                            .getDyFormData(dyFormData.getFormUuid(), list.get(0));
                                    dyFormFacade.copyFormData(dyFormData, srcDyFormData, false);
                                    dyFormFacade.saveFormData(srcDyFormData);
                                    continue;
                                }
                            } else {
                                dyFormFacade.saveFormData(dyFormData);
                                continue;
                            }
                        }
                    }
                    dyFormFacade.saveFormData(dyFormData);
                } catch (Exception e) {
                    dataReport.getFail().add(
                            new ExcelRowDataAnalysedResult(false, "导入保存数据异常", entry.getKey()));
                }


            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
