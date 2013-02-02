package px.tileEngineV2.desktop.graphics;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.IntBuffer;
import java.util.Scanner;

import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;

public class ShaderLoader {
    
    /**When provided with the locations of the files containing your vertex and fragment 
     * shaders, automatically loads them into OpenGL's memory, compiles them, links them
     * into a program and returns the reference OpenGL will use for the new shader program
     * @param drawable Drawable context to enable access to OpenGL functions.
     * @param vertex File-path and name of Verex Shader File
     * @param fragment File-path and name of Fragment Shader File.
     * @param loggingEnabled Permits printing log information to the System.out stream.
     * @param outStream if loggingEnabled is true, the method will print output to this
     * stream object, if this parameter is left null then output will instead print to
     * System.out.
     * @return OpenGL reference number to the newly created shader program. */
    @SuppressWarnings("null")
    public static int loadShaders(GLAutoDrawable drawable, String vertex, 
            String fragment, boolean loggingEnabled, PrintStream outStream) {
        
        // Load in shader text.
        Scanner vertexScanner = null;
        Scanner fragmentScanner = null;
        String vertexShader = "";
        String fragmentShader = "";
        
        if (loggingEnabled) {
            if (outStream == null) {
                outStream = System.out;
            }
            outStream.println("Reading Shaders");
        }
        try {
            vertexScanner = new Scanner(
                    new FileInputStream(vertex));
            fragmentScanner = new Scanner(
                    new FileInputStream(fragment));
            
            while(vertexScanner.hasNextLine()) {
                vertexShader += vertexScanner.nextLine() + "\n";
            }
            
            while (fragmentScanner.hasNextLine()) {
                fragmentShader += fragmentScanner.nextLine() + "\n";
            }
        } catch (FileNotFoundException e) {
            // TODO Shaders not found
            e.printStackTrace();
            System.exit(-1);
        } finally {
            if (vertexScanner == null) { vertexScanner.close(); }
            if (fragmentScanner == null) { fragmentScanner.close(); }
        }

        //Retrieve OpenGL context.
        GL4 gl = drawable.getGL().getGL4();
        int[] result = new int[] {GL4.GL_FALSE};
        int[] infoLogLength = new int [] {0};

        // Compile Vertex Shaders.
        if (loggingEnabled) {
            outStream.println("Compiling Vertex Shader");
        }
        int vertexShaderId = gl.glCreateShader(GL4.GL_VERTEX_SHADER);
        gl.glShaderSource(vertexShaderId, 1, new String[]{vertexShader}, 
                IntBuffer.wrap(new int[]{ vertexShader.length() }));
        gl.glCompileShader(vertexShaderId);
        //Check Vertex Shader.
        if (loggingEnabled) {
            gl.glGetShaderiv(vertexShaderId, GL4.GL_COMPILE_STATUS, result, 0);
            gl.glGetShaderiv(vertexShaderId, GL4.GL_INFO_LOG_LENGTH, infoLogLength, 0);
            if (infoLogLength[0] > 0) {
                byte[] infoLog = new byte[infoLogLength[0] + 1];
                gl.glGetShaderInfoLog(vertexShaderId, 
                        infoLogLength[0], null, 0, infoLog, 0);
                outStream.println(new String(infoLog));
            }
        }

        //Compile Fragment Shader.
        if (loggingEnabled) { 
            outStream.println("Compiling Fragment Shader");
        }
        int fragmentShaderId = gl.glCreateShader(GL4.GL_FRAGMENT_SHADER);
        gl.glShaderSource(fragmentShaderId, 1, new String[]{fragmentShader},
                IntBuffer.wrap(new int[]{ fragmentShader.length() }));
        gl.glCompileShader(fragmentShaderId);
        //Check Fragment Shader
        if (loggingEnabled) {
            gl.glGetShaderiv(fragmentShaderId, GL4.GL_COMPILE_STATUS, result, 0);
            gl.glGetShaderiv(fragmentShaderId, GL4.GL_INFO_LOG_LENGTH, infoLogLength, 0);
            if (infoLogLength[0] > 0) {
                byte[] infoLog = new byte[infoLogLength[0] + 1];
                gl.glGetShaderInfoLog(fragmentShaderId, 
                        infoLogLength[0], null, 0, infoLog, 0);
                outStream.println(new String(infoLog));
            }
        }
        
        //Link Program.
        if (loggingEnabled) { 
            outStream.println("Linking Program.");
        }
        int programId = gl.glCreateProgram();
        gl.glAttachShader(programId, vertexShaderId);
        gl.glAttachShader(programId, fragmentShaderId);
        gl.glLinkProgram(programId);
        //Check program.
        if (loggingEnabled) {
            gl.glGetProgramiv(programId, GL4.GL_LINK_STATUS, result, 0);
            gl.glGetProgramiv(programId, GL4.GL_INFO_LOG_LENGTH, infoLogLength, 0);
            if (infoLogLength[0] > 0) {
                byte[] infoLog = new byte[infoLogLength[0] + 1];
                gl.glGetProgramInfoLog(programId, infoLogLength[0], null, 0, infoLog, 0);
                outStream.println(new String(infoLog));
            }
        }
        
        //Cleanup shaders.
        gl.glDeleteShader(vertexShaderId);
        gl.glDeleteShader(fragmentShaderId);
        
        return programId;
    }
    
    /**When provided with the locations of the files containing your vertex and fragment 
     * shaders, automatically loads them into OpenGL's memory, compiles them, links them
     * into a program and returns the reference OpenGL will use for the new shader 
     * program.
     * <br>
     * This is the same function as above, except that it doesn't log any information.
     * @param drawable Drawable context to enable access to OpenGL functions.
     * @param vertex File-path and name of Verex Shader File
     * @param fragment File-path and name of Fragment Shader File.
     * @return OpenGL reference number to the newly created shader program. */
    public static int loadShaders(GLAutoDrawable drawable, 
            String vertex, String fragment) {
        return loadShaders(drawable, vertex, fragment, true, null);
    }
}

