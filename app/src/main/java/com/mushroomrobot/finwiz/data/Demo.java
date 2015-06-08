package com.mushroomrobot.finwiz.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Nick.
 */
public class Demo {

    public void demoSetUp(Context context){

        EverythingDbHelper dbHelper = new EverythingDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(EverythingContract.Category.TABLE_NAME, null, null);
        db.delete(EverythingContract.Transactions.TABLE_NAME, null, null);
        db.delete(EverythingContract.Accounts.TABLE_NAME, null, null);

        ArrayList<ContentValues> cvList = new ArrayList<>();
        ContentValues cv = new ContentValues();

        cv.put(EverythingContract.Category.COLUMN_NAME, "Alcohol");
        cv.put(EverythingContract.Category.COLUMN_BUDGET, 7500);
        cvList.add(cv);
        cv = new ContentValues();
        cv.put(EverythingContract.Category.COLUMN_NAME, "Gas");
        cv.put(EverythingContract.Category.COLUMN_BUDGET, 6000);
        cvList.add(cv);
        cv = new ContentValues();
        cv.put(EverythingContract.Category.COLUMN_NAME, "Groceries");
        cv.put(EverythingContract.Category.COLUMN_BUDGET, 20000);
        cvList.add(cv);
        cv = new ContentValues();
        cv.put(EverythingContract.Category.COLUMN_NAME, "Eating Out");
        cv.put(EverythingContract.Category.COLUMN_BUDGET, 6000);
        cvList.add(cv);
        cv = new ContentValues();
        cv.put(EverythingContract.Category.COLUMN_NAME, "Health");
        cv.put(EverythingContract.Category.COLUMN_BUDGET, 5000);
        cvList.add(cv);
        cv = new ContentValues();
        cv.put(EverythingContract.Category.COLUMN_NAME, "Entertainment");
        cv.put(EverythingContract.Category.COLUMN_BUDGET, 5000);
        cvList.add(cv);
        cv = new ContentValues();
        cv.put(EverythingContract.Category.COLUMN_NAME, "Monthly Bills");
        cv.put(EverythingContract.Category.COLUMN_BUDGET, 11400);
        cvList.add(cv);
        cv = new ContentValues();
        cv.put(EverythingContract.Category.COLUMN_NAME, "Misc");
        cv.put(EverythingContract.Category.COLUMN_BUDGET, 5000);
        cvList.add(cv);

        db.beginTransaction();
        try {
            for (ContentValues values : cvList){
                db.insertOrThrow(EverythingContract.Category.TABLE_NAME,null,values);
            }
            db.setTransactionSuccessful();
            context.getContentResolver().notifyChange(EverythingContract.Category.CONTENT_URI, null);
        } finally {
            db.endTransaction();
        }

        String[] category = {"Alcohol", "Alcohol", "Alcohol", "Alcohol", "Alcohol", "Alcohol",
        "Eating Out", "Eating Out", "Eating Out", "Eating Out", "Eating Out", "Eating Out", "Eating Out",
        "Gas",
        "Groceries", "Groceries", "Groceries", "Groceries",
        "Health", "Health",
        "Misc", "Misc",
        "Monthly Bills"};
        int[] amount = {1079, 1000, 4200, 2000, 2300, 900,
        389, 271, 324, 684, 1533, 336, 389,
        3781,
        1249, 799, 900, 3300,
        1072, 1000,
        1000, 7489,
        11400};

        Calendar calendar = Calendar.getInstance();
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);

        String[] stringDate = {month + "/15/15",month + "/21/15",month + "/21/15",month + "/25/15",month + "/28/15",month + "/28/15",
                month + "/12/15",month + "/13/15",month + "/13/15",month + "/15/15",month + "/17/15",month + "/22/15",month + "/28/15",
        month + "/19/15",
                month + "/09/15",month + "/16/15",month + "/22/15",month + "/23/15",
                month + "/06/15",month + "/08/15",
        month + "/16/15", month + "/01/15",
        month + "/09/15"};
        Date[] dates = new Date[stringDate.length];
        try {
            for (int i = 0; i < dates.length; i++) {
                dates[i] = new SimpleDateFormat("M/dd/yy", Locale.US).parse(stringDate[i]);
            }
        }catch (ParseException e){

        }
        long[] dateInMilis = new long[dates.length];
        for (int i = 0; i<dateInMilis.length; i++){
            dateInMilis[i] = dates[i].getTime();
        }
        String[] descrption = {"Drink with coworkers", "Bullet (cash)", "2x Fireball, bullet, vodka soda", "Safeway", "Round of beers harry hofbrau", "Paper planes",
        "McDonald's work lunch", "McDonald's work lunch", "McDonald's work breakfast", "El maguey taqueria", "Chipotle with Jenny", "McDonald's", "Mcdonald's work lunch",
        "No Description",
                "Safeway lasanga and salad","Safeway milk and salad","Safeway fried chicken and ice cream","Safeway beer cheese",
                "L-theanine","Caffeine",
        "Haircut", "Car repairs",
        "Caltrain"};

        ArrayList<ContentValues> contentValuesList = new ArrayList<>();

        for (int i=0; i<category.length; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("category", category[i]);
            contentValues.put("amount", amount[i]);
            contentValues.put("date", dateInMilis[i]);
            contentValues.put("description", descrption[i]);
            contentValuesList.add(contentValues);
        }

        db.beginTransaction();
        try {
            for (ContentValues contentValues : contentValuesList){
                db.insertOrThrow(EverythingContract.Transactions.TABLE_NAME,null,contentValues);
            }
            db.setTransactionSuccessful();
            context.getContentResolver().notifyChange(EverythingContract.Category.CONTENT_URI, null);
            context.getContentResolver().notifyChange(EverythingContract.Transactions.CONTENT_URI_AMOUNT_BY_DAY,null);
            context.getContentResolver().notifyChange(EverythingContract.Transactions.CONTENT_URI_AMOUNT_BY_MONTH, null);
            context.getContentResolver().notifyChange(EverythingContract.Transactions.CONTENT_URI_HISTORY,null);
        } finally {
            db.endTransaction();
        }

        String[] accountNames = {"Checkings", "Savings", "Vanguard Brokerage", "Employer 401k", "Roth IRA", "Mortgage", "Student Loans", "Auto Loan"};
        String[] accountTypes = {"Asset", "Asset", "Asset", "Asset", "Asset", "Debt", "Debt", "Debt"};
        int[] accountBalances = {61386, 540912, 364250, 173685, 550000, 237500, 908612, 430017};

        SimpleDateFormat dateName = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
        String dateString = dateName.format(calendar.getTime());

        String[] accountDates = {dateString, dateString, dateString, dateString, dateString, dateString, dateString, dateString};

        ArrayList<ContentValues> accountCVList = new ArrayList<>();
        for (int i=0; i<accountNames.length; i++){
            ContentValues accountCV = new ContentValues();
            accountCV.put("name", accountNames[i]);
            accountCV.put("type", accountTypes[i]);
            accountCV.put("last_date", accountDates[i]);
            accountCV.put("balance", accountBalances[i]);
            accountCV.put("budget_flag", 0);
            accountCVList.add(accountCV);
        }

        db.beginTransaction();
        try {
            for (ContentValues contentValues : accountCVList){
                db.insertOrThrow(EverythingContract.Accounts.TABLE_NAME,null,contentValues);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }


    }
}
