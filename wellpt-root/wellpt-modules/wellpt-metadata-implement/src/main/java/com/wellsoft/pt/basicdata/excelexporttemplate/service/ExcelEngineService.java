package com.wellsoft.pt.basicdata.excelexporttemplate.service;

import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface ExcelEngineService {
    public static final String FORM_UUID = "formUuid";
    public static final String FORM_DATAS = "formDatas";
    public static final String FORM_ID = "formId";
    public static final String DELETED_FORM_DATAS = "deletedFormDatas";

    public File parseExcel(MultipartFile inputStream, String jsonObject);

    public File parseExcel(String jsonObject);

    public String parseJSON(MultipartFile inputStream);

    public String parseJSONS(MultipartFile inputStream);

    public Map<String, List<Map<String, Object>>> parseFormDatas(MultipartFile part);

    public MongoFileEntity getExcelTemplateStream(String id);

    public String getExcelTemplateName(String id);

}
