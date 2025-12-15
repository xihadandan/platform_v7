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
package com.wellsoft.pt.basicdata.mapper.test;

import com.wellsoft.pt.basicdata.mapper.MapperDataBuilder;
import com.wellsoft.pt.basicdata.mapper.MapperException;
import com.wellsoft.pt.basicdata.mapper.QueryParameter;
import net.sf.json.JSONObject;
import org.dozer.classmap.MappingFileData;
import org.dozer.loader.api.*;
import org.dozer.util.DozerConstants;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
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
public class TestMapperBuilder implements MapperDataBuilder {

    public static final String SCHEMA_MAP = "java.util.Map";
    public static final String SCHEMA_DYFORM = "com.wellsoft.pt.dyform.support.DyFormData";
    public TestMapperBuilder() {

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
        // 根据mapId在数据库中找最新版本映射关系
        // 根据映射关系构建BeanMappingBuilder
        //
        final List<Map<String, String>> fieldMappings = new ArrayList<Map<String, String>>();
        Map<String, String> fieldMapping = new HashMap<String, String>();
        fieldMapping.put("a", "id");
        fieldMapping.put("b", "name");
        fieldMappings.add(fieldMapping);
        Map<String, String> fieldMapping2 = new HashMap<String, String>();
        fieldMapping2.put("a", "name");
        fieldMapping2.put("b", "id");
        fieldMappings.add(fieldMapping2);
        Map<String, String> fieldMapping3 = new HashMap<String, String>();
        fieldMapping3.put("a", "bb");
        fieldMapping3.put("b", "id");
        fieldMappings.add(fieldMapping3);
        Map<String, String> fieldMapping4 = new HashMap<String, String>();
        fieldMapping4.put("a", "bdate");
        fieldMapping4.put("b", "name");
        fieldMappings.add(fieldMapping4);
        Map<String, String> fieldMapping5 = new HashMap<String, String>();
        fieldMapping5.put("a", "name");
        fieldMapping5.put("b", "dydata");
        fieldMappings.add(fieldMapping5);
        //
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
                    TypeDefinition typeA = TestMapperBuilder.this.type(uriA, mapId);
                    TypeDefinition typeB = TestMapperBuilder.this.type(uriB, mapId);
                    typeMappingBuilder = mapping(typeA, typeB, typeMappingOptions(typeA, typeB, uriA, uriB, mapId));
                    for (Map<String, String> fieldMapping : fieldMappings) {
                        String a = fieldMapping.get("a");
                        String b = fieldMapping.get("b");
                        FieldDefinition fieldA = TestMapperBuilder.this.field(a, typeA, uriA);
                        FieldDefinition fieldB = TestMapperBuilder.this.field(b, typeB, uriB);
                        typeMappingBuilder.fields(fieldA, fieldB,
                                fieldsMappingOptions(a, b, fieldA, fieldB, typeA, typeB));
                    }
                } catch (ClassNotFoundException ex) {
                    throw new MapperException("mapId格式有误", ex);
                } catch (UnsupportedEncodingException ex) {
                    throw new MapperException("mapId格式有误", ex);
                }
            }
        };
        return mappingBuilder.build();
    }

    protected TypeDefinition type(URI uri, String mapId) throws ClassNotFoundException, UnsupportedEncodingException {
        TypeDefinition type = new TypeDefinition(uri.getScheme());
        QueryParameter params = new QueryParameter(uri.getQuery());
        type.beanFactory(params.getString("beanFactory"));
        type.mapMethods(params.getString("factoryBeanId"));
        String[] mapMethods = mapMethods(uri, Class.forName(type.getName()));
        return type.mapMethods(mapMethods[0], mapMethods[1]);
    }

    protected String[] mapMethods(URI uri, Class<?> clazz) {
        if (SCHEMA_DYFORM.equals(uri.getScheme())) {
            return new String[]{"getFieldValue", "setFieldValue"};
        } else if (SCHEMA_MAP.equals(uri.getScheme())) {
            return new String[]{"get", "put"};
        }
        return new String[]{null, null};
    }

    protected TypeMappingOption[] typeMappingOptions(TypeDefinition typeA, TypeDefinition typeB, URI uriA, URI uriB,
                                                     String mapId) {
        return new TypeMappingOption[]{TypeMappingOptions.mapId(mapId), TypeMappingOptions.wildcard(false)};
    }

    protected FieldDefinition field(String field, TypeDefinition type, URI uri) {
        if (field.contains(":")) {
            // JSON
            field = JSONObject.fromObject(field).getString("value");
        }
        if (SCHEMA_DYFORM.equals(uri.getScheme())) {
            return new FieldDefinition(DozerConstants.SELF_KEYWORD).mapKey(field);
        } else if (SCHEMA_MAP.equals(uri.getScheme())) {
            return new FieldDefinition(DozerConstants.SELF_KEYWORD).mapKey(field);
        }
        return new FieldDefinition(field);
    }

    protected FieldsMappingOption[] fieldsMappingOptions(String a, String b, FieldDefinition fieldA,
                                                         FieldDefinition fieldB, TypeDefinition typeA, TypeDefinition typeB) {
        return new FieldsMappingOption[]{};
    }
}
