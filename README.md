# OpenGLES Android


## Steps

### Create Canvas

1. Enable OpenGl feature inside Manifest file .
```
<uses-feature
android:glEsVersion="0x00020000"
android:required="true" />
```
2. Create two activity extending - GLSurfaceView and GLSurfaceView.Renderer . 
3. Add GLSurfaceView extended activity in the layout file .

### Hello world in OpenGLES


We will draw the triangle as the hello_opengl example , This the minimum opengl program .


The drawing process in opengl is fully based on the graphics pipeline . The graphics pipeline takes as input a set of 3D coordinates and transforms these to colored 2D pixels on your screen.The graphics pipeline can be divided into several steps where each step requires the output of the previous step as its input.

Nowadays Graphic Processor have thousands of small processing cores to quickly process your data. The processing cores run small programs on the GPU for each step of the pipeline. These small programs are called shaders .

Some of these shaders are configurable by the developer which allows us to write our own shaders to replace the existing default shaders .Shaders are written in the OpenGL Shading Language (GLSL)

<img src="https://i.ibb.co/tzF1PvX/pipeline.png">

Abstract representation of all the stages of the graphics pipeline. Note that the blue sections represent sections where we can inject our own shaders.

### Static Part (One time execution)

The Static part passes two thing to the loop part : - Vertices Buffer and OpenGL ES Program.

1. Define the Vertices of the three ends of triangle ,  Vertex Shader Code (GLSL language) and fragment Shader Code  (GLSL language).
2. Move the Vertices data to the buffer .
3. Compline Vertex Shader  and Fragment Shader Code.
4. Create  empty OpenGL ES Program 
5. Attach Vertex and Fragment Code to OpenGL ES Program


### Loop Part 

1. Activate OpenGL ES Program
2. Create  get handle to vertex shader's vPosition member from OpenGL ES Program
3. Create glVertexAttribPointer to vertice buffer and pass it to position handle
4. Create handle to fragment shader's vColor member from OpenGL ES Program
5. Pass Color value to the colorHandle 
6. Call Draw function