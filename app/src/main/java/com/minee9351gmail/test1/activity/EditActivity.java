package com.minee9351gmail.test1.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.minee9351gmail.test1.R;
import com.minee9351gmail.test1.module.ImageEdit;
import com.minee9351gmail.test1.module.SLog;
import com.minee9351gmail.test1.module.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class EditActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener{


    private static final String TAG = "EditActivity";

    private ImageView mOriginalImageView, tempImageView;
    CropImageView cropImageView;
    Button btn_rotate, btn_contrast, btn_crop, btn_cropStart, btn_brightness, btn_filter,btn_save,btn_share;
    Bitmap test, startEdit, editted;
    SeekBar seekbar, seekbar_rotate, seekbar_cont;
    String status;
    double value = 0;
    ImageEdit editor;
    Matrix edittedMatrix;
    ArrayList<String> path;
    //int position;
    Bundle extra;


    private File outFile = null;
    private String filePath;
    private String fileName = "";


    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    // private ColorMatrix mCM = new ColorMatrix();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e(TAG, "create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        btn_share = (Button) findViewById(R.id.btn_share);
        btn_share.setOnClickListener(this);

        //RelativeLayout edit_space = (RelativeLayout) findViewById(R.id.edit_space);
        // Intent intent = new Intent(this, MainActivity.class);

        ImageView size = (ImageView) findViewById(R.id.size);
        Intent intent = new Intent();

        Log.e(TAG, "intent");
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        int bmp_width = bmp.getWidth();
        int bmp_height = bmp.getHeight();

        float ratio = 1;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            /*if(size.getMaxWidth() >= bmp_width || size.getMaxHeight() >=bmp_height)
            {
                ratio = 1;
            }
            else */if(bmp_width >= bmp_height)
            {
                ratio = size.getMaxWidth()/bmp_width;
            }
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ratio = size.getMaxHeight()/bmp_height;
            }
        }

        test = Bitmap.createScaledBitmap(bmp, bmp_width*(int)ratio, bmp_height*(int)ratio, true);
        Toast.makeText(EditActivity.this, "WIDTH"+bmp_width*(int)ratio, Toast.LENGTH_SHORT).show();
        //test = Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), true);
        Log.e(TAG, "bitmap create");
        //test = bmp;
        // BitmapFactory.Options options = new BitmapFactory.Options();
        // test = BitmapFactory.decodeFile(path.get(position), options);

        //int pos = intent.getExtras().getInt("position");

        //Log.e(TAG, "pos : "+ pos);

        //test = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        //editor = new ImageEdit();
        Log.e(TAG, "bitmap");
        mOriginalImageView = (ImageView) findViewById(R.id.imageView);
        mOriginalImageView.setImageBitmap(test);

        plusLoad();
        init();
    }

    public void plusLoad() {
        tempImageView = (ImageView) findViewById(R.id.temp);
        tempImageView.setImageBitmap(test);
        Log.e(TAG, "bitmap ok");
        cropImageView = (CropImageView) findViewById(R.id.CropImageView);
        cropImageView.setVisibility(View.INVISIBLE);
    }

    public void init()
    {
        btn_rotate = (Button) findViewById(R.id.btn_rotate);
        btn_rotate.setOnClickListener(this);
        btn_contrast = (Button) findViewById(R.id.btn_contrast);
        btn_contrast.setOnClickListener(this);
        btn_crop = (Button) findViewById(R.id.btn_crop);
        btn_crop.setOnClickListener(this);
        btn_brightness = (Button) findViewById(R.id.btn_brightness);
        btn_brightness.setOnClickListener(this);
        btn_filter = (Button) findViewById(R.id.btn_filter);
        btn_filter.setOnClickListener(this);
        seekbar = (SeekBar) findViewById(R.id.seekBar);
        seekbar.setOnSeekBarChangeListener(this);
        seekbar.setVisibility(View.INVISIBLE);
        btn_cropStart = (Button) findViewById(R.id.btn_crop2);
        btn_cropStart.setVisibility(View.INVISIBLE);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);

        startEdit = test;
        editted = test;

    }
    @Override
    public void onClick(View v) {
        imageShow();
        value = 0;
        Log.e(TAG, "imageShow");
        switch (v.getId()) {
            case R.id.btn_share:
                Intent intent1 = new Intent(Intent.ACTION_SEND);
                intent1.setType("image/jpg");
                intent1.putExtra(Intent.EXTRA_STREAM, Uri.parse(BluetoothCameraApplication.shareImg));
                intent1.setPackage("com.kakao.talk");
                startActivity(intent1);
                break;
            case R.id.btn_rotate:
                Toast.makeText(EditActivity.this, "회전", Toast.LENGTH_SHORT).show();
                status = "rotate";
                seekbar.setVisibility(View.VISIBLE);
                seekbar.setProgress(0);
                break;

            case R.id.btn_contrast:
                Toast.makeText(EditActivity.this, "대비", Toast.LENGTH_SHORT).show();
                status = "contrast";
                seekbar.setVisibility(View.VISIBLE);
                seekbar.setProgress(0);
                break;

            case R.id.btn_crop:
                seekbar.setVisibility(View.INVISIBLE);
                Toast.makeText(EditActivity.this, "자르기", Toast.LENGTH_SHORT).show();
                status = "crop";
                editted = crop();
                break;

            case R.id.btn_brightness:
                Toast.makeText(EditActivity.this, "밝기", Toast.LENGTH_SHORT).show();
                status = "brightness";
                seekbar.setVisibility(View.VISIBLE);
                seekbar.setProgress(0);
                break;

            case R.id.btn_filter:
                Toast.makeText(EditActivity.this, "필터", Toast.LENGTH_SHORT).show();
                status = "filter";
                seekbar.setVisibility(View.VISIBLE);
                seekbar.setProgress(0);
                break;

            case R.id.btn_save:
                Toast.makeText(EditActivity.this, "저장", Toast.LENGTH_SHORT).show();
                status = "save";
                seekbar.setVisibility(View.INVISIBLE);
                Save();
                break;
        }
    }

    public void imageShow()
    {
        mOriginalImageView.setImageBitmap(editted);
        mOriginalImageView.setVisibility(View.VISIBLE);
        startEdit = editted;
        tempImageView.setVisibility(View.INVISIBLE);
        tempImageView.setImageBitmap(startEdit);
        Log.e(TAG, "ok imageshow");
    }

    public Bitmap rotate(Bitmap originalImage, double degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float)degree);

        return Bitmap.createBitmap(originalImage, 0, 0, originalImage.getWidth(),
                originalImage.getHeight(), matrix, true);
    }

    public Bitmap setContrast(Bitmap bitmap, double value) {

        ColorMatrix cm = new ColorMatrix();

        float x = 0;
        float y = 0;

        //Canvas canvas = new Canvas(bitmap);
        //Paint paint = new Paint();

        float contrast = (float)Math.pow((100 + value) / 100, 2);
        float scale = contrast + 1.f;
        float translate = (-.5f * scale + .5f) * 255.f;
        cm.set(new float[] {
                scale, 0, 0, 0, translate,
                0, scale, 0, 0, translate,
                0, 0, scale, 0, translate,
                0, 0, 0, 1, 0 });

        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(cm);
        //paint.setColorFilter(new ColorMatrixColorFilter(cm));
        //canvas.drawBitmap(bitmap, 0, 0, paint);

        tempImageView.setColorFilter(colorMatrixFilter);
        bitmap = viewToBitmap(tempImageView);
        cm.set(new float[]{
                1, 0, 0, 0, 1,
                0, 1, 0, 0, 1,
                0 ,0 ,1 ,0 ,1,
                0, 0, 0, 1, 1
        });

        return bitmap;
    }

    public Bitmap setBrightness(Bitmap bitmap, double value) {

        ColorMatrix cm = new ColorMatrix();

        float x = 0;
        float y = 0;

        //Canvas canvas = new Canvas(startEdit);
        float brightness = (float)value;
        float scale = brightness * 1.f;
        float translate = brightness * 2.f;
        cm.set(new float[] {
                1, 0, 0, 0, translate,
                0, 1, 0, 0, translate,
                0, 0, 1, 0, translate,
                0, 0, 0, 1, 0 });

        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(cm);
        tempImageView.setColorFilter(colorMatrixFilter);
        bitmap = viewToBitmap(tempImageView);
        cm.set(new float[]{
                1, 0, 0, 0, 1,
                0, 1, 0, 0, 1,
                0 ,0 ,1 ,0 ,1,
                0, 0, 0, 1, 1
        });

        return bitmap;
    }

    public Bitmap setFilter(Bitmap bitmap, double value) {

        ColorMatrix cm = new ColorMatrix();

        float x = 0;
        float y = 0;

        //Canvas canvas = new Canvas(startEdit);

        float filter = (float)value;
        float scale = filter + 1.f;
        float translate = scale * 2f;
        cm.set(new float[] {
                1.4f, 0, 0, 0, translate,
                0, 1.4f, 0, 0, translate,
                0, 0, 1.1f, 0, translate,
                0, 0, 0, 1, 0 });

        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(cm);

        tempImageView.setColorFilter(colorMatrixFilter);
        bitmap = viewToBitmap(tempImageView);
        cm.set(new float[]{
                1, 0, 0, 0, 1,
                0, 1, 0, 0, 1,
                0 ,0 ,1 ,0 ,1,
                0, 0, 0, 1, 1
        });
        return bitmap;
    }
    public Bitmap crop()
    {
        cropImageView.setImageBitmap(startEdit);
        cropImageView.setVisibility(View.VISIBLE);
        Log.e(TAG, "cropImageView");
        cropImageView.setFixedAspectRatio(false);
        Log.e(TAG, "ok crop");
        btn_cropStart.setVisibility(View.VISIBLE);
        btn_cropStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditActivity.this, "CROP", Toast.LENGTH_SHORT).show();
                mOriginalImageView.setVisibility(View.INVISIBLE);
                editted = cropImageView.getCroppedImage();
                tempImageView.setImageBitmap(editted);
                cropImageView.setVisibility(View.INVISIBLE);
                btn_cropStart.setVisibility(View.INVISIBLE);
                tempImageView.setVisibility(View.VISIBLE);

            }
        });
        return editted;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        value = seekBar.getProgress();

        if(status.equals("rotate"))
        {
            editted = rotate(startEdit, value);
        }

        else if(status.equals("contrast"))
        {
            editted = setContrast(startEdit, value);
        }
        else if(status.equals("brightness"))
        {
            editted = setBrightness(startEdit, value);
        }
        else if(status.equals("filter"))
        {
            editted= setFilter(startEdit, value);
        }

        tempImageView.setImageBitmap(editted);
        tempImageView.setVisibility(View.VISIBLE);
        mOriginalImageView.setVisibility(View.INVISIBLE);


        //editted = viewToBitmap(tempImageView);

        Toast.makeText(EditActivity.this, "now value : "+value, Toast.LENGTH_SHORT).show();
    }

    public static Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        if (view instanceof SurfaceView) {
            SurfaceView surfaceView = (SurfaceView) view;
            surfaceView.setZOrderOnTop(true);
            surfaceView.draw(canvas);
            surfaceView.setZOrderOnTop(false);
            return bitmap;
        } else {
            //For ViewGroup & View
            view.draw(canvas);
            return bitmap;
        }
    }

    public void Save()
    {
        Bitmap saveImg = viewToBitmap(tempImageView);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        saveImg.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] currentData = stream.toByteArray();

        new SaveImageTask().execute(currentData);
        Toast.makeText(EditActivity.this, "IMAGE SAVE END", Toast.LENGTH_SHORT).show();
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
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
            return null;
        }
    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
        outFile = file;
    }
}
