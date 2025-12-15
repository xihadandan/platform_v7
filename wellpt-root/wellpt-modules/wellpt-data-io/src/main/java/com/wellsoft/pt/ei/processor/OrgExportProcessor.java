package com.wellsoft.pt.ei.processor;

import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.entity.DataExportRecord;
import com.wellsoft.pt.ei.processor.utils.ExpImpServiceBeanUtils;
import com.wellsoft.pt.ei.service.ExpImpService;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021/9/29.1	liuyz		2021/9/29		Create
 * </pre>
 * @date 2021/9/29
 */
@Component
public class OrgExportProcessor extends AbstractExportProcessor {

    /**
     * 获取ExportService集合
     *
     * @return
     */
    @Override
    public List<ExpImpService> getExportServiceList(DataExportRecord record) {
        JSONObject type = JSONObject.fromObject(record.getDataTypeJson());
        List<ExpImpService> expImpServices = new ArrayList<>();
        JSONObject orgData = type.getJSONObject("org_data");
        String head = "org";
        String tail = "ExpImpService";
        for (Object key : orgData.keySet()) {
            if (DataExportConstants.DATA_SWITCH_ON.equals(orgData.optString(key.toString()))) {
                ExpImpServiceBeanUtils.addExpImpService(expImpServices, head, tail, key.toString());
            }
        }
        return expImpServices;
    }


}
