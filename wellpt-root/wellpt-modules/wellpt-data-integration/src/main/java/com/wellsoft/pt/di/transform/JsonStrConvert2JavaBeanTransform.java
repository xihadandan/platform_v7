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
public class JsonStrConvert2JavaBeanTransform extends AbstractDataTransform<String, Object> {

    private Gson gson;
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
    @ProcessorParameter(name = "全路径类名", domType = DIParameterDomType.INPUT)
    private String className;

    public JsonStrConvert2JavaBeanTransform() {
        super();

    }

    private Gson gson() {
        if (gson == null) {

            /**
             * 创建gson格式化
             */
            GsonBuilder gsonBuilder = new GsonBuilder();
            if ("时间戳".equals(dateFormate)) {
                gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    public Date deserialize(JsonElement json, Type typeOfT,
                                            JsonDeserializationContext context) throws JsonParseException {
                        return new Date(json.getAsJsonPrimitive().getAsLong());
                    }
                });
            } else {
                gsonBuilder.setDateFormat(dateFormate);
            }

            gson = gsonBuilder.create();
        }
        return gson;
    }

    @Override
    public Object transform(String s) throws Exception {

        return gson().fromJson(s, Class.forName(className));

    }

    public String getDateFormate() {
        return dateFormate;
    }

    public void setDateFormate(String dateFormate) {
        this.dateFormate = dateFormate;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String name() {
        return "json字符串转java对象";
    }

}
