/**
 * Copyright 2005-2013 Dozer Project
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wellsoft.pt.basicdata.mapper;

import com.wellsoft.context.util.reflection.ReflectionUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.dozer.MappingException;
import org.dozer.classmap.ClassMap;
import org.dozer.classmap.ClassMappings;
import org.dozer.classmap.Configuration;
import org.dozer.classmap.MappingFileData;
import org.dozer.loader.CustomMappingsLoader;
import org.dozer.loader.LoadMappingsResult;
import org.dozer.util.MappingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Description:
 * 1、可动态添加和修改Mapper
 * 2、ClassMapper未配置全局参数时使用初始化时添加的全局参数
 * 3、动态加载数据库中定义的Mapper
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年10月12日.1	zhongzh		2017年10月12日		Create
 * </pre>
 * @date 2017年10月12日
 */
public class DataMapper implements Mapper, MapperManager {

    private final static String FIELD_CUSTOMM_APPINGS = "customMappings";
    private final static String FIELD_GLOBAL_CONFIGURATION = "globalConfiguration";
    private final static DefaultMapperBuilder defaultMapperBuilder = new DefaultMapperBuilder();
    private final static DataMapper instance = new DataMapper();
    private static Logger logger = LoggerFactory.getLogger(DataMapper.class);
    private final DozerBeanMapper mapper = new DozerBeanMapper(Arrays.asList(new String[]{}));
    private final ConcurrentMap<String, MapperDataBuilder> mapping = new ConcurrentHashMap<String, MapperDataBuilder>();

    private DataMapper() {
        init();
        // 初始化
        mapper.getMappingMetadata();
    }

    ;

    public static DataMapper getInstance() {
        return instance;
    }

    protected void init() {
        InputStream xmlStream = null;
        String configFileName = "com/wellsoft/pt/basicdata/mapper/dozerDataMapping.xml";
        try {
            logger.info("** Reading mapping file " + configFileName + " **");
            ClassLoader classLoader = DataMapper.class.getClassLoader();
            xmlStream = classLoader.getResourceAsStream(configFileName);
            mapper.addMapping(xmlStream);
        } catch (Exception ex) {
            logger.info("** Reading mapping file " + configFileName + " error", ex);
        } finally {
            IOUtils.closeQuietly(xmlStream);
        }
    }

    @Override
    public <T> T map(Object source, Class<T> destinationClass) throws MappingException {
        return map(source, destinationClass, null);
    }

    @Override
    public void map(Object source, Object destination) throws MappingException {
        map(source, destination, null);
    }

    @Override
    public <T> T map(Object source, Class<T> destinationClass, String mapId) throws MappingException {
        preDataMappingCheck(mapId);
        return mapper.map(source, destinationClass, mapId);
    }

    @Override
    public void map(Object source, Object destination, String mapId) throws MappingException {
        preDataMappingCheck(mapId);
        mapper.map(source, destination, mapId);
    }

    private void preDataMappingCheck(String mapId) {
        if (MappingUtils.isBlankOrNull(mapId) || mapping.containsKey(mapId)) {
            return;
        }
        synchronized (mapping) {
            if (mapping.containsKey(mapId)) {
                return;// 重复检查
            }
            addDataMapper(mapId, null);
        }
    }

    //
    protected ClassMappings getClassMappings() {
        return (ClassMappings) ReflectionUtils.getFieldValue(mapper, FIELD_CUSTOMM_APPINGS);
    }

    protected Configuration getGlobalConfiguration() {
        return (Configuration) ReflectionUtils.getFieldValue(mapper, FIELD_GLOBAL_CONFIGURATION);
    }

    /**
     * 检查是否已经添加(不需要动态更新时做检查,防止多次添加)
     * if(!contains(mapId)){
     * addDataMapper(mapId,mappingDataBuilder);
     * }
     *
     * @param mapId
     * @return
     */
    protected boolean contains(String mapId) {
        return mapping.containsKey(mapId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.mapper.MapperManager#addDataMapper(java.lang.String, com.wellsoft.pt.basicdata.mapper.MapperDataBuilder)
     */
    @Override
    public synchronized MapperDataBuilder addDataMapper(String mapId, MapperDataBuilder mappingDataBuilder) {
        // 构建ClassMappings并添加到mapper的customMappings中
        // 缓存mappingDataBuilder并返回mapper
        mappingDataBuilder = mappingDataBuilder == null ? defaultMapperBuilder : mappingDataBuilder;
        String mappingBuilder = mappingDataBuilder.getClass().getSimpleName();
        logger.info("trying add mapId[" + mapId + "]MapperDataBuilder[{}]", mappingBuilder);
        MappingFileData mappingFileData = mappingDataBuilder.build(mapId);
        if (mappingFileData == null || CollectionUtils.isEmpty(mappingFileData.getClassMaps())) {
            return null;// 下一次继续加载
        }
        if (mappingFileData.getConfiguration() == null && true) {
            // ClassMapper未配置全局参数时使用初始化时添加的全局参数
            mappingFileData.setConfiguration(getGlobalConfiguration());
        }
        CustomMappingsLoader customMappingsLoader = new CustomMappingsLoader();
        LoadMappingsResult loadMappingsResult = customMappingsLoader.load(Arrays.asList(mappingFileData));
        getClassMappings().addAll(loadMappingsResult.getCustomMappings());
        for (ClassMap classMap : mappingFileData.getClassMaps()) { // mapping文件中有多个mapId
            if (MappingUtils.isBlankOrNull(classMap.getMapId())) {
                continue;
            }
            mapping.put(classMap.getMapId(), mappingDataBuilder);
        }
        logger.info("Successsfule add mapId[" + mapId + "]Thread[{0}]", Thread.currentThread().getName());
        return mapping.put(mapId, mappingDataBuilder);
    }

    /**
     * 添加MappingFileData(如果已经存在则不添加)
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.mapper.MapperManager#addDataMapperIfNeed(java.lang.String, com.wellsoft.pt.basicdata.mapper.MapperDataBuilder)
     */
    @Override
    public synchronized MapperDataBuilder addDataMapperIfNeed(String mapId, MapperDataBuilder mappingDataBuilder) {
        if (contains(mapId)) {
            return null;
        }
        return addDataMapper(mapId, mappingDataBuilder);
    }
}
