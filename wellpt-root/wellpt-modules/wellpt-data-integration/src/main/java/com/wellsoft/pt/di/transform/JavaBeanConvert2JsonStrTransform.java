package com.wellsoft.pt.di.transform;

import com.google.gson.*;
import com.wellsoft.pt.di.anotation.ProcessorParameter;
import com.wellsoft.pt.di.enums.DIParameterDomType;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/21
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/21    chenq		2019/8/21		Create
 * </pre>
 */
public class JavaBeanConvert2JsonStrTransform extends AbstractDataTransform<Object, String> {

    private Gson gson;
    @ProcessorParameter(name = "格式化Json", domType = DIParameterDomType.CHECKBOX, dataJSON = "{\n" +
            "        \"是\": \"true\"\n" +
            "    }\n")
    private boolean prettyFormate = false;
    @ProcessorParameter(name = "日期格式", domType = DIParameterDomType.SELECT, dataJSON = "{\n" +
            "        \"yyyy-MM-dd HH:mm:ss\": \"yyyy-MM-dd HH:mm:ss\",\n" +
            "        \"yyyy年MM月dd日 HH时mm分ss秒\": \"yyyy年MM月dd日 HH时mm分ss秒\",\n" +
            "        \"yyyy-MM-dd HH:mm:ss:SSS\": \"yyyy-MM-dd HH:mm:ss:SSS\",\n" +
            "        \"yyyy年MM月dd日 HH时mm分ss秒SSS毫秒\": \"yyyy年MM月dd日 HH时mm分ss秒SSS毫秒\",\n" +
            "        \"yyyy-MM-dd\": \"yyyy-MM-dd\",\n" +
            "        \"yyyy年MM月dd日\": \"yyyy年MM月dd日\",\n" +
            "        \"时间戳\": \"时间戳\"\n" +
            "    }")
    private String dateFormate = "yyyy-MM-dd HH:mm:ss";

    @Override
    public String transform(Object o) throws Exception {
        return gson().toJson(o);
    }


    // 以下是转换器对外的参数设置

    private Gson gson() {
        if (gson == null) {
            /**
             * 创建gson格式化
             */
            GsonBuilder gsonBuilder = new GsonBuilder();
            if ("时间戳".equals(dateFormate)) {
                gsonBuilder.registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
                    @Override
                    public JsonElement serialize(Date date, Type type,
                                                 JsonSerializationContext jsonSerializationContext) {
                        return new JsonPrimitive(date.getTime());
                    }
                });
            } else {
                gsonBuilder.setDateFormat(dateFormate);
            }
            if (prettyFormate) {
                gsonBuilder.setPrettyPrinting();
            }
            gsonBuilder.setLongSerializationPolicy(LongSerializationPolicy.STRING);

            gson = gsonBuilder.create();
        }

        return gson;

    }

    @Override
    public String name() {
        return "java对象转json字符串";
    }

    public boolean getPrettyFormate() {
        return prettyFormate;
    }

    public void setPrettyFormate(boolean prettyFormate) {
        this.prettyFormate = prettyFormate;
    }

    public String getDateFormate() {
        return dateFormate;
    }

    public void setDateFormate(String dateFormate) {
        this.dateFormate = dateFormate;
    }
}
