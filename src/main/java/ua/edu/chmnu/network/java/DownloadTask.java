package ua.edu.chmnu.network.java;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class DownloadTask implements Runnable {
    private String fileURL;
    private String saveFilePath;
    private JTextArea logArea;
    private volatile boolean isStopped = false;
    private long downloadedBytes = 0;

    public DownloadTask(String fileURL, String saveFilePath, JTextArea logArea) {
        this.fileURL = fileURL;
        this.saveFilePath = saveFilePath;
        this.logArea = logArea;
    }

    @Override
    public void run() {
        try {

            logArea.append("URL: " + fileURL + "\n");
            logArea.append("Saving to: " + saveFilePath + "\n");

            URL url = new URL(fileURL);

            URLConnection connection = url.openConnection();

            connection.connect();

            int fileSize = connection.getContentLength();
            if (fileSize == -1) {
                logArea.append("Warning: File size is not provided by the server. Proceeding without knowing the size.\n");
            }

            InputStream inputStream = connection.getInputStream();
            File outputFile = new File(saveFilePath);

            if (outputFile.exists()) {
                logArea.append("File already exists: " + saveFilePath + "\n");
            } else {
                logArea.append("Creating new file...\n");
            }

            RandomAccessFile file = new RandomAccessFile(outputFile, "rw");
            if (fileSize != -1) {
                file.setLength(fileSize);
            }

            byte[] buffer = new byte[1024];
            int bytesRead;
            long totalBytesRead = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1 && !isStopped) {
                file.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                logArea.append("Downloaded " + totalBytesRead + " bytes...\n");
            }

            inputStream.close();
            file.close();

            if (!isStopped) {
                logArea.append("Download complete!\n");
            }
        } catch (IOException e) {
            logArea.append("Error downloading file: " + e.getMessage() + "\n");
        }
    }

    public void stopDownload() {
        isStopped = true;
    }

    public void resumeDownload() {
        isStopped = false;
        new Thread(this).start();
    }
}
