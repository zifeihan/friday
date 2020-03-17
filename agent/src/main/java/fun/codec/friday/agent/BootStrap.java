package fun.codec.friday.agent;

import fun.codec.friday.agent.mbean.DumpService;
import fun.codec.friday.agent.mbean.DumpServiceMBean;
import fun.codec.friday.agent.transformer.DefaultClassTransformer;
import fun.codec.friday.agent.util.EFile;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.jar.JarFile;

/**
 * @author echo
 */
public class BootStrap {

    {
        File file = new File(SystemInfo.WORK_SPACE);
        if (file.exists()) {
            EFile.deleteFile(file);
        } else {
            file.mkdirs();
        }
    }

    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        instrumentation.addTransformer(new DefaultClassTransformer(), true);
        addJarToBootStrapClassLoad(instrumentation);
        start(instrumentation);
    }

    private static void addJarToBootStrapClassLoad(Instrumentation instrumentation) {
        try {
            String clazzName = BootStrap.class.getName().replace(".", "/") + ".class";
            URL resource = ClassLoader.getSystemClassLoader().getResource(clazzName);
            if (resource.getProtocol().equals("jar")) {
                int index = resource.getPath().indexOf("!/");
                if (index > -1) {
                    String jarFile = resource.getPath().substring("file:".length(), index);
                    instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(new File(jarFile)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void start(Instrumentation instrumentation) {
        try {
            ObjectName serverName = new ObjectName("fun.codec.friday:type=DumpService");
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            try {
                mbs.getMBeanInfo(serverName);
            } catch (InstanceNotFoundException e) {
                DumpServiceMBean dumpMBean = new DumpService(instrumentation);
                mbs.registerMBean(dumpMBean, serverName);
                System.out.println("Waiting...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
