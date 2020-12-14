package fun.codec.friday.agent.transformer;

import fun.codec.friday.agent.SystemInfo;
import fun.codec.friday.agent.log.SampleLogger;
import fun.codec.friday.agent.util.RuntimeMXBeanUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class DefaultClassTransformer implements ClassFileTransformer {

    private final SampleLogger logger = SampleLogger.getLogger(getClass().getName());

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (null != classfileBuffer) {
            try {
                File tmpFile = new File(SystemInfo.getClazzPath(RuntimeMXBeanUtils.getPid()) + File.separator + className.replaceAll("/", ".") + ".class");
                if (!tmpFile.exists()) {
                    String parent = tmpFile.getParent();
                    File file = new File(parent);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    tmpFile.createNewFile();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(tmpFile);
                fileOutputStream.write(classfileBuffer);
                fileOutputStream.flush();
                logger.info(String.format("transform %s success", className));
            } catch (Throwable e) {
                logger.warn(String.format("transform %s error", className), e);
            }
        }
        return classfileBuffer;
    }
}
