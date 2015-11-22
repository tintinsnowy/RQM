package com.ibm.rqm;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by c on 2015/6/15.
 */
public class ImageDisplayPage extends Activity {

    private String path;

    DragImageView image;

    private int window_width, window_height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_image_display_page);
        path = getIntent().getStringExtra("path");
        image = new DragImageView(this);

        WindowManager manager = getWindowManager();
        window_width = manager.getDefaultDisplay().getWidth();
        window_height = manager.getDefaultDisplay().getHeight();

        image.setLayoutParams(new LinearLayout.LayoutParams(window_width,
                window_height));

        image.setPadding(0, 0, 0, 0);

        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        setContentView(image);

        path = getIntent().getStringExtra("path");
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        image.setImageBitmap(bitmap);
        image.setOnClickListener(new OnFinishImageClickListener());
    }

    private class OnFinishImageClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            finish();
        }
    }
}
