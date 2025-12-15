package com.wellsoft.pt.repository.convert.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.ResourceBundle;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年07月17日   chenq	 Create
 * </pre>
 */
public class SpireLicenseInstall {

    private static Logger logger = LoggerFactory.getLogger(DocumentToPdfUtils.class);
    private static boolean installed = false;


    /**
     * 获取并设置许可证信息
     */
    public static void installLicense() {
        if (!installed) {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("config/system-spire-license");
            String licenseKey = resourceBundle.containsKey("spire.license.key") ? resourceBundle.getString("spire.license.key") : null;
            if (StringUtils.isBlank(licenseKey) && resourceBundle.containsKey("spire.license.path")) {
                try {
                    licenseKey = IOUtils.toString(new FileInputStream(new File(resourceBundle.getString("spire.license.path"))));
                } catch (Exception e) {
                    logger.error("spire 证书文件读取失败: ", e);
                }
            }

            if (StringUtils.isNotBlank(licenseKey)) {
                // 新版本授权码加载
                com.spire.doc.license.LicenseProvider.setLicenseKey(licenseKey);
                com.spire.xls.license.LicenseProvider.setLicenseKey(licenseKey);
                com.spire.pdf.license.LicenseProvider.setLicenseKey(licenseKey);
                com.spire.presentation.license.LicenseProvider.setLicenseKey(licenseKey);
                com.spire.barcode.license.LicenseProvider.setLicenseKey(licenseKey);
                com.spire.ocr.license.LicenseProvider.setLicenseKey(licenseKey);
                com.spire.doc.license.LicenseProvider.loadLicense();
                com.spire.xls.license.LicenseProvider.loadLicense();
                com.spire.pdf.license.LicenseProvider.loadLicense();
                com.spire.presentation.license.LicenseProvider.loadLicense();
                com.spire.barcode.license.LicenseProvider.loadLicense();
                com.spire.ocr.license.LicenseProvider.loadLicense();
                logger.info("------------------------ Spire License Key loaded ------------------------");
            } else {
                logger.warn("------------------------ Spire License Key not load ------------------------");
            }
            installed = true;
        }
    }
}
