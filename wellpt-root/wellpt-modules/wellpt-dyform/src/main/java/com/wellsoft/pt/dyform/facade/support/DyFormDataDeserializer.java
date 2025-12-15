package com.wellsoft.pt.dyform.facade.support;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import java.io.IOException;

public class DyFormDataDeserializer extends JsonDeserializer<DyFormData> {

    @Override
    public DyFormData deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        DyFormFacade dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
        DyFormData dyFormData = dyFormFacade.deserializeDyformData(jp, ctxt);
        return dyFormData;
    }

}
