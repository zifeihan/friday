package fun.codec.friday.starter;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.util.function.Function;


public class FileTreeItem extends TreeItem<String> {

    //判断树节点是否被初始化，没有初始化为真
    private boolean notInitialized = true;

    private final File file;
    private final Function<File, File[]> supplier;
    private Controller controller;

    public FileTreeItem(File file) {
        super(file.getName());
        this.file = file;
        supplier = (File f) -> f.listFiles();
    }

    public FileTreeItem(Controller controller, File file, Function<File, File[]> supplier) {
        super(file.getName());
        this.file = file;
        this.supplier = supplier;
        this.controller = controller;
    }

    @Override
    public ObservableList<TreeItem<String>> getChildren() {
        ObservableList<TreeItem<String>> children = super.getChildren();
        //没有加载子目录时，则加载子目录作为树节点的孩子
        if (this.notInitialized && this.isExpanded()) {
            this.notInitialized = false;
            if (file.isDirectory()) {
                for (File f : supplier.apply(file)) {
                    if (f == null) {
                        System.out.println(1);
                    }
                    children.add(new FileTreeItem(f));
                }

            }
        }
        return children;
    }

    @Override
    public boolean isLeaf() {
        return file.isFile();
    }

    public File getFile() {
        return file;
    }
}