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
package com.wellsoft.pt.basicdata.mapper.support;

import com.wellsoft.pt.basicdata.mapper.DefaultMapperBuilder;
import com.wellsoft.pt.basicdata.mapper.MapperException;
import org.dozer.classmap.MappingFileData;

import java.util.List;

/**
 * Description: ApiMapperBuilder
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
public class ApiMapperBuilder<T> extends DefaultMapperBuilder {

    private List<T> fieldMappings;

    public ApiMapperBuilder(List<T> mappings) {
        if (mappings == null || mappings.isEmpty()) {
            throw new MapperException("QueryItemMapperBuilder mappings is notEmpty");
        }
        this.fieldMappings = mappings;
    }

    @Override
    public MappingFileData build(final String mapId) {
        return doBuild(mapId, fieldMappings);
    }
}
