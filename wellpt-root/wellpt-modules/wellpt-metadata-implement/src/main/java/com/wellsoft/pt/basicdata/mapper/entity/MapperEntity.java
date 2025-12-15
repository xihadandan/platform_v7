/*
 * @(#)2017年10月13日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.mapper.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: Mapper配置存储
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
@Entity
@Table(name = "CD_DATA_MAPPER")
@DynamicUpdate
@DynamicInsert
public class MapperEntity extends TenantEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * mapId
     * classa://:?dataFactory=dataFactory&factoryDataId=factoryDataId>>classb://:?beanFactory=beanFactory&factoryBeanId=factoryBeanId[>>version]
     */
    @Length(max = 4000)
    private String mapId;

    @Length(max = 255)
    private String name;

    /**
     * version
     */
    private Double version;

    /**
     * mapping(json vs xml)
     */
    @Length(max = Integer.MAX_VALUE)
    private String mapping;

    /**
     * @return the mapId
     */
    public String getMapId() {
        return mapId;
    }

    /**
     * @param mapId 要设置的mapId
     */
    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the version
     */
    public Double getVersion() {
        return version;
    }

    /**
     * @param version 要设置的version
     */
    public void setVersion(Double version) {
        this.version = version;
    }

    /**
     * @return the mapping
     */
    public String getMapping() {
        return mapping;
    }

    /**
     * @param mapping 要设置的mapping
     */
    public void setMapping(String mapping) {
        this.mapping = mapping;
    }
}
