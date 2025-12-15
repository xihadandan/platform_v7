package com.wellsoft.pt.dms.service;

import com.wellsoft.pt.dms.bean.DataActiveDyformParam;
import com.wellsoft.pt.dms.entity.DataActiveReportEntity;

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
public interface DmsDataActiveReportService {

    void batchAddDataActive(List<String> dataValues, List<String> dataTexts,
                            Class<? extends DataActiveReportEntity> reportClass);

    void addDataActive(String dataValue, String dataText,
                       Class<? extends DataActiveReportEntity> reportClass);

    void addDataActiveByDyform(DataActiveDyformParam dyformParam);

    void removeUserDataActiveByDyForm(String userId, String formUuid);

    void removeUserDataActive(String userId, Class<? extends DataActiveReportEntity> reportClass);

    List<? extends DataActiveReportEntity> listByActiveTimeDesc(String userId,
                                                                Class<? extends DataActiveReportEntity> reportClass);

    List<? extends DataActiveReportEntity> listByCntDescAndActiveTimeDesc(String userId,
                                                                          Class<? extends DataActiveReportEntity> reportClass);

    List<? extends DataActiveReportEntity> listDataActives(String userId,
                                                           Class<? extends DataActiveReportEntity> reportClass,
                                                           String orderBy);
}
