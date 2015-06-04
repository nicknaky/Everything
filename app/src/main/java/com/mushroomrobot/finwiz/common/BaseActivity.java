package com.mushroomrobot.finwiz.common;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by NLam.
 */
public class BaseActivity extends Activity {

    private static final int NOT_SET = 78912;

    private static int UNLOCK_CODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
