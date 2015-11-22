package com.ibm.rqm;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.Volley;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.util.Calendar;

/**
 * Created by JACK on 2014/12/14.
 * 修改网络框架为volley，整个程序保持一个volley队列的实例
 *去掉httpClientHelper
 */

public class IBMApplication extends Application implements
        SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String TAG = "IBMApplication";
    public static final String REQUEST_TAG = "VolleyRequests";

    private AlarmManager mAManager;
    private SharedPreferences mPrefs;
    private boolean serviceRunning;

    private boolean isLogined;
    private boolean isNotifyOpen;

    private RequestQueue requestQueue = null;
    private HttpClientHelper httpClientHelper = null;

    UrlManager mUrlManager = null;
    AutoUpdater mUpdater = null;
    public SharedPreferences getPrefs() {
        return mPrefs;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //DBFlow
        FlowManager.init(this);
        //UrlManager
        mUrlManager = new UrlManager(this);

        //AutoUpdater
        httpClientHelper = new HttpClientHelper();
        mUpdater = new AutoUpdater(this.getRequestQueue(), mUrlManager);

        Log.d(TAG, "Application create..");
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPrefs.registerOnSharedPreferenceChangeListener(this);

        mAManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        isLogined = mPrefs.getBoolean("isLogined", false);
    }

    /**
     * 用来监听各种设置的变化
     * */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("isLogined")){
            if(!(isLogined = sharedPreferences.getBoolean(key, false))){
                Intent intent = new Intent(this, LoginActivity.class);
                //launch the login Activity. Clear all Activities in the Stack
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }else if(key.equals("user_name")){
            Log.d(TAG, "userName Changed!");
        }else if(key.equals("Notification_Time")){
            //TODO 鎬濊�杩欐鏄惁鏈夊瓨鍦ㄦ剰涔�
            String time = mPrefs.getString("notification_time","");
            int hourOfDay = Integer.parseInt(time.substring(0,1));
            int minute = Integer.parseInt(time.substring(3,4));

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            c.set(Calendar.HOUR_OF_DAY,hourOfDay);
            c.set(Calendar.MINUTE, minute);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            mAManager = (AlarmManager)getSystemService(ALARM_SERVICE);
            mAManager.set(AlarmManager.RTC_WAKEUP,
                    c.getTimeInMillis(),
                    generatePendingIntent()
            );
        }else if(key.equals("host") || key.equals("port")){
            mUrlManager.hostOrPortChanged();
        }else if(key.equals("projectUUID")){
            mUrlManager.currentProjectChanged();
        }else if(key.equals("isConnected")){
            boolean isConnectd = sharedPreferences.getBoolean("isConnected", false);
            boolean hasInitialized = sharedPreferences.getBoolean("hasInitialized", true);
            if (isConnectd && isLogined() && !hasInitialized) {
                //执行登录请求。
                String userName = sharedPreferences.getString("username", "");
                String pwd = sharedPreferences.getString("password", "");
                getUpdater().excuteLoginRequest(this, userName, pwd, mUrlManager.getRootUrl());
            }
        }
    }

    private PendingIntent generatePendingIntent(){

        //TODO: setting the data that is going to sent to Receiver.
        //for exmaple:
        //intent.putExtra("city", mPrefs.getString("city", "NoPlace"));
        Intent intent = new Intent(this, RQMReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        return pIntent;
    }

    public synchronized AutoUpdater getUpdater(){
        return mUpdater;
    }


    private int getRepeatTimeLength(){
        return Integer.parseInt(mPrefs.getString("notification_frequency", "0"));
    }

    public boolean isLogined() {
        return isLogined;
    }

    public boolean isNotifyOpen() {
        isNotifyOpen = mPrefs.getBoolean("notifications_enable", false);
        return isNotifyOpen;
    }

    public RequestQueue getRequestQueue() {

        if(requestQueue == null){
            //为了方便cookie的管理，依然沿用了之前的HttpClientHelper中配置的HttpClient对象
            requestQueue = new Volley().newRequestQueue(this,
                    new HttpClientStack(httpClientHelper.getHttpClient()));
        }

        return requestQueue;
    }

    // 全局的volley请求。
    public <T> void addToRequestQueue(Request<T> req, String tag){
        //当tag为空是， 设置默认tag为REQUEST_TAG
        req.setTag(TextUtils.isEmpty(tag) ? REQUEST_TAG : tag);

        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req){
        req.setTag(REQUEST_TAG);
        getRequestQueue().add(req);
    }

    public void cancelAllRequests(Object tag){
        if(requestQueue != null){
            requestQueue.cancelAll(tag);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        //关闭数据库
        FlowManager.destroy();
    }

    /**
     * 方便UrlManager监听url的改变。
     * 主要包括host, port, project
     * */
    public interface UrlChangedListener{
        void hostOrPortChanged();
        void currentProjectChanged();
    }

    //方便MainActivity监听个人信息改变，包括头像，名字等.
    public interface ProfileChangedListener{
        void nickNameChanged();
        //TODO 实现其他的
    }
}
