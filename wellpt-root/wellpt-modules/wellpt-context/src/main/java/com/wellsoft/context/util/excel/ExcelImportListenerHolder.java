package com.wellsoft.context.util.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Separator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/12/9
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/12/9    chenq		2019/12/9		Create
 * </pre>
 */
@Component
public class ExcelImportListenerHolder {

    private static Logger logger = LoggerFactory.getLogger(ExcelImportListenerHolder.class);
    private static ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private static MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(
            resourcePatternResolver);
    private static Map<String, Class<?>> excelImportListenerClasses;

    static {
        try {
            initExcelImportListenerClasses();
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    private static void initExcelImportListenerClasses() throws Exception {
        if (excelImportListenerClasses != null) {
            return;
        }
        excelImportListenerClasses = new HashMap<String, Class<?>>();
        String[] basePackages = StringUtils.split(Config.BASE_PACKAGES, Separator.COMMA.getValue());
        for (String basePackage : basePackages) {
            String searchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + org.springframework.util.ClassUtils.convertClassNameToResourcePath(
                    basePackage)
                    + "/**/*ExcelImportListener.class";
            Resource[] resources = resourcePatternResolver.getResources(searchPath);
            for (Resource resource : resources) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                String className = metadataReader.getClassMetadata().getClassName();
                Class<AbstractEasyExcelImportListener> mappedClass = (Class<AbstractEasyExcelImportListener>) Class.forName(
                        className);
                if (AbstractEasyExcelImportListener.class.isAssignableFrom(mappedClass)
                        && !Modifier.isAbstract(mappedClass.getModifiers())) {
                    AbstractEasyExcelImportListener ls = mappedClass.newInstance();
                    excelImportListenerClasses.put(ls.name(), mappedClass);
                }
            }
        }
    }

    public static ExcelImportDataReport importInstream(
            InputStream inputStream,
            Class<?> listenerClass, ExcelImportRule excelImportRule) {

        try {
            Type genType = listenerClass.getGenericSuperclass();
            if (genType instanceof ParameterizedType) {
                AbstractEasyExcelImportListener listener = (AbstractEasyExcelImportListener) listenerClass.newInstance();
                listener.setExcelImportRule(excelImportRule);
                Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
                ExcelReaderBuilder builder = null;
                if (params[0] instanceof Class<?>) {
                    Class<?> clazz = (Class<?>) params[0];
                    if (Map.class.isAssignableFrom(clazz)) {
                        builder = EasyExcel.read(inputStream);
                    } else {
                        Class importDataClass = (Class) params[0];
                        builder = EasyExcel.read(inputStream, importDataClass, null);
                    }

                } else if (params[0] instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) params[0];
                    Type rawType = parameterizedType.getRawType();
                    if (rawType instanceof Class<?> && Map.class.isAssignableFrom((Class<?>) rawType)) {
                        builder = EasyExcel.read(inputStream);
                    }
                }
                ExcelReader excelReader = builder.build();
                if (CollectionUtils.isNotEmpty(excelImportRule.getSheetImportRules())) {
                    int sheet = 0;
                    for (SheetImportRule rule : excelImportRule.getSheetImportRules()) {
                        excelReader.read(EasyExcel.readSheet(sheet++).headRowNumber(rule.getHeaderRowIndex()).registerReadListener(listener).build());
                    }
                }

                return listener.getDataReport();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Select2QueryData getAllExcelImportListenerClass(Select2QueryInfo queryInfo) {
        Select2QueryData select2QueryData = new Select2QueryData();
        Set<String> names = excelImportListenerClasses.keySet();
        for (String n : names) {
            select2QueryData.addResultData(
                    new Select2DataBean(excelImportListenerClasses.get(n).getCanonicalName(), n));
        }
        return select2QueryData;
    }


}
