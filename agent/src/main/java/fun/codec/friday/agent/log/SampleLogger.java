/*
 * Copyright 2016 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package fun.codec.friday.agent.log;

import fun.codec.friday.agent.SystemInfo;

import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;

/**
 * @author Woonduk Kang(emeroad)
 */
public final class SampleLogger {

    /**
     * 日志格式信息
     */
    private final String messagePattern;

    private static PrintStream printStream;

    private static final SampleLogger logger = SampleLogger.getLogger(SampleLogger.class.getName());

    static {
        try {
            String agentLogErrorFileStr = SystemInfo.getLogDir() + File.separator + "friday.log";
            File agentLogErrorFile = new File(agentLogErrorFileStr);
            if (!agentLogErrorFile.exists()) {
                agentLogErrorFile.createNewFile();
            }
            printStream = new PrintStream(agentLogErrorFile);
        } catch (Exception e) {
            logger.warn("init log error:", e);
        }
    }


    public SampleLogger(String loggerName) {
        if (loggerName == null) {
            throw new NullPointerException("loggerName must not be null");
        }
        this.messagePattern = "{0,date,yyyy-MM-dd HH:mm:ss} [{1}] (" + loggerName + ") {2}{3}";
    }

    public static SampleLogger getLogger(String loggerName) {
        return new SampleLogger(loggerName);
    }

    private String format(String logLevel, String msg, String exceptionMessage) {
        exceptionMessage = defaultString(exceptionMessage, "");

        MessageFormat messageFormat = new MessageFormat(messagePattern);
        final long date = System.currentTimeMillis();
        Object[] parameter = {date, logLevel, msg, exceptionMessage};
        return messageFormat.format(parameter);
    }

    public boolean isInfoEnabled() {
        return true;
    }

    public void info(String msg) {
        String formatMessage = format("INFO", msg, "");
        printStream.println(formatMessage);
    }

    public boolean isWarnEnabled() {
        return true;
    }

    public void warn(String msg) {
        warn(msg, null);
    }

    public void warn(String msg, Throwable throwable) {
        String exceptionMessage = toString(throwable);
        String formatMessage = format("WARN", msg, exceptionMessage);
        printStream.println(formatMessage);
    }

    private String toString(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println();
        throwable.printStackTrace(pw);
        pw.close();
        return sw.toString();
    }

    private String defaultString(String exceptionMessage, String defaultValue) {
        if (exceptionMessage == null) {
            return defaultValue;
        }
        return exceptionMessage;
    }
}
