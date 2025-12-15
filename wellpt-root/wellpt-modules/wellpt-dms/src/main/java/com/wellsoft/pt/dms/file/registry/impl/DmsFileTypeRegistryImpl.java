/*
 * @(#)2018年9月26日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.registry.impl;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.dms.enums.FileTypeEnum;
import com.wellsoft.pt.dms.file.registry.DmsFileTypeRegistry;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年9月26日.1	zhulh		2018年9月26日		Create
 * </pre>
 * @date 2018年9月26日
 */
@Component
public class DmsFileTypeRegistryImpl implements DmsFileTypeRegistry, InitializingBean {

    private Map<String, String> typeNameMap = new LinkedCaseInsensitiveMap<String>();

    private Map<String, Boolean> downloadableMap = new LinkedCaseInsensitiveMap<Boolean>();

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.registry.DmsFileTypeRegistry#register(java.lang.String, java.lang.String)
     */
    @Override
    public DmsFileTypeRegistry register(String type, String name, boolean downloadable) {
        if (typeNameMap.containsKey(type)) {
            throw new RuntimeException("文件类型[" + type + "]已经注册，不能重复注册！");
        }
        typeNameMap.put(type, name);
        downloadableMap.put(type, Boolean.valueOf(downloadable));
        return this;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.registry.DmsFileTypeRegistry#isRegistered(java.lang.String)
     */
    @Override
    public boolean isRegistered(String type) {
        return typeNameMap.containsKey(type);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.registry.DmsFileTypeRegistry#getName(java.lang.String)
     */
    @Override
    public String getName(String type) {
        return typeNameMap.get(type);
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        FileTypeEnum[] enums = FileTypeEnum.values();
        for (FileTypeEnum fileTypeEnum : enums) {
            String typeString = fileTypeEnum.getType();
            List<String> types = Arrays.asList(StringUtils.split(typeString, Separator.COMMA.getValue()));
            for (String type : types) {
                register(type, fileTypeEnum.getName(), !(isDyform(type) || isUnknow(type)));
            }
        }
    }

    /**
     * @param type
     * @return
     */
    private boolean isDyform(String type) {
        return FileTypeEnum.DYFORM.getType().equals(type);
    }

    /**
     * @param type
     * @return
     */
    private boolean isUnknow(String type) {
        return FileTypeEnum.UNKNOW.getType().equals(type);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.registry.DmsFileTypeRegistry#getTypeNameAsMap()
     */
    @Override
    public Map<String, String> getTypeNameAsMap() {
        Map<String, String> tmp = new HashMap<String, String>();
        tmp.putAll(this.typeNameMap);
        return tmp;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.registry.DmsFileTypeRegistry#getDownloadableAsMap()
     */
    @Override
    public Map<String, Boolean> getDownloadableAsMap() {
        Map<String, Boolean> tmp = new HashMap<String, Boolean>();
        tmp.putAll(this.downloadableMap);
        return tmp;
    }

}
