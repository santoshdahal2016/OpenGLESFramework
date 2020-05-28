package com.santosh.opengles.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.santosh.opengles.activity.MainActivity;
import com.santosh.opengles.helper.IOHelper;

import java.io.IOException;

public class GLSurfaceViewOpengl extends GLSurfaceView {

    private MainActivity parent;

    /**
     * Standard View constructor. In order to render something, you
     * must call {@link #setRenderer} to register a renderer.
     *
     * @param context
     * @param attrs
     */
    public GLSurfaceViewOpengl(Context context, AttributeSet attrs) throws IOException, IllegalAccessException {
        super(context, attrs);

        IOHelper.SetContext(context);

        // Create an OpenGL ES 3.0 context
        setEGLContextClientVersion(3);
        GLRendererOpengl m_Renderer = new GLRendererOpengl(context, this);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(m_Renderer);
    }


    public void setParent(MainActivity parent) {
        this.parent = parent;
    }

    public MainActivity getModelActivity() {
        return parent;
    }


}
