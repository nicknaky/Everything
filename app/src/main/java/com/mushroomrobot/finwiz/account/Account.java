package com.mushroomrobot.finwiz.account;

import java.util.Date;

/**
 * Created by Nick.
 */
public class Account {

    private String name, type;
    private int accountId;
    private double balance;
    private boolean isBudgeted;
    private Date lastDate;

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type = type;
    }
    public int getId(){
        return accountId;
    }
    public void setId(int accountId){
        this.accountId = accountId;
    }
    public double getBalance(){
        return balance;
    }
    public void setBalance(double balance){
        this.balance = balance;
    }
    public boolean checkIsBudgeted(){
        return isBudgeted;
    }
    public void setIsBudgeted(boolean isBudgeted){
        this.isBudgeted = isBudgeted;
    }
    public Date getLastDate(){
        return lastDate;
    }
    public void setLastDate(Date lastDate){
        this.lastDate = lastDate;
    }


}
