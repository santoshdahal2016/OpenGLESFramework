package com.santosh.opengles.opengl.element;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.santosh.opengles.helper.IOHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Model {

    public FloatBuffer vertexBuffer , textureBuffer = null;

    public IntBuffer indexBuffer = null;

    public int vertexCount ;
    public int indexLength , vertexLength , textureLength;


    public boolean isTextured = false ;

    private float[] color;
    private String textureFile = "crate.jpg";

    int[] vao = new int[1];
    int[] vbo = new int[3];
    public int[] textureID = new int[1];



    protected float[] position = new float[] { 0f, 0f, 0f };
    protected float[] rotation = new float[] { 0f, 0f, 0f };
    protected float[] scale = new float[] { 1, 1, 1 };
    protected float[] bindShapeMatrix = new float[16];

    protected float[] modelMatrix = new float[16];
    protected float[] newModelMatrix = new float[16];
    {
        //
        Matrix.setIdentityM(modelMatrix,0);
        Matrix.setIdentityM(bindShapeMatrix,0);
        Matrix.setIdentityM(newModelMatrix,0);
    }




    public  Model(float[] vertices , int[] index , float[] texture ){


        indexLength = index.length;
        vertexLength = vertices.length;
        textureLength = texture.length;


        // number of coordinates per vertex in this array
        int COORDS_PER_VERTEX = 3;
        vertexCount = vertices.length / COORDS_PER_VERTEX;


        vertexBuffer = FloatBuffer.allocate(vertices.length);
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);


        indexBuffer = IntBuffer.allocate(index.length);
        indexBuffer.put(index);
        indexBuffer.position(0);

        textureBuffer = FloatBuffer.allocate(texture.length);
        textureBuffer.put(texture);
        textureBuffer.position(0);

        // Texture File Loading
        textureLoading(textureFile);






    }


    public  Model(float[] vertices , int[] index){


        indexLength = index.length;
        vertexLength = vertices.length;


        // number of coordinates per vertex in this array
        int COORDS_PER_VERTEX = 3;
        vertexCount = vertices.length / COORDS_PER_VERTEX;


        vertexBuffer = FloatBuffer.allocate(vertices.length);
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);


        indexBuffer = IntBuffer.allocate(index.length);
        indexBuffer.put(index);
        indexBuffer.position(0);


    }

    public void textureLoading(String textureFile){


        isTextured = true;
        Bitmap b = IOHelper.LoadBitmapFromAssets(textureFile);

        GLES30.glGenTextures(1, textureID, 0);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureID[0]);

        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);

        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, b, 0);
        b.recycle();
    }



    public boolean isTextured(){
        return isTextured;
    }

    public FloatBuffer getTextureCoordsArrayBuffer(){
        return textureBuffer;
    }



    //Model Transformation Operation

    public void setScale(float[] scale){
        this.scale = scale;
        updateModelMatrix();
    }


    public void setRotationY(float rotY) {
        this.rotation[1] = rotY;
        updateModelMatrix();
    }


    public void setRotationZ(float rotZ) {
        this.rotation[2] = rotZ;
        updateModelMatrix();
    }

    public void incrementRotationZ(float rotZ) {
        this.rotation[2] =  this.rotation[2]+rotZ;
        updateModelMatrix();
    }

    public void setRotationX(float rotX) {
        this.rotation[0] = rotX;
        updateModelMatrix();
    }

    protected void updateModelMatrix(){
        Matrix.setIdentityM(modelMatrix, 0);
        if (getRotation() != null) {
            Matrix.rotateM(modelMatrix, 0, getRotation()[0], 1f, 0f, 0f);
            Matrix.rotateM(modelMatrix, 0, getRotation()[1], 0, 1f, 0f);
            Matrix.rotateM(modelMatrix, 0, getRotationZ(), 0, 0, 1f);
        }
        if (getScale() != null) {
            Matrix.scaleM(modelMatrix, 0, getScaleX(), getScaleY(), getScaleZ());
        }
        if (getPosition() != null) {
            Matrix.translateM(modelMatrix, 0, getPositionX(), getPositionY(), getPositionZ());
        }
        if (this.bindShapeMatrix == null){
            // geometries not linked to any joint does not have bind shape transform
            System.arraycopy(this.modelMatrix,0,this.newModelMatrix,0,16);
        } else {
            Matrix.multiplyMM(newModelMatrix, 0, this.modelMatrix, 0, this.bindShapeMatrix, 0);
        }
    }


    public float[] getRotation() {
        return rotation;
    }

    public float getRotationX(){
        return rotation[0];
    }

    public float getRotationY(){
        return rotation[1];
    }

    public float getRotationZ() {
        return rotation[2];
    }


    public float[] getScale(){
        return scale;
    }

    public float getScaleX() {
        return getScale()[0];
    }

    public float getScaleY() {
        return getScale()[1];
    }

    public float getScaleZ() {
        return getScale()[2];
    }



    public float[] getPosition() {
        return position;
    }

    public float getPositionX() {
        return position != null ? position[0] : 0;
    }

    public float getPositionY() {
        return position != null ? position[1] : 0;
    }

    public float getPositionZ() {
        return position != null ? position[2] : 0;
    }

    public float[] getModelMatrix() {
        return modelMatrix;
    }
}
