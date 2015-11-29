package com.tvja.render;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.tvja.utils.MathUtils;

import java.util.List;
import java.util.Map;

public class Shader {
    protected static final Matrix4 biasMat = new Matrix4(new float[]{
            0.5f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.5f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f
    });

    private String fs;
    private boolean useShadow;
    private ShaderProgram shaderProgram;

    public Shader(String fs, boolean useShadow) {
        this.fs = fs;
        this.useShadow = useShadow;
    }

    public void render(ViewWorldObject view, Map<String, List<BaseModel>> models) {
        render(view, models, null);
    }

    protected void setCommonModelUniforms(ViewWorldObject view, BaseModel model) {
        setUniform4fv("u_cam_pos", MathUtils.toVec4fPoint(view.getPosition()));
        setUniformMat4("u_model_mat", model.getTRS());
        setUniformMat4("u_model_rotation_mat", model.getR());
        setUniformi("u_texture", 0); // Must match with glActiveTexture call
        setUniformi("u_shininess", model.getShininess());
    }

    protected void setModelUniforms(ViewWorldObject view, BaseModel model) {
        //Do nothing by default
    }

    protected void setLightUniforms(Light light, BaseModel model) {
        //Do nothing by default
    }

    protected void setShadowUniforms(FrameBuffer shadowMap) {
        //Do nothing by default
    }

    protected Map<Light, FrameBuffer> setUpShader(Map<String, List<BaseModel>> models, List<Light> lights) {
        return null;
    }

    public void render(ViewWorldObject view, Map<String, List<BaseModel>> models, List<Light> lights) {
        if (view == null || models == null || models.isEmpty()) {
            return;
        }

        Map<Light, FrameBuffer> shadowMaps = setUpShader(models, lights);

        for (String vs : models.keySet()) {
            BaseModel m = models.get(vs).get(0);
            String vertex = useShadow ? m.getShadowVS() : m.getVS();
            shaderProgram = ShaderProgramPool.getInstance().getShaderProgram(vertex, fs);
            shaderProgram.begin();
            for (BaseModel model : models.get(vs)) {

                model.bind();

                setModelUniforms(view, model);

                if (lights != null && !lights.isEmpty()) {
                    for (Light light : lights) {
                        setUniform4fv("u_light_color", MathUtils.toVec4fPoint(light.getColor()));
                        setLightUniforms(light, model);
                        setShadowUniforms(shadowMaps != null ? shadowMaps.get(light) : null);
                        model.render(shaderProgram, GL20.GL_TRIANGLES, view);
                    }
                } else {
                    model.render(shaderProgram, GL20.GL_TRIANGLES, view);
                }
            }
            shaderProgram.end();
        }
        if (shadowMaps != null) {
            for (FrameBuffer fb : shadowMaps.values()) {
                FrameBufferPool.getInstance().returnFrameBuffer(fb);
            }
        }

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
