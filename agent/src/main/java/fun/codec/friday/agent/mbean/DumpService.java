package fun.codec.friday.agent.mbean;

import fun.codec.friday.agent.SystemInfo;
import fun.codec.friday.agent.log.SampleLogger;
import fun.codec.friday.agent.tree.Clazz;
import fun.codec.friday.agent.tree.Package;
import fun.codec.friday.agent.util.RuntimeMXBeanUtils;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.Map;

public class DumpService implements DumpServiceMBean {

    private String clazz;
    private Instrumentation instrumentation;
    private Map<String, ClassLoader> classClassLoaderMap = new HashMap<>();
    private static final SampleLogger logger = SampleLogger.getLogger(DumpService.class.getName());

    public DumpService(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
        Class[] allLoadedClasses = instrumentation.getAllLoadedClasses();
        Package dir = new Package("root");
        for (Class clazz : allLoadedClasses) {
            classClassLoaderMap.put(clazz.getName(), clazz.getClassLoader());
            String name = clazz.getName();
            add(name, dir);
        }
        File file = new File(SystemInfo.WORK_SPACE + File.separator + "dir");
        if (file.exists()) {
            file.delete();
        } else {
            file.mkdirs();
        }
        dumpClazz(dir, file);
        logger.info("dump class tree success.");
    }

    public static void add(String className, Package parent) {
        if (className.startsWith("[")) {
            return;
        }
        String[] split = className.split("\\.");

        for (int i = 0; i < split.length - 1; i++) {
            String dirName = split[i];
            Map<String, Package> childList = parent.getChildList();
            Package dir = childList.get(dirName);
            if (null == dir) {
                Package newDir = new Package(dirName);
                childList.put(dirName, newDir);
                parent = newDir;
            } else {
                parent = dir;
            }
        }
        parent.getChildList().put(split[split.length - 1], new Clazz(className + ".class"));
    }

    private static void dumpClazz(Package pck, File dir) {
        for (Map.Entry<String, Package> entry : pck.getChildList().entrySet()) {
            Package value = entry.getValue();
            try {
                File file = new File(dir.getAbsolutePath() + File.separator + entry.getKey());
                if (value instanceof Clazz) {
                    try {
                        file.createNewFile();
                    } catch (Exception e) {
                    }
                } else {
                    file.mkdirs();
                    dumpClazz(value, file);
                }
            } catch (Exception e) {
                logger.warn("dump clazz error", e);
            }
        }
    }

    @Override
    public String setClazz(String clazz) {
        this.clazz = clazz;

        try {
            ClassLoader classLoader = classClassLoaderMap.get(clazz);
            Class<?> redefineClass = null == classLoader ? ClassLoader.getSystemClassLoader().loadClass(clazz) : classLoader.loadClass(clazz);
            instrumentation.retransformClasses(redefineClass);
            return new File(SystemInfo.getClazzPath(RuntimeMXBeanUtils.getPid()) + File.separator + clazz + ".class").getAbsolutePath();
        } catch (Exception e) {
            logger.warn("rmi call setClazz error", e);
            return null;
        }
    }

    @Override
    public String toString() {
        return "DumpService{" +
                "clazz='" + clazz + '\'' +
                '}';
    }
}
