package ua.edu.chmnu.network.java;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class FileUtils {

    public static void ensureDirectory(String saveDir) {
        File dir = new File(saveDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static boolean isFileUpToDate(String saveFilePath, String fileURL) {
        File file = new File(saveFilePath);
        if (file.exists()) {
            try {
                URL url = new URL(fileURL);
                URLConnection connection = url.openConnection();
                connection.setRequestProperty("Range", "bytes=0-0");
                connection.connect();
                int contentLength = connection.getContentLength();
                return contentLength == file.length();
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }
}
