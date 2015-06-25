package com.mushroomrobot.finwiz.account;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mushroomrobot.finwiz.R;
import com.mushroomrobot.finwiz.data.EverythingContract.Accounts;
import com.mushroomrobot.finwiz.data.EverythingDbHelper;

import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Nick on 4/27/2015.
 */
public class AddAccountFragment extends Fragment {

    EverythingDbHelper dbHelper = new EverythingDbHelper(getActivity());
    static EditText accountDate, accountName, accountBalance;
    RadioButton asset, debt;
    Button createAccount;
    TextView configureTitle;
    long accountId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_addaccount, container, false);

        configureTitle = (TextView) rootView.findViewById(R.id.configure_title);

        accountName = (EditText) rootView.findViewById(R.id.account_name_edittext);
        accountBalance = (EditText) rootView.findViewById(R.id.currentBalance_edit);
        accountDate = (EditText) rootView.findViewById(R.id.accountDate_edit);


        asset = (RadioButton) rootView.findViewById(R.id.asset_button);
        debt = (RadioButton) rootView.findViewById(R.id.debt_button);

        createAccount = (Button) rootView.findViewById(R.id.create_account_button);

        accountName.setOnClickListener(mClickListener);

        accountBalance.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(s)) {
                    accountBalance.removeTextChangedListener(this);

                    String replaceable = String.format("[%s,.\\s]", NumberFormat.getCurrencyInstance().getCurrency().getSymbol());
                    String cleanString = s.toString().replaceAll(replaceable, "");

                    BigDecimal parsed;
                    try {
                        parsed = new BigDecimal(cleanString).divide(new BigDecimal(100));
                    } catch (NumberFormatException e) {
                        parsed = new BigDecimal(0.00);
                    }
                    String formatted = NumberFormat.getCurrencyInstance().format(parsed);
                    current = formatted;
                    accountBalance.setText(formatted);
                    accountBalance.setSelection(formatted.length());
                    accountBalance.addTextChangedListener(this);
                }
            }
        });

        accountBalance.setOnClickListener(mClickListener);
        accountDate.setOnClickListener(mClickListener);

        asset.setOnClickListener(mClickListener);
        debt.setOnClickListener(mClickListener);
        createAccount.setOnClickListener(mClickListener);


        accountId = getArguments().getLong("accountId");
        if (accountId!=-1){
            Uri mUri = Uri.parse(getArguments().getString("uri"));
            Log.v("accountId",String.valueOf(accountId));
            populateWithAccountInfo(accountId, mUri);
        }

        return rootView;
    }

    private void populateWithAccountInfo(long id, Uri uri){

        String mSelectionClause = Accounts._ID + " = ?";
        String[] mSelectionArgs = {Long.toString(id)};

        Cursor cursor = getActivity().getContentResolver().query(uri,null,mSelectionClause,mSelectionArgs,null);
        cursor.moveToFirst();
        accountName.setText(cursor.getString(cursor.getColumnIndex(Accounts.COLUMN_NAME)));
        accountBalance.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(Accounts.COLUMN_BALANCE))));
        accountDate.setText(cursor.getString(cursor.getColumnIndex(Accounts.COLUMN_LAST_UPDATE)));
        String accountType = cursor.getString(cursor.getColumnIndex(Accounts.COLUMN_TYPE));
        if (accountType.equals("Asset")){
            asset.setChecked(true);
        }else if (accountType.equals("Debt")){
            debt.setChecked(true);
        }
        configureTitle.setText(R.string.configure_update_title);
        getActivity().getActionBar().setTitle("Update Account");

    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == accountDate) {
                FragmentManager fm = getFragmentManager();
                DialogFragment newFragment = new DateFragment();
                newFragment.show(fm, "date");
            }
            if (v == asset) {
                asset.setChecked(true);
                debt.setChecked(false);
            }
            if (v == debt) {
                asset.setChecked(false);
                debt.setChecked(true);
            }

            //TODO: This is lazy and bad coding. Need to refactor this directly into saveAccount() where the errors are occurring.
            //Code technically works because the errors are being caught before actual database changes, but this can be
            //dangerous if errors are caught after changes are made.
            if (v == createAccount) {
                try {
                    saveAccount();
                }
                catch (SQLiteConstraintException e) {
                    Toast.makeText(getActivity(), "Account name already in use.", Toast.LENGTH_SHORT).show();
                }
                catch (NullPointerException e) {
                    Toast.makeText(getActivity(), "Could not create the account, please make sure all fields are valid.", Toast.LENGTH_SHORT).show();
                }
                catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), "Please enter a valid balance.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    public void saveAccount() {

        //TODO: Need to implement try catch to show missing fields error

        String saveName = accountName.getText().toString();
        if (saveName.equals("")){
            saveName = null;
        }
        Log.v("Saved name: ", saveName);

        String cleanedBalance = accountBalance.getText().toString().replaceAll("[^0-9]", "");
        Log.v(cleanedBalance, cleanedBalance);
        int saveBalance = Integer.valueOf(cleanedBalance);

        String saveDate = accountDate.getText().toString();
        if (saveDate.equals("")){
            saveDate = null;
        }
        Log.v("Saved Date: ", saveDate);

        String saveType;
        if (asset.isChecked()) {
            saveType = "Asset";
        } else if (debt.isChecked()) {
            saveType = "Debt";
        } else saveType = null;
        Log.v("Saved Type: ", saveType);

        EverythingDbHelper dbHelper = new EverythingDbHelper(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        //Retrieves number of rows in table.
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + Accounts.TABLE_NAME, null);
        cursor.moveToFirst();

        //Sets the unique id based on current rows; remember to close the cursor after use.
        int id = 1 + cursor.getInt(0);
        cursor.close();


        ContentValues contentValues = new ContentValues();
        //contentValues.put(Accounts._ID, id);
        contentValues.put(Accounts.COLUMN_NAME, saveName);
        contentValues.put(Accounts.COLUMN_BALANCE, saveBalance);
        contentValues.put(Accounts.COLUMN_LAST_UPDATE, saveDate);
        contentValues.put(Accounts.COLUMN_TYPE, saveType);

        //TODO: This is reserved for future implementations of Budget Feature
        contentValues.put(Accounts.COLUMN_BUDGET_FLAG, 0);

        if (accountId!=-1){
            db.update(Accounts.TABLE_NAME,contentValues,"_ID = " + accountId,null);
        }
        else {
            db.insertOrThrow(Accounts.TABLE_NAME, null, contentValues);
        }
        startActivity(new Intent(getActivity(), DisplayAccountActivity.class));
        getActivity().finish();

        Log.v("Saved id: ", String.valueOf(id));
        Log.v("Saved balance: ", String.valueOf(saveBalance));

    }


    public static class DateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();

            String editDate = accountDate.getText().toString();
            if (editDate != ""){
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
                Date date;
                try{
                    date = sdf.parse(editDate);
                    c.setTime(date);
                } catch (ParseException e){
                    Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_SHORT);
                }
            }
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            String month = new DateFormatSymbols().getMonths()[monthOfYear];
            String result = month + " " + dayOfMonth + ", " + year;
            accountDate.setText(result);
        }
    }

}
