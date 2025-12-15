package com.wellsoft.pt.dms.support.renderer.docexchanger;

import com.wellsoft.pt.dms.entity.DmsDocExchangeRecordEntity;
import com.wellsoft.pt.dms.enums.DocExchangeOvertimeLevelEnum;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Description: 文档交换-文档延期程度渲染器
 *
 * @author chenq
 * @date 2018/5/26
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/26    chenq		2018/5/26		Create
 * </pre>
 */
@Component
public class DocExchangeDataStoreDelayRender extends DocExchangeDataStoreRender {


    private static final String NO_DELAY = "一般";
    private static final String IMPORTANT_DELAY_LEVEL = "重要";
    private static final String SERIOUS_DELAY_LEVEL = "紧急";

    @Override
    public String doRenderData(String columnIndex, Object recordUuid, Map<String, Object> rowData,
                               String param) {
        if (recordUuid != null) {
            DmsDocExchangeRecordEntity recordEntity = dmsDocExchangeRecordService.getOne(
                    recordUuid.toString());
            if (recordEntity.getOvertimeLevel().equals(
                    DocExchangeOvertimeLevelEnum.NONE.ordinal())) {
                return "";
            }
            if (recordEntity.getOvertimeLevel().equals(
                    DocExchangeOvertimeLevelEnum.NORMAL.ordinal())) {
                return "<span class=\"def-view-normal\">" + NO_DELAY + "</span>";
            }

            if (recordEntity.getOvertimeLevel().equals(
                    DocExchangeOvertimeLevelEnum.IMPORTANT.ordinal())) {
                return "<span class=\"def-view-remind\">" + IMPORTANT_DELAY_LEVEL + "</span>";
            }

            if (recordEntity.getOvertimeLevel().equals(
                    DocExchangeOvertimeLevelEnum.URGE.ordinal())) {
                return "<span class=\"def-view-warning\">" + SERIOUS_DELAY_LEVEL + "</span>";
            }

        }

        return "";


    }

    @Override
    public String getType() {
        return "docExchangeDataStoreDelayRender";
    }

    @Override
    public String getName() {
        return "数据管理_文档交换_文档延期程度渲染器";
    }

}
