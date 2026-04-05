package com.example.a2d;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BattleActivity extends AppCompatActivity {

    Button buttonMove1;
    Button buttonMove2;
    Button buttonMove3;
    Button buttonMove4;
    Button buttonUseSpecial;
    TextView textViewOpponentName;
    TextView textViewOpponentHp;
    TextView textViewPlayerName;
    TextView textViewPlayerHp;
    TextView textViewBattleLog;
    ImageView imageViewOpponentSprite;
    ImageView imageViewPlayerSprite;

    public final String TAG = "BattleActivity";
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.a2d.battleprefs";
    public static final String XP_KEY = "XP_Key";

    // Battle state
    int playerHp = 100;
    int opponentHp = 100;
    int playerXp = 0;
    boolean isPlayerTurn = true;

    String playerPlantName = "Bay Biscayne Creeping Oxeye";
    String opponentPlantName = "Saw Palmetto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        // Get SharedPreferences — load saved XP
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        playerXp = mPreferences.getInt(XP_KEY, 0);

        // Get references to widgets
        buttonMove1 = findViewById(R.id.buttonMove1);
        buttonMove2 = findViewById(R.id.buttonMove2);
        buttonMove3 = findViewById(R.id.buttonMove3);
        buttonMove4 = findViewById(R.id.buttonMove4);
        buttonUseSpecial = findViewById(R.id.buttonUseSpecial);
        textViewOpponentName = findViewById(R.id.textViewOpponentName);
        textViewOpponentHp = findViewById(R.id.textViewOpponentHp);
        textViewPlayerName = findViewById(R.id.textViewPlayerName);
        textViewPlayerHp = findViewById(R.id.textViewPlayerHp);
        textViewBattleLog = findViewById(R.id.textViewBattleLog);
        imageViewOpponentSprite = findViewById(R.id.imageViewOpponentSprite);
        imageViewPlayerSprite = findViewById(R.id.imageViewPlayerSprite);

        // Set initial display values
        textViewOpponentName.setText(opponentPlantName);
        textViewOpponentHp.setText("HP: " + opponentHp + "/100");
        textViewPlayerName.setText(playerPlantName);
        textViewPlayerHp.setText("HP: " + playerHp + "/100");
        textViewBattleLog.setVisibility(View.GONE);

        // Set placeholder sprites
        imageViewOpponentSprite.setImageResource(R.mipmap.ic_launcher);
        imageViewPlayerSprite.setImageResource(R.mipmap.ic_launcher);

        // Move 1 — Solar Bloom
        buttonMove1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlayerTurn) {
                    useMove("Solar Bloom", 15);
                } else {
                    Toast.makeText(BattleActivity.this,
                            "Wait for your turn!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Move 2 — Tidal Root Snare
        buttonMove2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlayerTurn) {
                    useMove("Tidal Root Snare", 20);
                } else {
                    Toast.makeText(BattleActivity.this,
                            "Wait for your turn!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Move 3 — Golden Pollen Burst
        buttonMove3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlayerTurn) {
                    useMove("Golden Pollen Burst", 18);
                } else {
                    Toast.makeText(BattleActivity.this,
                            "Wait for your turn!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Move 4 — Sunpetal Burst
        buttonMove4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlayerTurn) {
                    useMove("Sunpetal Burst", 25);
                } else {
                    Toast.makeText(BattleActivity.this,
                            "Wait for your turn!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Use Special button
        buttonUseSpecial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlayerTurn) {
                    useSpecialMove();
                } else {
                    Toast.makeText(BattleActivity.this,
                            "Wait for your turn!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void useMove(String moveName, int damage) {
        opponentHp = Math.max(0, opponentHp - damage);
        textViewOpponentHp.setText("HP: " + opponentHp + "/100");

        String logText = playerPlantName + " used " + moveName + "!";
        textViewBattleLog.setText(logText);
        textViewBattleLog.setVisibility(View.VISIBLE);
        Log.d(TAG, logText);

        if (opponentHp <= 0) {
            endBattle(true);
            return;
        }

        isPlayerTurn = false;
        opponentTurn();
    }

    private void useSpecialMove() {
        int specialDamage = 35;
        opponentHp = Math.max(0, opponentHp - specialDamage);
        textViewOpponentHp.setText("HP: " + opponentHp + "/100");

        String logText = playerPlantName + " Special used!";
        textViewBattleLog.setText(logText);
        textViewBattleLog.setVisibility(View.VISIBLE);
        Log.d(TAG, logText);

        if (opponentHp <= 0) {
            endBattle(true);
            return;
        }

        isPlayerTurn = false;
        opponentTurn();
    }

    private void opponentTurn() {
        // TODO: Replace with real opponent AI or multiplayer logic
        int opponentDamage = 12;
        playerHp = Math.max(0, playerHp - opponentDamage);
        textViewPlayerHp.setText("HP: " + playerHp + "/100");

        String logText = opponentPlantName + " attacks for "
                + opponentDamage + " damage!";
        textViewBattleLog.setText(logText);
        Log.d(TAG, logText);

        if (playerHp <= 0) {
            endBattle(false);
            return;
        }

        isPlayerTurn = true;
    }

    private void endBattle(boolean playerWon) {
        buttonMove1.setEnabled(false);
        buttonMove2.setEnabled(false);
        buttonMove3.setEnabled(false);
        buttonMove4.setEnabled(false);
        buttonUseSpecial.setEnabled(false);

        if (playerWon) {
            playerXp += 100;
            textViewBattleLog.setText("You won! +100 XP. Total XP: " + playerXp);
            Toast.makeText(this, "Victory! +100 XP", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Player won. XP: " + playerXp);
        } else {
            textViewBattleLog.setText("You lost! Better luck next time.");
            Toast.makeText(this, "Defeated...", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Player lost");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(XP_KEY, playerXp);
        editor.apply();
        Log.d(TAG, "onPause — XP saved: " + playerXp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        playerXp = mPreferences.getInt(XP_KEY, 0);
        Log.d(TAG, "onResume — XP loaded: " + playerXp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
    }
}