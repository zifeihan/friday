![Total visitors](https://komarev.com/ghpvc/?username=zifeihan&color=blue)

### JAVA实时反编译-命令行版本

#### 1. 如果是使用下载的 extractor.jar 使用类似如下启动命令:
```
java -cp extractor.jar:/Users/zifeihan/program/jdk1.8.0_181.jdk/Contents/Home/lib/tools.jar fun.codec.friday.extractor.ClassFileExtractor 76420 fun.codec.at.AT

1. 注意需要在 classpath 中指定jdk的 tools.jar
2. fun.codec.friday.extractor.ClassFileExtractor 启动类的名字
3. 76420 目标进程号
4. fun.codec.at.AT 需要导出的java类名(注意需要包含包名)
```
#### 2. 如果是在自己电脑上编译的 extractor.jar ,且在本地电脑上使用时, 可以使用如下简化命令:
```
java -jar extractor.jar 75345 fun.codec.at.AT

1. 76420 目标进程号
2. fun.codec.at.AT 需要导出的java类名(注意需要包含包名)
注意: jar 包中指定了启动类, 也指定了classpath 指向 tools.jar
```