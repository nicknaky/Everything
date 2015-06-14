package com.mushroomrobot.finwiz.data;

import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.io.FileWriter;

/**
 * Created by Nick.
 */
public class ExportCsv {

    String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    String fileName = "FinWizData.csv";
    String filePath = baseDir + File.separator + fileName;
    File f = new File(filePath);


    public void write(){

        try{
            FileWriter fileWriter = new FileWriter(f);

            fileWriter.write("Testing 1, 2, 3");

            fileWriter.flush();
            fileWriter.close();

        } catch (java.io.IOException e){
            System.err.print("Error with FileWriter");
        }

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


}
