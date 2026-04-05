package com.example.a2d;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SpriteActivity extends AppCompatActivity {

    Button buttonSaveSprite;
    TextView textViewSpriteName;
    TextView textViewSpriteHeader;
    ImageView imageViewSprite;

    public final String TAG = "SpriteActivity";
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.a2d.spriteprefs";
    public static final String SPRITE_NAME_KEY = "Sprite_Name_Key";

    String plantName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprite);

        // Get references to widgets
        buttonSaveSprite = findViewById(R.id.buttonSaveSprite);
        textViewSpriteName = findViewById(R.id.textViewSpriteName);
        textViewSpriteHeader = findViewById(R.id.textViewSpriteHeader);
        imageViewSprite = findViewById(R.id.imageViewSprite);

        // Get SharedPreferences
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        // Retrieve plant name passed from ScannerActivity
        Intent intent = getIntent();
        plantName = intent.getStringExtra(ScannerActivity.INTENT_PLANT_NAME);

        if (plantName == null || plantName.isEmpty()) {
            plantName = mPreferences.getString(SPRITE_NAME_KEY, "Unknown Plant");
        }

        textViewSpriteName.setText(plantName);
        Log.d(TAG, "Sprite generated for: " + plantName);

        // TODO: Load real pixel-art sprite using Glide or image generation API
        imageViewSprite.setImageResource(R.mipmap.ic_launcher);

        // Save Sprite to Collection button
        buttonSaveSprite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (plantName.isEmpty()) {
                    Toast.makeText(SpriteActivity.this,
                            "No plant to save",
                            Toast.LENGTH_SHORT).show();
                } else {
                    saveSprite();
                    Toast.makeText(SpriteActivity.this,
                            plantName + " saved to collection!",
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Sprite saved: " + plantName);
                    finish();
                }
            }
        });
    }

    private void saveSprite() {
        // TODO: Save to Room database or Firebase Firestore
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(SPRITE_NAME_KEY, plantName);
        editor.apply();
        Log.d(TAG, "Saved to SharedPreferences: " + plantName);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(SPRITE_NAME_KEY, plantName);
        editor.apply();
        Log.d(TAG, "onPause — sprite name saved");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
    }
}