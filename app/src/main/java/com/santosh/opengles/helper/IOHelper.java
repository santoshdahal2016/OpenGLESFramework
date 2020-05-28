package com.santosh.opengles.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

/**
 * Created by ARF on 31/10/2016.
 */

public class IOHelper {

    private static Context IOHContext;

    private IOHelper() {

    }


    public static void SetContext(Context context) {

        IOHContext = context;
    }


    public static Bitmap LoadBitmapFromAssets(String filename) {

        Bitmap bitmap = Bitmap.createBitmap(1,1, Bitmap.Config.ARGB_8888);
        InputStream istr = null;

        try {
            istr = IOHContext.getAssets().open(filename);
            bitmap = BitmapFactory.decodeStream(istr);

        } catch (IOException e) {
            Log.e("Error","Loading Texture");

            e.printStackTrace();

        }
        finally {
            //Always clear and close
            try {
                if (istr != null) {
                    istr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }


     public static String LoadJSONFromAssets(String filename) {

        String json;

        try {
            InputStream is = IOHContext.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    public static String LoadShaderFileFromAssets(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(IOHContext.getAssets().open(filename)));

        String result = new String();
        String mLine = reader.readLine();
        while (mLine != null) {
            result += mLine; // process line
            result += "\n";
            mLine = reader.readLine();
        }
        reader.close();
        return result;
    }


    public static InputStream LoadModelFileFromAssets(String path) throws IOException {


            return getInputStream(Uri.parse("assets://" + IOHContext.getPackageName() + "/models/" + path));

    }

    public static InputStream getInputStream(Uri uri) throws IOException {
        return IOHContext.getAssets().open(uri.getPath().substring(1));
    }

}
