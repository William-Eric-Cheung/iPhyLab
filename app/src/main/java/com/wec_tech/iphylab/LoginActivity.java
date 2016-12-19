package com.wec_tech.iphylab;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.wec_tech.iphylab.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    Button btLogin;
    Button btFeedback;
    EditText etUser;
    EditText etPassword;
    EditText etVerifation;
    ImageView ivVerifation;
    Bitmap bmVerifation;

    String VERIFATIONURL = "http://phylab.buaa.edu.cn/checkcode.php";
    String LOGINURL = "http://phylab.buaa.edu.cn/login.php";
    String HOSTURL = "http://phylab.buaa.edu.cn";
    String MAINBODYHTML = "";
    String GETSCOREURL = "http://phylab.buaa.edu.cn/elect.php";
    String GETSCOREHOST = "http://phylab.buaa.edu.cn/index.php";

    private boolean isGetting = false;
    public static String[][] score = new String[3][10];
    public String name;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FeedbackAPI.init(this.getApplication(),FeedbackApplication.DEFAULT_APPKEY);
        initView();
        initEvent();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.arg1) {
                    case 1:
                        Toast.makeText(LoginActivity.this, "try to login", Toast.LENGTH_SHORT).show();
                        break;
                    /*case 1:
                        Toast.makeText(MainActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(MainActivity.this, "密码或用户名错误", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(MainActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        Toast.makeText(MainActivity.this, "验证码不能为空，如看不清请刷新", Toast.LENGTH_SHORT).show();
                        break;
                    */
                    case 5:
                        Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
                        break;
                    case 6:
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        break;
                    case 10:
                        Log.i("xyz","get checkcode");
                        ivVerifation.setImageBitmap(bmVerifation);
                        break;
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        DoGetVerifation();
    }

    private void initView() {
        btLogin = (Button) findViewById(R.id.bt_login);
        btFeedback = (Button) findViewById(R.id.bt_feedback);
        etUser = (EditText) findViewById(R.id.et_user);
        etPassword = (EditText) findViewById(R.id.et_password);
        etVerifation = (EditText) findViewById(R.id.et_verifation);
        ivVerifation = (ImageView) findViewById(R.id.iv_verifation);
    }

    private void initEvent() {
        FeedbackAPI.init(this.getApplication(),FeedbackApplication.DEFAULT_APPKEY);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.bt_login:
                        //Toast.makeText(LoginActivity.this, "try to login", Toast.LENGTH_SHORT).show();
                        DoLogin(etUser.getText().toString(), etPassword.getText().toString(), etVerifation.getText().toString());
                        //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        //startActivity(intent);
                        //finish();
                        break;
                    case R.id.bt_feedback:
                        OpenFeedbackPage(true);
                        break;
                }
            }
        };
        btLogin.setOnClickListener(onClickListener);
        btFeedback.setOnClickListener(onClickListener);
    }

    private void DoGetVerifation() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL verificationURL = new URL(VERIFATIONURL);
                    connection = (HttpURLConnection) verificationURL.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    bmVerifation = BitmapFactory.decodeStream(in);
                    //HttpPost httPost = new HttpPost(VERIFATIONURL);
                    //HttpClient client = new DefaultHttpClient();
                }/*
                    try {
                    HttpResponse httpResponse = client.execute(httPost);
                    byte[] bytes = new byte[1024];
                    bytes = EntityUtils.toByteArray(httpResponse.getEntity());
                    bmCheckcodeImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                } */
                catch (IOException e) {
                    e.printStackTrace();
                }
                if (bmVerifation == null)
                    Toast.makeText(LoginActivity.this, "获取验证码失败请连接至校园网", Toast.LENGTH_SHORT).show();
                Message msg = new Message();
                msg.arg1 = 10;
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void DoLogin(final String user, final String password, final String verifation) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient defaultclient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(LOGINURL);
                HttpResponse httpResponse;

                //设置post参数
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                params.add(new BasicNameValuePair("txtUid", user));
                params.add(new BasicNameValuePair("txtPwd", password));
                params.add(new BasicNameValuePair("txtChk", verifation));

                //获得个人主界面的HTML
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    httpResponse = defaultclient.execute(httpPost);
                    Log.i("xyz", String.valueOf(httpResponse.getStatusLine().getStatusCode()));

                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        StringBuffer sb = new StringBuffer();
                        HttpEntity entity = httpResponse.getEntity();
                        MAINBODYHTML = EntityUtils.toString(entity,"gb2312");
                        IsLoginSuccessful(MAINBODYHTML);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try{
                    Toast.makeText(LoginActivity.this, "try to login", Toast.LENGTH_SHORT).show();
                    URL url = new URL(LOGINURL);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    OutputStream out = connection.getOutputStream();
                    String data = "txtUid="+user+"txtPwd"+password+"txtChk"+verifation;
                    out.write(data.getBytes());
                    out.flush();
                    Toast.makeText(LoginActivity.this, "try to login", Toast.LENGTH_SHORT).show();
                    if(connection.getResponseCode()==200){
                        IsLoginSuccessful(GETSCOREURL);
                        Toast.makeText(LoginActivity.this,"登陆成功",Toast.LENGTH_LONG);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.arg1 = 1;
                handler.sendMessage(msg);
            }
        }).start();*/
    }

    private void IsLoginSuccessful(String loginresult) {

        Document doc = Jsoup.parse(loginresult);
        Elements alert = doc.select("script[language]");
        Elements success = doc.select("td.class");

        Message msg = new Message();
        //先判断是否登录成功，若成功直接退出
        for (Element link : success) {
            //获取所要查询的URL,这里对应地址按钮的名字叫成绩查询
            if (link.text().equals("mefont")) {
                Log.i("LoginActivity", "登录成功");
                msg.arg1 = 6;
                handler.sendMessage(msg);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
        //msg.arg1 = 5;
        //handler.sendMessage(msg);
        for (Element link : alert) {
            //刷新验证码
            DoGetVerifation();
            //获取错误信息
            if (link.data().contains("验证码不正确")) {
                Log.i("xyz", "验证码错误");
                msg.arg1 = 0;
                handler.sendMessage(msg);
            } else if (link.data().contains("你还没有登陆，无权访问。")) {
                msg.arg1 = 5;
                handler.sendMessage(msg);
            }
            /*else if (link.data().contains("用户名不能为空")) {
                Log.i("xyz", "用户名不能为空");
                msg.arg1 = 1;
                handler.sendMessage(msg);
            } else if (link.data().contains("密码错误")) {
                Log.i("xyz", "密码或用户名错误");
                msg.arg1 = 2;
                handler.sendMessage(msg);
            } else if (link.data().contains("密码不能为空")) {
                Log.i("xyz", "密码不能为空");
                msg.arg1 = 3;
                handler.sendMessage(msg);
            } else if (link.data().contains("验证码不能为空，如看不清请刷新")) {
                Log.i("xyz", "验证码不能为空，如看不清请刷新");
                msg.arg1 = 4;
                handler.sendMessage(msg);
            }
            else if (link.data().contains("用户名不存在")) {
                Log.i("xyz", "用户名不存在或未按照要求参加教学活动");
                msg.arg1 = 5;
                handler.sendMessage(msg);
            }
        }*/

        }
    }
    private void DoGetName() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        };

        if (MAINBODYHTML.equals("")) {
            Toast.makeText(LoginActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
        }
        Document doc = Jsoup.parse(MAINBODYHTML);
        Elements links = doc.select("td.class");
        StringBuffer sb = new StringBuffer();
        for (Element link : links) {
            //获取所要查询的URL,这里对应地址按钮的名字叫成绩查询
            if (link.text().equals("mefont")) {
                sb.append(link.data().toString());
            }
        }
        GETSCOREURL = sb.toString();
        Log.i("xyz", GETSCOREURL);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //HttpClient httpClient = new DefaultHttpClient();
                //HttpPost httpPost = new HttpPost(GETSCOREHOST + GETSCOREURL);
                //HttpResponse httpResponse = httpClient.execute(httpPost);
                //Log.i("xyz", String.valueOf(httpResponse.getStatusLine().getStatusCode()));
                // StringBuffer sb2 = new StringBuffer();
                // HttpEntity entity = httpResponse.getEntity();
                // String re = EntityUtils.toString(entity);
                //parse(re);
                parse(GETSCOREHOST);
                Message msg = new Message();
                handler.sendMessage(msg);
            }
        }).start();
    }



    private void parse(String parse) {

        Document doc = Jsoup.parse(parse);
        Elements elements = doc.select("table").select("td.class");
        for(Element e: elements){
            if(e.text().equals("mefont")){

            }
        }
        /*for (int i = 0; i < trs.size(); i++) {
            Elements tds = trs.get(i).select("td");
            for (int j = 0; j < tds.size(); j++) {
                String text = tds.get(j).text();
                score[i][j] = text;
                Log.i("xyz", score[i][j]);
            }
        }*/
    }

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
