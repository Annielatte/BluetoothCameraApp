package com.minee9351gmail.test1.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.minee9351gmail.test1.Constants;
import com.minee9351gmail.test1.R;
import com.minee9351gmail.test1.module.BluetoothService;
import com.minee9351gmail.test1.module.CameraService;
import com.minee9351gmail.test1.module.Preview;
import com.minee9351gmail.test1.module.SLog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK;
import static com.minee9351gmail.test1.Constants.MESSAGE_READ;
import static com.minee9351gmail.test1.module.GalleryAdapter.decodeFileBitmap;


public class CameraActivity extends AppCompatActivity implements View.OnClickListener{
    ProgressDialog dialogSet = null;
   // String upLoadUri = "";
    int serverResponseCode = 0;
    ImageView mImageView;
    CameraService cs = new CameraService();
    Bitmap mBmp = null;
    String downloadUri = "http://218.209.45.76/bluetoothImageUp/uploads/";
    private File outFile = null;

    private String tempFilePath = null;
    private Button btn_imnge;

    private static byte[] response;
    private String filePath;
    private static int readSize = 0;
    public byte[] lilfilebuffer = null;
    private String fileName = "";

    private Preview preview;

    private Camera camera;
    private Camera.Parameters mCP;
    private BluetoothService bluetoothService;

    private Button btn_blutooth,btn_blueclose,btn_share;
    private ImageButton btn_capture;
    private TextView textview;
    private ToggleButton toggle_timer,toggle_light;
    private SurfaceView msurface;

    private Button btn_gallery;
    private static final String TAG = "BluetoothFragment";
   // private static String BtoD = "BluetoothFragment";

    public static int CAMERA_FACING = CAMERA_FACING_BACK;

    /** Local Bluetooth adapter */
    private BluetoothAdapter mBluetoothAdapter = null;

    private String mConnectedDeviceName = null;

    /** Member object for the chat services*/
    private BluetoothService mService = null;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Request code for camera
    private final int CAMERA_REQUEST_CODE = 100;

    // Request code for runtime permissions
    private final int REQUEST_CODE_STORAGE_PERMS = 321;

    private boolean hasPermissions() {
        int res = 0;
        String[] permissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        for (String perms : permissions){
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }

    private void requestNecessaryPermissions() {
        String[] permissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, REQUEST_CODE_STORAGE_PERMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grandResults) {
        // this boolean will tell us that user granted permission or not.
        boolean allowed = true;
        switch (requestCode) {
            case REQUEST_CODE_STORAGE_PERMS:
                for (int res : grandResults) {
                    // if user granted all required permissions then 'allowed' will return true.
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                    Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                // if user denied then 'allowed' return false
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                allowed = false;
                break;
        }
        if (allowed) {
            doRestart(this);
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    Toast.makeText(this, "Camera Permissions denied", Toast.LENGTH_SHORT).show();
                }
                else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    Toast.makeText(this, "Storage Permissions denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public static void doRestart(Context c) {
        try {
            if (c != null) {
                PackageManager pm = c.getPackageManager();
                if (pm != null) {
                    Intent mStartActivity = pm.getLaunchIntentForPackage(
                            c.getPackageName()
                    );
                    if (mStartActivity != null) {
                        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        int mPendingIntentId = 223344;
                        PendingIntent mPendingIntent = PendingIntent
                                .getActivity(c, mPendingIntentId, mStartActivity,
                                        PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);

                        System.exit(0);
                    } else {
                        SLog.d("Was not able to restart application, mStartActivity null");
                    }
                } else {
                    SLog.d("Was not able to restart application, PM null");
                }
            } else {
                SLog.d("Was not able to restart application, Context null");
            }
        } catch (Exception ex) {
            SLog.d("Was not able to restart application");
        }
    }

    public void startCamera() {
        if ( preview == null ) {
            preview = new Preview(this,msurface);
            preview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ((FrameLayout) findViewById(R.id.layout)).addView(preview);

            preview.setKeepScreenOn(true);
        }

        preview.setCamera(null);
        if (camera != null) {
            camera.release();
            camera = null;
        }

        int numCams = Camera.getNumberOfCameras();
        if (numCams > 0) {
            try {
                // Camera.CameraInfo.CAMERA_FACING_FRONT or Camera.CameraInfo.CAMERA_FACING_BACK
                camera = Camera.open(CAMERA_FACING);
                // camera orientation
                camera.setDisplayOrientation(setCameraDisplayOrientation(this, CAMERA_FACING, camera));
                // get Camera parameters
                Camera.Parameters params = camera.getParameters();
                // picture image orientation
                params.setRotation(setCameraDisplayOrientation(this, CAMERA_FACING, camera));
                camera.startPreview();

            } catch (RuntimeException ex) {
                Toast.makeText(this, "camera_not_found " + ex.getMessage().toString(), Toast.LENGTH_LONG).show();
                SLog.d("camera_not_found " + ex.getMessage().toString());
            }
        }
        preview.setCamera(camera);
    }

    private void permission(){
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //   Toast.makeText(MainActivity.this, "NFC 권한 허가", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                Toast.makeText(CameraActivity.this, "읽기 권한 거부\n" + arrayList.toString(), Toast.LENGTH_SHORT).show();
                finish();
            }
        };
        new TedPermission(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("접근 권한이 필요합니다.")
                .setDeniedMessage("권한설정을 하지 않으면 이용하기가 어렵습니다.")
                .setPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mImageView = (ImageView) findViewById(R.id.img_downImage) ;
        btn_imnge = (Button) findViewById(R.id.btn_imnge); //이미지 블루투스 통해 불러오기
        btn_imnge.setOnClickListener(this);
        btn_imnge.setVisibility(View.GONE);

        btn_share = (Button) findViewById(R.id.btn_share);
        btn_share.setOnClickListener(this);
        btn_share.setVisibility(View.GONE);

        btn_blueclose =(Button) findViewById(R.id.btn_blueclose);//블투연결해지
        btn_blueclose.setOnClickListener(this);

        permission();

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) { //얘도 메인으로
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
        } //0430 min
        // Handler를 이용해서 메신지를 던져보십시오.
        bluetoothService = BluetoothService.getInstance(this,mHandler);
        mService = new BluetoothService(getApplicationContext(),mHandler);

        //  bluetoothService.setinterface(this);

        msurface = (SurfaceView)findViewById(R.id.surfaceView);

        btn_blutooth = (Button)findViewById(R.id.btn_blutooth_connect);
        btn_blutooth.setOnClickListener(this);

        btn_capture = (ImageButton)findViewById(R.id.btn_capture);
        btn_capture.setOnClickListener(this);

        //타이머 토글 버튼
        toggle_timer = (ToggleButton)findViewById(R.id.toggle_timer);
        toggle_timer.setOnClickListener(this);
        textview = (TextView)findViewById(R.id.textView);

        //플래시 부분
        toggle_light = (ToggleButton)findViewById(R.id.toggle_light);
        toggle_light.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(toggle_light.isChecked()) {
                    mCP = camera.getParameters();
                    mCP.setFlashMode("torch");
                    camera.setParameters(mCP);
                }
                else
                    mCP.setFlashMode("off");
                camera.setParameters(mCP);
            }
        });

        //전후면 카메라 전환 버튼
        ImageButton button2 = (ImageButton) findViewById(R.id.btn_change_facing);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CAMERA_FACING == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_BACK;
                    startCamera();
                }
                else {
                    CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_FRONT;
                    msurface.setClickable(false);
                    startCamera();
                }
            }
        });

        //이전 메인 화면으로 가는 버튼 -> 여기서 블투가 끊기네..
        /*Button button3 = (Button)findViewById(R.id.go_main);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });*/

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            if (!hasPermissions()) {
                requestNecessaryPermissions();
            } else {
                startCamera();
            }
        } else {
            Toast.makeText(this, "Camera not supported", Toast.LENGTH_LONG).show();
        }

        btn_gallery = (Button) findViewById(R.id.btn_gallery);
        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_g = new Intent(CameraActivity.this, GalleryActivity.class);
                startActivity(intent_g);
            }
        });
    }
    /////////////////////
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
        // BtoD =BluetoothCameraApplication.state; //Tag 변경 170416
        mService.connect(device, secure,BluetoothCameraApplication.state);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // FragmentActivity activity = getActivity();
            // android.util.Log.i(TAG,"로그: Handler");
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                           if(BluetoothCameraApplication.state.equals("remote")){
                                msurface.setVisibility(View.GONE);
                               mImageView.setVisibility(View.VISIBLE);
                               btn_imnge.setVisibility(View.VISIBLE); // 사진불러오기
                               btn_share.setVisibility(View.VISIBLE);//공유
                               //Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                                //startActivity(intent); //카메라 액티비티 띄우기  170416
                            }
                           /* else
                            {
                                BtoD="BluetoothFragment";
                                Intent intent1 = new Intent(getApplicationContext(),RemoteActivity.class);
                                startActivity(intent1);  //리모트 액티비티 띄우기  170416
                            }*/

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
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;

                    String readMessage = new String(readBuf, 0, msg.arg1);
                    String a=readMessage.substring(readMessage.length()-3,readMessage.length())+"";

                    if(readMessage.equals("TAKE_PICTURE")){
                        Toast.makeText(getApplicationContext(),"TAKE_PICTURE",Toast.LENGTH_SHORT).show();
                        camera.takePicture(shutterCallback, rawCallback, jpegCallback);
                        //pickerListener.messagePicker();
                    }
                    else if(readMessage.equals("SEND_IMAGE")){
                        sendImage(fileName);
                    }
                    else if(a.equals("jpg"))
                        { // filename
                        Toast.makeText(getApplicationContext(),readMessage,Toast.LENGTH_SHORT).show();
                        Log.d(TAG,"filename 전송 :"+readMessage);
                        fileName=fileName+readMessage;
                      //  filename = readMessage;
                    }
                    else {
                        Log.d(TAG, "image 전송!");
                        if (readSize == 0) {
                            if (response == null)
                                response = new byte[0];
                            response = addPacket(response, readBuf);
                            response = divPacket(response);
                        } else {
                            response = addPacket(response, readBuf);
                            Log.d(TAG, "respose.length()" + response.length + " readSize :" + readSize);
                            if (readSize <=response.length) { //여기부분문제
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d(TAG, "saveImageTask");
                                        new SaveImageTask().execute(response);
                                        Toast.makeText(getApplicationContext(),"IMAGE SAVE!",Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "save end");

                                        editor();
                                        Glide.with(getApplicationContext()).load(filePath).into(mImageView);
                                    }
                                });
                                response = null;
                                readSize = 0;
                            }
                        }
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
    protected void onStart() {
        super.onStart();

        if (!mBluetoothAdapter.isEnabled()) {  //0430 min
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //     Log.i(TAG,"로그: startActivityforresult()");
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mService != null) {
            if (mService.getState() == BluetoothService.STATE_NONE) {
                Log.i(TAG, "로그: i onresume-> mServic start()");
                mService.start();
            }
        }
        SLog.d("Resume");
        startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Surface will be destroyed when we return, so stop the preview.
        if(camera != null) {
            // Call stopPreview() to stop updating the preview surface
            camera.stopPreview();
            preview.setCamera(null);
            camera.release();
            camera = null;
        }
        ((FrameLayout) findViewById(R.id.layout)).removeView(preview);
        preview = null;
    }

    private void resetCam() { startCamera(); }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
        outFile = file;
    }

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            //             Log.d(TAG, "onShutter'd");
        }
    };

    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            //             Log.d(TAG, "onPictureTaken - raw");
        }
    };

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            //이미지의 너비와 높이 결정
            int w = camera.getParameters().getPictureSize().width;
            int h = camera.getParameters().getPictureSize().height;

            int orientation = setCameraDisplayOrientation(CameraActivity.this,
                    CAMERA_FACING, camera);
            if(CAMERA_FACING == Camera.CameraInfo.CAMERA_FACING_FRONT)
                orientation = -90;

            //byte array를 bitmap으로 변환
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inSampleSize = 4;
            Bitmap bitmap = BitmapFactory.decodeByteArray( data, 0, data.length, options);
            bitmap = Bitmap.createScaledBitmap( bitmap, w, h, true ); //이미지 크기 줄이기

            //Bitmap bitmap = BitmapFactory.decodeByteArray( data, 0, data.length, options);
            //int w = bitmap.getWidth();
            //int h = bitmap.getHeight();

            //이미지를 디바이스 방향으로 회전
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);
            bitmap =  Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);

            //bitmap을 byte array로 변환
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] currentData = stream.toByteArray();
            //파일로 저장
            new SaveImageTask().execute(currentData);
           // imageUpLoad(outFile.getAbsolutePath());
            resetCam();

            SLog.d("onPictureTaken - jpeg");
        }
    };

    public void toasttoast() {

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

    @Override
    public void onClick(View v) { //0430 min

        switch (v.getId()) {
            case R.id.btn_share:

                Intent intent1 = new Intent(Intent.ACTION_SEND);
                intent1.setType("image/jpg");
                intent1.putExtra(Intent.EXTRA_STREAM, Uri.parse(BluetoothCameraApplication.shareImg));
                intent1.setPackage("com.kakao.talk");
                startActivity(intent1);

                break;
            case R.id.btn_blueclose:
                if(mService.mState == 3) //블투연결됐을시
                {
                    Toast.makeText(getApplicationContext(), "블루투스 연결이 해제되었습니다. ", Toast.LENGTH_SHORT).show();
                    BluetoothCameraApplication.state = "Camera";
                    recreate();
                    mService.unconnect();
                }

                break;
            case R.id.btn_imnge:
                Log.d(TAG,"imgbutton click filename :"+fileName);
                mService.sendMessage("SEND_IMAGE");
                break;

            case R.id.btn_blutooth_connect:
                Intent intent = new Intent(getApplicationContext(), DeviceListActivity.class);
                startActivityForResult(intent, 1);
                break;

            case R.id.btn_capture:
                if (BluetoothCameraApplication.state.equals("remote")) {
                    Log.d(TAG, "remote로 변환 BluetoothCameraApplication.state: "+BluetoothCameraApplication.state);

                    if(toggle_timer.isChecked()) {//토스트 딜레이 시간 강제로 바꾸는거 짱나네 ㄷㄷㄷㄷ 우쒸

                        toasttoast();

                        Handler hand = new Handler();
                        hand.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                textview.setText(" ");
                                String message = "TAKE_PICTURE";
                                mService.sendMessage(message);
                            }
                        }, 6000);
                    }
                    else {
                        String message = "TAKE_PICTURE";
                        mService.sendMessage(message);
                    }
                    break;
                }
                else {
                    Log.d(TAG, "camera 찍어 BluetoothCameraApplication.state: "+BluetoothCameraApplication.state);

                    if(toggle_timer.isChecked()) {//토스트 딜레이 시간 강제로 바꾸는거 짱나네 ㄷㄷㄷㄷ 우쒸

                        toasttoast();

                        Handler hand = new Handler();
                        hand.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                textview.setText(" ");
                                camera.takePicture(shutterCallback, rawCallback, jpegCallback);
                            }
                        }, 6000);
                    }
                    else {
                        camera.takePicture(shutterCallback, rawCallback, jpegCallback);
                    }
                    break;
                }
        }
    }
    /************sendimage to bluetooth**************/
    private void sendImage(String message) {
        Log.d(TAG,"sendImage filename"+fileName+ "message :"+message);
        message="/storage/emulated/0/camtest/"+message;
        File file = new File(message);
        lilfilebuffer = read(file);
        lilfilebuffer = addPacket(lilfilebuffer, lilfilebuffer.length);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mService.write(lilfilebuffer);
            }
        }).start();
    }

    private byte[] divPacket(byte[] origin){
        byte[] packet_data = new byte[origin.length-32];
        byte[] header = new byte[32];
        System.arraycopy(origin,0,header,0,32);
        readSize = byteToInt(header,0);
        System.arraycopy(origin, 32, packet_data, 0, origin.length-32);
        return packet_data;
    }

    private byte[] addPacket(byte[] origin, int size){
        byte[] packet_data = new byte[origin.length+32];
        byte[] header = new byte[32];
        intToByte(size, header,0);
        System.arraycopy(header,0,packet_data,0,32);
        System.arraycopy(origin, 0, packet_data, 32, origin.length);
        String str = new String(header);
        return packet_data;
    }

    public void intToByte(int value, byte data[], int idx) {
        data[idx] = (byte)(value >> 24);
        data[++idx] = (byte)(value >> 16);
        data[++idx] = (byte)(value >> 8);
        data[++idx] = (byte)value;
    }
    public int byteToInt(byte data[], int idx) {
        return (	((data[idx] & 0xFF) << 24) | ((data[++idx] & 0xFF) << 16) |
                ((data[++idx] & 0xFF) << 8) | (data[++idx] & 0xFF));
    }
    private byte[] read(final File file) {
        Throwable pending = null;
        FileInputStream in = null;
        final byte buffer[] = new byte[(int) file.length()];
        try {
            in = new FileInputStream(file);
            in.read(buffer);
        } catch (Exception e) {
            pending = new RuntimeException("Exception occured on reading file "
                    + file.getAbsolutePath(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    if (pending == null) {
                        pending = new RuntimeException(
                                "Exception occured on closing file"
                                        + file.getAbsolutePath(), e);
                    }
                }
            }
            if (pending != null) {
                throw new RuntimeException(pending);
            }
        }
        return buffer;
    }

    public void editor()
    {
        Bitmap image = decodeFileBitmap(filePath);
        Log.d(TAG, "bitmap set");
        
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 90, stream);
        byte[] byteArray = stream.toByteArray();

        Intent in1 = new Intent(CameraActivity.this, EditActivity.class);
        in1.putExtra("image",byteArray);
        Log.d(TAG, "intent start");
        //Toast.makeText(_activity, "클릭", Toast.LENGTH_SHORT).show();
        CameraActivity.this.startActivity(in1);
        Log.d(TAG, "activity end");
    }
/////////////////
    // @Override
    // public void messagePicker() {
    //   camera.takePicture(shutterCallback, rawCallback, jpegCallback);
    //}
    /*
    private void imageUpLoad(final String uri){
        dialogSet = ProgressDialog.show(CameraActivity.this, "", "업로드 중...", true);
        new Thread(new Runnable() {
            public void run() {
                uploadFile(uri);
            }
        }).start();
    }
*/
    /*
    //bluetooth로 데이터 전송하면서 fileName 보내서 다운로드 할 수 있겠끔 할 것
    private String filename;

    private void imageDownload(String f){
        //"http://218.209.45.76/bluetoothImageUp/uploads/"+fileName;
       // String fileName = "1495853029914.jpg";

         String fileName = f;
        String downloadPath = downloadUri + fileName;
        new HttpReqTask().execute(downloadPath);
    }*/
    /*
    // 서버에서 이미지 다운로드를 수행하는 쓰레드
    private class HttpReqTask extends AsyncTask<String,String,String> {
        @Override // 쓰레드 주업무를 수행하는 함수
        protected String doInBackground(String... arg) {
            boolean result = false;
            if( arg.length == 1 )
                // 서버에서 전달 받은 데이터를 Bitmap 이미지에 저장
                result = loadWebImage(arg[0]);
            else {
                // 서버에서 다운로드 한 데이터를 파일로 저장
                result = downloadFile(arg[0], arg[1]);
                if( result ) {
                    // 파일을 로딩해서 Bitmap 객체로 생성
                    String sdRootPath = Environment.getDataDirectory().getAbsolutePath();
                    String filePath = sdRootPath + "/" + arg[1];
                    mBmp = BitmapFactory.decodeFile("/0/"+arg[1]);
                }
            }
            if( result )
                return "True";
            return "";
        }

        // 쓰레드의 업무가 끝났을 때 결과를 처리하는 함수
        protected void onPostExecute(String result) {
            if( result.length() > 0 )
            // 서버에서 다운받은 Bitmap 이미지를 ImageView 에 표시
                mImageView.setImageBitmap(mBmp);
        }
    }*/
    /*

    // 서버에서 다운로드 한 데이터를 파일로 저장
    boolean downloadFile(String strUrl, String fileName) {
        try {
            URL url = new URL(strUrl);
            // 서버와 접속하는 클라이언트 객체 생성
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 입력 스트림을 구한다
            InputStream is = conn.getInputStream();
            // 파일 저장 스트림을 생성
            FileOutputStream fos = openFileOutput(fileName, 0);

            // 입력 스트림을 파일로 저장
            byte[] buf = new byte[1024];
            int count;
            while( (count = is.read(buf)) > 0 ) {
                fos.write(buf, 0, count);
            }
            // 접속 해제
            conn.disconnect();
            // 파일을 닫는다
            fos.close();
        } catch (Exception e) {
            Log.d("tag", "Image download error.");
            return false;
        }
        return true;
    }

    // 서버에서 전달 받은 데이터를 Bitmap 이미지에 저장
    public boolean loadWebImage(String strUrl) {
        try {
            // 스트림 데이터를 Bitmap 에 저장
            InputStream is = new URL(strUrl).openStream();
            mBmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch(Exception e) {
            Log.d("tag", "Image Stream error.");
            return false;
        }
        return true;
    }*/
    /*
    public int uploadFile(String sourceFileUri) {
        final String fileName = sourceFileUri;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            dialogSet.dismiss();
            Log.e("uploadFile", "Source File not exist :"
                    + fileName);*/
               /*
            runOnUiThread(new Runnable() {
                public void run() {
                    //messageText.setText("Source File not exist :"
                          //  + fileName );
                }
            });
               */
//            return 0;
/*
        } else {
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);
                //	conn.setRequestProperty("TagId", tagID);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {
                    BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()),8192);
                    final StringBuilder response = new StringBuilder();
                    String strLine = null;
                    while ((strLine = input.readLine()) != null) {
                        response.append(strLine);
                        Log.v("Tag",strLine);
                    }
                    input.close();

                    runOnUiThread(new Runnable() {
                        public void run() {

                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    + response.toString();

                            //	messageText.setText(msg);
                            Toast.makeText(CameraActivity.this, "업로드가 완료 되었습니다.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialogSet.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        //messageText.setText("MalformedURLException Exception : check script url.");
                        Toast.makeText(CameraActivity.this, "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialogSet.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        //messageText.setText("Got Exception : see logcat ");
                        Toast.makeText(CameraActivity.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file Exception", "Exception : " + e.getMessage(), e);
            }
            dialogSet.dismiss();

            return serverResponseCode;
        } // End else block
    }
*////
    private byte[] addPacket(byte[] origin, byte[] addPacket){
        byte[] packetData = new byte[origin.length+addPacket.length];
        System.arraycopy(origin, 0, packetData, 0 , origin.length);
        System.arraycopy(addPacket, 0, packetData, origin.length, addPacket.length);
        return packetData;
    }

    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {
        @Override
        protected Void doInBackground(byte[]... data) {
            FileOutputStream outStream = null;

            // Write to SD Card
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File (sdCard.getAbsolutePath() + "/camtest");
                dir.mkdirs();

                 fileName = String.format("%d.jpg",  System.currentTimeMillis());

                if(mService.mState==3){ //filename 전송
                    mService.sendMessage(fileName);
                }
                final File outFile = new File(dir, fileName);
                BluetoothCameraApplication.shareImg = outFile.getAbsolutePath(); //파일 경로 저장
                Log.d("#####",outFile.getAbsolutePath());
                filePath = outFile.getAbsolutePath();
                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);
                outStream.flush();
                outStream.close();
                SLog.d( "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());
                refreshGallery(outFile);
                tempFilePath = filePath;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
            return null;
        }
    }
    /**
     *
     * @param activity
     * @param cameraId  Camera.CameraInfo.CAMERA_FACING_FRONT, Camera.CameraInfo.CAMERA_FACING_BACK
     * @param camera
     *
     * Camera Orientation
     * reference by https://developer.android.com/reference/android/hardware/Camera.html
     */
    public static int setCameraDisplayOrientation(Activity activity,
                                                  int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    //선택 영역 초점맞추기
    Camera.AutoFocusCallback myAutoFocusCallback = new Camera.AutoFocusCallback(){
        @Override
        public void onAutoFocus(boolean arg0, Camera arg1) {
            // TODO Auto-generated method stub
            if (arg0){
                camera.cancelAutoFocus();
            }
            float focusDistances[] = new float[3];
            arg1.getParameters().getFocusDistances(focusDistances);
        }};

    public void touchFocus(final int posX, final int posY){
        if(BluetoothCameraApplication.state.equals("Camera")){
        camera.stopFaceDetection();
        setAutoFocusArea(camera, posX, posY, 128, true, new Point(msurface.getWidth(), msurface.getHeight()));
        camera.autoFocus(myAutoFocusCallback);
        btn_capture.setEnabled(true);
        }
    }

    private void setAutoFocusArea(Camera camera, int posX, int posY,
                                  int focusRange, boolean flag, Point point) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            /** 영역을 지정해서 포커싱을 맞추는 기능은 ICS 이상 버전에서만 지원됩니다.  **/
            return;
        }
        if (posX < 0 || posY < 0) {
            setArea(camera, null);
            return;
        }
        int touchPointX;
        int touchPointY;
        int endFocusY;
        int startFocusY;

        if (!flag) {
            /** Camera.setDisplayOrientation()을 이용해서 영상을 세로로 보고 있는 경우. **/
            touchPointX = point.y >> 1;
            touchPointY = point.x >> 1;

            startFocusY = posX;
            endFocusY   = posY;
        } else {
            /** Camera.setDisplayOrientation()을 이용해서 영상을 가로로 보고 있는 경우. **/
            touchPointX = point.x >> 1;
            touchPointY = point.y >> 1;

            startFocusY = posY;
            endFocusY = point.x - posX;
        }

        float startFocusX   = 1000F / (float) touchPointY;
        float endFocusX     = 1000F / (float) touchPointX;

        /** 터치된 위치를 기준으로 focusing 영역으로 사용될 영역을 구한다. **/
        startFocusX = (int) (startFocusX * (float) (startFocusY - touchPointY)) - focusRange;
        startFocusY = (int) (endFocusX * (float) (endFocusY - touchPointX)) - focusRange;
        endFocusX = startFocusX + focusRange;
        endFocusY = startFocusY + focusRange;

        if (startFocusX < -1000)
            startFocusX = -1000;

        if (startFocusY < -1000)
            startFocusY = -1000;

        if (endFocusX > 1000) {
            endFocusX = 1000;
        }

        if (endFocusY > 1000) {
            endFocusY = 1000;
        }

        //ArrayList에 Camera.Area를 2개 이상 넣게 되면 에러가 발생
        Rect rect = new Rect((int) startFocusX, (int) startFocusY, (int) endFocusX, (int) endFocusY);
        ArrayList<Camera.Area> arraylist = new ArrayList<Camera.Area>();
        arraylist.add(new Camera.Area(rect, 1000)); // 지정된 영역을 100%의 가중치를 두겠다는 의미입니다.

        setArea(camera, arraylist);
    }

    private void setArea(Camera camera, List<Camera.Area> list) {

        boolean enableFocusModeMacro = true;

        Camera.Parameters parameters = camera.getParameters();

        int maxNumFocusAreas = parameters.getMaxNumFocusAreas();
        int maxNumMeteringAreas = parameters.getMaxNumMeteringAreas();

        if (maxNumFocusAreas > 0) {
            parameters.setFocusAreas(list);
        }

        if (maxNumMeteringAreas > 0) {
            parameters.setMeteringAreas(list);
        }

        if (list == null || maxNumFocusAreas < 1 || maxNumMeteringAreas < 1) {
            enableFocusModeMacro = false;
        }

        if (enableFocusModeMacro == true) {
        /*
         * FOCUS_MODE_MACRO을 사용하여 근접 촬영이 가능하도록 해야
         * 지정된 Focus 영역으로 초점이 좀더 선명하게 잡히는 것을 볼 수 있습니다.
         */
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
            Log.d(TAG, "focus mode macro");
        } else {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            Log.d(TAG, "focus mode auto");
        }
        camera.setParameters(parameters);
    }

    //////////////////////////////////////////////////////////////////////////////////////0430 min
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
