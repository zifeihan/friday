package fun.codec.friday.agent.tree;

import java.util.HashMap;
import java.util.Map;

public class Package {
    private String packageName;
    private Map<String, Package> childList = new HashMap<>();

    public Package(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Map<String, Package> getChildList() {
        return childList;
    }

    public void setChildList(Map<String, Package> childList) {
        this.childList = childList;
    }

    @Override
    public String toString() {
        return "Package{" +
                "packageName='" + packageName + '\'' +
                ", childList=" + childList +
                '}';
    }
}
