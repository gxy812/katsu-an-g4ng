package com.example.a2d;

public class Move {
    private String name;
    private int power;

    public Move(String name, int power) {
        this.name = name;
        this.power = power;
    }

    public String getName() { return name; }
    public int getPower() { return power; }
}
