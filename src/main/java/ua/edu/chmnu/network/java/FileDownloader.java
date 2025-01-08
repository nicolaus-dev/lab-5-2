package ua.edu.chmnu.network.java;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileDownloader extends JFrame {
    private JTextField urlField;
    private JTextField saveDirField;
    private JButton downloadButton, stopButton, resumeButton;
    private JTextArea logArea;
    private ExecutorService executorService;
    private DownloadTask currentTask;

    public FileDownloader() {
        setTitle("Multi-threaded File Downloader");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        urlField = new JTextField(40);
        saveDirField = new JTextField(40);
        downloadButton = new JButton("Download");
        stopButton = new JButton("Stop");
        resumeButton = new JButton("Resume");
        logArea = new JTextArea(10, 40);
        logArea.setEditable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Enter File URL:"));
        panel.add(urlField);
        panel.add(new JLabel("Enter Save Directory:"));
        panel.add(saveDirField);
        panel.add(downloadButton);
        panel.add(stopButton);
        panel.add(resumeButton);
        panel.add(new JScrollPane(logArea));

        add(panel, BorderLayout.CENTER);

        downloadButton.addActionListener(e -> startDownload());
        stopButton.addActionListener(e -> stopDownload());
        resumeButton.addActionListener(e -> resumeDownload());

        stopButton.setEnabled(false);
        resumeButton.setEnabled(false);

        executorService = Executors.newFixedThreadPool(5);
    }

    private void startDownload() {
        String fileURL = urlField.getText().trim();
        String saveDir = saveDirField.getText().trim();

        if (fileURL.isEmpty() || saveDir.isEmpty()) {
            logArea.append("Please provide both URL and Save Directory.\n");
            return;
        }

        FileUtils.ensureDirectory(saveDir);

        String fileName = new File(fileURL).getName();
        String saveFilePath = saveDir + File.separator + fileName;

        if (FileUtils.isFileUpToDate(saveFilePath, fileURL)) {
            logArea.append("File already exists and is up to date.\n");
            return;
        }

        logArea.append("Starting download...\n");

        currentTask = new DownloadTask(fileURL, saveFilePath, logArea);
        executorService.submit(currentTask);

        stopButton.setEnabled(true);
        resumeButton.setEnabled(false);
        downloadButton.setEnabled(false);
    }

    private void stopDownload() {
        if (currentTask != null) {
            currentTask.stopDownload();
            logArea.append("Download stopped.\n");
        }

        stopButton.setEnabled(false);
        resumeButton.setEnabled(true);
        downloadButton.setEnabled(true);
    }

    private void resumeDownload() {
        if (currentTask != null) {
            currentTask.resumeDownload();
            logArea.append("Resuming download...\n");
        }

        stopButton.setEnabled(true);
        resumeButton.setEnabled(false);
        downloadButton.setEnabled(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FileDownloader frame = new FileDownloader();
            frame.setVisible(true);
        });
    }
}