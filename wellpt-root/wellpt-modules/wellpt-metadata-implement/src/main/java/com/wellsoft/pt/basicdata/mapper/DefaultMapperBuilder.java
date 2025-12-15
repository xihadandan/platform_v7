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

import com.wellsoft.pt.basicdata.mapper.support.ResouceMapperBuilder;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.dozer.classmap.ClassMap;
import org.dozer.classmap.DozerClass;
import org.dozer.classmap.MappingDirection;
import org.dozer.classmap.MappingFileData;
import org.dozer.loader.api.*;
import org.dozer.util.DozerConstants;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static com.wellsoft.pt.basicdata.mapper.support.MapperContants.*;

/**
 * Description: DefaultMapperBuilder
 * API构建MappingFileData
 * doBuild
 * --typeDef(类定义)
 * --typeMapMethods(类的MapMethods)
 * --typeMappingOptions(返回类的可选项)
 * --fieldsSplit(映射字段拆分a>>b)
 * --fieldDef(字段定义)
 * --fieldMapMethods(字段的MapMethods)
 * --fieldSetMethods(字段的SetMethod)
 * --fieldGetMethods(字段的GetMethod)
 * --fieldMappingOptions(字段可选项)
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
public class DefaultMapperBuilder implements MapperDataBuilder {

    public DefaultMapperBuilder() {

    }

    public static void main(String[] args) throws URISyntaxException {
        URI uri = new URI("class://:?beanFactory=DyformDataFactory");// [scheme:][//host:port][path][?query][#fragment]
        System.out.println(uri.getScheme());// bean
        System.out.println(uri.getHost());// com.wellsoft.pt.test.BeanTest
        System.out.println(uri.getPort());// 0
        System.out.println(uri.getQuery());// beanFactory=DyformDataFactory
    }

    @Override
    public MappingFileData build(final String mapId) {
        // 默认使用数据库
        return ResouceMapperBuilder.getDatabaseMapppingBuilder().build(mapId);
    }

    protected MappingFileData doBuild(final String mapId, final List<?> fieldMappings) {
        BeanMappingBuilder mappingBuilder = new BeanMappingBuilder() {
            protected void configure() {
                String[] mapIds = mapId.split(">>");
                URI uriA = null;
                URI uriB = null;
                try {
                    uriA = new URI(mapIds[0]);
                    uriB = new URI(mapIds.length == 1 ? mapIds[0] : mapIds[1]);
                } catch (URISyntaxException ex) {
                    throw new MapperException("mapId格式有误", ex);
                }
                TypeMappingBuilder typeMappingBuilder = null;
                try {
                    TypeDefinition typeA = typeDef(uriA, mapId);
                    TypeDefinition typeB = typeDef(uriB, mapId);
                    typeMappingBuilder = mapping(typeA, typeB, typeMappingOptions(typeA, typeB, uriA, uriB, mapId));
                    for (Object fieldMapping : fieldMappings) {
                        String[] fields = fieldsSplitA2B(fieldMapping, mapId);
                        org.dozer.loader.api.FieldDefinition fieldA = fieldDef(fields[0], typeA, uriA);
                        org.dozer.loader.api.FieldDefinition fieldB = fieldDef(fields[1], typeB, uriB);
                        typeMappingBuilder.fields(fieldA, fieldB,
                                fieldMappingOptions(fields[0], fields[1], fieldA, fieldB, typeA, typeB));
                    }
                } catch (ClassNotFoundException ex) {
                    throw new MapperException("mapId格式有误", ex);
                } catch (UnsupportedEncodingException ex) {
                    throw new MapperException("mapId格式有误", ex);
                }
            }
        };
        MappingFileData mappingFileData = mappingBuilder.build();
        for (ClassMap classMap : mappingFileData.getClassMaps()) {
            if (MappingDirection.ONE_WAY.equals(classMap.getType())) {
                continue;
            }
            DozerClass srcClass = classMap.getSrcClass();
            DozerClass destClass = classMap.getDestClass();
            if (StringUtils.equals(srcClass.getName(), destClass.getName())) {
                // 相同类型双向映射时,srcClass和destClass的参数取反
                DozerClass tmpClass = srcClass;
                classMap.setSrcClass(destClass);
                classMap.setDestClass(tmpClass);
            }
        }
        return mappingFileData;
    }

    /**
     * 返回类定义
     *
     * @param uri
     * @param mapId
     * @return
     * @throws ClassNotFoundException
     * @throws UnsupportedEncodingException
     */
    protected TypeDefinition typeDef(URI uri, String mapId) throws ClassNotFoundException, UnsupportedEncodingException {
        TypeDefinition type = new TypeDefinition(uri.getScheme());
        QueryParameter params = new QueryParameter(uri.getQuery());
        String beanFactory = params.getString(DATA_FACTORY);
        String factoryBeanId = params.getString(FACTORY_DATA_ID);
        type.beanFactory(beanFactory != null ? beanFactory : params.getString(BEAN_FACTORY));
        type.mapMethods(factoryBeanId != null ? factoryBeanId : params.getString(FACTORY_BEAN_ID));
        String[] mapMethods = typeMapMethods(uri, Class.forName(type.getName()));
        return type.mapMethods(mapMethods[0], mapMethods[1]);
    }

    /**
     * 返回类的MapMethods
     *
     * @param uri
     * @param clazz
     * @return
     */
    protected String[] typeMapMethods(URI uri, Class<?> clazz) {
        if (SCHEMA_DYFORM.equals(uri.getScheme())) {
            return MAPPING_METHODS_DYFORM;
        } else if (SCHEMA_MAP.equals(uri.getScheme())) {
            return MAPPING_METHODS_MAP;
        }
        return MAPPING_METHODS_DEFAULT;
    }

    /**
     * 返回类的可选项
     *
     * @param typeA
     * @param typeB
     * @param uriA
     * @param uriB
     * @param mapId
     * @return
     */
    protected TypeMappingOption[] typeMappingOptions(TypeDefinition typeA, TypeDefinition typeB, URI uriA, URI uriB,
                                                     String mapId) {
        return new TypeMappingOption[]{TypeMappingOptions.mapId(mapId), TypeMappingOptions.wildcard(false)};
    }

    /**
     * 映射字段拆分a>>b
     *
     * @param fieldMapping
     * @param mapId
     * @return
     */
    protected String[] fieldsSplitA2B(Object fieldMapping, String mapId) {
        return ((String) fieldMapping).split(">>");
    }

    /**
     * 返回字段定义
     *
     * @param field
     * @param type
     * @param uri
     * @return
     */
    protected FieldDefinition fieldDef(String field, TypeDefinition type, URI uri) {
        if (field.contains(":")) {
            // JSON
            field = JSONObject.fromObject(field).getString("value");
        }
        FieldDefinition fieldDef = null;
        if (SCHEMA_DYFORM.equals(uri.getScheme())) {
            String[] mapMethods = fieldMapMethods(field, type, uri);
            fieldDef = new FieldDefinition(DozerConstants.SELF_KEYWORD).mapKey(field);
            fieldDef.mapMethods(mapMethods[0], mapMethods[1]);
        } else if (SCHEMA_MAP.equals(uri.getScheme())) {
            String[] mapMethods = fieldMapMethods(field, type, uri);
            fieldDef = new FieldDefinition(DozerConstants.SELF_KEYWORD).mapKey(field);
            fieldDef.mapMethods(mapMethods[0], mapMethods[1]);
        } else {
            fieldDef = new FieldDefinition(field);
        }
        return fieldDef.setMethod(fieldSetMethod(field, type, uri)).getMethod(fieldGetMethod(field, type, uri));
    }

    /**
     * 返回字段可选项(Map和DyFormData字段值默认为引用拷贝)
     *
     * @param a
     * @param b
     * @param fieldA
     * @param fieldB
     * @param typeA
     * @param typeB
     * @return
     */
    protected FieldsMappingOption[] fieldMappingOptions(String a, String b, FieldDefinition fieldA,
                                                        FieldDefinition fieldB, TypeDefinition typeA, TypeDefinition typeB) {
        return new FieldsMappingOption[]{FieldsMappingOptions.copyByReference()};
    }

    /**
     * 返回字段的SetMethod
     *
     * @param field
     * @param type
     * @param uri
     * @return
     */
    protected String fieldSetMethod(String field, TypeDefinition type, URI uri) {
        return null;
    }

    /**
     * 返回字段的GetMethod
     *
     * @param field
     * @param type
     * @param uri
     * @return
     */
    protected String fieldGetMethod(String field, TypeDefinition type, URI uri) {
        return null;
    }

    /**
     * 返回字段的MapMethods
     *
     * @param field
     * @param type
     * @param uri
     * @return
     */
    protected String[] fieldMapMethods(String field, TypeDefinition type, URI uri) {
        if (SCHEMA_DYFORM.equals(uri.getScheme())) {
            return MAPPING_METHODS_DYFORM;
        } else if (SCHEMA_MAP.equals(uri.getScheme())) {
            return MAPPING_METHODS_MAP;
        }
        return MAPPING_METHODS_DEFAULT;
    }
}
