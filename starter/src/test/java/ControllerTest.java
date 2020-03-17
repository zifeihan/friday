import fun.codec.friday.starter.Controller;
import junit.framework.TestCase;

import javax.management.MBeanServerConnection;

public class ControllerTest extends TestCase {

    public void test1() {

        MBeanServerConnection localMBeanServerConnectionStatic = Controller.getLocalMBeanServerConnectionStatic(80191);
        System.out.println(localMBeanServerConnectionStatic);
    }
}
