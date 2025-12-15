package com.wellsoft.pt.dyform.implement.data.exceptions;

import com.wellsoft.context.enums.JsonDataErrorCode;
import com.wellsoft.context.exception.JsonDataException;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DyformDataSaveException extends JsonDataException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Logger logger = LoggerFactory.getLogger(DyformDataSaveException.class);
    private String msg = "";

    public DyformDataSaveException() {
    }

    public DyformDataSaveException(String msg) {
        this.msg = msg;
    }

    public DyformDataSaveException(String msg, String code) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("code", code);
            jo.put("msg", msg);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
        this.msg = jo.toString();
    }

    @Override
    public Object getData() {
        return this.msg;
    }

    @Override
    public JsonDataErrorCode getErrorCode() {
        return JsonDataErrorCode.SaveData;
    }

}
