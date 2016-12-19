package com.wec_tech.iphylab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Layout;
import android.view.Window;
import android.widget.RelativeLayout;

/**
 * Created by William Eric Cheung on 12/7/2016.
 */

public class AboutActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.acitivity_about);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AboutActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
