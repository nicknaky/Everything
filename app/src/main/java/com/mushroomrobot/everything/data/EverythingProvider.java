package com.mushroomrobot.everything.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.mushroomrobot.everything.budget.Budget;
import com.mushroomrobot.everything.data.EverythingContract.Accounts;
import com.mushroomrobot.everything.data.EverythingContract.Category;
import com.mushroomrobot.everything.data.EverythingContract.Transactions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Nick on 4/28/2015.
 */
public class EverythingProvider extends ContentProvider {

    //Database
    private EverythingDbHelper database;

    //Used for the UriMatcher
    private static final int ACCOUNTS = 10;
    private static final int ACCOUNTS_ID = 11;

    //List of categories
    private static final int CATEGORY = 20;

    //Category Details
    private static final int CATEGORY_ID = 21;

    //wtf is this
    private static final int CATEGORY_WITH_TRANSACTIONS_ID = 25;

    //All transactions
    private static final int TRANSACTIONS = 30;

    private static final int TRANSACTIONS_HISTORY = 65;
    private static final int TRANSACTIONS_BY_DAY = 35;
    private static final int TRANSACTIONS_BY_MONTH = 45;

    //Transaction details
    private static final int TRANSACTIONS_ID = 31;

    private static final String AUTHORITY = "com.mushroomrobot.everything.data";


    private static final int CATEGORY_OVERVIEW = 255;

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/accounts";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/accounts";

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY, EverythingContract.PATH_ACCOUNTS, ACCOUNTS);
        sUriMatcher.addURI(AUTHORITY, EverythingContract.PATH_ACCOUNTS + "/#", ACCOUNTS_ID);

        sUriMatcher.addURI(AUTHORITY, EverythingContract.PATH_CATEGORY, CATEGORY);

        sUriMatcher.addURI(AUTHORITY, EverythingContract.PATH_CATEGORY + "/#", CATEGORY_ID);

        sUriMatcher.addURI(AUTHORITY, EverythingContract.PATH_CATEGORY + "/#/*", CATEGORY_WITH_TRANSACTIONS_ID);

        sUriMatcher.addURI(AUTHORITY, EverythingContract.PATH_TRANSACTIONS, TRANSACTIONS);
        sUriMatcher.addURI(AUTHORITY, EverythingContract.PATH_TRANSACTIONS + "/BY_DAY", TRANSACTIONS_BY_DAY);
        sUriMatcher.addURI(AUTHORITY, EverythingContract.PATH_TRANSACTIONS + "/BY_MONTH", TRANSACTIONS_BY_MONTH);
        sUriMatcher.addURI(AUTHORITY, EverythingContract.PATH_TRANSACTIONS + "/HISTORY", TRANSACTIONS_HISTORY);

        sUriMatcher.addURI(AUTHORITY, EverythingContract.PATH_TRANSACTIONS + "/#", TRANSACTIONS_ID);

        sUriMatcher.addURI(AUTHORITY, EverythingContract.PATH_CATEGORY + "/OVERVIEW", CATEGORY_OVERVIEW);
    }




    public boolean onCreate() {
        database = new EverythingDbHelper(getContext());
        return false;
    }

    //TODO: Custom query for budget categories and their real spend
    //Template code:
    //Also good to remember: using TOTAL instead of SUM allows us to add null values which translates to zero's
    /*  select name,budget, total(transactions.amount) as spent, (budget - total(transactions.amount)) as remaining from category c
        left join transactions on transactions.category = category.name
        group by name;
    */



    private Cursor getAccounts(Uri uri, String[] projection,String selection,String[] selectionArgs, String sortOrder){

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(Accounts.TABLE_NAME);

        int uriType = sUriMatcher.match(uri);
        switch(uriType){
            case ACCOUNTS: break;
            case ACCOUNTS_ID: queryBuilder.appendWhere(Accounts._ID + "=" + uri.getLastPathSegment());
                break;
            default: throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db,projection,selection,selectionArgs,null,null,sortOrder);
        return cursor;
    }

    private Cursor getAllTransactions(){

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(Transactions.TABLE_NAME);

        String[] projection = {Transactions._ID,Transactions.COLUMN_CATEGORY,Transactions.COLUMN_AMOUNT,Transactions.COLUMN_DATE,Transactions.COLUMN_DESCRIPTION};

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db,projection,null,null,null,null,null);

        return cursor;
    }

    private Cursor getBudgets(String selection){

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        Calendar myCalendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.US);
        String monthYear = sdf.format(myCalendar.getTime());



        //This is set to query for only the current month, note that we are not using a WHERE clause as that will filter out the entire category
        //when there are no transactions (ie. when a category is first set up)
        //http://stackoverflow.com/questions/4752455/left-join-with-where-clause
        queryBuilder.setTables("category LEFT JOIN transactions on (category.name = transactions.category) and (strftime('%Y-%m', transactions.date/1000, 'unixepoch', 'localtime') = '" + monthYear + "')");

        SQLiteDatabase db = database.getWritableDatabase();

        String sum_amount = "(total(transactions.amount)) as spent";
        String sum_remaining = "(budget - total(transactions.amount)) as remaining";
        String sum_percent = "(total(transactions.amount)/budget*100) as percent";

        //To prevent ambigious column name error since transactions table has _id column as well.
        String categoryID = "category._id";
        String[] projection = {categoryID,Category.COLUMN_NAME,Category.COLUMN_BUDGET,sum_amount, sum_remaining,sum_percent};
        String groupBy = "category.name";

        Cursor cursor = queryBuilder.query(db, projection, selection, null, groupBy, null, null);

        String getBudgetCursor = cursor.getColumnName(1);
        Log.v("getBudgetCursor", getBudgetCursor);

        return cursor;
    }

    public Budget getBudgetById(int budgetId){

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = db.query(Category.TABLE_NAME,null,Category._ID + "=?",new String[] {String.valueOf(budgetId)},null,null,null);
        if (cursor.moveToFirst()){
            Log.v("getBudgetById method", "works");
            return new Budget(cursor.getInt(0),cursor.getString(1),cursor.getDouble(2));

        }
        return null;
    }

    private Cursor getCategoryTransactions(String selection, String[]selectionArgs){

        //Remember, the selectionArgs is not the category name but rather the category ID, which transaction table does not have.
        int categoryId = Integer.valueOf(selectionArgs[0]);
        Budget budget = getBudgetById(categoryId);

        String budgetAmount = String.valueOf(budget.getBudget());
        String budgetAmountColumn = "(" + budgetAmount + ") as budget";

        String[] parsedArgs = {budget.getName()};

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(Transactions.TABLE_NAME );

        Calendar myCalendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.US);
        String monthYear = sdf.format(myCalendar.getTime());
        queryBuilder.appendWhere("strftime('%Y-%m', date/1000, 'unixepoch', 'localtime') = '" + monthYear + "'");


        String[] projection = {Transactions._ID,Transactions.COLUMN_CATEGORY,Transactions.COLUMN_AMOUNT,Transactions.COLUMN_DATE,Transactions.COLUMN_DESCRIPTION};
        String orderBy = Transactions.COLUMN_DATE + " desc, " + Transactions._ID + " desc";

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, parsedArgs, null, null, orderBy);

        return cursor;
    }

    // For specific category
    private Cursor getCategoryTransactionsAmountByDay(String selection, String[] selectionArgs){
        int categoryId = Integer.valueOf(selectionArgs[0]);
        Budget budget = getBudgetById(categoryId);


        String[] parsedArgs = {budget.getName()};

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(Transactions.TABLE_NAME);

        Calendar myCalendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.US);
        String monthYear = sdf.format(myCalendar.getTime());
        queryBuilder.appendWhere("strftime('%Y-%m', date/1000, 'unixepoch', 'localtime') = '" + monthYear + "'");


        String[] projection = {Transactions._ID, Transactions.COLUMN_DATE, "sum(amount)"};
        String groupBy = Transactions.COLUMN_DATE;
        String orderBy = Transactions.COLUMN_DATE + " asc";

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, parsedArgs, groupBy, null, orderBy);

        return cursor;
    }

    //For all categories
    private Cursor getCategoryTransactionsAmountByDay(){

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(Transactions.TABLE_NAME);

        Calendar myCalendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.US);
        String monthYear = sdf.format(myCalendar.getTime());
        queryBuilder.appendWhere("strftime('%Y-%m', date/1000, 'unixepoch', 'localtime') = '" + monthYear + "'");

        String[] projection = {Transactions._ID, Transactions.COLUMN_DATE, "sum(amount)"};
        String groupBy = Transactions.COLUMN_DATE;
        String orderBy = Transactions.COLUMN_DATE + " asc";

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, null, null, groupBy, null, orderBy);

        return cursor;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        int uriType = sUriMatcher.match(uri);

        switch (uriType){
            case ACCOUNTS: retCursor = getAccounts(uri,projection,selection,selectionArgs,sortOrder);
                break;
            case ACCOUNTS_ID: retCursor = getAccounts(uri,projection,selection,selectionArgs,sortOrder);
                break;
            case CATEGORY: retCursor = getBudgets(selection);
                break;
            case CATEGORY_ID: retCursor = getBudgets(selection);
                break;
            case TRANSACTIONS: retCursor = getCategoryTransactions(selection, selectionArgs);
                break;
            case TRANSACTIONS_ID:
                SQLiteDatabase db = database.getWritableDatabase();
                retCursor =  db.query(Transactions.TABLE_NAME,null,selection,null,null,null,null);
                break;
            case TRANSACTIONS_BY_DAY:
                if (selection != null){
                    retCursor = getCategoryTransactionsAmountByDay(selection, selectionArgs);
                }else retCursor = getCategoryTransactionsAmountByDay();
                break;
            case TRANSACTIONS_BY_MONTH: retCursor = getHistoryTransactionsAmountByMonth(selection);
                break;
            case TRANSACTIONS_HISTORY: retCursor = getHistoryTransactions(selection);
                break;
            default: throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        String getRetCursor = retCursor.getColumnName(1);
        Log.v("getRetCursor", getRetCursor);
        //Make sure that potential listeners are getting notified
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);

        return retCursor;
    }

    private Cursor getHistoryTransactions(String selection){

        SQLiteDatabase db = database.getWritableDatabase();

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(Transactions.TABLE_NAME);

        String orderBy = Transactions.COLUMN_DATE + " desc, " + Transactions._ID + " desc" ;
        Cursor cursor = builder.query(db,null,selection,null,null,null,orderBy);

        return cursor;
    }
    private Cursor getHistoryTransactionsAmountByMonth(String selection){

        SQLiteDatabase db = database.getWritableDatabase();

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(Transactions.TABLE_NAME);
        //REMEMBER TO DIVIDE THE DATE BY 1000. JAVA RECORDS IN MILLISECONDS, BUT SQLITE VIES IT AS SECONDS!!!
        String[] projections = {"_id", "strftime('%Y-%m',date/1000,'unixepoch','localtime') as year_month", "total(amount) as monthly_total"};

        String groupBy = "year_month";
        String orderBy = "year_month asc";
        Cursor cursor = builder.query(db,projections,selection,null,groupBy,null,orderBy);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();

        Uri returnUri;
        long id;
        switch (uriType){
            case ACCOUNTS:
                id = sqlDB.insert(Accounts.TABLE_NAME,null,values);
                if (id > 0)
                    returnUri = Accounts.buildAccountsUri(id);
                else
                    throw new SQLException("Failed to insert account into row: " + uri);
                break;
            case CATEGORY:
                id = sqlDB.insert(Category.TABLE_NAME,null,values);
                if (id > 0){
                    returnUri = Category.buildCategoryUri(id);
                    getContext().getContentResolver().notifyChange(Transactions.CONTENT_URI_AMOUNT_BY_DAY,null);
                }
                else
                    throw new SQLException("Failed to insert category into row: " + uri);
                break;
            case TRANSACTIONS:
                id = sqlDB.insert(Transactions.TABLE_NAME,null,values);
                if (id > 0) {
                    returnUri = Transactions.buildTransactionsUri(id);
                    getContext().getContentResolver().notifyChange(Category.CONTENT_URI, null);
                    getContext().getContentResolver().notifyChange(Transactions.CONTENT_URI_AMOUNT_BY_DAY,null);
                    getContext().getContentResolver().notifyChange(Transactions.CONTENT_URI_AMOUNT_BY_MONTH,null);
                    getContext().getContentResolver().notifyChange(Transactions.CONTENT_URI_HISTORY,null);
                }
                else
                    throw new SQLException("Failed to insert transaction into row: " + uri);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted =0;
        String id;

        switch (uriType){
            case ACCOUNTS:
                rowsDeleted = sqlDB.delete(Accounts.TABLE_NAME,selection,selectionArgs);
                getContext().getContentResolver().notifyChange(uri,null);
                break;
            case ACCOUNTS_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)){
                    rowsDeleted = sqlDB.delete(Accounts.TABLE_NAME,Accounts._ID + "=" + id,null);
                }else {
                    rowsDeleted = sqlDB.delete(Accounts.TABLE_NAME,Accounts._ID + "=" + id + " and " + selection,selectionArgs);
                }
                getContext().getContentResolver().notifyChange(uri,null);
                break;

            case CATEGORY_ID:
                id = uri.getLastPathSegment();
                rowsDeleted = sqlDB.delete(Category.TABLE_NAME, Category._ID + "=" + id, null);
                rowsDeleted += sqlDB.delete(Transactions.TABLE_NAME,Transactions.COLUMN_CATEGORY + "=" + selection,null);
                //Do not notify.
                //getContext().getContentResolver().notifyChange(uri,null);
                break;
            case TRANSACTIONS_ID:
                id = uri.getLastPathSegment();
                rowsDeleted = sqlDB.delete(Transactions.TABLE_NAME, Transactions._ID + "=" + id, null);
                getContext().getContentResolver().notifyChange(uri,null);
                getContext().getContentResolver().notifyChange(Category.CONTENT_URI,null);
                //Might be unnecessary as Category.CONTENT_URI should be enough, double check.
                //getContext().getContentResolver().notifyChange(Uri.parse(Category.CONTENT_URI + "/#"),null);
                getContext().getContentResolver().notifyChange(Transactions.CONTENT_URI_AMOUNT_BY_DAY,null);
                getContext().getContentResolver().notifyChange(Transactions.CONTENT_URI_AMOUNT_BY_MONTH,null);
                getContext().getContentResolver().notifyChange(Transactions.CONTENT_URI_HISTORY,null);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }


        return rowsDeleted;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        String id;

        switch (uriType){
            case ACCOUNTS:
                rowsUpdated = sqlDB.update(Accounts.TABLE_NAME,values,selection,selectionArgs);
                break;
            case ACCOUNTS_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)){
                    rowsUpdated = sqlDB.update(Accounts.TABLE_NAME,values, Accounts._ID + "=" + id, null);
                }else{
                    rowsUpdated = sqlDB.update(Accounts.TABLE_NAME, values, Accounts._ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            case CATEGORY:
                rowsUpdated = sqlDB.update(Category.TABLE_NAME,values,selection,selectionArgs);
                updateCategoryTransactions(values, selectionArgs);
                getContext().getContentResolver().notifyChange(Transactions.CONTENT_URI_AMOUNT_BY_DAY, null);
                break;

            case TRANSACTIONS:
                rowsUpdated = sqlDB.update(Transactions.TABLE_NAME,values,selection,selectionArgs);
                break;
            case CATEGORY_ID:
                id = uri.getLastPathSegment();
                rowsUpdated = sqlDB.update(Category.TABLE_NAME, values, selection, selectionArgs);

                getContext().getContentResolver().notifyChange(Transactions.CONTENT_URI,null);
                getContext().getContentResolver().notifyChange(Transactions.CONTENT_URI_AMOUNT_BY_DAY,null);
                break;

            case TRANSACTIONS_ID:
                id = uri.getLastPathSegment();
                rowsUpdated = sqlDB.update(Transactions.TABLE_NAME, values, Transactions._ID + "=" + id, null);

                getContext().getContentResolver().notifyChange(Category.CONTENT_URI,null);
                getContext().getContentResolver().notifyChange(Transactions.CONTENT_URI_AMOUNT_BY_DAY,null);
                getContext().getContentResolver().notifyChange(Transactions.CONTENT_URI_AMOUNT_BY_MONTH,null);
                getContext().getContentResolver().notifyChange(Transactions.CONTENT_URI_HISTORY,null);

                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);

        return rowsUpdated;
    }
    public void updateCategoryTransactions(ContentValues values, String[] selectionArgs){

        String newCategory = values.getAsString(Category.COLUMN_NAME);
        String oldCategory = selectionArgs[0];

        SQLiteDatabase db = database.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Transactions.COLUMN_CATEGORY,newCategory);
        db.update(Transactions.TABLE_NAME,contentValues,Transactions.COLUMN_CATEGORY + " = ?",new String[]{oldCategory});
        getContext().getContentResolver().notifyChange(Transactions.CONTENT_URI,null);
    }
}
