package com.xuhai.wngs.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by changliang on 14/12/16.
 */
public class ImageUtils {
    public static Bitmap compressImage(String path, int width, int height) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / width, photoH / height);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(path, bmOptions);
    }

    public static File saveFileBitmap(String filename, Bitmap bit) {
        String sdPath = Environment.getExternalStorageDirectory().getPath();
        File dir = new File(sdPath + File.separator + "temp");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(sdPath + File.separator + "temp" + File.separator + filename);
        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            bit.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e1) {
            file = null;
        }
        return file;
    }

    public static void deleteFileBitmap() {
        String sdPath = Environment.getExternalStorageDirectory().getPath();
        File dir = new File(sdPath + File.separator + "temp");
        if (dir.exists()) {
            if (dir.listFiles().length > 0) {
                for (File temp : dir.listFiles()) {
                    if (temp.isDirectory()) {

                    } else {
                        temp.delete();
                    }
                }
            }
        }
    }

    public static void deleteTempImageFile(String filename) {
        String sdPath = Environment.getExternalStorageDirectory().getPath();
        File dir = new File(sdPath + File.separator + "temp");
        if (dir.exists()) {
            if (dir.listFiles().length > 0) {
                for (File temp : dir.listFiles()) {
                    if (temp.getName().equals(filename)) {
                        temp.delete();
                    }
                }
            }
        }
    }
}
