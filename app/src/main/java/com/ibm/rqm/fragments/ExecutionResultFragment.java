package com.ibm.rqm.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.rqm.AutoUpdater;
import com.ibm.rqm.IBMApplication;
import com.ibm.rqm.Model.Executionresult;
import com.ibm.rqm.R;
import com.ibm.rqm.adapter.ExecutionresultAdapter;
import com.raizlabs.android.dbflow.sql.language.Select;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExecutionResultFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    TextView mHeader;
    ListView mList;
    ExecutionresultAdapter mAdapter;

    private SwipeRefreshLayout mSwipeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragement_header_list, container, false);
        mHeader = (TextView)rootView.findViewById(R.id.header);
        mList = (ListView)rootView.findViewById(R.id.list);

        setUpList();

        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_header_list);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        mSwipeLayout.setOnRefreshListener(this);//设置下拉的监听
        mSwipeLayout.setEnabled(false);

        mList.setOnScrollListener(new AbsListView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(AbsListView absListView,int i){

                    }

            @Override
            public void onScroll(AbsListView absListView,int firstVisibleItem,
            int visibleItemCount,int totalItemCount){
            //View firstView=absListView.getChildAt(firstVisibleItem);

            // 当firstVisibleItem是第0位。如果firstView==null说明列表为空，需要刷新;或者top==0说明已经到达列表顶部, 也需要刷新
            if(firstVisibleItem==0){
            mSwipeLayout.setEnabled(true);
            }else{
            mSwipeLayout.setEnabled(false);
                    }
                }
        });


        return rootView;
    }

    @Override
    public void onRefresh() {
        reflesh();
    }

    private void setUpList(){
        if (mAdapter == null){
            mAdapter = new ExecutionresultAdapter(getActivity().getBaseContext());
        }
        mList.setAdapter(mAdapter);
        long Num = new Select().count().from(Executionresult.class).count();
        mHeader.setText("There are " + Num + " Executionresults in the current project");
    }


    // TODO 刷新操作需要添加
    public void reflesh()
    {
        AutoUpdater autoUpdater = ((IBMApplication)getActivity().getApplication()).getUpdater();
        autoUpdater.updateExecutionresult(new AutoUpdater.UpdateListener() {
            @Override
            public void onUpdateSuccess() {
                //Toast.makeText(getActivity(), R.string.reflesh, Toast.LENGTH_SHORT).show();
                mAdapter.notifyDataSetChanged();
                if(null != mSwipeLayout)
                    mSwipeLayout.setRefreshing(false);
            }

            @Override
            public void onUpdateFail(int errorCode) {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.reflesh_fail, Toast.LENGTH_SHORT).show();
                if(null != mSwipeLayout)
                    mSwipeLayout.setRefreshing(false);
            }
        });

    }

}
