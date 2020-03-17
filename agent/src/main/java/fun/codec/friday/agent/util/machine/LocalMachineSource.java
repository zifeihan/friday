package fun.codec.friday.agent.util.machine;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachineDescriptor;
import fun.codec.friday.agent.util.CollectionUtil;
import fun.codec.friday.agent.util.RuntimeMXBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 进程发现者
 *
 * @author echo
 */
public class LocalMachineSource {
    private static final String VM_ARGS = "sun.jvm.args";
    private static final String USER_DIR = "user.dir";
    private static final long DEFAULT_SLEEP_PERIOD = 5000;

    private final Logger logger;
    private MachineListener listener;
    private final long sleepPeriod;
    private final ThreadedAgent threadedAgent;

    private static Map<VirtualMachineDescriptor, Process> vmMap;

    public LocalMachineSource(final Logger logger, final MachineListener listener) {
        this(logger, listener, DEFAULT_SLEEP_PERIOD);
    }

    public LocalMachineSource(final Logger logger,
                              final MachineListener listener,
                              final long sleepPeriod) {
        this.logger = logger;
        this.listener = listener;
        this.sleepPeriod = sleepPeriod;
        vmMap = new HashMap<>();
        threadedAgent = new ThreadedAgent(
                LoggerFactory.getLogger(ThreadedAgent.class),
                this::discoverVirtualMachines);
    }

    @PostConstruct
    public void start() {
        threadedAgent.start();
    }

    public boolean discoverVirtualMachines() {
        poll();

        return sleep();
    }

    private boolean sleep() {
        try {
            Thread.sleep(sleepPeriod);
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    public static List<String> getPidList() {
        if (CollectionUtil.isNotEmpty(vmMap)) {
            Collection<Process> values = vmMap.values();
            if (CollectionUtil.isNotEmpty(values)) {
                return values.stream().map(Process::getPid).collect(Collectors.toList());
            }
        }
        return null;
    }

    private void poll() {
        Set<VirtualMachineDescriptor> current = new HashSet<>(com.sun.tools.attach.VirtualMachine.list());
        difference(current, vmMap.keySet(), this::onNewDescriptor);
        difference(new HashSet<>(vmMap.keySet()), current, this::onClosedDescriptor);
    }

    private void difference(
            Set<VirtualMachineDescriptor> left,
            Set<VirtualMachineDescriptor> right,
            Consumer<VirtualMachineDescriptor> action) {

        // TODO: only attach once per vm
        left.stream()
                .filter(vm -> !right.contains(vm))
                .forEach(action);
    }

    private void onNewDescriptor(VirtualMachineDescriptor descriptor) {
        Process vm = attach(descriptor);
        if (vm != null) {
            vmMap.put(descriptor, vm);
            listener.onNewMachine(vm);
        }
    }

    private void onClosedDescriptor(VirtualMachineDescriptor descriptor) {
        Process vm = vmMap.remove(descriptor);
        if (vm != null) {
            listener.onClosedMachine(vm);
        }
    }

    private Process attach(VirtualMachineDescriptor descriptor) {
        try {
            com.sun.tools.attach.VirtualMachine vm = com.sun.tools.attach.VirtualMachine.attach(descriptor);

            String vmArgs = vm.getAgentProperties().getProperty(VM_ARGS);
            String id = descriptor.id();
            String displayName = descriptor.displayName();
            String userDir = getUserDir(vm);

            return new Process(id, displayName, userDir, vmArgs, RuntimeMXBeanUtils.getVmStartTime());
        } catch (AttachNotSupportedException e) {
            logger.warn(e.getMessage());
        } catch (IOException e) {
            if (!noSuchProcess(e)) {
                logger.warn(e.getMessage(), e);
            }
        }
        return null;
    }

    private String getUserDir(com.sun.tools.attach.VirtualMachine vm) throws IOException {
        final String userDir = vm.getAgentProperties().getProperty(USER_DIR);
        if (userDir != null) {
            return userDir;
        }

        return vm.getSystemProperties().getProperty(USER_DIR);
    }

    private boolean noSuchProcess(IOException e) {
        return e.getMessage().contains("No such process");
    }

    @PreDestroy
    public void stop() {
        threadedAgent.stop();
    }
}
