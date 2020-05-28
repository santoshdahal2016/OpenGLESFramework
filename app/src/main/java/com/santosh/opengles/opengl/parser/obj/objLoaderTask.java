package com.santosh.opengles.opengl.parser.obj;

import android.app.Activity;
import android.net.Uri;
import android.opengl.GLES20;
import android.util.Log;


import com.santosh.opengles.helper.IOHelper;
import com.santosh.opengles.opengl.element.Model;
import com.santosh.opengles.opengl.parser.ParserTask;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * Wavefront loader implementation
 *
 * @author santosh
 */

public class objLoaderTask extends ParserTask {

    public objLoaderTask(final Activity parent, final Uri uri, final Callback callback) {
        super(parent, uri, callback);
    }

    @Override
    protected List<Model> build() throws IOException {
        InputStream params0 = IOHelper.getInputStream(uri);
        objLoader wfl = new objLoader("");

        // allocate memory`
        publishProgress(0);
        wfl.analyzeModel(params0);
        params0.close();

        // Allocate memory
        publishProgress(1);
        wfl.allocateBuffers();
        wfl.reportOnModel();

        // create the 3D object
        Model data3D = new Model(wfl.numVerts,wfl.numFaces,wfl.getVerts(), wfl.getNormals(), wfl.getTexCoords(), wfl.getFaces(),
                wfl.getFaceMats(), wfl.getMaterials());


        data3D.setId(uri.getPath());
        data3D.setUri(uri);
        data3D.setLoader(wfl);
        data3D.setDrawMode(GLES20.GL_TRIANGLES);
        data3D.setDimensions(data3D.getLoader().getDimensions());

        return Collections.singletonList(data3D);
    }

    @Override
    protected void build(List<Model> datas) throws Exception {
        InputStream stream = IOHelper.getInputStream(uri);
        try {
            Model data = datas.get(0);

            // parse model
            publishProgress(2);
            data.getLoader().loadModel(stream);
            stream.close();

            // scale object
            publishProgress(3);
            data.setScale(new float[]{5, 5, 5});
            // build 3D object buffers

            data.setDrawMode(GLES20.GL_TRIANGLES);

            data.ObjtoModel();
            publishProgress(4);


        } catch (Exception e) {
            Log.e("Object3DBuilder", e.getMessage(), e);
            throw e;
        }
    }
}
