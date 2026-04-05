package com.example.a2d;

public class PlantSprite {
    private String name;
    private int hp;

    public PlantSprite(String name, int hp) {
        this.name = name;
        this.hp = hp;
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; }
}
