#### JAVA实时反编译工具

##### 初衷：在开发pinpoint,skywalking agent过程中有时候想看看修改后的字节码.

##### 打包方式：在目录下执行：mvn clean install -Dmaven.test.skip，

##### 启动方式：执行后打包命令后在starter/target后生成：friday-starter.tar.gz，解压后执行bin/run.sh即可

##### 原理：通过java agent方式调用Instrumentation#retransformClasses方法，保存JVM返回的字节码，然后调用cfr将其反编译为JAVA类

##### 声明：只在macOS系统下进行过测试，原理上应该能够支持windows，linux。因为获取进程号调用的是openjdk的tools.jar，目前暂不支持其他jdk，如openj9

##### 更新：2021.4.20 添加搜索窗口，为了方便只能使用类全名搜索，希望以后搜索之后能够在左边的树结构中定位到。

![start.png (2880×1800)](https://raw.githubusercontent.com/zifeihan/friday/master/doc/start.png)

#### 1.Decompiler pinpoint agent

![pinpoint-runtime.png (2880×1800)](https://raw.githubusercontent.com/zifeihan/friday/master/doc/pinpoint-runtime.png)

#### 2.Decompiler skywalking agent

![skywalking-runtime.png (2880×1800)](https://raw.githubusercontent.com/zifeihan/friday/master/doc/skywalking-runtime.png)

![skywalking-runtime-proxy.png (2880×1800)](https://raw.githubusercontent.com/zifeihan/friday/master/doc/skywalking-runtime-proxy.png)

#### Search every class in your jvm

![search.png (2880×1800)](https://raw.githubusercontent.com/zifeihan/friday/master/doc/search.png)