package com.minee9351gmail.test1.module;

/**
 * Created by 백은서 on 2017-04-15.
 */
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.minee9351gmail.test1.AppConstant;

public class Utils {

    private static final String TAG = "Utils";


    private Context _context;
    ArrayList<String> filePaths = new ArrayList<String>();

    // constructor
    public Utils(Context context) {
        this._context = context;
    }

    public void subDirList( String filePath){

        File dir = new File(filePath);
        File[] fileList = dir.listFiles();

        try {
            for (int i = 0; i < fileList.length; i++) {
                File file = fileList[i];
                if (file.isFile()) {
                    //Log.e(TAG, "File :" + fileList[i].getName());
                    if (IsSupportedFile(fileList[i].getName())) {
                        filePath=fileList[i].getPath();
                        filePaths.add(filePath);
                        Log.e(TAG, "File :" + fileList[i].getName());
                    }
                }
                else if (file.isDirectory()) {
                    subDirList(file.getCanonicalFile().getAbsolutePath());
                    // Log.e(TAG,"Directory : " + fileList[i].getName());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Reading file paths from SDCard

    public ArrayList<String> getFilePaths() {

        File sdCard = Environment.getExternalStorageDirectory();

        File directory = new File(
                sdCard.getAbsolutePath());

        String filePathss = sdCard.getAbsolutePath()+"/camtest";
        Log.e(TAG, "directory searching start");
        // check for directory
        File[] listFiles = directory.listFiles();


        if (directory.isDirectory()) {
            Log.e("TAG", directory.getAbsolutePath() + " dir exist.");
        } else {
            Log.e("TAG", directory.getAbsolutePath() + " dir not exist.");
        }

        subDirList(filePathss);

        return filePaths;
    }


    //  Check supported file extensions
    private boolean IsSupportedFile(String filePath) {
        File f = new File(filePath);
        String ext = f.getName().substring((f.getName().lastIndexOf("."))+1,
                f.getName().length());
        // Log.e("TAG", ext+ " did");
        if (GalleryConstants.FILE_EXTN
                .contains(ext.toLowerCase(Locale.getDefault())))
            return true;
        else
            return false;

    }


    //  * getting screen width

    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) _context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.x;
        return columnWidth;
    }
}
