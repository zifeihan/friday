package fun.codec.friday.extractor;

import com.sun.tools.attach.VirtualMachine;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Arrays;
import org.benf.cfr.reader.Main;

public class ClassFileExtractor {

    public static void agentmain(String cmdline, Instrumentation inst) throws Exception {

        Class<?>[] classes = Arrays.stream(inst.getAllLoadedClasses())
                                   .filter(c -> c.getName().startsWith(cmdline) && !c.isArray() && inst.isModifiableClass(c))
                                   .toArray(Class[]::new);

        ClassFileTransformer extractor = new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> cls, ProtectionDomain pd, byte[] classBytes) {
                if (null != classBytes) {
                    try {
                        File tmpFile = new File(SystemInfo.WORK_SPACE + File.separator + className.replace('.', '/') + ".class");
                        if (!tmpFile.exists()) {
                            String parent = tmpFile.getParent();
                            File file = new File(parent);
                            if (!file.exists()) {
                                file.mkdirs();
                            }
                            tmpFile.createNewFile();
                        }
                        FileOutputStream fileOutputStream = new FileOutputStream(tmpFile);
                        fileOutputStream.write(classBytes);
                        fileOutputStream.flush();

                        String clazzName = className.replaceAll("/", ".");
                        System.out.println(String.format("[FRIDAY] dump class: %s -> %s", clazzName, tmpFile.getAbsolutePath()));
                        decompile(clazzName);
                    } catch (Throwable e) {
                        System.out.println(String.format("[FRIDAY] dump class: %s error %s", className, e.getMessage()));
                    }
                }
                return classBytes;
            }
        };

        inst.addTransformer(extractor, true);
        try {
            inst.retransformClasses(classes);
        } finally {
            inst.removeTransformer(extractor);
        }
    }

    private static void decompile(String className) {
        try {
            String clazzPath = SystemInfo.WORK_SPACE + File.separator + className.replace('.', '/') + ".class";
            Main.main(new String[] {
                clazzPath,
                "--outputdir",
                SystemInfo.WORK_SPACE
            });
            System.out.println(String.format("[FRIDAY] decompile class: %s -> %s", className, clazzPath.replace(".class",".java")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        String url = ClassFileExtractor.class.getProtectionDomain().getCodeSource().getLocation().toString();
        if (!url.startsWith("file:/") || !url.endsWith(".jar")) {
            System.out.println("Must be a JAR file");
            System.exit(1);
        }
        String jar = url.substring(5);
        if (args.length < 2) {
            System.out.println("Usage: java -jar " + jar + " <pid> [prefix]");
            System.exit(1);
        }

        VirtualMachine vm = VirtualMachine.attach(args[0]);
        String cmdline = args.length > 2 ? args[1] + ' ' + args[2] : args[1];
        try {
            vm.loadAgent(jar, cmdline);
        } finally {
            vm.detach();
        }

        System.out.println("Done");
    }
}