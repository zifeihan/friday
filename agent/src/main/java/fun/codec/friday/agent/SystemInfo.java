package fun.codec.friday.agent;

import fun.codec.friday.agent.util.RuntimeMXBeanUtils;

import java.io.File;

public class SystemInfo {

    public static String WORK_SPACE = System.getProperty("user.home") + File.separator + "friday" + File.separator + RuntimeMXBeanUtils.getPid();
}
