import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


public class EmbededTomcat {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Tomcat tomcat = new Tomcat();

    //tomcat 监听端口
    private int port;


    public EmbededTomcat(int port) {
        this.port = port;
    }

    public void start() throws Exception {

        //设置端口
        tomcat.setPort(port);

        try {

            //设置项目访问路径
            String contextPath = "";

            //设置项目文件路径
            String docBase = new File("").getAbsolutePath() + "/src/main/webapp/";
            logger.info("docBase = {}", docBase);
            tomcat.addWebapp(contextPath, docBase);

            //启动tomcat
            tomcat.start();

            logger.info("Tomcat started.");

            //监听关闭端口，阻塞式。没有这一句，方法执行完后会直接结束
            tomcat.getServer().await();

        } catch (LifecycleException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }

    }

    public void stop() throws Exception {

        tomcat.stop();

        logger.info("Tomcat shutdown");
    }


    public static class Shutdown extends Thread {

        private EmbededTomcat tomcat;

        public Shutdown(EmbededTomcat tomcat) {

            this.tomcat = tomcat;
        }

        @Override
        public void run() {

            System.out.println("shutdown is running");

            try {
                Thread.sleep(15 * 1000);      //15秒后执行
                tomcat.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}