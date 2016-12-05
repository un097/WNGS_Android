package com.xuhai.wngs.ui.imgchoose;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapBucket {

    public static final int MAX_COUNT = 3;

    public static int max = 3;
    public static List<Bitmap> bitlist = new ArrayList<Bitmap>();
    public static List<String> pathList = new ArrayList<String>();

    public static void clear() {
        bitlist.clear();
        pathList.clear();
    }

    public static Bitmap revitionImageSize(String path, int size) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        in.close();
        int i = 0;
        Bitmap bitmap = null;
        while (true) {
            if ((options.outWidth >> i <= size) && (options.outHeight >> i <= size)) {
                in = new BufferedInputStream(new FileInputStream(new File(path)));
                options.inJustDecodeBounds = false;
                options.inSampleSize = (int) Math.pow(2.0D, i);
                bitmap = BitmapFactory.decodeStream(in, null, options);
                in.close();
            }
            i += 1;
        }
    }
}
