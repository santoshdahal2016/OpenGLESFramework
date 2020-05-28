package com.santosh.opengles.opengl.element;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Set;

public class Drawer {



    // specification
    private final String id;
    private final Set<String> features;

    String[] attributes ;


    // opengl program
    private final int mProgram;

    private final String[] shaderIdTemp = new String[3];


    public int[] vao = new int[1];
    public int[] vbo = new int[3];



    // Shader Loading Stuff
    private Drawer(String id, String vertexShaderCode, String fragmentShaderCode, Set<String> features) {

        this.id = id;
        this.features = features;
        Log.i("Drawer", "Compiling 3D Drawer... " + id);

        // load shaders

        int m_vsID = GLES30.glCreateShader(GLES30.GL_VERTEX_SHADER);
        GLES30.glShaderSource(m_vsID, vertexShaderCode);
        GLES30.glCompileShader(m_vsID);

        int m_fsID = GLES30.glCreateShader(GLES30.GL_FRAGMENT_SHADER);
        GLES30.glShaderSource(m_fsID, fragmentShaderCode);
        GLES30.glCompileShader(m_fsID);


        // compile program

        mProgram = GLES30.glCreateProgram();


        // Bind the vertex shader to the program.
        GLES30.glAttachShader(mProgram, m_vsID);

        // Bind the fragment shader to the program.
        GLES30.glAttachShader(mProgram, m_fsID);

        // Bind attributes
        attributes = features.toArray(new String[features.size()]);
        if (attributes != null) {
            final int size = attributes.length;
            for (int i = 0; i < size; i++) {
                GLES30.glBindAttribLocation(mProgram, i, attributes[i]);
            }
        }

        // Link the two shaders together into a program.
        GLES30.glLinkProgram(mProgram);



        int[] status = new int[2];
        GLES30.glGetIntegerv(GLES30.GL_MAJOR_VERSION, status, 0);
        GLES30.glGetIntegerv(GLES30.GL_MINOR_VERSION, status, 1);
        Log.d("Shader", "GLES version "  + String.valueOf(status[0]) + "." + String.valueOf(status[1]));
        GLES30.glGetIntegerv(GLES30.GL_SHADING_LANGUAGE_VERSION, status, 0);
        Log.d("Shader", "Shader version "  + String.valueOf(status[0]));
        GLES30.glGetProgramiv(mProgram, GLES30.GL_LINK_STATUS, status, 0);
        if (status[0] == 0) {
            Log.d("Shader", "Program InfoLog: " + GLES30.glGetProgramInfoLog(mProgram));
        }
        Log.d("Shader", "Link Status: " + String.valueOf(status[0]));
        GLES30.glGetShaderiv(m_vsID, GLES30.GL_COMPILE_STATUS, status, 0);
        Log.d("Shader", "Vertex Shader Compile Status: " + String.valueOf(status[0]));
        if (status[0] == 0) {
            Log.d("Shader", GLES30.glGetShaderInfoLog(m_vsID));
        }
        GLES30.glGetShaderiv(m_fsID, GLES30.GL_COMPILE_STATUS, status, 0);
        Log.d("Shader", "Fragment Shader Compile Status: " + String.valueOf(status[0]));
        if (status[0] == 0) {
            Log.d("Shader", GLES30.glGetShaderInfoLog(m_fsID));
        }
    }

    public static Drawer getInstance(String id, String vertexShaderCode, String fragmentShaderCode) {
        Set<String> shaderFeatures = new HashSet<>();
        testShaderFeature(shaderFeatures, vertexShaderCode, "vPosition");
        testShaderFeature(shaderFeatures, vertexShaderCode, "textureCords");
        return new Drawer(id, vertexShaderCode, fragmentShaderCode, shaderFeatures);
    }

    private static void testShaderFeature(Set<String> outputFeatures, String shaderCode, String feature) {
        if (shaderCode.contains(feature)) {
            outputFeatures.add(feature);
        }
    }







    public void loadToVAO(Model m_model) {

        createVAO();
        createVBO();


        if (attributes != null) {
            final int size = attributes.length;
            for (int i = 0; i < size; i++) {
                if(attributes[i].equals("vPosition")){
                    storeFloatDataInAttributeList(0,m_model.getVertexArrayBuffer(),m_model.vertexLength,3 );

                }
                if(attributes[i].equals("textureCords")){
                    storeFloatDataInAttributeList(1,m_model.textureBuffer,m_model.textureLength,2);

                }

            }
        }

        storeIntDataInAttributeList(2,m_model.indexBuffer, m_model.indexLength);

        unbindVAO();


    }


    public void createVAO(){
        GLES30.glGenVertexArrays(1, vao, 0);
        GLES30.glBindVertexArray(vao[0]);
    }
    public  void createVBO(){
        GLES30.glGenBuffers(3, vbo, 0);

    }

    private void storeFloatDataInAttributeList(int attributeNumber , FloatBuffer buffer ,int length, int size){
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,vbo[attributeNumber]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,length * 4,buffer,GLES30.GL_STATIC_DRAW);
        GLES30.glEnableVertexAttribArray(attributeNumber);
        GLES30.glVertexAttribPointer(attributeNumber, size, GLES30.GL_FLOAT, false, 0, 0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0);


    }


    private void storeIntDataInAttributeList(int attributeNumber , IntBuffer buffer , int length){
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,vbo[attributeNumber]);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER,length * 4,buffer,GLES30.GL_STATIC_DRAW);


    }

    private void unbindVAO(){
        GLES30.glBindVertexArray(0);

    }


    public void cleanUp(){
            GLES30.glDeleteVertexArrays(1, vao, 0);
            GLES30.glDeleteBuffers(3, vbo, 0);
    }



    public void draw(Model m_model){


        GLES30.glUseProgram(mProgram);

        setModelMatrix(m_model.modelMatrix);

        setTexture(m_model.textureID[0]);

        GLES30.glBindVertexArray(vao[0]);

        for (int i = 0; i <  attributes.length; i++) {
            GLES30.glEnableVertexAttribArray(i);
        }

        GLES30.glDrawElements(GLES20.GL_TRIANGLES, m_model.indexLength, GLES30.GL_UNSIGNED_INT, 0);
        GLES30.glDisableVertexAttribArray(0);

        for (int i = 0; i <  attributes.length; i++) {
            GLES30.glDisableVertexAttribArray(i);
        }

        GLES30.glBindVertexArray(0);
    }



    private void setModelMatrix(float[] modelMatrix) {

        // get handle to shape's transformation matrix
        int transformationMatrix = GLES30.glGetUniformLocation(mProgram, "transformationMatrix");
        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(transformationMatrix, 1, false, modelMatrix, 0);
    }


    public void setTexture(int unit){

        int mTextureUniformHandle  = GLES30.glGetUniformLocation(mProgram, "textureSampler");

        GLES20.glActiveTexture(0);
        GLES30.glUniform1i(mTextureUniformHandle, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, unit);

    }
}
