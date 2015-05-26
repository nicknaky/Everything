package com.mushroomrobot.finwiz.budget;

/**
 * Created by Nick.
 */
public class Budget {

    private String name;
    private int id;
    private double budget;

    public Budget(int id, String name, double budget) {
        super();
        this.name=name;
        this.id=id;
        this.budget=budget;
    }

    public String getName(){
        return this.name;
    }

    public int getId(){
        return this.id;
    }

    public double getBudget(){
        return this.budget;
    }
}
