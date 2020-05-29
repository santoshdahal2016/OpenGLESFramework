package com.santosh.opengles.opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.santosh.opengles.helper.IOHelper;
import com.santosh.opengles.opengl.element.BuildDrawer;
import com.santosh.opengles.opengl.element.Drawer;
import com.santosh.opengles.opengl.element.Model;
import com.santosh.opengles.opengl.parser.obj.objParser;


import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRendererOpengl implements GLSurfaceView.Renderer {


    float triangleCoords[] = {   // in counterclockwise order:
            0.0f,  0.622008459f, 0.0f, // top
            -0.5f, -0.311004243f, 0.0f, // bottom left
            0.5f, -0.311004243f, 0.0f  // bottom right
    };



    float pyramidsCoords[] = {

            //front face
            0.0f,  1.0f, 0.0f,
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,


            //right face
            0.0f,  1.0f, 0.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, -1.0f,


            //back face
            0.0f,  1.0f, 0.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,

            //left face
            0.0f,  1.0f, 0.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, 1.0f,
    };


    float cubeCoords[] = {

            //front face
            -1.0f,  1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,
            1.0f,  1.0f, 1.0f,
            1.0f,  1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,


            //right face
            1.0f,  1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f,  1.0f, -1.0f,
            1.0f,  1.0f, -1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, -1.0f,


            //back face
            1.0f,  1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f,  1.0f, -1.0f,
            -1.0f,  1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,

            -1.0f, -1.0f, -1.0f,

            //back face
            -1.0f,  1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f,  1.0f, 1.0f,
            -1.0f,  1.0f, 1.0f,
            -1.0f, -1.0f, -1.0f,

            -1.0f, -1.0f, 1.0f,

    };



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


    private Model m_Model;



    /**
     * Build Drawer to get right renderer/shader based on object attributes
     */
    private BuildDrawer buildDrawer;

    Drawer drawerObject;


    private objParser objparser;


    public GLRendererOpengl(Context context) throws IOException, IllegalAccessException {
        super();
        IOHelper.SetContext(context);
        buildDrawer = new BuildDrawer(context);

        objparser = new objParser();

    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0, 0, 0, 1);


        GLES20.glEnable(GLES20.GL_DEPTH_TEST);



        m_Model = objparser.load("ship.obj");
//        m_Model  = new Model(cubeCoordswithIdex, cobeIndex  );

        m_Model.setScale(new float[]{0.5f,0.5f,0.5f});
        m_Model.setRotationZ(45);

        m_Model.setRotationY(55);

        drawerObject = buildDrawer.getDrawer(m_Model, m_Model.isTextured());

        drawerObject.loadToVAO(m_Model);

    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }


    @Override
    public void onDrawFrame(GL10 gl) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT| GLES20.GL_DEPTH_BUFFER_BIT);


        m_Model.incrementRotationZ(-1);

        drawerObject.draw(m_Model);



    }
}
