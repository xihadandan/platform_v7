package com.wellsoft.pt.security.core.listener;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletContext;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DmsContextLoaderListener extends ContextLoaderListener {

    @Override
    protected void configureAndRefreshWebApplicationContext(ConfigurableWebApplicationContext wac, ServletContext sc) {
        if (ObjectUtils.identityToString(wac).equals(wac.getId())) {
            String idParam = sc.getInitParameter(CONTEXT_ID_PARAM);
            if (idParam != null) {
                wac.setId(idParam);
            } else {
                if (sc.getMajorVersion() == 2 && sc.getMinorVersion() < 5) {
                    wac.setId(ConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX
                            + ObjectUtils.getDisplayString(sc.getServletContextName()));
                } else {
                    wac.setId(ConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX
                            + ObjectUtils.getDisplayString(sc.getContextPath()));
                }
            }
        }

        wac.setServletContext(sc);
        String initParameter = sc.getInitParameter(CONFIG_LOCATION_PARAM);
        if (initParameter != null) {
            wac.setConfigLocation(initSecurityParameter(initParameter));
        }
        customizeContext(sc, wac);
        wac.refresh();
    }

    private void updateProperties(String loginBoxCasUrl, String loginBoxCasAppUrl) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = this.getClass().getResourceAsStream("/system-security.properties");
            Properties pro = new Properties();
            pro.load(in);

            out = new FileOutputStream(this.getClass().getResource("/").getPath() + "system-security.properties");
            pro.setProperty("security.cas.isuse", "true");
            pro.setProperty("security.cas.url", loginBoxCasUrl);
            pro.setProperty("security.cas.application.url", loginBoxCasAppUrl);

            pro.store(out, "单点配置");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("resource")
    private String initSecurityParameter(String parameter) {
        InputStream in = null;
        try {
            in = this.getClass().getResourceAsStream("/system-jdbc.properties");
            Properties pro = new Properties();
            pro.load(in);

            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
            dataSource.setUrl(pro.getProperty("multi.tenancy.tenant.url"));
            dataSource.setUsername(pro.getProperty("multi.tenancy.tenant.username"));
            dataSource.setPassword(pro.getProperty("multi.tenancy.tenant.password"));

            JdbcTemplate jdbcTemplate = new JdbcTemplate();
            jdbcTemplate.setDataSource(dataSource);

            List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT * FROM APP_LOGIN_PAGE_CONFIG");
            if (list == null || list.size() == 0 || !"0".equals(list.get(0).get("LOGIN_BOX_CAS"))) {
                return parameter;
            }
            Map<String, Object> map = list.get(0);
            if (map.get("LOGIN_BOX_CAS_URL") == null || StringUtils.isBlank(map.get("LOGIN_BOX_CAS_URL").toString())) {
                throw new RuntimeException("APP_LOGIN_PAGE_CONFIG配置表未配置LOGIN_BOX_CAS_URL(单点登陆地址)");
            }

            if (map.get("LOGIN_BOX_CAS_APP_URL") == null
                    || StringUtils.isBlank(map.get("LOGIN_BOX_CAS_APP_URL").toString())) {
                throw new RuntimeException("APP_LOGIN_PAGE_CONFIG配置表未配置LOGIN_BOX_CAS_APP_URL(应用地址)");
            }

            updateProperties(map.get("LOGIN_BOX_CAS_URL").toString(), map.get("LOGIN_BOX_CAS_APP_URL").toString());

            String regex = "classpath\\*:/applicationContext-security[-[a-zA-Z]]*\\.xml";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(parameter);
            String securityParameter = null;
            if (matcher.find()) {
                securityParameter = parameter.replaceAll(regex, "classpath*:/applicationContext-security-cas.xml");
            } else {
                securityParameter = parameter + ",classpath*:/applicationContext-security-cas.xml";
            }
            return securityParameter;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return parameter;
    }
}
