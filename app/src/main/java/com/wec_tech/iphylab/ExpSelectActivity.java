package com.wec_tech.iphylab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;

/**
 * Created by William Eric Cheung on 12/8/2016.
 */

public class ExpSelectActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isGetting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exp_select);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_exp_select);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_select);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_select);
        navigationView.setNavigationItemSelectedListener(this);

        FeedbackAPI.init(this.getApplication(),FeedbackApplication.DEFAULT_APPKEY);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_select);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(ExpSelectActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            //super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_record_button) {
            Intent intent = new Intent(ExpSelectActivity.this, ExpRecordActivity.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_select_button) {
            Intent intent = new Intent(ExpSelectActivity.this, ExpSelectActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_document) {
            Intent intent = new Intent(ExpSelectActivity.this, ExpDocumentActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_state) {
            Intent intent = new Intent(ExpSelectActivity.this, ExpStateActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_send) {
            OpenFeedbackPage(true);
        } else if (id == R.id.nav_about){
            Intent intent = new Intent(ExpSelectActivity.this,AboutActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_select);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //Feedback Page
    private void OpenFeedbackPage(final boolean isOpenFeedback){
        FeedbackAPI.init(this.getApplication(),FeedbackApplication.DEFAULT_APPKEY);
        final Activity context = this;
        //如果500ms内init未完成, openFeedbackActivity会失败, 可以延长时间>500ms
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isOpenFeedback) {
                    FeedbackAPI.openFeedbackActivity();
                } else {
                    /*FeedbackAPI.getFeedbackUnreadCount(new IUnreadCountCallback() {
                        @Override
                        public void onSuccess(final int unreadCount) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast toast = Toast.makeText(MainActivity.this, "未读数：" + unreadCount, Toast.LENGTH_SHORT);
                                    toast.show();
                                    isGetting = false;
                                }
                            });
                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });*/
                }
                isGetting = false;
            }
        }, 500);
    }
}
