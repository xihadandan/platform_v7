package com.wellsoft.pt.basicdata.excelexporttemplate.support;

import net.sf.json.JSONObject;
import org.apache.poi.ss.usermodel.Row;

public interface NameProcessor {
    public abstract Object process(Object value);

    public abstract Object process(Row headerRow, JSONObject jsonObject, Object value);

}
