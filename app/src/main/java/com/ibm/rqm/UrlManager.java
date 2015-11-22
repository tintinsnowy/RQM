package com.ibm.rqm;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ibm.rqm.Model.Project;
import com.ibm.rqm.Model.Project$Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

/**
 * Created by Jack on 2015/5/14 0014.
 * 用来管理和生成各种查询的URL
 * 实现了 @link IBMApplication.UrlChangedListener接口，
 * 用来监听host, port, project的改变，从而改变Url的对应的字段
 */
public class UrlManager implements IBMApplication.UrlChangedListener{

    private static final String TAG = "UrlManager";

    private static final String CONTEXT = "qm";

    //用于获取testplan,testcase,projects等
    private static final String RESOURCE_PATH = "/service/com.ibm.rqm.integration.service." +
            "IIntegrationService/resources/";
    //用于获取report的queryUUID
    private static final String REPORT_PATH = "/service/com.ibm.team.reports.common.internal.service." +
            "IReportRestService/";
    //用于获取project的UUID
    private static final String PROCESS_PATH = "/service/com.ibm.team.process.internal.common.service." +
            "IProcessRestService/allProjectAreas?hideArchivedProjects=true";

    private SharedPreferences mPrefs;
    //host, port, alias有可能会改变，所以必须有外部的接口
    private String mHost;
    private int mPort;
    private String mCurrentAlias;
    private String mCurrentUUID;

    public UrlManager(Context ctx){
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        //init
        hostOrPortChanged();
        currentProjectChanged();
    }

    public String getRootUrl(){
        return mHost + ":" + mPort + "/" + CONTEXT;
    }

    public String getAllProjectsUrl(){
        return getRootUrl() + RESOURCE_PATH + "projects";
    }

    public String getTestcasesUrl(){
        return getRootUrl() + RESOURCE_PATH + mCurrentAlias  + "/testcase?abbrivate=false";
    }

    public String getTestplansUrl(){
        return getRootUrl() + RESOURCE_PATH + mCurrentAlias  + "/testplan?abbrivate=false";
    }

    public String getWorkitemUrl(){
        return getRootUrl() + RESOURCE_PATH + mCurrentAlias + "/executionworkitem?abbrivate=false";
    }

    public String getExecutionresultUrl(){
        return getRootUrl() + RESOURCE_PATH + mCurrentAlias + "/executionresult?abbrivate=false";
    }

    public String getAllProjectsUUIDUrl(){
        return getRootUrl() + PROCESS_PATH;
    }

    public String getQueryUUIDUrl(){
        return getRootUrl() + REPORT_PATH + "queryDescriptors?projectAreaUUID=" + mCurrentUUID;
    }

    public String getRenderQueryUrl(){
        return getRootUrl() + "/service/com.ibm.team.reports.common.internal.service.IReportRestService/renderQuery";
    }

    public String getReportImagePath(){
        return getRootUrl() + "/service/com.ibm.team.reports.service.internal.";
    }

    public String getCurrentProjectUUID(){
        return mCurrentUUID;
    }

    @Override
    public void hostOrPortChanged() {
        //初始化host和port
        mHost = mPrefs.getString("host", "localhost");
        mPort = mPrefs.getInt("port", 0);
    }

    /**
     *当前的Project改变之后，需要根据UUID获得对应project的Alias，已完成Url拼接
     * */
    @Override
    public void currentProjectChanged() {
        mCurrentUUID = mPrefs.getString("projectUUID", "");

        //从数据库中查询对应的alias
        Project project = new Select().from(Project.class)
                .where(Condition.column(Project$Table.UUID).is(mCurrentUUID))
                .querySingle();
        if(project != null){
            mCurrentAlias = project.getAlias();
        }else {
            //TODO 当前的project为空的时候，刷新project。考虑使用service。
            mCurrentAlias = "none";
            Log.d(TAG, "No project!");
        }
    }
}
