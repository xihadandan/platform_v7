package com.wellsoft.context.util.enumtool;

import com.wellsoft.context.annotation.EnumClass;
import javassist.Modifier;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: 枚举管理类
 *
 * @author Asus
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年12月25日.1	Asus		2015年12月25日		Create
 * </pre>
 * @date 2015年12月25日
 */
public class EnumManage {
    private static Logger logger = LoggerFactory.getLogger(EnumManage.class);
    private static EnumManage instance = null;
    private static Map<String, JSONObject> enumsMap = new HashMap<String, JSONObject>();
    private String jsEnum;

    private EnumManage() throws IOException {
        InitEnumClass();
    }

    /**
     * 获取所有加了{@link EnumClass}注解的枚举转化的MAP对象
     * MAP-KEY - 枚举类名  MAP-VALUE -枚举转化的JSON对象
     *
     * @return 包含所有枚举的MAP对象
     */
    public static Map<String, JSONObject> getEnumsMap() {
        try {
            getInstance();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return EnumManage.enumsMap;
    }

    /**
     * 实例化方法
     *
     * @return EnumManage
     * @throws IOException 加载项目下所有class文件失败时抛出
     */
    public static EnumManage getInstance() throws IOException {
        if (instance == null) {
            synchronized (EnumManage.class) {
                if (instance == null) {
                    instance = new EnumManage();
                }
            }
        }
        return instance;
    }

    ;

    private void InitEnumClass() throws IOException {
        Map<String, Map<String, Object>> enumClasses = readEnumClassNames();
        initEnumJsObject(enumClasses);
    }

    private void initEnumJsObject(Map<String, Map<String, Object>> enumClasses) {

        Map<String, Map<String, Map<String, String>>> retJsonObject = new HashMap<String, Map<String, Map<String, String>>>();
        for (String className : enumClasses.keySet()) {
            try {
                Class<?> clazz = Class.forName(className);
                Map<String, Object> definedMap = enumClasses.get(className);
                String objcetName = definedMap.get("objectName").toString();
                String key = definedMap.get("keyName").toString();
                String value = definedMap.get("valueName").toString();
                Object objectCnNametmp = definedMap.get("objectRemarkName");
                String objectRemarkName = objectCnNametmp == null || objectCnNametmp.toString().trim().length() == 0 ? objcetName
                        : objectCnNametmp.toString();
                // enumsMap
                try {
                    Map<String, Map<String, String>> fieldMap = new HashMap<String, Map<String, String>>();
                    for (Field field : clazz.getDeclaredFields()) {
                        if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())
                                && Modifier.isPublic(field.getModifiers())) {
                            // 获取静态变量的值
                            Map<String, String> valueMap = new HashMap<String, String>();
                            Object object = field.get(null);
                            Method keyMethod = clazz.getMethod(getMethodByField(key));
                            if (keyMethod != null) {
                                String keyValue = keyMethod.invoke(object).toString();
                                valueMap.put("key", keyValue);
                            }
                            Method valueMethod = clazz.getMethod(getMethodByField(value));
                            if (valueMethod != null) {
                                String valueValue = valueMethod.invoke(object).toString();
                                valueMap.put("value", valueValue);
                            }
                            fieldMap.put(field.getName(), valueMap);
                        }
                    }
                    // fieldMap.put("objectCnName", objectCnName);
                    retJsonObject.put(objcetName, fieldMap);
                    JSONObject enumJson = new JSONObject();

                    enumJson.put("key", key);
                    enumJson.put("value", value);
                    enumJson.put("objectRemarkName", objectRemarkName);
                    enumJson.put("objectName", objcetName);
                    enumJson.put("fields", fieldMap);
                    enumsMap.put(objcetName, enumJson);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            } catch (ClassNotFoundException e) {
                logger.error(e.getMessage(), e);
            }
        }

        JSONObject retJson = JSONObject.fromObject(retJsonObject);
        this.jsEnum = retJson.toString();
    }

    private String getMethodByField(String field) {
        if (field.length() == 1) {
            return "get" + field.toUpperCase();
        }
        return "get" + (field.substring(0, 1).toUpperCase()) + field.substring(1);
    }

    private Map<String, Map<String, Object>> readEnumClassNames() throws IOException {
        Map<String, Map<String, Object>> enumClasses = new HashMap<String, Map<String, Object>>();
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "com/wellsoft/**/*.class";
        Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
        for (Resource resource : resources) {
            if (resource.isReadable()) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                // System.out.println(metadataReader.getAnnotationMetadata().getAnnotationTypes());
                if (metadataReader.getAnnotationMetadata().hasAnnotation(EnumClass.class.getName())) {
                    Map<String, Object> enumAnnotation = metadataReader.getAnnotationMetadata()
                            .getAnnotationAttributes(EnumClass.class.getName());
                    String className = metadataReader.getClassMetadata().getClassName();
                    enumClasses.put(className, enumAnnotation);
                }
            }
        }
        return enumClasses;
    }

    private Map<String, Map<String, Object>> readEnumClassByClassNames(Class<?> claszz) throws IOException {
        Map<String, Map<String, Object>> enumClasses = new HashMap<String, Map<String, Object>>();
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                + claszz.getName().replace(".", File.separator) + ".class";
        Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
        for (Resource resource : resources) {
            if (resource.isReadable()) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                // System.out.println(metadataReader.getAnnotationMetadata().getAnnotationTypes());
                if (metadataReader.getAnnotationMetadata().hasAnnotation(EnumClass.class.getName())) {
                    Map<String, Object> enumAnnotation = metadataReader.getAnnotationMetadata()
                            .getAnnotationAttributes(EnumClass.class.getName());
                    String className = metadataReader.getClassMetadata().getClassName();
                    enumClasses.put(className, enumAnnotation);
                }
            }
        }
        return enumClasses;
    }

    /**
     * 获取所有加了{@link EnumClass}注解的枚举转化的JSON字符串
     *
     * @return JSON字符串
     * @throws IOException 加载项目下所有class文件失败时抛出
     */
    public String getEnumJs() throws IOException {
        return this.jsEnum;
    }

    /**
     * enum通过key获取name值
     *
     * @param enumClass 枚举对应的class
     * @param key       枚举中keyName字段的值
     * @return keyName对应的枚举的valueName的值
     * @throws Exception 未知
     */
    public String getEnumByKey(Class<?> enumClass, Object key) throws Exception {
        String resultValue = "";
        Map<String, Map<String, Object>> enumClasses = readEnumClassByClassNames(enumClass);
        try {
            Class<?> clazz = enumClass;
            Map<String, Object> definedMap = enumClasses.get(enumClass.getName());
            String code = definedMap.get("keyName").toString();
            String value = definedMap.get("valueName").toString();
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())
                        && Modifier.isPublic(field.getModifiers())) {
                    // 获取静态变量的值
                    Object object = field.get(null);
                    Method keyMethod = clazz.getMethod(getMethodByField(code));
                    Method valueMethod = clazz.getMethod(getMethodByField(value));
                    if (keyMethod != null) {
                        String keyValue = keyMethod.invoke(object).toString();
                        if (keyValue.equals(String.valueOf(key))) {
                            resultValue = valueMethod.invoke(object).toString();
                            break;
                        }
                    }

                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return resultValue;
    }

}
