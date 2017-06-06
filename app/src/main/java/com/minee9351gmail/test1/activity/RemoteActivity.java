package com.minee9351gmail.test1.activity;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.minee9351gmail.test1.module.BluetoothService;
import com.minee9351gmail.test1.R;

public class RemoteActivity extends AppCompatActivity implements View.OnClickListener {

    private BluetoothAdapter mBluetoothAdapter;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    private BluetoothService btService = null;
    Button btn_send;
    private StringBuffer mOutStringBuffer;
  //  BluetoothService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote);
      //  mService = BluetoothService.getInstance(RemoteActivity.this,mha);
        btn_send = (Button)findViewById(R.id.btn_send); // 카메라 셔터
        btn_send.setOnClickListener(this);
        btService = new BluetoothService(getApplicationContext(),null);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(RemoteActivity.this, "누름", Toast.LENGTH_SHORT).show();
        String message = "TAKE_PICTURE";
        // ((BluetoothFragment)getSupportFragmentManager().findFragmentByTag("BluetoothFragment")).sendMessage(message);
               /* FragmentManager fm = getSupportFragmentManager();
               //fragment를 xml로 넣었을때
                BluetoothFragment fragment = (BluetoothFragment) fm.findFragmentById(R.id.sample_content_fragment);
                */
        btService.sendMessage(message);
    }
/*
    private void sendMessage(String message) {
        if (btService.getState() != BluetoothService.STATE_CONNECTED) {
        //    Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        if (message.length() > 0) {
            byte[] send = message.getBytes();
            btService.write(send);
            mOutStringBuffer.setLength(0);
        }
    }*/



}

