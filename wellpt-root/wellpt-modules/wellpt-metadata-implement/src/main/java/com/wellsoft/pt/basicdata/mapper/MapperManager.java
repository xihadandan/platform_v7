/*
 * @(#)2017年10月13日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.mapper;

/**
 * Description: 可动态添加和修改的Mapper
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
public interface MapperManager {

    /**
     * 添加MappingFileData(不要经常调用这个方法,映射内容有变更时调用该方法)
     *
     * @param mappingDataBuilder
     * @return
     */
    public MapperDataBuilder addDataMapper(String mapId, MapperDataBuilder mappingDataBuilder);

    /**
     * 添加MappingFileData(如果已经存在则不添加)
     *
     * @param mappingDataBuilder
     * @return
     */
    public MapperDataBuilder addDataMapperIfNeed(String mapId, MapperDataBuilder mappingDataBuilder);

}
