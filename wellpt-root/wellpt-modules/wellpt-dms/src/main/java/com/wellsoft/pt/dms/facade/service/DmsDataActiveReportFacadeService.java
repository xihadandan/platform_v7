package com.wellsoft.pt.dms.facade.service;

import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.dms.bean.DataActiveDyformParam;
import com.wellsoft.pt.dms.entity.DataActiveReportEntity;

import java.util.List;

/**
 * Description: 数据活动报告接口,记录业务数据活动参数
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
public interface DmsDataActiveReportFacadeService extends BaseService {


    /**
     * 新增数据活动
     *
     * @param dataValues  数据真实值
     * @param dataTexts   数据文本展示值
     * @param reportClass 数据活动报告实体类
     */
    void batchAddDataActiveByClass(List<String> dataValues, List<String> dataTexts,
                                   Class<? extends DataActiveReportEntity> reportClass);

    void batchAddDataActiveByClassName(List<String> dataValues, List<String> dataTexts,
                                       String reportClassName);


    /**
     * 新增数据活动
     *
     * @param dataValue   数据真实值
     * @param dataText    数据文本展示值
     * @param reportClass 数据活动报告实体类
     */
    void addDataActive(String dataValue, String dataText,
                       Class<? extends DataActiveReportEntity> reportClass);

    /**
     * 清空数据活动
     *
     * @param userId
     * @param reportClass
     */
    void removeUserDataActive(String userId, Class<? extends DataActiveReportEntity> reportClass);

    List<Select2DataBean> loadSelectionsByActiveTimeDesc(String userId,
                                                         Class<? extends DataActiveReportEntity> reportClass);

    void addDataActiveByDyform(DataActiveDyformParam param);

    void removeUserDataActiveByDyForm(String userId, String formUuid, String userIdColumn);

}
