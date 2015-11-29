package com.tvja.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hobbit on 11/28/15.
 */
public class ShaderProgramPool {
    private static ShaderProgramPool instance;

    private Map<String, ShaderProgram> shaders;

    private ShaderProgramPool() {
        shaders = new HashMap<>();
    }

    public static ShaderProgramPool getInstance() {
        if (instance == null) {
            instance = new ShaderProgramPool();
        }

        return instance;
    }

    public ShaderProgram getShaderProgram(String vs, String fs) {
        String key = vs + fs;
        if (!shaders.containsKey(key)) {
            String vertex = buildShaderString(vs);
            String fragment = buildShaderString(fs);
            ShaderProgram shaderProgram = new ShaderProgram(vertex, fragment);

            if (!shaderProgram.isCompiled()) {
                System.out.println(shaderProgram.getLog());
                throw new IllegalStateException("Unable to compile GLSL shader.");
            }
            shaders.put(key, shaderProgram);
        }
        return shaders.get(key);
    }

    /*
     *  :-(
     */
    private String buildShaderString(String fileName) {
        String[] lines = Gdx.files.internal(fileName).readString().split("\\r?\\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.startsWith("//%include")) {
                String[] parts = line.split("\\s+");
                if (parts.length != 2) {
                    throw new IllegalStateException("Invalid %include directive.");
                }

                String included = Gdx.files.internal(parts[1]).readString();
                lines[i] = included;
            }
        }

        return String.join(System.getProperty("line.separator"), lines);
    }
}
