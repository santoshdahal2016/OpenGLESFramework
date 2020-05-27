package com.santosh.opengles.opengl.element;

import android.content.Context;
import android.util.Log;

import com.santosh.opengles.helper.IOHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BuildDrawer {

    private Map<String, String> shadersCode = new HashMap<>();
    private Map<String, Drawer> drawers = new HashMap<>();


    private final String[] shaderIdTemp = new String[3];


    public BuildDrawer(Context context) throws IllegalAccessException, IOException {

        Log.i("DrawerFactory", "Discovering shaders...");
        String [] filelist = context.getAssets().list("shaders");

        if (filelist.length > 0) {
            for (String file : filelist) {
                String shaderCode =IOHelper.LoadShaderFileFromAssets("shaders/"+file);
                shadersCode.put(file.substring(0, file.lastIndexOf("."))
                        , shaderCode);
            }
        }
        Log.i("DrawerFactory", "Shaders loaded: " + shadersCode.size());
    }

    public Drawer getDrawer(Model obj, boolean usingTextures) {

        // double check features
        boolean isTextured = usingTextures && obj.getTextureCoordsArrayBuffer() != null;

        final String[] shaderId = getShaderId(isTextured);

        // get cached drawer
        Drawer drawer = drawers.get(shaderId[0]);
        if (drawer != null) return drawer;

        // build drawer
        String vertexShaderCode = shadersCode.get(shaderId[1]);
        String fragmentShaderCode = shadersCode.get(shaderId[2]);
        if (vertexShaderCode == null || fragmentShaderCode == null) {
            Log.e("DrawerFactory", "Shaders not found for " + shaderId[0]);
            return null;
        }


        drawer = Drawer.getInstance(shaderId[0], vertexShaderCode, fragmentShaderCode);

        // cache drawer
        drawers.put(shaderId[0], drawer);

        // return drawer
        return drawer;
    }

    private String[] getShaderId(boolean isTextured) {
        if (isTextured){

                shaderIdTemp[0]="basic_";
                shaderIdTemp[1]="basic_vert";
                shaderIdTemp[2]="basic_frag";

        } else{

                shaderIdTemp[0]="basicwithouttexture_";
                shaderIdTemp[1]="basicwithouttexture_vert";
                shaderIdTemp[2]="basicwithouttexture_frag";

        }
        return shaderIdTemp;
    }

}
