package fun.codec.friday.agent.tree;

public class Clazz extends Package {
    private String clazzName;

    public Clazz(String clazzName) {
        super(clazzName);
        this.setChildList(null);
        this.clazzName = clazzName;
    }

    public String getClazzName() {
        return clazzName;
    }

    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }

    @Override
    public String toString() {
        return "Clazz{" +
                "clazzName='" + clazzName + '\'' +
                '}';
    }
}
