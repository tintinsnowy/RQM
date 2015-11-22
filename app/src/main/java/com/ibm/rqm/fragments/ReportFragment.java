package com.ibm.rqm.fragments;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.rqm.AutoUpdater;
import com.ibm.rqm.IBMApplication;
import com.ibm.rqm.Model.Report;
import com.ibm.rqm.Model.Report$Table;
import com.ibm.rqm.R;
import com.ibm.rqm.utils.FileUtils;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * ReportFragment主要用在ViewPager中来展示图片
 */
public class ReportFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private static final String TAG = "ReportFragment";

    private static final String REPORT_NAME = "reportName";
    private static final String SECTION_NUM = "sectionNum";

    private String reportName;
    private int sectionNum;
    private Report report;

    TextView tvTitle;
    TextView tvDescription;
    ImageView ivReport;

    private Handler handler;

    private SwipeRefreshLayout mSwipeLayout;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * 产生一个新的实例需要两个参数
     * @param param1 报表的名字
     * @param param2 该Fragment在ViewPager中所处的位置(position)
     */
    public static ReportFragment newInstance(String param1, int param2) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();
        args.putString(REPORT_NAME, param1);
        args.putInt(SECTION_NUM, param2);
        fragment.setArguments(args);
        return fragment;
    }

    //产生新实例不能用该构造函数，而需要使用newInstance
    public ReportFragment() {
        // Required empty public constructor
    }

    /*
    * 当Fragment被重新绘制的时候，恢复参数的值来保存Fragment的状态
    * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            reportName = getArguments().getString(REPORT_NAME);
            sectionNum = getArguments().getInt(SECTION_NUM);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_report, container, false);

        tvTitle = (TextView)rootView.findViewById(R.id.title);
        tvTitle.setText(reportName);

        tvDescription = (TextView)rootView.findViewById(R.id.description);
        if(reportName.equals(getString(R.string.execution_trend_report))){
            tvDescription.setText(R.string.description_execution_trend_report);
        }
        else if(reportName.equals(getString(R.string.requirements_coverage))){
            tvDescription.setText(R.string.description_requirements_coverage);
        }
        else if(reportName.equals(getString(R.string.execution_status_by_owner))){
            tvDescription.setText(R.string.description_execution_status_by_owner);
        }
        else if(reportName.equals(getString(R.string.execution_status_by_testcase))){
            tvDescription.setText(R.string.description_execution_status_by_testcase);
        }

        ivReport = (ImageView)rootView.findViewById(R.id.report_image);
        ivReport.setOnClickListener(new OnReportImageClickListener());
        displayImage();
/*

        String path = getActivity().getFilesDir() + "/" + reportName + ".png";
        final File file = new File(path);

        if ( !file.exists() ) {
            //进行刷新操作。
            reflesh();
        }*/

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1) {
                    displayImage();
                }
            }
        };

        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_report);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        mSwipeLayout.setOnRefreshListener(this);//设置下拉的监听

        return rootView;
    }

    @Override
    public void onRefresh() {
        reflesh();
    }


    /**
     * 刷新图片操作。
     * 成功后 调用displayImage显示
     * */
    public void reflesh()
    {

        Activity mActivity = getActivity();
        if (mActivity == null) {
            return;
        }
        final AutoUpdater autoUpdater = ((IBMApplication)mActivity.getApplication()).getUpdater();
        autoUpdater.UpdateReportPicture(getActivity(), reportName, new AutoUpdater.UpdateListener() {
            @Override
            public void onUpdateSuccess() {
                //显示图片
                mNotifyImageSetChanged();
                if (null != mSwipeLayout)
                    mSwipeLayout.setRefreshing(false);
            }

            @Override
            public void onUpdateFail(int errorCode) {
                if (null != mSwipeLayout) {
                    mSwipeLayout.setRefreshing(false);
                }
                if (ReportFragment.this.getActivity() == null) {
                    return;
                }
                String toastText;
                if (-2 == errorCode) {
                    toastText = getString(R.string.report_unvailable);
                } else {
                    toastText = getString(R.string.reflesh_fail);
                }
                Toast.makeText(ReportFragment.this.getActivity(),
                        toastText, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void mNotifyImageSetChanged() {
        Message message = handler.obtainMessage();
        message.what = 1;
        handler.sendMessage(message);
    }

    /**
     *  显示图片
     *试着从内存中读取图片，如果失败则尝试刷新
     * TODO 找一张默认的正在加载的图片。
     * */
    private void displayImage() {
        try {
            String path = getActivity().getFilesDir() + "/" + reportName + ".png";
            final File file = new File(path);

            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                ivReport.setImageBitmap(bitmap);
                file.deleteOnExit();
            } else {
                throw new Exception();
            }
        } catch (Exception e){
            //失败，使用默认的图片
            ivReport.setImageResource(R.drawable.ic_picture);
            //进行刷新操作。
            reflesh();
        }
    }

    /**
     *用于名为reportName的Report实例。
     *@return 注意返回的Report可能为null
     * */
    private Report getReport() {
        if(report != null){
            return report;
        }

        //从数据库中选取report
        report = new Select().from(Report.class)
                .where(Condition.column(Report$Table.NAME).eq(reportName)).querySingle();

        //数据库中没有, 则执行更新操作，异步获取Report
        //返回的Report可能是null!
        if (report == null){
            final AutoUpdater autoUpdater = ((IBMApplication) getActivity().getApplication()).getUpdater();
            autoUpdater.updateQueryUUID(new AutoUpdater.UpdateListener() {
                @Override
                public void onUpdateSuccess() {
                    //从数据库中选取report
                    report = new Select().from(Report.class)
                            .where(Condition.column(Report$Table.NAME).eq(reportName)).querySingle();
                    if (report == null) {
                        tvDescription.setText(R.string.no_description);
                    }

                }

                @Override
                public void onUpdateFail(int errorCode) {
                    /*Toast.makeText(ReportFragment.this.getActivity(),
                            R.string.connect_fail, Toast.LENGTH_SHORT).show();*/
                }
            });
        }

        return report;
    }

    /**
     *用于使用系统默认的图片查看来 查看报表。
     * TODO 实现OnReportImageClickListener
     * */
    private class OnReportImageClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Log.d(TAG, reportName + "ReportImage Clicked!");
            String path = getActivity().getFilesDir() + "/" + reportName + ".png";
            final File file = new File(path);
            if (file.exists()) {
                String desPath = Environment.getExternalStorageDirectory().getPath()
                        + "/RQM/" + reportName + ".png";
                FileUtils.copyFile(path, desPath);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                File newFile = new File(desPath);
                intent.setDataAndType(Uri.fromFile(newFile), "image/*");
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "image is loading", Toast.LENGTH_SHORT).show();
            }

        }
    }

}
