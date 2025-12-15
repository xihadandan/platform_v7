package com.wellsoft.pt.ei.processor;

import com.wellsoft.pt.ei.entity.DataExportRecord;
import com.wellsoft.pt.ei.processor.utils.ExpImpServiceBeanUtils;
import com.wellsoft.pt.ei.service.ExpImpService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Auther: yt
 * @Date: 2021/9/24 10:18
 * @Description: 导出处理器
 */
@Component
public class FlowExportProcessor extends AbstractExportProcessor {

//    private static final ThreadLocal<DataExportTask> dataExportTaskThreadLocal = new ThreadLocal<>();


    /*public void handle(DataExportRecord record, DataExportTask dataExportTask) {

        dataExportTaskThreadLocal.set(dataExportTask);

        FlowExpImpService.dataExportRecordThreadLocal.set(record);

        super.handle(record, dataExportTask);

        List<ExpImpService> expImpServices = this.getExportServiceList(record);

        List<String> systemUnitIdList = Arrays.asList(record.getSystemUnitIds().split(Separator.COMMA.getValue()));

        List<String> systemUnitNameList = Arrays.asList(record.getSystemUnitNames().split(Separator.COMMA.getValue()));
        try {
            for (int i = 0; i < systemUnitIdList.size(); i++) {
                // 系统单位目录，命名规则：${系统单位名称}_${导出时间(年月日时分秒)}
                String systemUnitName = systemUnitNameList.get(i);
                String systemUnitId = systemUnitIdList.get(i);
                String systemUnitFilePath = this.getSystemUnitPath(record.getExportPath(), systemUnitName, record.getExportTime());
                for (ExpImpService expImpService : expImpServices) {
                    int total = Integer.valueOf(expImpService.total(systemUnitId) + "");
                    if (total == 0) {
                        continue;
                    }
                    //生成说明文件
                    String fileName = ExpImpServiceBeanUtils.configFileName(systemUnitFilePath,expImpService);
                    String fileToString = FileUtils.readFileToString(new File(fileName));
                    JSONArray jsonArray = JSONArray.fromObject(fileToString);
                    FieldDesc dyFormFieldDesc = null;
                    for (Object o : jsonArray) {
                        JSONObject jsonObject = (JSONObject) o;
                        String fieldName = jsonObject.getString("fieldName");
                        if (fieldName.equals("dyformData")) {
                            dyFormFieldDesc = JsonBinder.buildNormalBinder().fromJson(jsonObject.toString(), FieldDesc.class);
                        }
                    }
                    if (dyFormFieldDesc == null) {
                        dyFormFieldDesc = new FieldDesc("dyformData", "Object", "流程表单数据", "", "");
                    }

                    List<FieldDesc> fields = new ArrayList<>();
                    Map<String, List<FieldDesc>> fieldDescListMap = FlowExpImpService.dyFormFieldDescMapThreadLocal.get();
                    for (String formUuid : fieldDescListMap.keySet()) {
                        FieldDesc formFieldDesc = new FieldDesc();
                        formFieldDesc.setFieldName(formUuid);
                        formFieldDesc.setType("List<Object>");
                        formFieldDesc.setDesc("表单" + formUuid + "字段说明");
                        formFieldDesc.setFields(fieldDescListMap.get(formUuid));
                        fields.add(formFieldDesc);
                    }
                    dyFormFieldDesc.setFields(fields);
                    for (Iterator iterator = jsonArray.iterator(); iterator.hasNext(); ) {
                        Object next = iterator.next();
                        JSONObject jsonObject = (JSONObject) next;
                        String fieldName = jsonObject.getString("fieldName");
                        if (fieldName.equals("dyformData")) {
                            iterator.remove();
                        }
                    }
                    jsonArray.add(JSONObject.fromObject(FieldTypeUtils.descObject2Json(dyFormFieldDesc)));
                    FileUtils.writeStringToFile(new File(fileName), jsonArray.toString(), false);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    /**
     * 获取ExportService集合
     *
     * @return
     */
    @Override
    public List<ExpImpService> getExportServiceList(DataExportRecord record) {
        List<ExpImpService> expImpServices = ExpImpServiceBeanUtils.flowExpImpServices(record.getDataTypeJson());
        return expImpServices;
    }


}
