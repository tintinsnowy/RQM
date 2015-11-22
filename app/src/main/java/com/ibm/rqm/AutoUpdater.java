package com.ibm.rqm;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.ibm.rqm.Model.Executionresult;
import com.ibm.rqm.Model.Project;
import com.ibm.rqm.Model.Report;
import com.ibm.rqm.Model.Report$Table;
import com.ibm.rqm.Model.Testcase;
import com.ibm.rqm.Model.Testplan;
import com.ibm.rqm.Model.Workitem;
import com.ibm.rqm.utils.ImageUpdateListener;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Jack on 2015/4/6 0006.
 *
 * 负责所有的更新操作。对外的接口主要为：
 * 1.更新工程的alias, uuid,工程列表。updateProjects;
 * 2.更新testcases; updateTestcases;
 * 3.更新testplans updateTestplan;
 * 4.other..
 */
public class AutoUpdater {
    private static final String TAG = "AutoUpdater";

    private RequestQueue mQueue;
    //用来获取各种联网操作的Url
    private UrlManager mManager;
    private boolean isSuccess = false;
    XmlParser mParser;

    public AutoUpdater(RequestQueue queue, UrlManager urlManager) {
        mQueue = queue;
        mManager = urlManager;
        mParser = new XmlParser();
    }

    /**
     * 根据更新的类型获取对应的Url
     *@param kind 获取更新的类型
     *@return 返回对应的Url
     * */
    private String getUrl(int kind){
        String mUrl;
        switch (kind){
            case RQMConstant.PROJECTS: mUrl = mManager.getAllProjectsUrl(); break;
            case RQMConstant.PROJECTS_UUID: mUrl = mManager.getAllProjectsUUIDUrl(); break;
            case RQMConstant.QUERY_UUID: mUrl = mManager.getQueryUUIDUrl(); break;
            case RQMConstant.TEST_CASE: mUrl = mManager.getTestcasesUrl(); break;
            case RQMConstant.TEST_PLAN: mUrl = mManager.getTestplansUrl(); break;
            case RQMConstant.WORK_ITEM: mUrl = mManager.getWorkitemUrl();break;
            case RQMConstant.EXECUTION_RESULT: mUrl = mManager.getExecutionresultUrl();break;
            default:
                mUrl = mManager.getAllProjectsUrl(); break;
        }
        Log.d(TAG, "Download xml kind: " + RQMConstant.toString(kind));

        return mUrl;
    }

    void excuteLoginRequest(final Context context, final String userName, final String pwd, String rootUrl) {

        final String LOGIN_URL = rootUrl + "/authenticated/j_security_check";
        final SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        //第一个请求测试服务器能不能正确连接，并获得sessionID
        StringRequest accessRequest = new StringRequest(Request.Method.GET, rootUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Success to connect to Server! try to login");

                        //服务器连接成功，执行登录请求
                        StringRequest loginRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        mPrefs.edit().putBoolean("isLogined", true)
                                                .putBoolean("hasInitialized", true).apply();
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                mPrefs.edit().putBoolean("isLogined", false).commit();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("j_username", userName);
                                map.put("j_password", pwd);
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

                        };
                        mQueue.add(loginRequest);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Fail to connect to server!");

            }
        });
        mQueue.add(accessRequest);
    }

    /**
     *更新Projects。将更新的结果存入Project Table数据库中
     * @param listener 用于监听是否成功
     * */
    public void updateProjects(final UpdateListener listener){

        //首先，获取project的alias，解析。
        //接着，获取project对应的UUID，然后存入数据库。
        final StringRequest aliasRequest = new StringRequest(Request.Method.GET, getUrl(RQMConstant.PROJECTS),
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "download alias success!");
                //解析alias
                final ArrayList<Project> projectList = mParser.parseProjectXML(new ByteArrayInputStream(response.getBytes()));

                if(projectList.isEmpty()){
                    //通知listener没有Project
                    if (listener != null) {
                        listener.onUpdateFail(RQMConstant.NO_FRESH);
                    }
                    return;
                }
                StringRequest uuidRequest = new StringRequest(Request.Method.GET, getUrl(RQMConstant.PROJECTS_UUID),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //解析project的UUID
                                HashMap<String, String> hashMap = mParser.parseProjectUUID(
                                        new ByteArrayInputStream(response.getBytes()));

                                //先删除数据库中的旧数据
                                new Delete().tables(Project.class);
                                //根据HashMap设置对应project的UUID，并存入数据库
                                for(Project project : projectList){
                                    project.setUUID(hashMap.get(project.getTitle()));
                                    project.save();
                                }
                                //更新成功
                                if (listener != null) {
                                    listener.onUpdateSuccess();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                //加入请求队列
                uuidRequest.setTag(TAG);
                mQueue.add(uuidRequest);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "download xml failed!");
                if (listener != null)
                    listener.onUpdateFail(RQMConstant.CONNECT_FAILED);
            }
        });

        aliasRequest.setTag(TAG);
        mQueue.add(aliasRequest);
    }

    /**
     *更新Testcases。将更新的结果存入Testcase Table数据库中
     * @param listener 用于监听是否成功
     * */
    public void updateTestcase(final UpdateListener listener){
        StringRequest mRequest = new StringRequest(Request.Method.GET, getUrl(RQMConstant.TEST_CASE),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "download xml success!");
                        ArrayList<Testcase> list = mParser.parseTestcaseXML(new ByteArrayInputStream(response.getBytes()));
                        //删除以前的
                        new Delete().tables(Testcase.class);
                        //插入数据库
                        if(!list.isEmpty()){
                            for(Testcase testcase : list){
                                testcase.save();
                            }
                            if (listener != null)
                                listener.onUpdateSuccess();
                        }else {
                            if (listener != null)
                                listener.onUpdateFail(RQMConstant.NO_FRESH);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "download xml failed!");
                if (listener != null)
                    listener.onUpdateFail(RQMConstant.CONNECT_FAILED);
            }
        });

        mRequest.setTag(TAG);
        mQueue.add(mRequest);
    }

    /**
     *更新Testplans。将更新的结果存入Testplan Table数据库中
     * @param listener 用于监听是否成功
     * */
    public void updateTestplan(final UpdateListener listener){
        StringRequest mRequest = new StringRequest(Request.Method.GET, getUrl(RQMConstant.TEST_PLAN),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "download xml success!");
                        ArrayList<Testplan> list = mParser.parseTestplanXML(new ByteArrayInputStream(response.getBytes()));

                        new Delete().tables(Testplan.class);
                        //插入数据库
                        if(!list.isEmpty()){
                            for(Testplan testplan : list){
                                testplan.save();
                            }
                            if (listener != null)
                                listener.onUpdateSuccess();
                        }else {
                            if (listener != null)
                                listener.onUpdateFail(RQMConstant.NO_FRESH);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "download xml failed!");
                if (listener != null)
                    listener.onUpdateFail(RQMConstant.CONNECT_FAILED);
            }
        });

        mRequest.setTag(TAG);
        mQueue.add(mRequest);
    }

    /**
     *更新WorkItems。将更新的结果存入WorkItem Table数据库中
     * @param listener 用于监听是否成功
     * */
    public void updateWorkitem(final UpdateListener listener){
        StringRequest mRequest = new StringRequest(Request.Method.GET,getUrl(RQMConstant.WORK_ITEM),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "download xml success!");
                        ArrayList<Workitem> list = mParser.parseWorkItemXML(new ByteArrayInputStream(response.getBytes()));
                        //删除以前的
                        new Delete().tables(Workitem.class);
                        //插入数据库
                        if (!list.isEmpty()) {
                            for (Workitem workitem : list) {
                                workitem.save();
                                Log.d(TAG, "id" + workitem.getId() + "  " + "summary" + workitem.getSummary() + "  " +
                                        "title" + workitem.getTitle() + "  " + "updated" + workitem.getUpdated());
                            }
                            if (listener != null)
                                listener.onUpdateSuccess();
                        } else {
                            if (listener != null)
                                listener.onUpdateFail(RQMConstant.NO_FRESH);
                        }
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "download xml failed!");
                if (listener != null)
                    listener.onUpdateFail(RQMConstant.CONNECT_FAILED);
            }
        });

        mRequest.setTag(TAG);
        mQueue.add(mRequest);
    }

    /**
     *更新Executionresults。将更新的结果存入Executionresults Table数据库中
     * @param listener 用于监听是否成功
     * */
    public void updateExecutionresult(final UpdateListener listener){
        StringRequest mRequest = new StringRequest(Request.Method.GET,getUrl(RQMConstant.EXECUTION_RESULT),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "download xml success!");
                        ArrayList<Executionresult> list = mParser.parseExecutionresultXML(new ByteArrayInputStream(response.getBytes()));
                        //删除以前的
                        new Delete().tables(Executionresult.class);
                        //插入数据库
                        if (!list.isEmpty()) {
                            for (Executionresult executionresult : list) {
                                executionresult.save();
                            }
                            if (listener != null)
                                listener.onUpdateSuccess();
                        } else {
                            if (listener != null)
                                listener.onUpdateFail(RQMConstant.NO_FRESH);
                        }
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "download xml failed!");
                if (listener != null)
                    listener.onUpdateFail(RQMConstant.CONNECT_FAILED);
            }
        });

        mRequest.setTag(TAG);
        mQueue.add(mRequest);
    }

    /**
     *更新Reports。将更新的结果存入Report Table数据库中
     * @param listener 用于监听是否成功
     * */
    public void updateQueryUUID(final UpdateListener listener){
        final StringRequest mRequest = new StringRequest(Request.Method.GET, getUrl(RQMConstant.QUERY_UUID),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "download xml success!");
                        ArrayList<Report> reportList = mParser.parseReportXML(new ByteArrayInputStream(response.getBytes()));

                        new Delete().tables(Report.class);
                        //插入数据库
                        if(!reportList.isEmpty()){
                            for(Report report : reportList){
                                report.save();
                            }
                            if (listener != null)
                                listener.onUpdateSuccess();
                        }else {
                            if (listener != null)
                                listener.onUpdateFail(RQMConstant.NO_FRESH);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "download xml failed!");
                if (listener != null)
                    listener.onUpdateFail(RQMConstant.CONNECT_FAILED);
            }
        });

        mRequest.setTag(TAG);
        mQueue.add(mRequest);
    }

    /**
    *根据report的queryUUID获取该report的图片, 并保存path的文件中
     * @param context 上下文
     * @param reportName 报表名
     * @param queryUUID 查询的UUID
    */
    public void getReportPicture(final String queryUUID, final String reportName, final Context context, final UpdateListener listener){
        //用于获取report图片对应的UUID
        //该UUID在renderQuery返回的html代码片段中有
        final StringRequest renderQuery = new StringRequest(Request.Method.POST, mManager.getRenderQueryUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //利用正则表达式来寻找图片的UUID
                        Pattern pattern = Pattern.compile("IReportsContentService/contents/.*png");
                        Matcher matcher = pattern.matcher(response);

                        if(matcher.find()) {
                            String imageUUID = matcher.group();

                            //根据imageUUID获取图片
                            ImageRequest imageRequest = new ImageRequest(mManager.getReportImagePath() + imageUUID,
                                    new Response.Listener<Bitmap>() {
                                        @Override
                                        public void onResponse(Bitmap response) {
                                            //将图片保存到内存卡中
                                            try {

                                                OutputStream outStream = context.openFileOutput(reportName + ".png", Context.MODE_PRIVATE);
                                                response.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                                                outStream.flush();
                                                outStream.close();

                                                //通知listenr获取图片成功
                                                if (listener != null)
                                                    listener.onUpdateSuccess();
                                            } catch (Exception e){
                                                Log.d(TAG, "save " + reportName + "Picture failed!");
                                                if (listener != null)
                                                    listener.onUpdateFail(-1);
                                            }
                                        }
                                    }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d(TAG, "Download Image failed!");
                                    if (listener != null)
                                        listener.onUpdateFail(RQMConstant.CONNECT_FAILED);
                                }
                            });

                            //加入请求队列
                            imageRequest.setTag("imageRequest");
                            mQueue.add(imageRequest);
                        } else {
                            if (listener != null)
                                listener.onUpdateFail(-2);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "RenderQuery failed");
                if (listener != null)
                    listener.onUpdateFail(-1);
            }
        }){
            //设置renderQuery POST请求的参数。
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> mParamters = new HashMap<String, String>();
                //其中最重要的参数为projectAreaUUID和queryUUID
                mParamters.put("projectAreaUUID", mManager.getCurrentProjectUUID());
                mParamters.put("queryUUID", queryUUID);
                mParamters.put("accept", "text/json");
                mParamters.put("X-com-ibm-team-configuration-versions", "LATEST");
                mParamters.put("Accept-Encoding", "gzip, deflate");
                mParamters.put("Accept-Language", "zh-CN,zh;q=0.8");
                mParamters.put("Content-Type", "application/x-www-form-urlencoded");
                return mParamters;
            }
        };

        //加入请求队列
        renderQuery.setTag("readerQuery");
        mQueue.add(renderQuery);
    }

    /**
    * 根据report名字来更新图片。
    * 图片默认的存储路径的目录dirPath
    * reportName必须是RQMCONSTANTS中的一种报表名
     * 同@getReportPicture
    * */
    public void UpdateReportPicture(final Context context, final String reportName, final UpdateListener listener){

       /* //首先从数据库中获取对应的queryUUID
        Report report = new Select().from(Report.class)
                .where(Condition.column(Report$Table.NAME).eq(reportName)).querySingle();

        if(report == null){
            Log.d(TAG, "The Report table doesn't have the report:" + reportName);
            listener.onUpdateFail(-1);
            return;
        }
        //获取图片
        getReportPicture(report.getQueryUUID(), reportName, context, listener);*/


         List<Report> reportList = new Select().from(Report.class)
                .where(Condition.column(Report$Table.NAME).eq(reportName)).queryList();

        ImageUpdateListener mListner = new ImageUpdateListener(context, listener, reportList, this);
        mListner.start();
    }


    /**
     * 用于监听各种更新操作是否成功
     * */
    public interface UpdateListener{
        void onUpdateSuccess();
        void onUpdateFail(int errorCode);
    }
}

