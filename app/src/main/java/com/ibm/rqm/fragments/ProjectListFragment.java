package com.ibm.rqm.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.rqm.AutoUpdater;
import com.ibm.rqm.FragmentMain;
import com.ibm.rqm.IBMApplication;
import com.ibm.rqm.Model.Executionresult;
import com.ibm.rqm.Model.Project;
import com.ibm.rqm.Model.Report;
import com.ibm.rqm.Model.Testcase;
import com.ibm.rqm.Model.Testplan;
import com.ibm.rqm.Model.Workitem;
import com.ibm.rqm.R;
import com.ibm.rqm.adapter.ProjectAdapter;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.File;

/**
 *ProjectListFragment 用来显示 Project列表，方便用户切换Project
 *
 */
public class ProjectListFragment  extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "ProjectListFragment";

    ListView mList;
    private SwipeRefreshLayout mSwipeLayout;
    private Handler handler;
    private ProjectAdapter mAdapter;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private ProgressBar progressBar;
    //private SwitchProjectTask switchProjectTask = null;
    /**
     * 空的构造函数
     */
    public ProjectListFragment() {
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        manager = getFragmentManager();
        progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleLarge);
        progressBar.setVisibility(View.INVISIBLE);

        View rootView = inflater.inflate(R.layout.fragement_header_list, container, false);
        TextView textView = (TextView)rootView.findViewById(R.id.header);
        textView.setVisibility(View.GONE);
        mList = (ListView)rootView.findViewById(R.id.list);
        setUpList();

        if(mAdapter.getCount() == 0)
            reflesh();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        //setEmptyText("There are " + adapter.getCount() + " projects.");
                        setUpList();
                        break;
                    default:
                        //setEmptyText("There is no project!");
                        break;
                }
            }
        };


        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_header_list);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        mSwipeLayout.setOnRefreshListener(this);//设置下拉的监听
        mSwipeLayout.setEnabled(false);

        mList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0 ) {
                    mSwipeLayout.setEnabled(true);
                } else {
                    mSwipeLayout.setEnabled(false);
                }
            }
        });

        mList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        //获取被点击Item对应的Project
                        Project project = mAdapter.getItem(i);
                        //切换任务，设置List不可见，progressBar可见
                        progressBar.setVisibility(View.VISIBLE);

                        IBMApplication app = ((IBMApplication)getActivity().getApplication());

        /*
        switchProjectTask = new SwitchProjectTask(app, project);
        switchProjectTask.execute();*/

                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        String curProjectUUID = prefs.getString("projectUUID", "");
                        if (!curProjectUUID.equals(project.getUUID())){

                            //先改变当前Project的UUID
                            prefs.edit().putString("projectUUID", project.getUUID()).commit();

                            //删除无效的testplan,testcase，workitem等。
                            new Delete().tables(Testcase.class, Testplan.class, Workitem.class, Report.class, Executionresult.class);
                            //删除图表
                            String dirPath = getActivity().getFilesDir() + "/";
                            String [] reports = {getResources().getString(R.string.execution_status_by_testcase)
                                    ,getResources().getString(R.string.execution_status_by_owner)
                                    ,getResources().getString(R.string.execution_trend_report)
                                    ,getResources().getString(R.string.requirements_coverage)};

                            for (String name : reports) {
                                File file = new File(dirPath + "/" + name + ".png");
                                if (file != null) {
                                    file.delete();
                                }
                            }
                            final AutoUpdater autoUpdater = app.getUpdater();
                            AutoUpdater.UpdateListener listener = new AutoUpdater.UpdateListener() {
                                @Override
                                public void onUpdateSuccess() {
                                    autoUpdater.updateTestplan(null);
                                    autoUpdater.updateWorkitem(null);
                                    autoUpdater.updateTestcase(null);
                                    autoUpdater.updateExecutionresult(null);


                                    progressBar.setVisibility(View.GONE);
                                    switchToMainFragment();
                                }

                                @Override
                                public void onUpdateFail(int errorCode) {

                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "切换Project失败", Toast.LENGTH_SHORT).show();
                                }
                            };
                            autoUpdater.updateQueryUUID(listener);
                        } else {

                            progressBar.setVisibility(View.GONE);
                            switchToMainFragment();
                        }
                    }
                }
        );
        return rootView;
    }

    private void switchToMainFragment(){
        transaction = manager.beginTransaction();
        FragmentMain fragmentMain = new FragmentMain().newInstance("Reports");
        transaction.replace(this.getId(), fragmentMain, "Reports");
        transaction.commit();
    }

    private void mNotifyDataSetChanged() {
        mAdapter = new ProjectAdapter(this.getActivity());
        Message message = handler.obtainMessage();
        message.what = 1;
        handler.sendMessage(message);
        //adapter.notifyDataSetChanged();
    }

    private void setUpList(){
        if (mAdapter == null){
            mAdapter = new ProjectAdapter(getActivity().getBaseContext());
        }
        mList.setAdapter(mAdapter);
        long workItemNum = new Select().count().from(Testcase.class).count();
    }

    @Override
    public void onRefresh() {
        reflesh();
    }

    // TODO 重新刷新操作
    public void reflesh()
    {
        final AutoUpdater autoUpdater = ((IBMApplication)getActivity().getApplication()).getUpdater();
        autoUpdater.updateProjects(new AutoUpdater.UpdateListener() {
            @Override
            public void onUpdateSuccess() {
                mNotifyDataSetChanged();
                Log.d(TAG, "Project Num:" + mAdapter.getCount());
                if (null != mSwipeLayout)
                    mSwipeLayout.setRefreshing(false);
            }

            @Override
            public void onUpdateFail(int errorCode) {
                Toast.makeText(getActivity(), getString(R.string.reflesh_fail), Toast.LENGTH_SHORT).show();
                if (null != mSwipeLayout)
                    mSwipeLayout.setRefreshing(false);
            }
        });
    }


}
