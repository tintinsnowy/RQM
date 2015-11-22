package com.ibm.rqm;

/**
 * Created by JACK on 2015/5/6.
 * 拥有NavigationDrawer的MainActivity。
 */
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.View;

import com.ibm.rqm.fragments.ExecutionResultFragment;
import com.ibm.rqm.fragments.ProjectListFragment;
import com.ibm.rqm.fragments.TestcaseListFragment;
import com.ibm.rqm.fragments.TestplanListFragment;
import com.ibm.rqm.fragments.WorkItemFragment;

import java.util.ArrayList;
import java.util.List;

import br.liveo.interfaces.NavigationLiveoListener;
import br.liveo.navigationliveo.NavigationLiveo;

public class MainActivity extends NavigationLiveo implements NavigationLiveoListener {

    private final static String TAG = "MainActivity";

    public List<String> mListNameItem;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = ((IBMApplication)getApplication()).getPrefs();
        //todo : user_info
        this.mUserName.setText(mPrefs.getString("username","NULL"));
        mUserName.setTextSize(20);
        mUserName.setTextColor(Color.BLACK);
    }

    //在这里设置导航栏的用户信息
    @Override
    public void onUserInformation() {
        //User information here
        //this.mUserEmail.setText("rudsonlive@gmail.com");
        Bitmap bitmap = BitmapFactory.decodeFile(getString(R.string.head_path));
        if(bitmap == null){
            //如果自定义的头像没有，则使用默认头像
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
        }
        this.mUserPhoto.setImageBitmap(bitmap);
        this.mUserBackground.setImageResource(R.drawable.ic_user_background);
    }

    //这里设置导航栏的选择信息：
    @Override
    public void onInt(Bundle savedInstanceState) {
        //Creation of the list items is here

        // set listener {required}
        this.setNavigationListener(this);

        if (savedInstanceState == null) {
            //First item of the position selected from the list

            //在这里判断是否有已经选择了工程，如果没有选择工程，则首先选择工程。
            String projectUUID = ((IBMApplication)getApplication()).getPrefs()
                    .getString("projectUUID", "none");
            if(projectUUID.equals("none")){
                this.setDefaultStartPositionNavigation(6);
            }else{
                this.setDefaultStartPositionNavigation(0);
            }

        }

        // name of the list items
        mListNameItem = new ArrayList<>();

        //reports
        mListNameItem.add(0, getString(R.string.title_fragment_reports));

        //testplan
        mListNameItem.add(1, getString(R.string.title_testplan_list));
        //testcase
        mListNameItem.add(2, getString(R.string.title_testcase_list));

        //This item will be a subHeader
        mListNameItem.add(3, getString(R.string.more_markers));
        //workItem
        mListNameItem.add(4, getString(R.string.title_workitem_list));
        //execution result
        mListNameItem.add(5, getString(R.string.title_execution_result));
        //switch projects
        mListNameItem.add(6, getString(R.string.title_project_switch));
        // icons list items

        List<Integer> mListIconItem = new ArrayList<>();
        mListIconItem.add(0, R.drawable.ic_report);
        mListIconItem.add(1, R.drawable.ic_testplan); //Item no icon set 0
        mListIconItem.add(2, R.drawable.ic_testcase); //Item no icon set 0
        mListIconItem.add(3, 0); //When the item is a subHeader the value of the icon 0
        mListIconItem.add(4, R.drawable.ic_workitem);
        mListIconItem.add(5, R.drawable.ic_excution);
        mListIconItem.add(6, R.drawable.ic_switch);

        //{optional} - Among the names there is some subheader, you must indicate it here
        List<Integer> mListHeaderItem = new ArrayList<>();
        mListHeaderItem.add(3);

        //{optional} - Among the names there is any item counter, you must indicate it (position) and the value here
        SparseIntArray mSparseCounterItem = new SparseIntArray(); //indicate all items that have a counter

        //If not please use the FooterDrawer use the setFooterVisible(boolean visible) method with value false
        this.setFooterInformationDrawer(R.string.settings, R.drawable.ic_settings_black_24dp);

        this.setNavigationAdapter(mListNameItem, mListIconItem, mListHeaderItem, mSparseCounterItem);
    }

    @Override
    public void onItemClickNavigation(int position, int layoutContainerId) {
        //获得FragmentManager，对fragment进行替换操作:
        FragmentManager mFragmentManager = getSupportFragmentManager();
        //根据点击的位置，来判断目标fragment：
        Fragment mFragment = null;

        switch (position){
            case 0: mFragment = new FragmentMain().newInstance(mListNameItem.get(position));
                break;
            case 1: mFragment = new TestplanListFragment();
                break;

            case 2: mFragment = new TestcaseListFragment();
                break;

            case 4: mFragment = new WorkItemFragment();
                break;

            case 5: mFragment = new ExecutionResultFragment();
                break;

            case 6: mFragment = new ProjectListFragment();
                break;
            default: mFragment = new FragmentMain().newInstance(mListNameItem.get(position));
                break;
        }
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(mListNameItem.get(position));
        if (mFragment != null){
            mFragmentManager.beginTransaction().replace(layoutContainerId, mFragment).commit();
        }
    }

    @Override
    public void onPrepareOptionsMenuNavigation(Menu menu, int position, boolean visible) {

        //hide the menu when the navigation is opens
        /*switch (position) {
            case 0:
                menu.findItem(R.id.menu_reflesh).setVisible(!visible);
                break;

        }*/
    }

    @Override
    public void onClickUserPhotoNavigation(View v) {
        //user photo onClick
        //Toast.makeText(this, R.string.open_user_profile, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MyInfoActivity.class));
    }

    @Override
    public void onClickFooterItemNavigation(View v) {
        //footer onClick
        startActivity(new Intent(this, SettingsActivity.class));
    }

}
