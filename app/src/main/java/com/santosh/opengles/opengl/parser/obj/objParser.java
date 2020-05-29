package com.santosh.opengles.opengl.parser.obj;

import android.util.Log;

import com.santosh.opengles.helper.IOHelper;
import com.santosh.opengles.opengl.element.Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class objParser {

    private List<String> verticesList;
    private List<String> texturesList;
    private List<String> normalsList;

    private List<String> facesList;

    private FloatBuffer verticesBuffer;
    private IntBuffer indexBuffer;
    private FloatBuffer textureBuffer;
    private FloatBuffer normalBuffer;

    float[] texturesArray;
    float[] verticesArray;
    float[] normalsArray;

    public Model load(String name) {
        verticesList = new ArrayList<>();
        facesList = new ArrayList<>();
        texturesList = new ArrayList<>();
        normalsList = new ArrayList<>();

        try {


            BufferedReader reader = IOHelper.loadModel(name);
            String line = reader.readLine();

            while (line != null) {

                if (line.startsWith("v ")) {
                    verticesList.add(line);
                }
                else if (line.startsWith("vt ")) {
                    texturesList.add(line);
                }
                else if (line.startsWith("vn ")) {
                    normalsList.add(line);
                }
                else if (line.startsWith("f ")) {
                    facesList.add(line);
                }

                line = reader.readLine();
            }
            reader.close();


            // Create buffer for vertices
            ByteBuffer buffer1 = ByteBuffer.allocateDirect(verticesList.size() * 3 * 4);
            buffer1.order(ByteOrder.nativeOrder());
            verticesBuffer = buffer1.asFloatBuffer();

            // Create buffer for vertices
            ByteBuffer buffer2 = ByteBuffer.allocateDirect(facesList.size() * 3  * 2 * 4);
            buffer2.order(ByteOrder.nativeOrder());
            textureBuffer = buffer2.asFloatBuffer();



            // Create buffer for vertices
            ByteBuffer buffer3 = ByteBuffer.allocateDirect(facesList.size() * 3 *3* 4);
            buffer3.order(ByteOrder.nativeOrder());
            normalBuffer = buffer3.asFloatBuffer();

            indexBuffer = IntBuffer.allocate(facesList.size() * 3);

            texturesArray = new float[verticesList.size() * 2];
            verticesArray = new float[verticesList.size() * 3];
            normalsArray = new float[verticesList.size() * 3];

            for (String vertex : verticesList) {
                String coords[] = vertex.split(" ");
                float x = Float.parseFloat(coords[1]);
                float y = Float.parseFloat(coords[2]);
                float z = Float.parseFloat(coords[3]);

                verticesBuffer.put(x);
                verticesBuffer.put(y);
                verticesBuffer.put(z);
            }


            for (String face : facesList) {
                String vertexIndices[] = face.split(" ");


                String vextex1[] = vertexIndices[1].split("/");
                int vertex1 = Integer.parseInt(vextex1[0]);




                String vextex2[] = vertexIndices[2].split("/");
                int vertex2 = Integer.parseInt(vextex2[0]);


                String vextex3[] = vertexIndices[3].split("/");
                int vertex3 = Integer.parseInt(vextex3[0]);


                indexBuffer.put(vertex1 - 1);
                indexBuffer.put(vertex2 - 1);
                indexBuffer.put(vertex3 - 1);



                int texture1 = Integer.parseInt(vextex1[1]);

                String textureLine1= texturesList.get(texture1-1);
                String[] coords1 = textureLine1.split(" ");
                float x1 = Float.parseFloat(coords1[1]);
                float y1 = Float.parseFloat(coords1[2]);

                textureBuffer.put(x1);
                textureBuffer.put(1-y1);


                int texture2 = Integer.parseInt(vextex2[1]);

                String textureLine2 = texturesList.get(texture2-1);
                String[] coords2 = textureLine2.split(" ");
                float x2 = Float.parseFloat(coords2[1]);
                float y2 = Float.parseFloat(coords2[2]);

                textureBuffer.put(x2);
                textureBuffer.put(1-y2);


                int texture3 = Integer.parseInt(vextex3[1]);

                String textureLine3 = texturesList.get(texture3-1);
                String[] coords3 = textureLine3.split(" ");
                float x3 = Float.parseFloat(coords3[1]);
                float y3 = Float.parseFloat(coords3[2]);

                textureBuffer.put(x3);
                textureBuffer.put(1-y3);



                int normal1 = Integer.parseInt(vextex1[2]);

                String normalLine1= normalsList.get(normal1-1);
                String[] normalcoords1 = normalLine1.split(" ");
                float normalx1 = Float.parseFloat(normalcoords1[1]);
                float normaly1 = Float.parseFloat(normalcoords1[2]);
                float normalz1 = Float.parseFloat(normalcoords1[3]);

                normalBuffer.put(normalx1);
                normalBuffer.put(normaly1);
                normalBuffer.put(normalz1);





                int normal2 = Integer.parseInt(vextex2[2]);

                String normalLine2= normalsList.get(normal2-1);
                String[] normalcoords2 = normalLine2.split(" ");
                float normalx2 = Float.parseFloat(normalcoords2[1]);
                float normaly2 = Float.parseFloat(normalcoords2[2]);
                float normalz2 = Float.parseFloat(normalcoords2[3]);

                normalBuffer.put(normalx2);
                normalBuffer.put(normaly2);
                normalBuffer.put(normalz2);



                int normal3 = Integer.parseInt(vextex3[2]);

                String normalLine3= normalsList.get(normal3-1);
                String[] normalcoords3 = normalLine3.split(" ");
                float normalx3 = Float.parseFloat(normalcoords3[1]);
                float normaly3 = Float.parseFloat(normalcoords3[2]);
                float normalz3 = Float.parseFloat(normalcoords3[3]);

                normalBuffer.put(normalx3);
                normalBuffer.put(normaly3);
                normalBuffer.put(normalz3);
            }


        } catch (IOException e) {
            e.printStackTrace();

        }


        return  new Model(verticesBuffer, verticesList.size() *3, indexBuffer,facesList.size()*3);

//        return  new Model(verticesBuffer, verticesList.size() *3, indexBuffer,facesList.size()*3, textureBuffer , facesList.size()*3*2);

    }


}
