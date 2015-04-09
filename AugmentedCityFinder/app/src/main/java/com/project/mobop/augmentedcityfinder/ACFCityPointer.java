package com.project.mobop.augmentedcityfinder;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.Random;

/**
 * Created by Nils on 30.03.2015.
 */
public class ACFCityPointer extends LinearLayout implements View.OnClickListener, View.OnLongClickListener{
    private Context context;
    private final String TAG = "ACFCityPointer";
    private final int TEXT_SIZE = 20;

    private ImageView ivPointerFlag;
    private ImageView ivCountryFlag;

    private String cityName;
    private TextView tvCityName;
    private String countryName;
    private TextView tvCountryName;
    private String continentName;
    private TextView tvContinentName;
    private double distance;
    private TextView tvDistance;

    private boolean expanded = false;

    private int leftMargin, topMargin;
    private int cityPointerHeight, cityPointerWidth;

    private ACFCity city;

    public ACFCityPointer(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        setOnClickListener(this);
        setOnLongClickListener(this);

        constructCityPointer();
    }

    public ACFCityPointer(Context context, String cityName, String countryName, String continentName) {
        super(context);

        this.context = context;

        this.cityName = cityName;
        this.countryName = countryName;
        this.continentName = continentName;

        setOnClickListener(this);
        setOnLongClickListener(this);

        constructCityPointer();
    }

    public ACFCityPointer(Context context, ACFCity city) {
        super(context);

        this.context = context;
        this.city = city;

        this.cityName = city.getCityName();
        this.countryName = city.getCountryName();
        this.continentName = city.getContinentName();

        setOnClickListener(this);
        setOnLongClickListener(this);

        constructCityPointer();
    }

    private void constructCityPointer(){
        // Calculate text height in pixel
        float density = context.getApplicationContext().getResources().getDisplayMetrics().density;
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(TEXT_SIZE);
        Rect textBounds = new Rect();
        textPaint.getTextBounds("Py", 0, 2, textBounds);
        int textHeight = (int) Math.round(textBounds.height() * density);

        // Base layout
        setOrientation(VERTICAL);
        ViewGroup.LayoutParams linearParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(linearParams);

        // Components in layout
        TableRow.LayoutParams tableParams;
        Random rnd = new Random();
        int r = rnd.nextInt(256);
        int g = rnd.nextInt(256);
        int b = rnd.nextInt(256);
        int col = Color.argb(255, r, g, b);
        int invCol = Color.argb(100, 255-r, 255-g, 255-b);

        setBackgroundColor(invCol);

        // First row with pointer flag, city name and country flag
        LinearLayout imageHolder = new LinearLayout(context);
        imageHolder.setOrientation(HORIZONTAL);

        ivPointerFlag = new ImageView(context);
        ivPointerFlag.setImageResource(R.drawable.flag_small_pointer);
        tableParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, textHeight, 0f); // width, height, weight
        ivPointerFlag.setLayoutParams(tableParams);
        ivPointerFlag.setAdjustViewBounds(true);
        ivPointerFlag.setVisibility(View.VISIBLE);
        imageHolder.addView(ivPointerFlag);

        tvCityName = new TextView(context);
        tableParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0f); // width, height, weight
        tvCityName.setLayoutParams(tableParams);
        tvCityName.setText(this.cityName);
        tvCityName.setTextSize(TEXT_SIZE);
        tvCityName.setTextColor(col);
        tvCityName.setLines(1);
        tvCityName.setPaddingRelative(0, 0, 5, 0);
        tvCityName.setVisibility(View.VISIBLE);
        imageHolder.addView(tvCityName);

        ivCountryFlag = new ImageView(context);
        int resId = getResources().getIdentifier("flag_small_" + countryName.toLowerCase(), "drawable", context.getPackageName());
        if (resId == 0){
            Log.d(TAG, "No flag for " + countryName + " available.");
        }else{
            Log.d(TAG, countryName + " resId is: " + String.valueOf(resId));
            ivCountryFlag.setImageResource(resId);
        }
        tableParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, textHeight, 0f); // width, height, weight
        ivCountryFlag.setLayoutParams(tableParams);
        ivCountryFlag.setVisibility(View.VISIBLE);
        ivCountryFlag.setAdjustViewBounds(true);
        imageHolder.addView(ivCountryFlag);

        addView(imageHolder);

        // Second row with country name
        tvCountryName = new TextView(context);
        tvCountryName.setText(this.countryName);
        tvCountryName.setTextSize(TEXT_SIZE);
        tvCountryName.setTextColor(col);
        tvCountryName.setLines(1);
        tvCountryName.setVisibility(View.INVISIBLE);
        addView(tvCountryName);

        // Third row with continent name
        tvContinentName = new TextView(context);
        tvContinentName.setText(this.continentName);
        tvContinentName.setTextSize(TEXT_SIZE);
        tvContinentName.setTextColor(col);
        tvContinentName.setLines(1);
        tvContinentName.setVisibility(View.INVISIBLE);
        addView(tvContinentName);

        // Fourth row with distance
        tvDistance = new TextView(context);
        if (this.distance > 99999)
        {
            tvDistance.setText(((int) this.distance/1000) + "km");
        }else{
            tvDistance.setText(((int) this.distance) + "m");
        }
        tvDistance.setTextSize(TEXT_SIZE);
        tvDistance.setTextColor(col);
        tvDistance.setLines(1);
        tvDistance.setVisibility(View.VISIBLE);
        addView(tvDistance);

        addOnLayoutChangeListener(new OnLayoutChangeListener(){
            boolean isFirstCall = true;
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (isFirstCall) {
                    cityPointerHeight = getHeight();
                    tvCountryName.setVisibility(View.GONE);
                    tvContinentName.setVisibility(View.GONE);
                    isFirstCall = false;
                }
                cityPointerWidth = getWidth();
            }
        });
    }


    /***********************************************************************************************
    * Getter and setter methods for private fields.
    ***********************************************************************************************/
    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;

        tvCityName.setText(this.cityName);
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;

        tvCountryName.setText(this.countryName);
    }

    public String getContinentName() {
        return continentName;
    }

    public void setContinentName(String continentName) {
        this.continentName = continentName;

        tvContinentName.setText(this.continentName);
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance){
        this.distance = distance;

        if (this.distance > 99999)
        {
            tvDistance.setText(((int) this.distance/1000) + "km");
        }else{
            tvDistance.setText(((int) this.distance) + "m");
        }
    }

    public int getLeftMargin() {
        return leftMargin;
    }

    public void setLeftMargin(int leftMargin) {
        this.leftMargin = leftMargin;

        FrameLayout.LayoutParams frameParams = (FrameLayout.LayoutParams) getLayoutParams();
        frameParams.leftMargin = this.leftMargin;
        setLayoutParams(frameParams);
    }

    public int getTopMargin() {
        return topMargin;
    }

    public void setTopMargin(int topMargin) {
        this.topMargin = topMargin;

        FrameLayout.LayoutParams frameParams = (FrameLayout.LayoutParams) getLayoutParams();
        frameParams.topMargin = this.topMargin;
        setLayoutParams(frameParams);
    }

    public int getCityPointerHeight() {
        return cityPointerHeight;
    }

    public int getCityPointerWidth() {
        return cityPointerWidth;
    }

    public ACFCity getCity() {
        return city;
    }

    public void setCity(ACFCity city) {
        this.city = city;
    }

    /***********************************************************************************************
    * Event listener methods
    ***********************************************************************************************/
    @Override
    public void onClick(View v) {
        if (expanded){
            expanded = false;
            tvCountryName.setVisibility(View.GONE);
            tvContinentName.setVisibility(View.GONE);
        }else {
            expanded = true;
            tvCountryName.setVisibility(View.VISIBLE);
            tvContinentName.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        Toast.makeText(context, cityName + ", " + countryName + ", " + continentName +
                        "\nDistance: " + (int) distance + "m", Toast.LENGTH_LONG).show();
        return true;
    }
}
