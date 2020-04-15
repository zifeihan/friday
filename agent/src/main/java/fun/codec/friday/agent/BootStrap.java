package fun.codec.friday.agent;

import fun.codec.friday.agent.log.SampleLogger;
import fun.codec.friday.agent.mbean.DumpService;
import fun.codec.friday.agent.mbean.DumpServiceMBean;
import fun.codec.friday.agent.transformer.DefaultClassTransformer;

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

    private static final SampleLogger logger = SampleLogger.getLogger(BootStrap.class.getName());

    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        instrumentation.addTransformer(new DefaultClassTransformer(), true);
        addJarToBootStrapClassLoader(instrumentation);
        start(instrumentation);
    }

    private static void addJarToBootStrapClassLoader(Instrumentation instrumentation) {
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
            logger.warn("add agentJar to BootStrapClassLoader error.message:", e);
        }
    }

    private static void start(Instrumentation instrumentation) {
        try {
            ObjectName serverName = new ObjectName("fun.codec.friday:type=DumpService");
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            if (!mbs.isRegistered(serverName)) {
                DumpServiceMBean dumpMBean = new DumpService(instrumentation);
                mbs.registerMBean(dumpMBean, serverName);
            }
            mbs.getMBeanInfo(serverName);
            logger.info("start MBeanServer success.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
