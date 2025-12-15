package com.wellsoft.pt.security.config;

import com.wellsoft.pt.security.config.service.AppLoginPageConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;

/**
 * 根据登录页配置信息，刷新应用下的登录页资源信息：logo、背景、单点信息
 */
@Component
public class LoginPageConfigResourceRefreshListener implements
        ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    AppLoginPageConfigService appLoginPageConfigService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ServletContext servletContext;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (contextRefreshedEvent.getApplicationContext().getParent() == null) {
           /* AppLoginPageConfigEntity loginPageConfigEntity = appLoginPageConfigService.findOne();

            String loginResourcePath = servletContext.getRealPath("/") + "resources/pt/login";
            try {
                if (loginPageConfigEntity.getPageBackgroundImage() != null) {
                    String pageBackgroundImg = IOUtils.toString(
                            loginPageConfigEntity.getPageBackgroundImage().getCharacterStream());
                    BASE64Decoder decoder = new BASE64Decoder();
                    IOUtils.write(decoder.decodeBuffer(pageBackgroundImg), new FileOutputStream(
                            new File(
                                    loginResourcePath + File.separator + "def" + File.separator + "imgs" + File.separator
                                            + "body.png")));
                }

                if (loginPageConfigEntity.getPageLogo() != null) {
                    String pageLogo = IOUtils.toString(
                            loginPageConfigEntity.getPageLogo().getCharacterStream());
                    BASE64Decoder decoder = new BASE64Decoder();
                    IOUtils.write(decoder.decodeBuffer(pageLogo), new FileOutputStream(
                            new File(
                                    loginResourcePath + File.separator + "def" + File.separator + "imgs" + File.separator
                                            + "header.png")));
                }
            } catch (Exception e) {
                logger.error("更新登录页资源异常：", e);
            }*/
        }

    }


}
