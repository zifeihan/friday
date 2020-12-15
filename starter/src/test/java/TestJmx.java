//import com.sun.tools.attach.VirtualMachine;
//
//import java.io.File;
//import java.util.Properties;
//
//public class TestJmx {
//    public static void main(String[] args) {
//        String jmxAddress = getJMXAddress(98086);
//        System.out.println(jmxAddress);
//
//    }
//
//    private static String getJMXAddress(int pid) {
//        String address = null;
//        try {
//            VirtualMachine virtualMachine = VirtualMachine.attach(String.valueOf(pid));
//            Properties systemProperties = virtualMachine.getSystemProperties();
//            String javaHome = systemProperties.getProperty("java.home");
//            String jmxAgent = javaHome + File.separator + "lib" + File.separator + "management-agent.jar";
//            virtualMachine.loadAgent(jmxAgent, "com.sun.management.jmxremote");
//            Properties agentProperties = virtualMachine.getAgentProperties();
//            address = (String) agentProperties.get("com.sun.management.jmxremote.localConnectorAddress");
//            virtualMachine.detach();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return address;
//    }
//}
