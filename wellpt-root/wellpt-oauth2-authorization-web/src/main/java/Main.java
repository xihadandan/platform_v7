import org.apache.catalina.LifecycleException;

import javax.servlet.ServletException;


public class Main {

    private static int PORT = 7080;

    public static void main(String[] args) throws LifecycleException,
            ServletException, Exception {
        if (args != null && args.length > 0) {
            PORT = Integer.parseInt(args[0]);
        }
        new EmbededTomcat(PORT).start();
    }
}