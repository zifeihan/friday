package fun.codec.friday.starter;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.ManagementFactory;

public class JMX {
    public static void main(String[] args) throws IOException {

        try {
            ObjectName serverName = new ObjectName("fun.codec.friday:type=DumpService");
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            if (!mbs.isRegistered(serverName)) {
                JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:" + 10990 + "/" + "fridayServer");
                System.out.println("JMXServiceURL: " + url.toString());
                JMXConnectorServer jmxConnServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);
                jmxConnServer.start();
            }
            mbs.getMBeanInfo(serverName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
