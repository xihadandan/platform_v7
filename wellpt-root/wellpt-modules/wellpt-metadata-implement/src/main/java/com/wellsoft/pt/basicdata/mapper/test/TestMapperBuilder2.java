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

import com.wellsoft.pt.basicdata.mapper.MapperException;
import org.dozer.classmap.MappingFileData;
import org.dozer.loader.api.*;

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
public class TestMapperBuilder2 extends TestMapperBuilder {

    public static final String SCHEMA_MAP = "java.util.Map";
    public static final String SCHEMA_DYFORM = "com.wellsoft.pt.dyform.support.DyFormData";
    public TestMapperBuilder2() {

    }

    public MappingFileData build(final String mapId) {
        // 根据mapId在数据库中找映射关系
        // 根据映射关系构建BeanMappingBuilder
        //
        final List<Map<String, String>> fieldMappings = new ArrayList<Map<String, String>>();
        Map<String, String> fieldMapping = new HashMap<String, String>();
        fieldMapping.put("a", "shift_end_time");
        fieldMapping.put("b", "shift_code");
        fieldMappings.add(fieldMapping);
        Map<String, String> fieldMapping2 = new HashMap<String, String>();
        fieldMapping2.put("a", "shift_code");
        fieldMapping2.put("b", "shift_end_time");
        fieldMappings.add(fieldMapping2);
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
                    TypeDefinition typeA = TestMapperBuilder2.this.type(uriA, mapId);
                    TypeDefinition typeB = TestMapperBuilder2.this.type(uriB, mapId);
                    typeMappingBuilder = mapping(typeA, typeB, typeMappingOptions(typeA, typeB, uriA, uriB, mapId));
                    for (Map<String, String> fieldMapping : fieldMappings) {
                        String a = fieldMapping.get("a");
                        String b = fieldMapping.get("b");
                        FieldDefinition fieldA = TestMapperBuilder2.this.field(a, typeA, uriA);
                        FieldDefinition fieldB = TestMapperBuilder2.this.field(b, typeB, uriB);
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

    protected FieldsMappingOption[] fieldsMappingOptions(String a, String b, FieldDefinition fieldA,
                                                         FieldDefinition fieldB, TypeDefinition typeA, TypeDefinition typeB) {
        if ("name".equals(a) && "dydata".equals(b)) {
            // return new FieldsMappingOption[] { FieldsMappingOptions
            // .customConverter("com.wellsoft.pt.utils.mapper.DyFromDataConverter")
            // };
        }
        return new FieldsMappingOption[]{};
    }

}
