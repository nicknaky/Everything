package com.mushroomrobot.finwiz.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mushroomrobot.finwiz.data.EverythingContract.Accounts;
import com.mushroomrobot.finwiz.data.EverythingContract.Category;
import com.mushroomrobot.finwiz.data.EverythingContract.Transactions;

/**
 * Created by Nick on 4/27/2015.
 */
public class EverythingDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "everything.db";

    public EverythingDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
            db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_ACCOUNTS_TABLE =
                "CREATE TABLE "
                + Accounts.TABLE_NAME
                + " ("
                + Accounts._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + Accounts.COLUMN_NAME + " TEXT UNIQUE NOT NULL, "
                + Accounts.COLUMN_TYPE + " TEXT NOT NULL, "
                + Accounts.COLUMN_BALANCE + " INTEGER NOT NULL, "
                + Accounts.COLUMN_BUDGET_FLAG + " INTEGER NOT NULL, "
                + Accounts.COLUMN_LAST_UPDATE + " TEXT NOT NULL "
                + ");";

        final String SQL_CREATE_CATEGORY_TABLE =
                "CREATE TABLE "
                + Category.TABLE_NAME
                + " ("
                + Category._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + Category.COLUMN_NAME + " TEXT UNIQUE NOT NULL, "
                + Category.COLUMN_BUDGET + " INTEGER NOT NULL "
                + ");";

        final String SQL_CREATE_TRANSACTIONS_TABLE =
                "CREATE TABLE "
                + Transactions.TABLE_NAME
                + " ("
                + Transactions._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + Transactions.COLUMN_CATEGORY + " TEXT NOT NULL, "
                + Transactions.COLUMN_DESCRIPTION + " TEXT, "
                + Transactions.COLUMN_AMOUNT + " INTEGER NOT NULL, "
                + Transactions.COLUMN_DATE + " INTEGER NOT NULL, "
                + Transactions.COLUMN_ACCOUNT + " TEXT, "
                + Transactions.COLUMN_TYPE + " TEXT, "
                + Transactions.COLUMN_RECURRENCE + " INTEGER); "
                + "FOREIGN KEY (" + Transactions.COLUMN_CATEGORY + ") REFERENCES "
                + Category.TABLE_NAME + " (" + Category.COLUMN_NAME + ") ON DELETE CASCADE ON UPDATE CASCADE"
                + ");";

        db.execSQL(SQL_CREATE_ACCOUNTS_TABLE);
        db.execSQL(SQL_CREATE_CATEGORY_TABLE);
        db.execSQL(SQL_CREATE_TRANSACTIONS_TABLE);

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Accounts.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Category.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Transactions.TABLE_NAME);
        onCreate(db);
    }


}
