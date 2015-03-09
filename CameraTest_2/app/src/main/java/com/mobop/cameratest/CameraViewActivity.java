package com.mobop.cameratest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Nils on 08.03.2015.
 */
public class CameraViewActivity extends Activity implements View.OnTouchListener,
        View.OnClickListener{
    final private String TAG = "CameraViewActivity";
    private FrameLayout background, foreground;
    private CameraView cameraView;
    private ImageView iv_city_pointer;
    private TextView tv_orientation;
    private int view_width, view_height, city_pointer_width, city_pointer_height;
    private int touch_x, touch_y, city_pointer_margin_left, city_pointer_margin_top;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);

        background = (FrameLayout) findViewById(R.id.background);
        view_width = background.getWidth();
        view_height = background.getHeight();

        cameraView = (CameraView) findViewById(R.id.camera_preview);
        cameraView.setOnTouchListener(this);

        foreground = (FrameLayout) findViewById(R.id.foreground);

        iv_city_pointer = new ImageView(this);
        iv_city_pointer.setImageResource(R.drawable.locator_symbol);
        iv_city_pointer.setOnClickListener(this);
        //city_pointer_height = iv_city_pointer.getHeight();
        //city_pointer_width = iv_city_pointer.getWidth();
        city_pointer_height = 50;
        city_pointer_width = 50;
        Log.d(TAG, "City pointer width: " + String.valueOf(city_pointer_width) +
                ", City pointer height: " + String.valueOf(city_pointer_height));

        tv_orientation = (TextView) findViewById(R.id.tv_orientation);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "onTouch");
        touch_x = (int) event.getX();
        touch_y = (int) event.getY();

        Log.d(TAG, "X: " + String.valueOf(touch_x) + ", Y: " + String.valueOf(touch_y));

        city_pointer_margin_left = (int) Math.round(touch_x-(city_pointer_width/2));
        city_pointer_margin_top = (int) Math.round(touch_y-(city_pointer_height/2));
        Log.d(TAG, "Margin left: " + String.valueOf(city_pointer_margin_left) + ", Margin top: " +
                String.valueOf(city_pointer_margin_top));

        foreground.removeView(iv_city_pointer);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(city_pointer_width,
                city_pointer_height);
        params.leftMargin = city_pointer_margin_left;
        params.topMargin = city_pointer_margin_top;
        params.gravity = Gravity.LEFT + Gravity.TOP;

        foreground.addView(iv_city_pointer, params);
        //foreground.addView(iv_city_pointer);

        tv_orientation.setText("X: " + String.valueOf(touch_x) + ", Y: " + String.valueOf(touch_y));

        return true;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick");
        foreground.removeView(v);
        tv_orientation.setText("X: Gone, Y: Gone");
    }
}
