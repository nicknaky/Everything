package com.mushroomrobot.finwiz.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Nick on 4/27/2015.
 */
public class EverythingContract {

    public static final String CONTENT_AUTHORITY = "com.mushroomrobot.finwiz.data";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ACCOUNTS = "accounts";
    public static final String PATH_CATEGORY = "category";
    public static final String PATH_TRANSACTIONS = "transactions";


    public static final class Accounts implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACCOUNTS).build();

        //Table name
        public static final String TABLE_NAME = "accounts";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_BUDGET_FLAG = "budget_flag";
        //Stored as integer in cents. So "$6.00" would be "600".  Make sure to divide by 100 when displaying data.
        public static final String COLUMN_BALANCE = "balance";
        //Date which is first set when creating a new account and setting balance date.  Is updated if
        //user makes a reconciliation edit on the account.
        public static final String COLUMN_LAST_UPDATE = "last_date";

        public static Uri buildAccountsUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class Category implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();

        public static final Uri CONTENT_URI_OVERVIEW = CONTENT_URI.buildUpon().appendPath("OVERVIEW").build();
        public static final Uri CONTENT_URI_FREQUENCY = CONTENT_URI.buildUpon().appendPath("FREQUENCY").build();

        public static final String TABLE_NAME = "category";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_BUDGET = "budget";

        public static Uri buildCategoryUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        //Helper strings made by queries.  Remember using TOTAL allows us to add null values together (as zero's). Whereas using SUM will return NULL
        //SQLite code: total(transactions.amount) as spent
        public static final String COLUMN_SPENT = "spent";
        //SQLite code: (budget - total(transactions.amount)) as remaining
        public static final String COLUMN_REMAINING = "remaining";
        //SQLite code: (total(transactions.amount)/budget*100) as percent
        public static final String COLUMN_PERCENT = "percent";

    }

    public static final class Transactions implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRANSACTIONS).build();

        public static final Uri CONTENT_URI_AMOUNT_BY_DAY = CONTENT_URI.buildUpon().appendPath("BY_DAY").build();


        public static final Uri CONTENT_URI_HISTORY = CONTENT_URI.buildUpon().appendPath("HISTORY").build();
        public static final Uri CONTENT_URI_AMOUNT_BY_MONTH = CONTENT_URI.buildUpon().appendPath("BY_MONTH").build();

        //WARNING "transaction" is a SQLite keyword, thus make sure the "transactions" table name is referenced correctly!
        public static final String TABLE_NAME = "transactions";
        public static final String COLUMN_CATEGORY = "category";
        //Can be null, optional field if user desires to have a note of transaction other than category name.
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_AMOUNT = "amount";
        //Date and timestamp stored as milliseconds.
        public static final String COLUMN_DATE = "date";
        //TODO: Placerholder for future feature to have recurring transactions (bills and income).
        public static final String COLUMN_RECURRENCE = "recurrence";
        //TODO: Placeholder for future feature to have transactions linked to an account to provide closer budgeting.
        public static final String COLUMN_ACCOUNT = "account";
        //TODO: Type of transaction, whether expense or income. Will be a future feature. For now assume all transactions are expenses.
        public static final String COLUMN_TYPE = "type";

        public static Uri buildTransactionsUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
