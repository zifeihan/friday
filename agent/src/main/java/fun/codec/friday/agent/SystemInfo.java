package fun.codec.friday.agent;

import fun.codec.friday.agent.util.RuntimeMXBeanUtils;

import java.io.File;

public class SystemInfo {
    public static String HOME_PATH = System.getProperty("user.home") + File.separator + "friday";

    public static String WORK_SPACE = HOME_PATH + File.separator + RuntimeMXBeanUtils.getPid();

    public static String getTreePath(int pid) {
        return HOME_PATH + File.separator + pid + File.separator + "dir";
    }

    public static String getClazzPath(int pid) {
        return HOME_PATH + File.separator + pid + File.separator + "class";
    }

    public static String getDumpPath(int pid) {
        return HOME_PATH + File.separator + pid + File.separator + "dump";
    }
}
