package com.minee9351gmail.test1.module;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.minee9351gmail.test1.activity.EditActivity;
import com.minee9351gmail.test1.activity.GalleryActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by 백은서 on 2017-05-31.
 */

public class GalleryAdapter extends BaseAdapter {
    private Activity _activity;
    private ArrayList<String> _filePaths = new ArrayList<String>();
    private int imageWidth;
    Bitmap bit;
    int pos;
    ArrayList<String> path;

    public GalleryAdapter(Activity activity, ArrayList<String> filePaths,
                          int imageWidth) {
        this._activity = activity;
        this._filePaths = filePaths;
        this.imageWidth = imageWidth;

        Log.e("gallery", "created");
    }

    @Override
    public int getCount() {
        return this._filePaths.size();
    }

    @Override
    public Object getItem(int position) {
        return this._filePaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(_activity);
        } else {
            imageView = (ImageView) convertView;
        }

        // get screen dimensions
        Bitmap image = decodeFile(_filePaths.get(position), imageWidth,
                imageWidth);

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(imageWidth,
                imageWidth));
        imageView.setImageBitmap(image);

        // image view click listener
        imageView.setOnClickListener(new OnImageClickListener(position));

        return imageView;
    }

    class OnImageClickListener implements View.OnClickListener {

        final int _postion;

        // constructor
        public OnImageClickListener(int position) {
            this._postion = position;
            // Log.e("TAG", "position"+position);
        }

        @Override
        public void onClick(View v) {
            Bitmap image = decodeFile(_filePaths.get(OnImageClickListener.this._postion), imageWidth,
                    imageWidth);
            bit = image;
            Log.e("listener", "="+bit.getHeight());
            /*
            Bundle extra;
            extra = new Bundle();
            int pos = _postion;
            // on selecting grid view image
            // launch full screen activity
            Log.e("Adapter", "pos : "+ pos);
            Intent i = new Intent(_activity, EditActivity.class);
            extra.putInt("position", pos);
            i.putExtras(extra);
            _activity.startActivity(i);
            */

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bit.compress(Bitmap.CompressFormat.PNG, 90, stream);
            byte[] byteArray = stream.toByteArray();

            Intent in1 = new Intent(_activity, EditActivity.class);
            in1.putExtra("image",byteArray);
            Log.e("click", "=");
            //Toast.makeText(_activity, "클릭", Toast.LENGTH_SHORT).show();
            _activity.startActivity(in1);

            //Log.e("TAG", "path"+path);
        }
    }
    /*
     * Resizing image size
     */
    public static Bitmap decodeFile(String filePath, int WIDTH, int HIGHT) {
        try {

            File f = new File(filePath);

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            final int REQUIRED_WIDTH = WIDTH;
            final int REQUIRED_HIGHT = HIGHT;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
                    && o.outHeight / scale / 2 >= REQUIRED_HIGHT)
                scale *= 2;

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap decodeFileBitmap(String filePath) {
        try {

            File f = new File(filePath);

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            final int REQUIRED_WIDTH = 300;
            final int REQUIRED_HIGHT = 300;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
                    && o.outHeight / scale / 2 >= REQUIRED_HIGHT)
                scale *= 2;

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
