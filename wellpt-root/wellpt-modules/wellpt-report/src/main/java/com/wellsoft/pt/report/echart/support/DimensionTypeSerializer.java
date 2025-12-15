package com.wellsoft.pt.report.echart.support;

import com.google.common.base.Optional;
import com.wellsoft.pt.report.echart.enums.DimensionTypeEnum;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/5/15
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/5/15    chenq		2019/5/15		Create
 * </pre>
 */
public class DimensionTypeSerializer extends JsonSerializer<DimensionTypeEnum> {

    @Override
    public void serialize(DimensionTypeEnum value, JsonGenerator jgen,
                          SerializerProvider provider) throws IOException, JsonProcessingException {
        Optional<DimensionTypeEnum> data = Optional.of(value);
        if (data.isPresent()) {
            jgen.writeObject(data.get().toString());
        } else {
            jgen.writeString("");
        }
    }
}
