package com.tvja.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.tvja.utils.MathUtils;

import java.util.List;
import java.util.Map;

public abstract class Shader {
    private static final Matrix4 biasMat = new Matrix4(new float[]{
            0.5f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.5f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f
    });
    protected ShaderProgram shaderProgram;

    public Shader(String vertex, String fragment) {
        String vs = buildShaderString(vertex);
        String fs = buildShaderString(fragment);

        shaderProgram = new ShaderProgram(vs, fs);

        if (!shaderProgram.isCompiled()) {
            System.out.println(shaderProgram.getLog());
            throw new IllegalStateException("Unable to compile GLSL shader.");
        }
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

    public void render(ViewWorldObject view, List<ModelInstance> models) {
        render(view, models, null);
    }

    public void renderFullscreen(ModelInstance quad, int texture) {
        shaderProgram.begin();
        setUniformi("u_texture", texture);
        quad.getMesh().render(shaderProgram, GL20.GL_TRIANGLES);
        shaderProgram.end();
    }

//    public void renderShadow(Light light, ViewWorldObject cam, List<ModelInstance> models, int texture) {
//        if (light == null || cam == null || models == null || models.isEmpty()) {
//            return;
//        }
//
//        shaderProgram.begin();
//
//        setUniformi("u_shadow_map", texture);
//
//        for (ModelInstance model : models) {
//            setUniformMat4("u_mvp", cam.getViewProjection().mul(model.getTRS()));
//            setUniformMat4("u_light_bias_mvp", biasMat.cpy().tra().mul(light.getViewProjection().mul(model.getTRS())));
//            setUniformMat4("u_model_mat", model.getTRS());
//            setUniformf("u_view_far", light.getFarZ());
//            setUniform4fv("u_light_pos", MathUtils.toVec4fPoint(light.getPosition()));
//            model.getMesh().render(shaderProgram, GL20.GL_TRIANGLES);
//        }
//
//        shaderProgram.end();
//    }

    protected void setCommonModelUniforms(ViewWorldObject view, ModelInstance model) {
        setUniform4fv("u_cam_pos", MathUtils.toVec4fPoint(view.getPosition()));
        setUniformMat4("u_model_mat", model.getTRS());
        setUniformMat4("u_model_rotation_mat", model.getR());
        setUniformi("u_texture", 0); // Must match with glActiveTexture call
        setUniformi("u_shininess", model.getShininess());
    }

    protected abstract void setModelUniforms(ViewWorldObject view, ModelInstance model);

    protected abstract void setLightUniforms(Light light);

    protected void setShadowUniforms(List<FrameBuffer> shadowMaps) {
        //Do nothing by default
    }

    protected Map<Light, List<FrameBuffer>> setUpShader(ViewWorldObject view, List<ModelInstance> models, List<Light> lights) {
        return null;
    }

    public void render(ViewWorldObject view, List<ModelInstance> models, List<Light> lights) {
        if (view == null || models == null || models.isEmpty()) {
            return;
        }

        Map<Light, List<FrameBuffer>> shadowMaps = setUpShader(view, models, lights);

        shaderProgram.begin();

        for (ModelInstance model : models) {
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
            model.getTex().bind();
            setUniformMat4("u_mvp", view.getViewProjection().mul(model.getTRS()));

//            setCommonModelUniforms(view, model);
            setModelUniforms(view, model);

            if (lights != null && !lights.isEmpty()) {
                for (Light light : lights) {
                    setUniform4fv("u_light_color", MathUtils.toVec4fPoint(light.getColor()));
                    setLightUniforms(light);
                    setShadowUniforms(shadowMaps != null ? shadowMaps.get(light) : null);
                    model.getMesh().render(shaderProgram, GL20.GL_TRIANGLES);
                }
            } else {
                model.getMesh().render(shaderProgram, GL20.GL_TRIANGLES);
            }
        }

        shaderProgram.end();
    }

    protected void setUniform4fv(String name, float[] val) {
        shaderProgram.setUniform4fv(name, val, 0, 4);
    }

    protected void setUniformi(String name, int val) {
        shaderProgram.setUniformi(name, val);
    }

    protected void setUniformMat4(String name, Matrix4 val) {
        shaderProgram.setUniformMatrix(name, val);
    }

    protected void setUniformf(String name, float val) {
        shaderProgram.setUniformf(name, val);

    }
}
