package com.wellsoft.pt.ei.processor.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.ei.annotate.FieldType;

import java.util.List;
import java.util.Map;

/**
 * @Auther: yt
 * @Date: 2021/9/29 19:51
 * @Description:
 */
public class RequiredFile {

    /**
     * 必填项 未填
     */
    private List<FieldType> requiredNo = Lists.newArrayList();

    /**
     * 文件 key 数据uuid_字段名称
     */
    private Map<String, List<String>> attachFileMap = Maps.newHashMap();


    public List<FieldType> getRequiredNo() {
        return requiredNo;
    }

    public void setRequiredNo(List<FieldType> requiredNo) {
        this.requiredNo = requiredNo;
    }

    public Map<String, List<String>> getAttachFileMap() {
        return attachFileMap;
    }

    public void setAttachFileMap(Map<String, List<String>> attachFileMap) {
        this.attachFileMap = attachFileMap;
    }
}
