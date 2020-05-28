package com.santosh.opengles.opengl;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.santosh.opengles.activity.MainActivity;
import com.santosh.opengles.helper.IOHelper;
import com.santosh.opengles.opengl.element.Model;
import com.santosh.opengles.opengl.parser.ParserTask;
import com.santosh.opengles.opengl.parser.obj.objLoaderTask;

import java.util.ArrayList;
import java.util.List;

public class Scene implements ParserTask.Callback {

    public   Model object = null ;
    protected final MainActivity parent;

    public Scene(MainActivity parent) {
        this.parent = parent;

    }


    public void  addModel(Uri uri){
        new objLoaderTask(parent,uri, this).execute();
    }


    public void  staticLoad(){

        float cubeCoordswithIdex[] = {

                //front face
                //x,    y,      z
                -0.5f,  0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                0.5f,  0.5f, 0.5f,


                //x,    y,      z
                -0.5f,  0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,

        };


        int cobeIndex[] ={
                0,1,2,2,0,3,
                4,5,6,6,7,4,

                0,4,1,1,5,4,

                7,3,2,2,6,7,


                0,4,7,7,0,3
        };


        float textureCoords[] ={
                0,0,
                0,1,
                1,0,
                1,1,
                0,0,
                0,1,
                1,0,
                1,1,

        };

        object  = new Model(cubeCoordswithIdex, cobeIndex ,textureCoords);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onLoadError(Exception ex) {

    }

    @Override
    public void onLoadComplete(List<Model> data) {

        Log.i("Data", String.valueOf(data.size()));
        object = data.get(0);
    }
}
