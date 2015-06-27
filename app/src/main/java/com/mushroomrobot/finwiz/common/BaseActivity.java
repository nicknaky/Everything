package com.mushroomrobot.finwiz.common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by NLam.
 */
public class BaseActivity extends AppCompatActivity{

    private static final int NOT_SET = 78912;

    private static int UNLOCK_CODE;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*float d = getResources().getDisplayMetrics().density;
        int px = 10;
        int padding = (int) (d * px + 0.5f);
        ImageView view = (ImageView) findViewById(android.R.id.home);
        view.setPadding(padding, 0, padding, 0);
        */
    }

}
