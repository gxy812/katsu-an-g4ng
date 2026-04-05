package com.example.a2d;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class BattleViewModel extends AndroidViewModel {
    private MutableLiveData<BattleState> battleState = new MutableLiveData<>();

    public BattleViewModel(@NonNull Application application) {
        super(application);
    }

    public void useMove(PlantSprite attacker, Move move, PlantSprite defender) {
        int damage = calculateDamage(attacker, move);
        defender.setHp(Math.max(0, defender.getHp() - damage));
        BattleState newState = new BattleState(attacker, defender, move.getName() + " used!");
        battleState.setValue(newState);
        if (defender.getHp() == 0) endBattle(attacker);
    }

    private int calculateDamage(PlantSprite attacker, Move move) {
        // Placeholder damage calculation
        return 20;
    }

    private void endBattle(PlantSprite winner) {
        // Handle end battle logic
    }

    public LiveData<BattleState> getBattleState() {
        return battleState;
    }
}
