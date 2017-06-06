package com.minee9351gmail.test1.module;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.minee9351gmail.test1.R;

/**
 * Created by ehdal on 2017-04-29.
 */

public class CameraService extends AppCompatActivity{

    public  TextView textview;

    public CameraService(){ }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //textview = (TextView) findViewById(R.id.textView) ;
    }

    public void toasttoast() {

        textview = (TextView) findViewById(R.id.textView) ;

        Handler hand1 = new Handler();
        hand1.postDelayed(new Runnable() {
            @Override
            public void run() {
                textview.setText("5");
            }
        }, 1000);

        Handler hand2 = new Handler();
        hand2.postDelayed(new Runnable() {
            @Override
            public void run() {
                textview.setText("4");
            }
        }, 2000);

        Handler hand3 = new Handler();
        hand3.postDelayed(new Runnable() {
            @Override
            public void run() {
                textview.setText("3");
            }
        }, 3000);

        Handler hand4 = new Handler();
        hand4.postDelayed(new Runnable() {
            @Override
            public void run() {
                textview.setText("2");
            }
        }, 4000);

        Handler hand5 = new Handler();
        hand5.postDelayed(new Runnable() {
            @Override
            public void run() {
                textview.setText("1");
            }
        }, 5000);
    }

    public void cameraSutter(){}
}
