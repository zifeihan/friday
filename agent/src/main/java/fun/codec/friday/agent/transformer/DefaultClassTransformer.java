package fun.codec.friday.agent.transformer;

import fun.codec.friday.agent.SystemInfo;
import org.benf.cfr.reader.Main;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class DefaultClassTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (null != classBeingRedefined) {
            try {
                File tmpFile = new File(SystemInfo.WORK_SPACE + File.separator + "class" + File.separator + className + ".class");
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
                Main.main(new String[]{
                        tmpFile.getAbsolutePath(),
                        "--outputdir",
                        SystemInfo.WORK_SPACE + File.separator + "dump"
                });
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return classfileBuffer;
    }
}
