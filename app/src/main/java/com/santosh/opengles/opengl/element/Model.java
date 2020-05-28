package com.santosh.opengles.opengl.element;

import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.santosh.opengles.helper.IOHelper;
import com.santosh.opengles.opengl.math.Math;
import com.santosh.opengles.opengl.parser.obj.objLoader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class Model {


    private static float[] DEFAULT_COLOR = {1.0f, 1.0f, 0, 1.0f};
    /**
     * The directory where the files reside so we can build referenced files in the model like material and textures
     * files
     */
    private Uri uri;
    /**
     * The assets directory where the files reside so we can build referenced files in the model like material and
     * textures files
     */
    // private String assetsDir;
    private String id;
    private boolean flipTextCoords = true;

    private objLoader loader;
    private objLoader.ModelDimensions modelDimensions;

    public FloatBuffer vertexBuffer , textureBuffer = null;

    public IntBuffer indexBuffer = null;

    public int vertexCount ;
    public int indexLength , vertexLength , textureLength;

    private FloatBuffer vertexNormalsBuffer = null;

    private ArrayList<objLoader.Tuple3> texCoords;
    private objLoader.Faces faces;
    private objLoader.FaceMaterials faceMats;
    private objLoader.Materials materials;

    // Processed arrays
    private FloatBuffer vertexArrayBuffer = null;
    private FloatBuffer vertexColorsArrayBuffer = null;
    private FloatBuffer vertexNormalsArrayBuffer = null;
    private FloatBuffer textureCoordsArrayBuffer = null;


    private int drawMode = GLES20.GL_POINTS;

    private byte[] textureData = null;
    private String textureFile;

    public boolean isTextured = false ;

    private float[] color;

    int[] vao = new int[1];
    int[] vbo = new int[3];
    public int[] textureID = new int[1];


    public boolean loaded = false;

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


    }
    public void textureLoading(){


        isTextured = true;
        Bitmap b = IOHelper.LoadBitmapFromAssets("crate.jpg");

        GLES30.glGenTextures(1, textureID, 0);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureID[0]);

        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);

        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, b, 0);
        b.recycle();
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


    public Model(int vertexLength1 ,int indexLength1 ,FloatBuffer verts, FloatBuffer normals, ArrayList<objLoader.Tuple3> texCoords1, objLoader.Faces faces1,
                 objLoader.FaceMaterials faceMats1, objLoader.Materials materials1) {
        super();
        vertexBuffer = verts;
        vertexLength = vertexLength1*3;
        indexLength = indexLength1*3;


        vertexNormalsBuffer = normals;
        texCoords = texCoords1;
        faces = faces1;  // parameter "faces" could be null in case of async loading

        indexBuffer = faces.getIndexBuffer();

        faceMats = faceMats1;
        materials = materials1;
    }


    public Model ObjtoModel(){


        if (faces == null)  {
            Log.i("Object3DBuilder", "No faces. Not generating arrays");
            return this;
        }

        Log.i("Object3DBuilder", "Allocating vertex array buffer... Vertices ("+faces.getVerticesReferencesCount()+")");
        final FloatBuffer vertexArrayBuffer1 = createNativeByteBuffer(faces.getVerticesReferencesCount() * 3 * 4).asFloatBuffer();
        setVertexArrayBuffer(vertexArrayBuffer1);

        Log.i("Object3DBuilder", "Populating vertex array...");


        for (int i = 0; i < faces.getVerticesReferencesCount(); i++) {

            indexBuffer.get(i);
            vertexBuffer.get(indexBuffer.get(i) * 3);

            vertexArrayBuffer1.put(i*3,vertexBuffer.get(indexBuffer.get(i) * 3));
            vertexArrayBuffer1.put(i*3+1,vertexBuffer.get(indexBuffer.get(i) * 3 + 1));
            vertexArrayBuffer1.put(i*3+2,vertexBuffer.get(indexBuffer.get(i) * 3 + 2));
        }

        Log.i("Object3DBuilder", "Allocating vertex normals buffer... Total normals ("+faces.facesNormIdxs.size()+")");
        // Normals buffer size = Number_of_faces X 3 (vertices_per_face) X 3 (coords_per_normal) X 4 (bytes_per_float)
        final FloatBuffer vertexNormalsArrayBuffer = createNativeByteBuffer(faces.getSize() * 3 * 3 * 4).asFloatBuffer();;
        setVertexNormalsArrayBuffer(vertexNormalsArrayBuffer);

        // build file normals
        if (vertexNormalsBuffer != null && vertexNormalsBuffer.capacity() > 0) {
            Log.i("Object3DBuilder", "Populating normals buffer...");
            for (int n=0; n<faces.facesNormIdxs.size(); n++) {
                int[] normal = faces.facesNormIdxs.get(n);
                for (int i = 0; i < normal.length; i++) {
                    vertexNormalsArrayBuffer.put(n*9+i*3,vertexNormalsBuffer.get(normal[i] * 3));
                    vertexNormalsArrayBuffer.put(n*9+i*3+1,vertexNormalsBuffer.get(normal[i] * 3 + 1));
                    vertexNormalsArrayBuffer.put(n*9+i*3+2,vertexNormalsBuffer.get(normal[i] * 3 + 2));
                }
            }
        } else {
            // calculate normals for all triangles
            Log.i("Object3DBuilder", "Model without normals. Calculating [" + faces.getIndexBuffer().capacity() / 3 + "] normals...");

            final float[] v0 = new float[3], v1 = new float[3], v2 = new float[3];
            for (int i = 0; i < faces.getIndexBuffer().capacity(); i += 3) {
                try {
                    v0[0] = vertexBuffer.get(faces.getIndexBuffer().get(i) * 3);
                    v0[1] = vertexBuffer.get(faces.getIndexBuffer().get(i) * 3 + 1);
                    v0[2] = vertexBuffer.get(faces.getIndexBuffer().get(i) * 3 + 2);

                    v1[0] = vertexBuffer.get(faces.getIndexBuffer().get(i + 1) * 3);
                    v1[1] = vertexBuffer.get(faces.getIndexBuffer().get(i + 1) * 3 + 1);
                    v1[2] = vertexBuffer.get(faces.getIndexBuffer().get(i + 1) * 3 + 2);

                    v2[0] = vertexBuffer.get(faces.getIndexBuffer().get(i + 2) * 3);
                    v2[1] = vertexBuffer.get(faces.getIndexBuffer().get(i + 2) * 3 + 1);
                    v2[2] = vertexBuffer.get(faces.getIndexBuffer().get(i + 2) * 3 + 2);

                    float[] normal = Math.calculateNormal(v0, v1, v2);

                    vertexNormalsArrayBuffer.put(i*3,normal[0]);
                    vertexNormalsArrayBuffer.put(i*3+1,normal[1]);
                    vertexNormalsArrayBuffer.put(i*3+2,normal[2]);
                    vertexNormalsArrayBuffer.put(i*3+3,normal[0]);
                    vertexNormalsArrayBuffer.put(i*3+4,normal[1]);
                    vertexNormalsArrayBuffer.put(i*3+5,normal[2]);
                    vertexNormalsArrayBuffer.put(i*3+6,normal[0]);
                    vertexNormalsArrayBuffer.put(i*3+7,normal[1]);
                    vertexNormalsArrayBuffer.put(i*3+8,normal[2]);
                } catch (BufferOverflowException ex) {
                    throw new RuntimeException("Error calculating normal for face ["+i/3+"]");
                }
            }
        }


        FloatBuffer colorArrayBuffer = null;
        if (materials != null) {
            Log.i("Object3DBuilder", "Reading materials...");
            BufferedReader br = null;
            try {
                InputStream inputStream = IOHelper.LoadModelFileFromAssets(materials.mfnm);
                br = new BufferedReader(new InputStreamReader(inputStream));
                materials.readMaterials(br);
                materials.showMaterials();
                br.close();
            } catch (Exception ex){
                Log.e("Object3DBuilder","Couldn't load material file "+materials.mfnm+". "+ex.getMessage(), ex);
            }
        }

        if (materials != null && !faceMats.isEmpty()) {
            Log.i("Object3DBuilder", "Processing face materials...");
            colorArrayBuffer = createNativeByteBuffer(4 * faces.getVerticesReferencesCount() * 4)
                    .asFloatBuffer();
            boolean anyOk = false;
            float[] currentColor = DEFAULT_COLOR;
            for (int i = 0; i < faces.getSize(); i++) {
                // Is there any usemtl at this point ?
                final String materialName = faceMats.findMaterial(i);
                if (materialName != null) {
                    // Is material defined in material.mtl file ?
                    objLoader.Material mat = materials.getMaterial(materialName);
                    if (mat != null) {
                        currentColor = mat.getKdColor() != null ? mat.getKdColor() : currentColor;
                        anyOk = anyOk || mat.getKdColor() != null;
                    } else {
                        Log.w("Object3DBuilder", "Material not defined: "+ materialName);
                    }
                }
                colorArrayBuffer.put(currentColor);
                colorArrayBuffer.put(currentColor);
                colorArrayBuffer.put(currentColor);
            }
            if (!anyOk) {
                Log.i("Object3DBuilder", "Using single color.");
                colorArrayBuffer = null;
            }
        }
        setVertexColorsArrayBuffer(colorArrayBuffer);


        String texture = null;
        byte[] textureData = null;
        if (materials != null && !materials.materials.isEmpty()) {

            // TODO: process all textures
            for (objLoader.Material mat : materials.materials.values()) {
                if (mat.getTexture() != null) {
                    texture = mat.getTexture();
                    break;
                }
            }
            if (texture != null) {
                setTextureFile(texture);
                Log.i("Object3DBuilder","Texture "+texture);
            } else {
                Log.i("Object3DBuilder", "Found material(s) but no texture");
            }
        } else{
            Log.i("Object3DBuilder", "No materials -> No texture");
        }


        //if (textureData != null) {
        if (texCoords != null && texCoords.size() > 0) {

            Log.i("Object3DBuilder", "Allocating/populating texture buffer (flipTexCoord:"+isFlipTextCoords()+")...");
            FloatBuffer textureCoordsBuffer = createNativeByteBuffer(texCoords.size() * 2 * 4).asFloatBuffer();
            for (objLoader.Tuple3 texCor : texCoords) {
                textureCoordsBuffer.put(texCor.getX());
                textureCoordsBuffer.put(isFlipTextCoords() ? 1 - texCor.getY() : texCor.getY());
            }

            Log.i("Object3DBuilder", "Populating texture array buffer...");
            FloatBuffer textureCoordsArraysBuffer = createNativeByteBuffer(2 * faces.getVerticesReferencesCount() * 4).asFloatBuffer();
            setTextureCoordsArrayBuffer(textureCoordsArraysBuffer);

            try {

                boolean anyTextureOk = false;
                String currentTexture = null;

                Log.i("Object3DBuilder", "Populating texture array buffer...");
                int counter = 0;
                for (int i = 0; i < faces.facesTexIdxs.size(); i++) {

                    // get current texture
                    if (!faceMats.isEmpty() && faceMats.findMaterial(i) != null) {
                        objLoader.Material mat = materials.getMaterial(faceMats.findMaterial(i));
                        if (mat != null && mat.getTexture() != null) {
                            currentTexture = mat.getTexture();
                        }
                    }

                    // check if texture is ok (Because we only support 1 texture currently)
                    boolean textureOk = false;
                    if (currentTexture != null && currentTexture.equals(texture)) {
                        textureOk = true;
                    }

                    // populate texture coords if ok (in case we have more than 1 texture and 1 is missing. see face.obj example)
                    int[] text = faces.facesTexIdxs.get(i);
                    for (int j = 0; j < text.length; j++) {
                        if (textureData == null || textureOk) {
                            if (text[j] * 2 >= 0 && text[j] * 2 < textureCoordsBuffer.limit()) {
                                anyTextureOk = true;
                                textureCoordsArraysBuffer.put(counter++, textureCoordsBuffer.get(text[j] * 2));
                                textureCoordsArraysBuffer.put(counter++, textureCoordsBuffer.get(text[j] * 2 + 1));
                            } else{
                                Log.v("Object3DBuilder","Wrong texture for face "+i);
                                textureCoordsArraysBuffer.put(counter++, 0f);
                                textureCoordsArraysBuffer.put(counter++, 0f);
                            }
                        } else {
                            textureCoordsArraysBuffer.put(counter++, 0f);
                            textureCoordsArraysBuffer.put(counter++, 0f);
                        }
                    }
                }

                if (!anyTextureOk) {
                    Log.i("Object3DBuilder", "Texture is wrong. Applying global texture");
                    counter = 0;
                    for (int j=0; j<faces.facesTexIdxs.size(); j++) {
                        int[] text = faces.facesTexIdxs.get(j);
                        for (int i = 0; i < text.length; i++) {
                            textureCoordsArraysBuffer.put(counter++, textureCoordsBuffer.get(text[i] * 2));
                            textureCoordsArraysBuffer.put(counter++, textureCoordsBuffer.get(text[i] * 2 + 1));
                        }
                    }
                }
            } catch (Exception ex) {
                Log.e("Object3DBuilder", "Failure to load texture coordinates", ex);
            }
        }
        //}
        setTextureData(textureData);

        return this;
    }


    public int getDrawMode() {
        return drawMode;
    }

    public Model setDrawMode(int drawMode) {
        this.drawMode = drawMode;
        return this;
    }
    public boolean isFlipTextCoords() {
        return flipTextCoords;
    }

    public void setFlipTextCoords(boolean flipTextCoords) {
        this.flipTextCoords = flipTextCoords;
    }

    public byte[] getTextureData() {
        return textureData;
    }

    public void setTextureData(byte[] textureData) {
        this.textureData = textureData;
    }


    private static ByteBuffer createNativeByteBuffer(int length) {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(length);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());
        return bb;
    }

    public FloatBuffer getVerts() {
        return vertexBuffer;
    }

    public FloatBuffer getVertexArrayBuffer() {
        return vertexArrayBuffer;
    }

    public Model setVertexArrayBuffer(FloatBuffer vertexArrayBuffer) {
        this.vertexArrayBuffer = vertexArrayBuffer;
        return this;
    }

    public FloatBuffer getVertexNormalsArrayBuffer() {
        return vertexNormalsArrayBuffer;
    }

    public Model setVertexNormalsArrayBuffer(FloatBuffer vertexNormalsArrayBuffer) {
        this.vertexNormalsArrayBuffer = vertexNormalsArrayBuffer;
        return this;
    }


    public Model setTextureCoordsArrayBuffer(FloatBuffer textureCoordsArrayBuffer) {
        this.textureCoordsArrayBuffer = textureCoordsArrayBuffer;
        return this;
    }



    public boolean isTextured(){
        return isTextured;
    }

    public FloatBuffer getTextureCoordsArrayBuffer(){
        return textureBuffer;
    }

    public void setTextureFile(String textureFile) {
        this.textureFile = textureFile;
    }

    public String getTextureFile(){
        return textureFile;
    }

    public FloatBuffer getVertexColorsArrayBuffer() {
        return vertexColorsArrayBuffer;
    }

    public Model setVertexColorsArrayBuffer(FloatBuffer vertexColorsArrayBuffer) {
        this.vertexColorsArrayBuffer = vertexColorsArrayBuffer;
        return this;
    }

    public String getId() {
        return id;
    }

    public Model setId(String id) {
        this.id = id;
        return this;
    }
    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() {
        return this.uri;
    }



    public void setDimensions(objLoader.ModelDimensions modelDimensions) {
        this.modelDimensions = modelDimensions;
    }

    public objLoader.ModelDimensions getDimensions() {
        return modelDimensions;
    }

    public void setLoader(objLoader loader) {
        this.loader = loader;
    }


    public objLoader getLoader() {
        return loader;
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
