package com.mushroomrobot.finwiz.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Nick.
 */
public class ExportCsv {

    String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    String fileName = "FinWizData.csv";
    String filePath = baseDir + File.separator + fileName;
    File f = new File(filePath);
    FileWriter fileWriter = null;

    public void write(){

        try{
            fileWriter = new FileWriter(f);
            fileWriter.write("Testing 1, 2, 3");
        } catch (java.io.IOException e){
            System.err.print("Error with FileWriter");
        } finally {
            try{
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e){
                System.err.print("Error flushing or closing writer");
            }

        }


    }


}
