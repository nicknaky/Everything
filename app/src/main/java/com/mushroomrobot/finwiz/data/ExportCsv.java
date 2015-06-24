package com.mushroomrobot.finwiz.data;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;

import com.mushroomrobot.finwiz.data.EverythingContract.Transactions;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Nick.
 */
public class ExportCsv implements Runnable{

    Context context;

    String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    String fileName = "FinWizData.csv";
    String filePath = baseDir + File.separator + fileName;
    File f = new File(filePath);

    public ExportCsv(Context context){
        this.context = context;
    }


    public Intent getEmailIntent(){

        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/csv");
        //emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"someone@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "FinWiz Data");
        //emailIntent.putExtra(Intent.EXTRA_TEXT, "Body Text");
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filePath));
        return emailIntent;
    }

    @Override
    public void run() {

        SQLiteDatabase db = new EverythingDbHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from transactions order by transactions.date", null);

        try{
            FileWriter fileWriter = new FileWriter(f);
            //CSVWriter writer = new CSVWriter(fileWriter, ';');

            //String[] entries = "data;category;description;amount".split(";");
            //writer.writeNext(entries);

            fileWriter.write("date, category, description, amount");
            fileWriter.write("\n");

            while (cursor.moveToNext()){

                long dateInMillis = cursor.getLong(cursor.getColumnIndex(Transactions.COLUMN_DATE));
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(dateInMillis);
                String date = sdf.format(calendar.getTime());

                String category = cursor.getString(cursor.getColumnIndex(Transactions.COLUMN_CATEGORY));

                String description = cursor.getString(cursor.getColumnIndex(Transactions.COLUMN_DESCRIPTION));

                double amount = cursor.getDouble(cursor.getColumnIndex(Transactions.COLUMN_AMOUNT)) / 100;

                //String entriesString = date + ";" + category + ";" + description + ";" + String.valueOf(amount);
                //entries = entriesString.split(";");
                //writer.writeNext(entries);

                String entry = date + "," + category + "," +  "\"" + description + "\"" + "," +  String.valueOf(amount) + ",";
                fileWriter.write(entry);
                fileWriter.write("\n");
            }

            //writer.close();

            fileWriter.flush();
            fileWriter.close();

            try {
                context.startActivity(getEmailIntent());
            } catch (android.content.ActivityNotFoundException e){
                Toast.makeText(context, "No email clients installed.", Toast.LENGTH_SHORT).show();
            }

        } catch (java.io.IOException e){
            System.err.print("Error with FileWriter");
        }
    }
}
