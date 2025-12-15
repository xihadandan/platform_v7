import com.wellsoft.pt.license.authentication.LicenseAuthenticator;
import com.wellsoft.pt.license.generator.LicenseGenerator;

import java.io.File;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年07月01日   chenq	 Create
 * </pre>
 */
public class Main {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // 项目的唯一识别码，例如机器码，从平台的系统设置->许可管理页面中获取
        String defaultSubject = "2c:fd:a1:ae:28:24";//args[0];
        String subject = getInputSubject(args, defaultSubject);
        // 生成许可证
        File licenseFile = create(subject);
        // 安装许可证
        install(subject, licenseFile);
        // 验证许可证
        verify(subject);
    }

    /**
     * @param args
     * @param defaultSubject
     * @return
     */
    private static String getInputSubject(String[] args, String defaultSubject) {
        if (args.length == 1) {
            return args[0];
        }
        return defaultSubject;
    }

    /**
     * @return
     * @throws Exception
     */
    private static File create(String subject) throws Exception {
        LicenseGenerator licenseGenerator = new LicenseGenerator(subject, "/license-generator.properties");
        return licenseGenerator.create();
    }

    /**
     * @param licenseFile
     */
    private static void install(String subject, File licenseFile) {
        LicenseAuthenticator licenseAuthenticator = new LicenseAuthenticator(subject,
                "/license-authenticator.properties");
        licenseAuthenticator.install(licenseFile);
    }

    /**
     *
     */
    private static void verify(String subject) {
        LicenseAuthenticator licenseAuthenticator = new LicenseAuthenticator(subject,
                "/license-authenticator.properties");
        licenseAuthenticator.authenticate();
    }
}
