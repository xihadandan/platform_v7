package com.wellsoft.context.util.json;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wellsoft.context.util.groovy.GroovyUseable;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertySetStrategy;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description: JSON工具类
 *
 * @author Asus
 * @version 1.0
 * @date 2015年12月25日
 * @see com.google.gson.Gson
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年12月23日.1	Asus		2015年12月23日		Create
 * </pre>
 */
@GroovyUseable
public class JsonUtils {
    public static final PropertySetStrategy ignoreNotExistPropertySetStrategy = new PropertySetStrategy() {

        @Override
        public void setProperty(Object bean, String key, Object value) throws net.sf.json.JSONException {
            setProperty(bean, key, value, new JsonConfig());
        }

        @Override
        public void setProperty(Object bean, String key, Object value, JsonConfig jsonConfig)
                throws net.sf.json.JSONException {

            if (bean instanceof Map) {
                ((Map) bean).put(key, value);
            } else {
                if (!jsonConfig.isIgnorePublicFields()) {
                    try {
                        Field field = bean.getClass().getField(key);
                        if (field != null)
                            field.set(bean, value);
                    } catch (Exception e) {
                        _setProperty(bean, key, value);
                    }
                } else {
                    _setProperty(bean, key, value);
                }
            }

        }

        private void _setProperty(Object bean, String key, Object value) {
            try {
                PropertyUtils.setSimpleProperty(bean, key, value);
            } catch (Exception e) {
                // throw new net.sf.json.JSONException(e);
            }
        }
    };
    private static Logger LOG = Logger.getLogger(JsonUtils.class);
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    static {
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 代码有问题不使用
     *
     * @param JSONStr
     * @return
     */
    @Deprecated
    public static Collection toCollection(String JSONStr) {
        Collection rtn = new ArrayList();

        // JSONArray array = JSONArray.fromObject(JSONStr);
        // for (int i = 0; i < array.length(); i++) {
        // Object obj = array.get(i);
        // if (obj instanceof JSONObject) {
        // rtn.add(toMap((JSONObject) obj));
        // } else {
        // rtn.add(obj);
        // }
        // }

        return rtn;
    }

    /**
     * JSON数组字符串转集合
     *
     * @param JSONStr  JSON数组字符串
     * @param objClass 集合子项的对象类型
     * @return Collection集合
     */
    public static Collection toCollection(String JSONStr, Class objClass) {
        return toCollection(JSONStr, objClass, false);
    }

    public static Collection toCollection(String JSONStr, Class objClass, boolean ignoreNotExistProperty) {
        JSONArray array = JSONArray.fromObject(JSONStr);
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setRootClass(objClass);
        if (ignoreNotExistProperty) {
            jsonConfig.setPropertySetStrategy(ignoreNotExistPropertySetStrategy);
        }
        List list = (List) JSONArray.toCollection(array, jsonConfig);
        return list;
    }

    /**
     * json字符串转MAP
     *
     * @param jsonStr json字符串
     * @return Map
     */
    @Deprecated
    @SuppressWarnings("rawtypes")
    public static Map toMap(String jsonStr) {
        JSONObject jsonObject = JSONObject.fromObject(jsonStr);
        return toMap(jsonObject);
    }

    /**
     * JSONObject 转 MAP
     *
     * @param jsonObject JSONObject对象
     * @return MAP
     */
    @Deprecated
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Map toMap(JSONObject jsonObject) {
        Map rtn = new HashMap();

        if (jsonObject.isNullObject()) {
            return null;
        }
        for (Iterator iterator = jsonObject.keys(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            Object obj = jsonObject.get(key);

            if (obj instanceof JSONArray) {
                rtn.put(key, toArray((JSONArray) obj));
            } else if (obj instanceof JSONObject) {
                rtn.put(key, toMap((JSONObject) obj));
            } else {
                rtn.put(key, obj);
            }
        }

        return rtn;
    }

    @Deprecated
    private static Object[] toArray(JSONArray jsonArray) {
        // Object[] rtn = new Object[jsonArray.length()];
        //
        // for (int i = 0; i < jsonArray.length(); i++) {
        // Object obj = jsonArray.get(i);
        // if (obj instanceof JSONObject) {
        // rtn[i] = toMap((JSONObject) obj);
        // } else {
        // rtn[i] = obj;
        // }
        // }
        Object[] rtn = null;
        return rtn;
    }

    /**
     * json字符串转换成Bean对象
     *
     * @param jsonStr  json字符串
     * @param objClass 转换Bean的类型
     * @return Bean对象
     */
    @SuppressWarnings("rawtypes")
    public static Object toBean(String jsonStr, Class objClass) {
        JSONObject jsonObject = JSONObject.fromObject(jsonStr);
        Object obj = JSONObject.toBean(jsonObject, objClass);
        return obj;
    }

    /**
     * 集合转换成JsonArray字符串
     *
     * @param collection 转换的集合
     * @return JsonArray字符串
     */
    @SuppressWarnings("rawtypes")
    public static String collection2Json(Collection collection) {
        return collection2Json(collection, new String[]{});
    }

    /**
     * 集合转换成JsonArray字符串
     *
     * @param collection 转换的集合
     * @param excludes   无效参数
     * @return JsonArray字符串
     * @deprecated 用{@link #collection2Json(Collection)}替代
     */
    @SuppressWarnings("rawtypes")
    @Deprecated
    public static String collection2Json(Collection collection, String[] excludes) {
        // JsonConfig jsonConfig = new JsonConfig();
        JSONArray jsonArray = JSONArray.fromObject(collection);
        return jsonArray.toString();
    }

    /**
     * 对象转化为json字符串
     *
     * @param object 转换对象
     * @return json字符串
     */
    public static String object2Json(Object object) {
        StringWriter writer = new StringWriter();
        try {
            objectMapper.writeValue(writer, object);
        } catch (JsonGenerationException e) {
            LOG.error(e.getMessage(), e);
        } catch (JsonMappingException e) {
            LOG.error(e.getMessage(), e);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return writer.toString();
    }

    /**
     * json字符串转实体对象
     *
     * @param content   字符串内容
     * @param valueType 实例对象类型
     * @return 实例对象
     */
    public static <T extends Object> T json2Object(String content, Class<T> valueType) {
        try {
            return objectMapper.readValue(content, valueType);
        } catch (JsonParseException e) {
            LOG.error(e.getMessage(), e);
        } catch (JsonMappingException e) {
            LOG.error(e.getMessage(), e);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 对象转化为json字符串(Gson)
     *
     * @param object 转换对象
     * @return json字符串
     */
    public static String object2Gson(Object object) {
        return gson.toJson(object);
    }

    /**
     * json字符串转实体对象(Gson)
     *
     * @param content   字符串内容
     * @param valueType 实例对象类型
     * @return 实例对象
     */
    public static <T extends Object> T gson2Object(String content, Class<T> valueType) {
        return gson.fromJson(content, valueType);
    }

    public static <T> List<T> gson2List(String content, Type type) {
        return gson.fromJson(content, type);
    }

    /**
     * 将JSONObjec对象转换成List-Map集合
     *
     * @param json JSONObjec对象
     * @return List-Map集合
     * @see JSONHelper#reflect(JSONArray)
     */
    public static HashMap<String, Object> toListMap(JSONObject json) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        Set<?> keys = json.keySet();
        for (Object key : keys) {
            Object o = json.get(key);
            if (o instanceof JSONArray)
                map.put((String) key, toListMap((JSONArray) o));
            else if (o instanceof JSONObject)
                map.put((String) key, toListMap((JSONObject) o));
            else
                map.put((String) key, o);
        }
        return map;
    }

    /**
     * 将JSONArray对象转换成List-Map集合
     *
     * @param json JSONArray对象
     * @return List-Map集合
     * @see JSONHelper#reflect(JSONObject)
     */
    public static List<Object> toListMap(JSONArray json) {
        List<Object> list = new ArrayList<Object>();
        for (Object o : json) {
            if (o instanceof JSONArray)
                list.add(toListMap((JSONArray) o));
            else if (o instanceof JSONObject)
                list.add(toListMap((JSONObject) o));
            else
                list.add(o);
        }
        return list;
    }

    /**
     * 判断JSONObject是否为空
     *
     * @param object
     * @return
     */
    public static boolean isEmptyObject(JSONObject object) {
        if (object != null && object.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 判断JSONObject是否为空
     *
     * @param object
     * @return
     */
    public static boolean isEmptyObject(org.json.JSONObject object) {
        if (object != null && object.length() <= 0) {
            return true;
        }
        return false;
    }

    /**
     * 合并JSON字符串
     *
     * @param jsonString
     * @return
     */
    public static String mergeJsonString(String... jsonStrings) {
        Map<String, Object> values = Maps.newLinkedHashMap();
        for (String jsonString : jsonStrings) {
            if (StringUtils.isNotBlank(jsonString)) {
                Map<String, Object> jsonMap = json2Object(jsonString, LinkedHashMap.class);
                values.putAll(jsonMap);
            }
        }
        return object2Json(values);
    }

}
