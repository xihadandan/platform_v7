package com.wellsoft.pt.webmail.support;

import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
import com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractDataStoreRenderer;
import com.wellsoft.pt.webmail.service.WmMailboxService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * Description: 使用容量数据存储渲染
 *
 * @author chenq
 * @date 2019/7/19
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/19    chenq		2019/7/19		Create
 * </pre>
 */
@Component
public class WebmailUseCapacityDataStoreRender extends AbstractDataStoreRenderer {


    WmMailboxService wmMailboxService;

    public static String mailCapacityReadableFormate(Long number) {
        if (number > 0 && number < 1024) {
            return number + "B";
        }
        if (number >= 1024 && number < 1024 * 1024L) {
            return new BigDecimal(number / 1024.0f).setScale(2,
                    RoundingMode.HALF_UP) + "K";
        }

        if (number >= 1024 * 1024 && number < 1024L * 1024 * 1024) {
            return new BigDecimal(number / (1024 * 1024.0f)).setScale(2,
                    RoundingMode.HALF_UP) + "M";
        }

        if (number >= 1024L * 1024 * 1024) {
            return new BigDecimal(number / (1024 * 1024 * 1024.0f)).setScale(2,
                    RoundingMode.HALF_UP) + "G";
        }
        return "0B";
    }

    @Override
    public String getType() {
        return "WebmailUseCapacityDataStoreRender";
    }

    @Override
    public String getName() {
        return "平台邮件_邮件占用空间可读性渲染";
    }

    @Override
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData,
                               RendererParam param) {
        if (value != null && StringUtils.isNotBlank(value.toString())) {
            return mailCapacityReadableFormate(Long.parseLong(value.toString()));
        }
        return "0B";
    }

}
