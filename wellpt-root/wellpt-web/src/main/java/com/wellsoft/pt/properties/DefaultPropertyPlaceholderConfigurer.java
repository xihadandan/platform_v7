package com.wellsoft.pt.properties;

import com.google.common.collect.Maps;
import com.wellsoft.context.config.Config;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Constants;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.StringValueResolver;

import java.util.*;

/**
 * Description: 配置加载
 *
 * @author chenq
 * @date 2019-06-30
 * <p/>
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019-06-30    chenq		2019-06-30		Create
 * </pre>
 */
@Component("propertyConfigurer")
@Order(1)
@Lazy(false)
public class DefaultPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    private final static Map<String, String> PROPERTIES = Maps.newHashMap();
    private static final Constants constants = new Constants(PropertyPlaceholderConfigurer.class);
    private static final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final boolean trimValues = true;
    protected boolean ignoreUnresolvablePlaceholders = true;
    private int systemPropertiesMode = SYSTEM_PROPERTIES_MODE_FALLBACK;


    public DefaultPropertyPlaceholderConfigurer() {
        super();
        LinkedHashMap<String, Resource> resourcesMap = Maps.newLinkedHashMap();
        try {

            //加载system前缀的平台配置文件
            String webContextSystemProperties = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + "system*.properties";

            Resource[] resources = resourcePatternResolver.getResources(
                    webContextSystemProperties);

//            Arrays.sort(resources, new Comparator<Resource>() {
//                @Override
//                public int compare(Resource o1, Resource o2) {
//                    if (o1 instanceof FileSystemResource && false == (o2 instanceof FileSystemResource)) {
//                        return -1;
//                    } else if (false == (o1 instanceof FileSystemResource) && (o2 instanceof FileSystemResource)) {
//                        return 1;
//                    }
//                    return 0;
//                }
//            });

            for (Resource res : resources) {
                if (!resourcesMap.containsKey(res.getFilename())) {
                    resourcesMap.put(res.getFilename(), res);
                }

            }

            resourcesMap.put("basenames",
                    new ClassPathResource("/i18n/basenames.properties"));

        } catch (Exception e) {
            log.error("获取系统的属性配置文件异常：", e);
        }
        log.error("加载系统的属性配置文件列表：{}", resourcesMap.values());
        this.setLocations(resourcesMap.values().toArray(new Resource[]{}));
        this.setFileEncoding("UTF-8");
    }

    public static String getValue(String key) {
        if (PROPERTIES.containsKey(key)) {
            return PROPERTIES.get(key);
        }
        return "";
    }


    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess,
                                     Properties props)
            throws BeansException {
        StringValueResolver valueResolver = new PlaceholderResolvingStringValueResolver(props);
        //单点登录相关配置修改
        modifyCasLoginProperties(props);
        Set<Object> keys = props.keySet();
        for (Object k : keys) {
            PROPERTIES.put(k.toString(), props.get(k).toString());
            log.debug("加载配置参数：{}={}", k.toString(), PROPERTIES.get(k.toString()));

        }
        doProcessProperties(beanFactoryToProcess, valueResolver);
    }

    private void modifyCasLoginProperties(Properties props) {
        if (Config.getCasEnable()) {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
            dataSource.setUrl(props.getProperty("multi.tenancy.tenant.url"));
            dataSource.setUsername(props.getProperty("multi.tenancy.tenant.username"));
            dataSource.setPassword(props.getProperty("multi.tenancy.tenant.password"));


            JdbcTemplate jdbcTemplate = new JdbcTemplate();
            jdbcTemplate.setDataSource(dataSource);

            List<Map<String, Object>> list = jdbcTemplate.queryForList(
                    "SELECT * FROM APP_LOGIN_PAGE_CONFIG");
            if (list == null || list.size() == 0 || !"0".equals(list.get(0).get("LOGIN_BOX_CAS"))) {
                return;
            }
            Map<String, Object> map = list.get(0);
            if (map.get("LOGIN_BOX_CAS_URL") == null || StringUtils.isBlank(
                    map.get("LOGIN_BOX_CAS_URL").toString())) {
                throw new RuntimeException("APP_LOGIN_PAGE_CONFIG配置表未配置LOGIN_BOX_CAS_URL(单点登陆地址)");
            }

            if (map.get("LOGIN_BOX_CAS_APP_URL") == null
                    || StringUtils.isBlank(map.get("LOGIN_BOX_CAS_APP_URL").toString())) {
                throw new RuntimeException(
                        "APP_LOGIN_PAGE_CONFIG配置表未配置LOGIN_BOX_CAS_APP_URL(应用地址)");
            }
            props.setProperty("security.cas.isuse", "true");
            props.setProperty("security.cas.url", map.get("LOGIN_BOX_CAS_URL").toString());
            props.setProperty("security.cas.application.url",
                    map.get("LOGIN_BOX_CAS_APP_URL").toString());

            Config.setValue("security.cas.isuse", "true");
            Config.setValue("ssecurity.cas.url", map.get("LOGIN_BOX_CAS_URL").toString());
            Config.setValue("security.cas.application.url",
                    map.get("LOGIN_BOX_CAS_APP_URL").toString());

        }

    }

    public void setSystemPropertiesModeName(String constantName) throws IllegalArgumentException {
        this.systemPropertiesMode = constants.asNumber(constantName).intValue();
    }

    private class PlaceholderResolvingStringValueResolver implements StringValueResolver {

        private final PropertyPlaceholderHelper helper;

        private final PropertyPlaceholderHelper.PlaceholderResolver resolver;

        public PlaceholderResolvingStringValueResolver(Properties props) {
            this.helper = new PropertyPlaceholderHelper(
                    placeholderPrefix, placeholderSuffix, valueSeparator,
                    ignoreUnresolvablePlaceholders);
            this.resolver = new PropertyPlaceholderConfigurerResolver(props);
        }

        @Override
        public String resolveStringValue(String strVal) throws BeansException {
            String resolved = this.helper.replacePlaceholders(strVal, this.resolver);
            if (trimValues) {
                resolved = resolved.trim();
            }
            return (resolved.equals(nullValue) ? null : resolved);
        }
    }

    private final class PropertyPlaceholderConfigurerResolver implements
            PropertyPlaceholderHelper.PlaceholderResolver {

        private final Properties props;

        private PropertyPlaceholderConfigurerResolver(Properties props) {
            this.props = props;
        }

        @Override
        public String resolvePlaceholder(String placeholderName) {
            return DefaultPropertyPlaceholderConfigurer.this.resolvePlaceholder(placeholderName,
                    this.props, SYSTEM_PROPERTIES_MODE_FALLBACK);
        }
    }
}
