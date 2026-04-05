package com.example.a2d;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class PlantClassifier {

    private static final String TAG = "PlantClassifier";
    private static final String LABELS_FILE = "labels.txt";

    // Must match input size your Colab model was trained with
    private static final int INPUT_SIZE = 224;
    private static final int PIXEL_SIZE = 3; // RGB

    private Interpreter interpreter;
    private List<String> labels = new ArrayList<>();
    private Context context;
    private boolean isReady = false;

    public PlantClassifier(Context context) {
        this.context = context;
        try {
            loadLabels();
        } catch (IOException e) {
            Log.e(TAG, "Error loading labels: " + e.getMessage());
        }
    }

    // Call this after model is downloaded
    public void loadModel(File modelFile) {
        try {
            FileInputStream fis = new FileInputStream(modelFile);
            FileChannel fileChannel = fis.getChannel();
            MappedByteBuffer buffer = fileChannel.map(
                    FileChannel.MapMode.READ_ONLY, 0, modelFile.length());
            interpreter = new Interpreter(buffer);
            isReady = true;
            Log.d(TAG, "Model loaded from: " + modelFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "Error loading model: " + e.getMessage());
            isReady = false;
        }
    }

    // Load label names from assets/labels.txt
    private void loadLabels() throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(context.getAssets().open(LABELS_FILE)));
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                labels.add(line.trim());
            }
        }
        reader.close();
        Log.d(TAG, "Labels loaded: " + labels.toString());
    }

    // Run classification — returns plant name string
    public String classify(Bitmap bitmap) {
        if (!isReady) {
            Log.e(TAG, "Model not ready yet");
            return "Model not loaded";
        }

        Bitmap resized = Bitmap.createScaledBitmap(
                bitmap, INPUT_SIZE, INPUT_SIZE, true);
        ByteBuffer input = convertBitmapToByteBuffer(resized);

        // Output size must match number of classes in your Colab model
        float[][] output = new float[1][labels.size()];
        interpreter.run(input, output);

        // Find highest confidence label
        int maxIndex = 0;
        float maxConfidence = 0;
        for (int i = 0; i < output[0].length; i++) {
            if (output[0][i] > maxConfidence) {
                maxConfidence = output[0][i];
                maxIndex = i;
            }
        }

        String result = labels.isEmpty() ? "Unknown" : labels.get(maxIndex);
        Log.d(TAG, "Result: " + result + " confidence: " + maxConfidence);
        return result;
    }

    // Convert bitmap to float ByteBuffer for TFLite input
    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(
                4 * INPUT_SIZE * INPUT_SIZE * PIXEL_SIZE);
        buffer.order(ByteOrder.nativeOrder());

        int[] pixels = new int[INPUT_SIZE * INPUT_SIZE];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(),
                0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int pixel : pixels) {
            buffer.putFloat(((pixel >> 16) & 0xFF) / 255.0f); // R
            buffer.putFloat(((pixel >> 8) & 0xFF) / 255.0f);  // G
            buffer.putFloat((pixel & 0xFF) / 255.0f);          // B
        }
        return buffer;
    }

    public boolean isReady() {
        return isReady;
    }

    public void close() {
        if (interpreter != null) {
            interpreter.close();
        }
    }
}