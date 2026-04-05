package com.example.a2d;

public class BattleState {
    private PlantSprite attacker;
    private PlantSprite defender;
    private String lastMoveMessage;

    public BattleState(PlantSprite attacker, PlantSprite defender, String lastMoveMessage) {
        this.attacker = attacker;
        this.defender = defender;
        this.lastMoveMessage = lastMoveMessage;
    }

    public PlantSprite getAttacker() { return attacker; }
    public PlantSprite getDefender() { return defender; }
    public String getLastMoveMessage() { return lastMoveMessage; }
}
