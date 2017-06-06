package com.minee9351gmail.test1.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.minee9351gmail.test1.Constants;
import com.minee9351gmail.test1.R;
import com.minee9351gmail.test1.module.BluetoothService;

import static com.minee9351gmail.test1.R.id.button_Camera;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button Camera,btn_connect,btn_gallery;
    EditText edittxt_input;
    private static final String TAG = "BluetoothFragment";
    private static String BtoD = "BluetoothFragment";

    /**
     * String buffer for outgoing messages
     */
   // private StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    private String mConnectedDeviceName = null;

    //private BluetoothService.bluetoothPicker pickerListener;
    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> mConversationArrayAdapter;
    /**
     *
     *     /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    /* Member object for the chat services
     */
    private BluetoothService mService = null;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) { //얘도 메인으로
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
        }
        btn_connect = (Button) findViewById(R.id.btn_Bluetooth); // 블투연결
        btn_connect.setOnClickListener(this);
        mService = new BluetoothService(getApplicationContext(),mHandler);
        Camera = (Button) findViewById(button_Camera);
        Camera.setOnClickListener(this);
        btn_gallery = (Button) findViewById(R.id.button_Gellary);
        btn_gallery.setOnClickListener(this);
/*
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            BluetoothFragment fragment = new BluetoothFragment();
            transaction.replace(R.id.sample_content_fragment,fragment);
            transaction.commit();
           // Log.i(TAG,"i 2 Main oncreate");

        }*/
        //txt_name.setOnClickListener(this);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }



    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.button_Camera:
                // String message = "TAKE_PICTURE";
                //  mService.sendMessage
                // intent.putExtra("edit", edittxt_input.getText().toString());
                Intent intent = new Intent(this, CameraActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_Bluetooth:
                // Send a message using content of the edit text widget
                Intent serverIntent = new Intent(MainActivity.this, DeviceListActivity.class);
                // android.util.Log.i(TAG,"로그: secure_connect_scan");
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);

                Toast.makeText(MainActivity.this,"btn_connect버튼누름",Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_Gellary:
                Toast.makeText(getApplicationContext(),"버튼누름.",Toast.LENGTH_LONG).show();
               String m ="TAKE_PICTURE";
                mService.sendMessage(m);

                //Intent intent_gallery= new Intent(MainActivity.this, GridViewActivity.class);
                //startActivity(intent_gallery);
                break;
        }
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    public void connectDevice(Intent data, boolean secure) { //장치연결

        // Get the device MAC address
        Log.i(TAG,"로그 :connectDevice");
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
       // mAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        BluetoothCameraApplication.state="remote";
        BtoD ="RemoteActivity"; //Tag 변경 170416
        mService.connect(device, secure,BtoD);

    }
/*
    public interface bluetoothPicker{
        public void messagePicker();
    }

    public void setinterface(BluetoothService.bluetoothPicker picker){
        pickerListener = picker;
    }
  */
    /**
     * The Handler that gets information back from the BluetoothChatService
     */

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // FragmentActivity activity = getActivity();
            // android.util.Log.i(TAG,"로그: Handler");
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            if(BtoD.equals("BluetoothFragment")){
                                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                                startActivity(intent); //카메라 액티비티 띄우기  170416
                            }
                            else
                            {
                                BtoD="BluetoothFragment";
                                Intent intent1 = new Intent(getApplicationContext(),RemoteActivity.class);
                                startActivity(intent1);  //리모트 액티비티 띄우기  170416
                            }

                            //  setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            //mConversationArrayAdapter.clear();
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            // setStatus(R.string.title_connecting);
                            break;
                        case BluetoothService.STATE_LISTEN:

                            break;
                        case BluetoothService.STATE_NONE:
                            //setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    // mConversationArrayAdapter.add("Me:  " + writeMessage);

                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    if(readMessage.equals("TAKE_PICTURE")){
                        Toast.makeText(getApplicationContext(),"TAKE_PICTURE",Toast.LENGTH_SHORT).show();
                        //pickerListener.messagePicker();
                    }
                    if(readMessage.equals("gellary")){
                        Toast.makeText(getApplicationContext(),"TAKE_PICTURE",Toast.LENGTH_SHORT).show();
                        //pickerListener.messagePicker();
                    }

                    if(readMessage.equals("Connected")){

                    }
                    // mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;

                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != this) {//뭔가...아닌것같은데
                        Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();

                        // sendMessage(BtoD);
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != this) {
                        Toast.makeText(getApplicationContext(), msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();

                    }
                    break;
            }
        }
    };


    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
       //     Log.i(TAG,"로그: startActivityforresult()");
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
            // Otherwise, setup the chat session
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mService != null) {
            if (mService.getState() == BluetoothService.STATE_NONE) {
                Log.i(TAG, "로그: i onresume-> mServic start()");
                mService.start();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*Log.i(TAG,"로그 :onDestroy");
        if (mService != null) {
            mService.stop();
        }*/
    }

    @Override
    public void onStop() {
        super.onStop();
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    // setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.i(TAG, "로그: BT not enabled");
                    //Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                    //       Toast.LENGTH_SHORT).show();
                    //getActivity().finish();
                }
        }
    }






}
