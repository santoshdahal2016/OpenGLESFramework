package com.santosh.opengles.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.santosh.opengles.R;
import com.santosh.opengles.helper.IOHelper;
import com.santosh.opengles.opengl.GLSurfaceViewOpengl;
import com.santosh.opengles.opengl.Scene;

public class MainActivity extends AppCompatActivity {


    GLSurfaceViewOpengl gLView = null;
    private Dialog myDialog;
    private Handler handler;


    private Scene scene;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        gLView = findViewById(R.id.surfaceview);

        gLView.setParent(this);


        myDialog = new Dialog(this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));



        scene = new Scene(this);

        scene.addModel(Uri.parse("assets://com.santosh.android_ar/models/cube.obj"));

//        scene.staticLoad();

    }

    public Scene getScene() {
        return scene;
    }

}
