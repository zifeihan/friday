//import javax.management.JMException;
//import javax.management.MBeanServer;
//import javax.management.remote.JMXConnectorServer;
//import javax.management.remote.JMXConnectorServerFactory;
//import javax.management.remote.JMXServiceURL;
//import java.lang.management.ManagementFactory;
//import java.rmi.registry.LocateRegistry;
//
//public class Main {
//    public static void main(String[] args) throws JMException, Exception {
//        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
//        LocateRegistry.createRegistry(8081);
//        JMXServiceURL url = new JMXServiceURL
//                ("service:jmx:rmi:///jndi/rmi://localhost:8081/jmxrmi");
//        JMXConnectorServer jcs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, server);
//        jcs.start();
//    }
//}