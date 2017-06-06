package com.minee9351gmail.test1.activity;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.GridView;

import com.minee9351gmail.test1.R;
import com.minee9351gmail.test1.module.GalleryAdapter;
import com.minee9351gmail.test1.module.GalleryConstants;
import com.minee9351gmail.test1.module.Utils;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {
    private static final String TAG = "GalleryActivity";

    private Utils utils;
    private ArrayList<String> imagePaths = new ArrayList<String>();
    private GalleryAdapter adapter;
    private GridView gridView;
    private int columnWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        utils = new Utils(this);
        gridView = (GridView) findViewById(R.id.gallery);

        // Initilizing Grid View
        InitilizeGridLayout();

        imagePaths = utils.getFilePaths();

        adapter = new GalleryAdapter(GalleryActivity.this, imagePaths,
                columnWidth);

        // setting grid view adapter
        gridView.setAdapter(adapter);
        Log.e(TAG, "set adapter");

    }

    private void InitilizeGridLayout() {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                GalleryConstants.GRID_PADDING, r.getDisplayMetrics());

        columnWidth = (int) ((utils.getScreenWidth() - ((GalleryConstants.NUM_OF_COLUMNS + 1) * padding)) / GalleryConstants.NUM_OF_COLUMNS);
        Log.e(TAG, "gallery layout");
        gridView.setNumColumns(GalleryConstants.NUM_OF_COLUMNS);
        gridView.setColumnWidth(columnWidth);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setPadding((int) padding, (int) padding, (int) padding,
                (int) padding);
        gridView.setHorizontalSpacing((int) padding);
        gridView.setVerticalSpacing((int) padding);
    }

}