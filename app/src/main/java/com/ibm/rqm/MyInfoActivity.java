package com.ibm.rqm;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.rqm.Model.Project;
import com.ibm.rqm.Model.Testplan;
import com.ibm.rqm.cropimage.ChooseDialog;
import com.ibm.rqm.cropimage.CropHelper;
import com.ibm.rqm.utils.CircularImage;
import com.ibm.rqm.utils.OSUtils;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.File;
import java.io.FileOutputStream;


public class MyInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private SharedPreferences mPrefs;
    private CircularImage mImageView;
    private Button mLogoutBtn;
    private TextView userName;
    private TextView projectNum;
    private TextView planNum;
    private CropHelper mCropHelper;
    private ChooseDialog mDialog;


    private static final int PICTURE = 0;
    private static final int CAMERA = 1;
    private static final int RESULT_REQUEST_CODE = 2;

    private static final String IMAGE_FILE_NAME = "faceImage.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.title_activity_my_info);
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);

        }

        mCropHelper=new CropHelper(this, OSUtils.getSdCardDirectory()+"/head.png");
        mDialog=new ChooseDialog(this, mCropHelper);

        mPrefs = ((IBMApplication)getApplication()).getPrefs();
        mImageView = (CircularImage)findViewById(R.id.info_header);
        mImageView.setImageBitmap(BitmapFactory.decodeFile(getString(R.string.head_path)));
        mImageView.setOnClickListener(this);

        userName = (TextView)findViewById(R.id.info_userName);
        userName.setText(mPrefs.getString("username","NULL"));

        projectNum = (TextView)findViewById(R.id.project_num);
        planNum = (TextView)findViewById(R.id.plan_num);

        //设置project的个数
        long num = new Select().count().from(Project.class).count();
        projectNum.setText("Project : " + num + " project(s)");

        long num2 = new Select().count().from(Testplan.class).count();
        planNum.setText("TestPlan : " + num2 + " plan(s)");

        mLogoutBtn =  (Button)findViewById(R.id.logout_btn);
        mLogoutBtn.setOnClickListener(new OnLogoutClickListener());
    }

    @Override    public void onClick(View view) {
        mDialog.popSelectDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("onActivityResult", requestCode+"**"+resultCode);
        if(requestCode==RESULT_CANCELED){
            return;
        }else{
            switch (requestCode) {
                case CropHelper.HEAD_FROM_ALBUM:
                    mCropHelper.getDataFromAlbum(data);
                    Log.e("onActivityResult", "接收到图库图片");
                    break;
                case CropHelper.HEAD_FROM_CAMERA:
                    mCropHelper.getDataFromCamera(data);
                    Log.e("onActivityResult", "接收到拍照图片");
                    break;
                case CropHelper.HEAD_SAVE_PHOTO:
                    if(data!=null&&data.getParcelableExtra("data")!=null){
                        mImageView.setImageBitmap((Bitmap)data.getParcelableExtra("data"));
                        mCropHelper.savePhoto(data, OSUtils.getSdCardDirectory()+"/myHead.png");
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    //返回按钮！
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent localIntent = new Intent(this,MainActivity.class);
        localIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(localIntent);
        finish();
        return super.onOptionsItemSelected(item);
    }

    private class OnLogoutClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.avatar);
            //
            File file = new File(getString(R.string.head_path));
            if(!file.exists())
                file.mkdirs();
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file.getPath());
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mPrefs.edit().putBoolean("isLogined", false).commit();
            Toast.makeText(MyInfoActivity.this, "Logout Success!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

}