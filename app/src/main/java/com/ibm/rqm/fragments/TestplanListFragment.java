package com.ibm.rqm.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.ibm.rqm.Model.Testplan;
import com.ibm.rqm.R;
import com.ibm.rqm.adapter.TestplanAdapter;
import com.raizlabs.android.dbflow.sql.language.Select;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 */
public class TestplanListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    TextView mHeader;
    ListView mList;
    TestplanAdapter mAdapter;

    private SwipeRefreshLayout mSwipeLayout;
    @Nullable
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

        mList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                View firstView = absListView.getChildAt(firstVisibleItem);

                // 当firstVisibleItem是第0位。如果firstView==null说明列表为空，需要刷新;或者top==0说明已经到达列表顶部, 也需要刷新
                if (firstVisibleItem == 0) {
                    mSwipeLayout.setEnabled(true);
                } else {
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
            mAdapter = new TestplanAdapter(getActivity().getBaseContext());
        }
        mList.setAdapter(mAdapter);
        long workItemNum = new Select().count().from(Testplan.class).count();
        mHeader.setText("There are " + workItemNum + " testplans in the current project");
    }

    // TODO
    public void reflesh()
    {
        AutoUpdater autoUpdater = ((IBMApplication)getActivity().getApplication()).getUpdater();
        autoUpdater.updateTestplan(new AutoUpdater.UpdateListener() {
            @Override
            public void onUpdateSuccess() {
                if (getActivity() == null)
                    return;
                Toast.makeText(getActivity(), R.string.reflesh, Toast.LENGTH_SHORT).show();
                mAdapter.notifyDataSetChanged();
                if(null != mSwipeLayout)
                    mSwipeLayout.setRefreshing(false);
            }

            @Override
            public void onUpdateFail(int errorCode) {
                if (getActivity() == null)
                    return;
                Toast.makeText(getActivity(), R.string.reflesh_fail, Toast.LENGTH_SHORT).show();
                //todo: swipe refresh
                if(null != mSwipeLayout)
                    mSwipeLayout.setRefreshing(false);
            }
        });
    }
}
