package com.wellsoft.pt.ei.processor.utils;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * @Auther: yt
 * @Date: 2021/10/19 19:02
 * @Description:
 */
public class AttachFieldDesc {

    /**
     * key: uuid_fieldName  value: List<MongoFileEntityUuid>
     */
    private Map<String, List<String>> attachFileMap = Maps.newHashMap();

    /**
     * key: uuid_fieldName value:String
     */
    private Map<String, String> clobMap = Maps.newHashMap();

    /**
     * key: 导出类的fieldName value:List<FieldDesc>
     */
    private Map<String, List<FieldDesc>> fieldDesc = Maps.newHashMap();

    public Map<String, List<String>> getAttachFileMap() {
        return attachFileMap;
    }

    public void setAttachFileMap(Map<String, List<String>> attachFileMap) {
        this.attachFileMap = attachFileMap;
    }

    public Map<String, String> getClobMap() {
        return clobMap;
    }

    public void setClobMap(Map<String, String> clobMap) {
        this.clobMap = clobMap;
    }

    public Map<String, List<FieldDesc>> getFieldDesc() {
        return fieldDesc;
    }

    public void setFieldDesc(Map<String, List<FieldDesc>> fieldDesc) {
        this.fieldDesc = fieldDesc;
    }
}
