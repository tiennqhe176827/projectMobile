//package com.example.realizeractivity.realizer;
//
//import android.Manifest;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.media.ThumbnailUtils;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.realizeractivity.R;
//import com.example.realizeractivity.ml.ModelUnquant;
//
//import org.tensorflow.lite.DataType;
//import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
//
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//
//public class MainActivity extends AppCompatActivity {
//
//    private TextView recognitionResult, detailedConfidence;
//    private ImageView photoDisplay;
//    private Button captureButton;
//    private final int imgSize = 224;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        recognitionResult = findViewById(R.id.recognitionResult);
//        detailedConfidence = findViewById(R.id.detailedConfidence);
//        photoDisplay = findViewById(R.id.photoDisplay);
//        captureButton = findViewById(R.id.captureButton);
//
//        captureButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                    startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 1);
//                } else {
//                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
//                }
//            }
//        });
//    }
//
//    private void analyzeImage(Bitmap bitmap) {
//        try {
//            ModelUnquant model = ModelUnquant.newInstance(getApplicationContext());
//
//            TensorBuffer inputFeature = TensorBuffer.createFixedSize(new int[]{1, imgSize, imgSize, 3}, DataType.FLOAT32);
//            ByteBuffer buffer = ByteBuffer.allocateDirect(4 * imgSize * imgSize * 3);
//            buffer.order(ByteOrder.nativeOrder());
//
//            int[] pixelValues = new int[imgSize * imgSize];
//            bitmap.getPixels(pixelValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
//
//            int px = 0;
//            for (int i = 0; i < imgSize; i++) {
//                for (int j = 0; j < imgSize; j++) {
//                    int value = pixelValues[px++];
//                    buffer.putFloat(((value >> 16) & 0xFF) / 255.0f);
//                    buffer.putFloat(((value >> 8) & 0xFF) / 255.0f);
//                    buffer.putFloat((value & 0xFF) / 255.0f);
//                }
//            }
//
//            inputFeature.loadBuffer(buffer);
//            float[] confidences = model.process(inputFeature).getOutputFeature0AsTensorBuffer().getFloatArray();
//            model.close();
//
//            displayRecognitionResult(confidences);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void displayRecognitionResult(float[] confidences) {
//        String[] categories = {
//                "Edible Mushroom - King Oyster",
//                "Edible Mushroom - Snow White",
//                "Edible Mushroom - Oyster",
//                "Inedible Mushroom - Poisonous"
//        };
//
//        int highestConfidenceIndex = -1;
//        float maxConfidence = 0;
//        boolean recognized = false;
//
//        for (int i = 0; i < confidences.length; i++) {
//            if (confidences[i] > maxConfidence) {
//                maxConfidence = confidences[i];
//                highestConfidenceIndex = i;
//            }
//            if (confidences[i] >= 0.95f) {
//                recognized = true;
//            }
//        }
//
//        TextView mushroomType = findViewById(R.id.mushroomType);
//        if (recognized) {
//            mushroomType.setText("Mushroom Type");
//            recognitionResult.setText(categories[highestConfidenceIndex]);
//
//            StringBuilder confidenceText = new StringBuilder();
//            for (int i = 0; i < categories.length; i++) {
//                confidenceText.append(String.format("%s: %.1f%%\n", categories[i], confidences[i] * 100));
//            }
//            detailedConfidence.setText(confidenceText.toString());
//        } else {
//            recognitionResult.setText("Mushroom not recognized");
//            detailedConfidence.setText("");
//        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if (requestCode == 1 && resultCode == RESULT_OK) {
//            Bitmap image = (Bitmap) data.getExtras().get("data");
//            int dimension = Math.min(image.getWidth(), image.getHeight());
//            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
//            photoDisplay.setImageBitmap(image);
//
//            image = Bitmap.createScaledBitmap(image, imgSize, imgSize, false);
//            analyzeImage(image);
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//}



package com.example.realizeractivity.realizer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.realizeractivity.R;
import com.example.realizeractivity.ml.ModelUnquant;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity {

    private TextView detectionResult, confidenceDetails;
    private ImageView displayedImage;
    private Button actionButton;
    private final int imgResolution = 224;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        detectionResult = findViewById(R.id.detectionResult);
        confidenceDetails = findViewById(R.id.confidenceDetails);
        displayedImage = findViewById(R.id.displayedImage);
        actionButton = findViewById(R.id.actionButton);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(captureIntent, 1);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });
    }

    private void interpretImage(Bitmap imgBitmap) {
        try {
            ModelUnquant model = ModelUnquant.newInstance(getApplicationContext());

            TensorBuffer imgBuffer = TensorBuffer.createFixedSize(new int[]{1, imgResolution, imgResolution, 3}, DataType.FLOAT32);
            ByteBuffer imgByteBuffer = ByteBuffer.allocateDirect(4 * imgResolution * imgResolution * 3);
            imgByteBuffer.order(ByteOrder.nativeOrder());

            int[] pixelData = new int[imgResolution * imgResolution];
            imgBitmap.getPixels(pixelData, 0, imgBitmap.getWidth(), 0, 0, imgBitmap.getWidth(), imgBitmap.getHeight());

            int index = 0;
            for (int i = 0; i < imgResolution; i++) {
                for (int j = 0; j < imgResolution; j++) {
                    int pixelVal = pixelData[index++];
                    imgByteBuffer.putFloat(((pixelVal >> 16) & 0xFF) / 255.0f);
                    imgByteBuffer.putFloat(((pixelVal >> 8) & 0xFF) / 255.0f);
                    imgByteBuffer.putFloat((pixelVal & 0xFF) / 255.0f);
                }
            }

            imgBuffer.loadBuffer(imgByteBuffer);
            float[] confidenceScores = model.process(imgBuffer).getOutputFeature0AsTensorBuffer().getFloatArray();
            model.close();

            updateDetectionResult(confidenceScores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateDetectionResult(float[] confidenceScores) {
        String[] mushroomTypes = {
                "Nấm đùi gà - Ăn được",
                "Nấm bạch tuyết (hải sản) - Ăn được",
                "Nấm sò trắng - Ăn được",
                "Nấm độc - Không ăn được"
        };

        int bestMatchIndex = -1;
        float highestConfidence = 0;
        boolean isIdentified = false;

        for (int i = 0; i < confidenceScores.length; i++) {
            if (confidenceScores[i] > highestConfidence) {
                highestConfidence = confidenceScores[i];
                bestMatchIndex = i;
            }
            if (confidenceScores[i] >= 0.95f) {
                isIdentified = true;
            }
        }

        TextView detectedLabel = findViewById(R.id.detectedLabel);
        if (isIdentified) {
            detectedLabel.setText("Mushroom Type");
            detectionResult.setText(mushroomTypes[bestMatchIndex]);

            StringBuilder confidenceText = new StringBuilder();
            for (int i = 0; i < mushroomTypes.length; i++) {
                confidenceText.append(String.format("%s: %.1f%%\n", mushroomTypes[i], confidenceScores[i] * 100));
            }
            confidenceDetails.setText(confidenceText.toString());
        } else {
            detectionResult.setText("Mushroom type could not be identified");
            confidenceDetails.setText("");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Bitmap capturedImg = (Bitmap) data.getExtras().get("data");
            int smallestDim = Math.min(capturedImg.getWidth(), capturedImg.getHeight());
            capturedImg = ThumbnailUtils.extractThumbnail(capturedImg, smallestDim, smallestDim);
            displayedImage.setImageBitmap(capturedImg);

            Bitmap scaledImg = Bitmap.createScaledBitmap(capturedImg, imgResolution, imgResolution, false);
            interpretImage(scaledImg);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

