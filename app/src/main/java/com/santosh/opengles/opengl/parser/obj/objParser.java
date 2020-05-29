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
    private List<String> facesList;

    private FloatBuffer verticesBuffer;
    private IntBuffer indexBuffer;



    public Model load(String name) {
        verticesList = new ArrayList<>();
        facesList = new ArrayList<>();
        try {


            BufferedReader reader = IOHelper.loadModel(name);
            String line = reader.readLine();

            while (line != null) {

                if (line.startsWith("v ")) {
                    Log.i("Vertices","v");
                    verticesList.add(line);
                } else if (line.startsWith("f ")) {
                    facesList.add(line);
                }

                line = reader.readLine();
            }
            reader.close();


            // Create buffer for vertices
            ByteBuffer buffer1 = ByteBuffer.allocateDirect(verticesList.size() * 3 * 4);
            buffer1.order(ByteOrder.nativeOrder());
            verticesBuffer = buffer1.asFloatBuffer();


            indexBuffer = IntBuffer.allocate(facesList.size() * 3);



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
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


        return  new Model(verticesBuffer, verticesList.size() *3, indexBuffer,facesList.size()*3);
    }


}
