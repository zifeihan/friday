package fun.codec.friday.starter;

import com.sun.tools.attach.VirtualMachine;
import fun.codec.friday.agent.BootStrap;
import fun.codec.friday.agent.SystemInfo;
import fun.codec.friday.agent.util.EFile;
import fun.codec.friday.starter.util.machine.LocalMachineSource;
import fun.codec.friday.starter.util.machine.MachineListener;
import fun.codec.friday.starter.util.machine.Process;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.benf.cfr.reader.Main;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Controller extends Application {

    private static Logger logger = LoggerFactory.getLogger(Controller.class);

    public static void main(String[] args) {
        launch(args);
    }

    public int pid;

    private CodeArea codeArea;

    private MenuButton menuButton;

    private TextField searchField;

    private TreeView treeView;

    private static final String[] KEYWORDS = new String[] {
        "abstract",
        "assert",
        "boolean",
        "break",
        "byte",
        "case",
        "catch",
        "char",
        "class",
        "const",
        "continue",
        "default",
        "do",
        "double",
        "else",
        "enum",
        "extends",
        "final",
        "finally",
        "float",
        "for",
        "goto",
        "if",
        "implements",
        "import",
        "instanceof",
        "int",
        "interface",
        "long",
        "native",
        "new",
        "package",
        "private",
        "protected",
        "public",
        "return",
        "short",
        "static",
        "strictfp",
        "super",
        "switch",
        "synchronized",
        "this",
        "throw",
        "throws",
        "transient",
        "try",
        "void",
        "volatile",
        "while"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
        "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        HBox top_HBox = new HBox(8);
        MenuButton button = new MenuButton("Monitor");
        this.menuButton = button;
        this.searchField = textField();
        top_HBox.getChildren().addAll(button, searchField);

        TreeView<String> treeView = new TreeView<>();
        this.treeView = treeView;

        treeView.setRoot(null);
        treeView.setShowRoot(false);
        treeView.setMinWidth(250);
        treeView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue.isLeaf()) {
                        if (newValue instanceof FileTreeItem) {
                            String absolutePath = ((FileTreeItem) newValue).getFile().getAbsolutePath();
                            String filePath = absolutePath.substring(
                                absolutePath.indexOf("dir") + "dir".length() + File.separator.length());
                            String clazz = filePath.replaceAll(File.separator, ".");
                            showClazz(clazz);
                        }
                    }
                });

        this.codeArea = new CodeArea();
        // add line numbers to the left of area
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        // recompute the syntax highlighting 500 ms after user stops editing area
        Subscription cleanupWhenNoLongerNeedIt = codeArea

            // plain changes = ignore style changes that are emitted when syntax highlighting is reapplied
            // multi plain changes = save computation by not rerunning the code multiple times
            //   when making multiple changes (e.g. renaming a method at multiple parts in file)
            .multiPlainChanges()

            // do not emit an event until 500 ms have passed since the last emission of previous stream
            .successionEnds(Duration.ofMillis(500))

            // run the following code block when previous stream emits an event
            .subscribe(ignore -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText())));

        // when no longer need syntax highlighting and wish to clean up memory leaks
        // run: `cleanupWhenNoLongerNeedIt.unsubscribe();`

        // auto-indent: insert previous line's indents on enter
        final Pattern whiteSpace = Pattern.compile("^\\s+");
        codeArea.addEventHandler(KeyEvent.KEY_PRESSED, KE ->
        {
            if (KE.getCode() == KeyCode.ENTER) {
                int caretPosition = codeArea.getCaretPosition();
                int currentParagraph = codeArea.getCurrentParagraph();
                Matcher m0 = whiteSpace.matcher(codeArea.getParagraph(currentParagraph - 1).getSegments().get(0));
                if (m0.find())
                    Platform.runLater(() -> codeArea.insertText(caretPosition, m0.group()));
            }
        });

        codeArea.replaceText(0, 0, "");
        codeArea.getStylesheets().add(Controller.class.getResource("/java-keywords.css").toExternalForm());
        codeArea.setEditable(false);

        root.setTop(top_HBox);
        root.setLeft(treeView);
        root.setCenter(new StackPane(new VirtualizedScrollPane<>(codeArea)));
        root.setPadding(new Insets(10));
        BorderPane.setMargin(top_HBox, new Insets(0, 0, 10, 0));
        BorderPane.setMargin(treeView, new Insets(0, 0, 0, 0));
        stage.setScene(new Scene(root));
        stage.setWidth(1000);
        stage.setHeight(600);
        stage.setTitle("Java Decompiler");
        stage.show();

        monitorProcess();

        stage.setOnCloseRequest(e -> {
            System.exit(0);
        });
    }

    private TextField textField() {
        TextField textField = new TextField();
        textField.setDisable(true);
        textField.setText("Search class in this jvm");
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            String text = newValue.trim();
            if (text.length() > 0) {
                doSearch(text);
            }
        });
        return textField;
    }

    private void doSearch(String keyword) {
        showClazz(keyword);
    }

    public void showClazz(String clazz) {
        String body = "Not Found this class:" + clazz;
        try {
            invokeDumpClazz(pid, clazz);
            String path = SystemInfo.getClazzPath(pid) + File.separator + clazz + ".class";
            final File clazzFile = new File(path);
            if (clazzFile.exists()) {
                Main.main(new String[] {
                    path,
                    "--outputdir",
                    SystemInfo.getDumpPath(pid)
                });
                String file = SystemInfo.getDumpPath(pid) + File.separator + clazz.replace(
                    ".", File.separator) + ".java";
                body = EFile.readFile(file);
            }
        } catch (Exception e) {
            logger.error("Not Found this class" + clazz, e);
        }
        codeArea.clear();
        codeArea.replaceText(0, 0, body);
    }

    private Object invokeDumpClazz(Integer pid, String clazz) {
        try {
            MBeanServerConnection serverConnection = getLocalMBeanServerConnectionStatic(pid);
            ObjectName serverName = new ObjectName("fun.codec.friday:type=DumpService");
            return serverConnection.invoke(
                serverName, "setClazz", new String[] {clazz}, new String[] {"java.lang.String"});
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static MBeanServerConnection getLocalMBeanServerConnectionStatic(int pid) {
        try {
            JMXServiceURL jmxUrl = new JMXServiceURL(getJMXAddress(pid));
            return JMXConnectorFactory.connect(jmxUrl).getMBeanServerConnection();
        } catch (IOException e) {
            throw new RuntimeException("Of course you still have to implement a good connection handling");
        }
    }

    private static String getJMXAddress(int pid) {
        String address = null;
        try {
            VirtualMachine virtualMachine = VirtualMachine.attach(String.valueOf(pid));
            Properties systemProperties = virtualMachine.getSystemProperties();
            String javaHome = systemProperties.getProperty("java.home");
            String jmxAgent = javaHome + File.separator + "lib" + File.separator + "management-agent.jar";
            virtualMachine.loadAgent(jmxAgent, "com.sun.management.jmxremote");
            Properties agentProperties = virtualMachine.getAgentProperties();
            address = (String) agentProperties.get("com.sun.management.jmxremote.localConnectorAddress");
            virtualMachine.detach();
        } catch (Exception e) {
            logger.error("Get JMXAddress error, message:", e);
        }
        return address;
    }

    private void monitorProcess() {
        LocalMachineSource localMachineSource = new LocalMachineSource(logger, new MachineListener() {
            @Override
            public void onNewMachine(Process machine) {
                MenuItem menuItem = new MenuItem(machine.getPid() + ":" + machine.getDisplayName());
                menuItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        try {
                            MenuItem source = (MenuItem) event.getSource();
                            String text = source.getText();
                            pid = Integer.parseInt(text.substring(0, text.indexOf(":")));

                            VirtualMachine vm = VirtualMachine.attach(String.valueOf(pid));
                            vm.loadAgent(getJarFile());

                            FileTreeItem treeItem = new FileTreeItem(new File(SystemInfo.getTreePath(pid)), f -> {
                                File[] directorFiles = f.listFiles(File::isDirectory);
                                List<File> list = new ArrayList<>(Arrays.asList(directorFiles));
                                return list.toArray(directorFiles);
                            });
                            treeView.setRoot(treeItem);
                            treeView.refresh();
                            menuButton.setText("Monitor" + String.format("(%s)", pid));

                            //enable searchField
                            searchField.setDisable(false);
                            searchField.clear();
                        } catch (Exception e) {
                            logger.error("Monitor Java process error, message:", e);
                        }

                    }
                });
                menuButton.getItems().add(menuItem);
            }

            @Override
            public void onClosedMachine(Process machine) {
                ObservableList<MenuItem> items = menuButton.getItems();
                Iterator<MenuItem> iterator = items.iterator();
                while (iterator.hasNext()) {
                    MenuItem menuItem = iterator.next();
                    String text = menuItem.getText();
                    if (text.substring(0, text.indexOf(":")).equals(machine.getPid())) {
                        iterator.remove();
                    }
                }
            }
        });
        localMachineSource.discoverVirtualMachines();
        localMachineSource.start();
    }

    private static String getJarFile() {
        String clazzName = BootStrap.class.getName().replace(".", File.separator) + ".class";
        URL resource = ClassLoader.getSystemClassLoader().getResource(clazzName);
        if (resource.getProtocol().equals("jar")) {
            int index = resource.getPath().indexOf("!/");
            if (index > -1) {
                String jarFile = resource.getPath().substring("file:".length(), index);
                //window的路径/d:/friday-starter的格式转换为d:\friday-starter
                if (System.getProperties().getProperty("os.name").toLowerCase().contains("window")) {
                    jarFile = jarFile.replace("!/", "\\").substring(1);
                    logger.info("find agent path:{}", jarFile);
                }
                return jarFile;
            }
        } else {
            return Controller.class.getClassLoader().getResource("lib/agent-1.0-SNAPSHOT.jar").getFile();
        }
        return null;
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass =
                matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("PAREN") != null ? "paren" :
                        matcher.group("BRACE") != null ? "brace" :
                            matcher.group("BRACKET") != null ? "bracket" :
                                matcher.group("SEMICOLON") != null ? "semicolon" :
                                    matcher.group("STRING") != null ? "string" :
                                        matcher.group("COMMENT") != null ? "comment" :
                                            null; /* never happens */
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}
