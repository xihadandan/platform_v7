/*
 * @(#)2017年10月13日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.mapper.bean;

import com.wellsoft.pt.basicdata.mapper.MapperException;
import com.wellsoft.pt.basicdata.mapper.MapperSchema;
import com.wellsoft.pt.basicdata.mapper.entity.MapperEntity;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年10月13日.1	zhongzh		2017年10月13日		Create
 * </pre>
 * @date 2017年10月13日
 */
public class MapperBean extends MapperEntity implements MapperSchema {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private Boolean updateMapping;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.mapper.MapperSchema#getSchemaA()
     */
    @Override
    @JsonIgnore
    public Object getSchemaA() {
        String mapId = getMapId();
        String[] mapIds = mapId.split(">>");
        URI uriA = null;
        try {
            uriA = new URI(mapIds[0]);
        } catch (URISyntaxException ex) {
            throw new MapperException("mapId格式有误", ex);
        }
        uriA.getScheme();
        return null;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.mapper.MapperSchema#getSchemaB()
     */
    @Override
    @JsonIgnore
    public Object getSchemaB() {
        return null;
    }

    /**
     * @return the updateMapping
     */
    public Boolean getUpdateMapping() {
        return updateMapping;
    }

    /**
     * @param updateMapping 要设置的updateMapping
     */
    public void setUpdateMapping(Boolean updateMapping) {
        this.updateMapping = updateMapping;
    }
}
