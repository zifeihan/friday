@echo off

set APP_HOME=%cd%


if %cd%==%cd:~,3% echo 当前目录已经是%cd:~,1%盘的根目录！&goto end
cd..
set "bd=%cd%"
cd..
set "bbd=%cd%"
if "%bbd%"=="%bd%" (echo 上一级目录是： %cd:~,1%盘的根目录！
) else call set APP_HOME="%%bd:%bbd%\=%%"
:end

if defined JAVA_HOME (
 set _EXECJAVA="%JAVA_HOME%\bin\java"
)

if not defined JAVA_HOME (
 echo "JAVA_HOME not set."
 set _EXECJAVA=java
)

if not exist %JAVA_HOME%\jre\bin\attach.dll (copy %JAVA_HOME%\bin\attach.dll %JAVA_HOME%\jre\bin\attach.dll)

set DEBUG_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=28004,server=y,suspend=n"
%_EXECJAVA% %DEBUG_OPTS% -cp %APP_HOME%\lib\*;%JAVA_HOME%\lib\*;%JAVA_HOME%\bin\*;%JAVA_HOME%\jre\bin\* -Dapp.home=%APP_HOME% fun.codec.friday.starter.Controller