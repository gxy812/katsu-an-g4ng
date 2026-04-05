package com.example.a2d;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ModelDownloader {

    private static final String TAG = "ModelDownloader";

    // Temporary model from Hugging Face
    private static final String MODEL_URL =
            "https://huggingface.co/umutbozdag/plant-identity/resolve/main/model.tflite";

    private static final String MODEL_FILENAME = "model.tflite";

    // Callback interface to notify when download is done
    public interface DownloadCallback {
        void onSuccess(File modelFile);
        void onFailure(String errorMessage);
        void onProgress(int progressPercent);
    }

    // Check if model is already downloaded to avoid re-downloading
    public static boolean isModelDownloaded(Context context) {
        File modelFile = getModelFile(context);
        return modelFile.exists() && modelFile.length() > 0;
    }

    // Get the local file path where model is saved
    public static File getModelFile(Context context) {
        return new File(context.getFilesDir(), MODEL_FILENAME);
    }

    // Download model on a background thread
    public static void downloadModel(Context context, DownloadCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Skip download if already exists
                if (isModelDownloaded(context)) {
                    Log.d(TAG, "Model already downloaded, skipping");
                    callback.onSuccess(getModelFile(context));
                    return;
                }

                HttpURLConnection connection = null;
                InputStream inputStream = null;
                FileOutputStream outputStream = null;

                try {
                    Log.d(TAG, "Starting model download from: " + MODEL_URL);
                    URL url = new URL(MODEL_URL);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(15000);
                    connection.setReadTimeout(15000);
                    connection.connect();

                    // Handle redirects (Hugging Face often redirects to LFS)
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP || 
                        responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                        responseCode == 307 || responseCode == 308) {
                        String newUrl = connection.getHeaderField("Location");
                        connection = (HttpURLConnection) new URL(newUrl).openConnection();
                        connection.connect();
                    }

                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        callback.onFailure("Server returned: "
                                + connection.getResponseCode());
                        return;
                    }

                    int fileLength = connection.getContentLength();
                    inputStream = connection.getInputStream();
                    File outputFile = getModelFile(context);
                    outputStream = new FileOutputStream(outputFile);

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    long totalBytesRead = 0;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                        totalBytesRead += bytesRead;

                        // Report progress if file size is known
                        if (fileLength > 0) {
                            int progress = (int) (totalBytesRead * 100 / fileLength);
                            callback.onProgress(progress);
                        }
                    }

                    Log.d(TAG, "Model downloaded successfully to: "
                            + outputFile.getAbsolutePath());
                    callback.onSuccess(outputFile);

                } catch (IOException e) {
                    Log.e(TAG, "Download failed: " + e.getMessage());
                    callback.onFailure("Download failed: " + e.getMessage());
                } finally {
                    try {
                        if (inputStream != null) inputStream.close();
                        if (outputStream != null) outputStream.close();
                        if (connection != null) connection.disconnect();
                    } catch (IOException e) {
                        Log.e(TAG, "Error closing streams: " + e.getMessage());
                    }
                }
            }
        }).start();
    }
}