package fun.codec.friday.agent.util.machine;


/**
 * 上下线通知器
 *
 * @author echo
 */
public interface MachineListener {

    /**
     * 机器上线
     *
     * @param machine
     */
    default void onNewMachine(Process machine) {

    }

    /**
     * 机器下线通知
     *
     * @param machine
     */
    default void onClosedMachine(Process machine) {

    }

}
