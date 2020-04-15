package fun.codec.friday.starter.util.machine;


import java.util.Objects;

public class Process implements Comparable<Process> {
    /**
     * 进程PID
     */
    private final String pid;
    /**
     * 启动类名称
     */
    private final String displayName;

    /**
     * 用户启动目录
     */
    private final String userDir;

    /**
     * jvm参数
     */
    private final String vmArgs;

    /**
     * 启动时间
     */
    private final long startTime;

    public Process(String pid, String displayName, String userDir, String vmArgs, long startTime) {
        this.pid = pid;
        this.displayName = displayName;
        this.userDir = userDir;
        this.vmArgs = vmArgs;
        this.startTime = startTime;
    }

    public String getPid() {
        return pid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUserDir() {
        return userDir;
    }

    public String getVmArgs() {
        return vmArgs;
    }

    public long getStartTime() {
        return startTime;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        return Objects.equals(pid, ((Process) other).pid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pid);
    }

    @Override
    public String toString() {
        return "Process{" +
                "pid='" + pid + '\'' +
                ", displayName='" + displayName + '\'' +
                ", userDir='" + userDir + '\'' +
                ", vmArgs='" + vmArgs + '\'' +
                ", startTime=" + startTime +
                '}';
    }

    @Override
    public int compareTo(Process o) {
        return pid.compareTo(o.getPid());
    }


}
