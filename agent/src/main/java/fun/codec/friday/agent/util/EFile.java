package fun.codec.friday.agent.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class EFile {
    public static String readFile(String fileName) {
        File file = new File(fileName);
        byte[] bytes = loadFile(file, 0, file.length());
        return new String(bytes);
    }

    /**
     * 删除文件
     *
     * @param file 文件对象
     */
    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
                file.delete();
            }
        }
    }

    public static byte[] loadFile(File file, long beginPos, long endPos) {

        try {
            if (!file.exists()) {
                return null;
            }

            long fileSize = file.length();

            if (endPos > fileSize) {
                endPos = (int) fileSize;
            }

            if (beginPos < 0) {
                return null;
            }

            if (beginPos >= fileSize) {
                return null;
            }

            if (beginPos == endPos) {
                return null;
            }

            // 计算需要读取的差高难度
            long loadLength = 0;
            if (endPos < 0) {
                loadLength = (int) fileSize - beginPos + 1;
            } else {
                loadLength = endPos - beginPos + 1;
            }
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            byte[] fileBytes = new byte[(int) loadLength - 1];
            randomAccessFile.seek(beginPos);
            randomAccessFile.read(fileBytes);
            randomAccessFile.close();
            return fileBytes;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
