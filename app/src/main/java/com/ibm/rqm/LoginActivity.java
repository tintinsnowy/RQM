package com.ibm.rqm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ibm.rqm.utils.CircularImage;

import java.util.HashMap;
import java.util.Map;


/**
 * A login screen that offers login via email/password
 *将用于登录验证的AsyncTask该为使用volley。
 *
 *
 */
public class LoginActivity extends AppCompatActivity{

    private static final String TAG = "LoginActivity";
    private SharedPreferences mPrefs;

    private RequestQueue mQueue = null;

    // UI references.
    private EditText mUserNameView;
    private EditText mPasswordView;
    private EditText mHostView;
    private EditText mPortView;
    private View mProgressView;
    private View mLoginFormView;

    private CircularImage mAvatarView;
    private Bitmap userAvatar;

    boolean isSuccess = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle(R.string.title_activity_login);

        //获得请求队列
        mQueue = ((IBMApplication)getApplication()).getRequestQueue();

        mProgressView = findViewById(R.id.login_progress);
        mLoginFormView = findViewById(R.id.login_form);

        // Set up the login form.
        mUserNameView = (EditText) findViewById(R.id.userName);

        mPasswordView = (EditText) findViewById(R.id.password);
        mAvatarView = (CircularImage) findViewById(R.id.avatar);
        mAvatarView.setImageResource(R.drawable.avatar);

        mHostView = (EditText) findViewById(R.id.host);
        mPortView = (EditText) findViewById(R.id.port);

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


        mPrefs = ((IBMApplication)getApplication()).getPrefs();
        if(mPrefs.getBoolean("isLogined", false)){
            boolean isConnected = false;
            ConnectivityManager connectivityManager =
                    (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetInfo == null || !activeNetInfo.isConnected()) {
                    isConnected = false;
                } else {
                    isConnected = true;
                }
            }

            if (isConnected) {
                //任然联网认证账号。
                // perform the user login attempt.
                String userName = mPrefs.getString("username", null);
                String password = mPrefs.getString("password", null);
                String host = mPrefs.getString("host", null);
                Integer port = mPrefs.getInt("port", 0);

                //autosave
                mUserNameView.setText(userName);
                mPasswordView.setText(password);
                mHostView.setText(host);
                mPortView.setText(port.toString());

                showProgress(true);

                //执行登录请求。
                executeLogin(userName, password, host, port);
            } else {
                mPrefs.edit().putBoolean("hasInitialized", false).commit();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

        }
    }

    /**
     * ExecuteLogin函数用来执行登陆请求
     * @param host  服务器地址
     * @param port 端口
     * @param userName 用户名
     * @param pwd 密码
     * */
    private void executeLogin(final String userName, final String pwd, String host, int port) {
		//测试，绕过登录。。

        if(userName == null || userName.equals("test")){
            mPrefs.edit().putBoolean("isLogined", true).commit();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

		//*********************************************//

        //保存当前提交的输入。
        mPrefs.edit().putString("username", userName)
            .putString("password", pwd)
            .putString("host", host)
            .putInt("port", port)
            .commit();

        String ROOT_URL = host + ":" + port + "/qm";
        final String LOGIN_URL = ROOT_URL + "/authenticated/j_security_check";

        //第一个请求测试服务器能不能正确连接，并获得sessionID
        StringRequest accessRequest = new StringRequest(Request.Method.GET, ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Success to connect to Server! try to login");

                        //服务器连接成功，执行登录请求
                        LoginRequest loginRequest = new LoginRequest(LOGIN_URL, userName, pwd);
                        mQueue.add(loginRequest);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Fail to connect to server!");
                        showProgress(false);
                        Toast.makeText(LoginActivity.this, getString(R.string.error_connection_failed),
                                Toast.LENGTH_SHORT).show();
                        mHostView.requestFocus();
                    }
                });
        mQueue.add(accessRequest);
    }

    /*
    * 登录请求类
    * 重写了getParams方法，用于提交用户名和密码
    * 重写了parseNetworkResponse方法，用于从header中判断是否登录成功
    * */

    class LoginRequest extends StringRequest{
        String mUserName;
        String mPwd;


        public LoginRequest(String url, String userName, String pwd){
            super(Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if (isSuccess) {
                        showProgress(false);

                        //保持登陆状态
                        mPrefs.edit().putBoolean("isLogined", true).commit();

                        //TODO 在这里发送广播，AutoUpdater更新projects。
                        ((IBMApplication)getApplication()).getUpdater().updateProjects(null);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }else{
                        showProgress(false);
                        mPrefs.edit().putBoolean("isLogined", false).commit();
                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                        mPasswordView.requestFocus();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showProgress(false);
                    mPrefs.edit().putBoolean("isLogined", false).commit();
                    mHostView.setError(getString(R.string.error_connection_failed));
                    mHostView.requestFocus();
                }
            });

            mUserName = userName;
            mPwd = pwd;
        }

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            Map<String, String> map = new HashMap<String, String>();
            map.put("j_username", mUserName);
            map.put("j_password", mPwd);
            return map;
        }

        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {
            Map<String, String> responseHeaders = response.headers;
            String key = "X-com-ibm-team-repository-web-auth-msg";
            String value = responseHeaders.get(key);

            //TODO 检查服务器cookie，判断不完全，可能绕过登陆。

            if(value != null && (value.equals("authrequired") || value.equals("authfailed"))){
                isSuccess = false;
            }else{
                isSuccess = true;
            }

            return super.parseNetworkResponse(response);
        }
    }


    //点击两次返回键退出
    private long exitTime = 0;
    @Override
    public void onBackPressed() {

        if((System.currentTimeMillis() - exitTime) > 2000) {
            exitTime = System.currentTimeMillis();
            Toast.makeText(this, "Press back twice to exit.", Toast.LENGTH_SHORT).show();
        }else {
            finish();
            System.exit(0);
        }

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid userName, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     * attemptLogin函数首先检查 各个字段是否有问题，然后执行登陆操作
     */
    public void attemptLogin() {

        // Reset errors.
        mUserNameView.setError(null);
        mPasswordView.setError(null);
        mHostView.setError(null);
        mPortView.setError(null);

        // Store values at the time of the login attempt.
        String userName = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String host = mHostView.getText().toString();
        String portStr = mPortView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(userName)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        }

        //check for a valid host and port
        if(TextUtils.isEmpty(host)){
            mHostView.setError(getString(R.string.error_field_required));
            focusView = mHostView;
            cancel = true;
        }

        if(TextUtils.isEmpty(portStr)){
            mPortView.setError(getString(R.string.error_field_required));
            focusView = mPortView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            executeLogin(userName, password, host, Integer.parseInt(portStr));
        }
    }


    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 3;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}



