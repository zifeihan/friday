##### 初衷：在开发pinpoint,skywalking agent过程中有时候想看看修改后的字节码.

##### 打包方式：在目录下执行：mvn clean install -Dmaven.test.skip，

##### 启动方式：执行后打包命令后在starter/target后生成：friday-starter.tar.gz，解压后执行bin/run.sh即可

##### 声明：只在macOS系统下进行过测试，原理上应该能够支持windows，linux

![start.png (2880×1800)](https://raw.githubusercontent.com/zifeihan/friday/master/doc/start.png)

#### 1.Decompiler pinpoint agent

![pinpoint-runtime.png (2880×1800)](https://raw.githubusercontent.com/zifeihan/friday/master/doc/pinpoint-runtime.png)

#### 2.Decompiler skywalking agent

![skywalking-runtime.png (2880×1800)](https://raw.githubusercontent.com/zifeihan/friday/master/doc/skywalking-runtime.png)

![skywalking-runtime-proxy.png (2880×1800)](https://raw.githubusercontent.com/zifeihan/friday/master/doc/skywalking-runtime-proxy.png)
