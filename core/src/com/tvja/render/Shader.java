package com.tvja.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.tvja.camera.OpenGLCamera;
import com.tvja.utils.VecUtils;

import java.util.List;

/**
 * Created by Hobbit on 9/4/15.
 */
public class Shader {
    protected ShaderProgram shaderProgram;

    public Shader(String vertex, String fragment) {
        String vs = Gdx.files.internal(vertex).readString();
        String fs = Gdx.files.internal(fragment).readString();

        shaderProgram = new ShaderProgram(vs, fs);

        if (!shaderProgram.isCompiled()) {
            System.out.println(shaderProgram.getLog());
            throw new IllegalStateException("Unable to compile GLSL shader.");
        }
    }

    public void render(OpenGLCamera cam, List<ModelInstance> models, List<Light> lights) {
        shaderProgram.begin();

        for (ModelInstance model : models) {
            Gdx.gl.glActiveTexture(0);
            model.getTex().bind(0);


            shaderProgram.setUniformi("u_texture", 0);
            shaderProgram.setUniformMatrix("u_mvp", cam.getViewProjection().mul(model.getTRS()));
            shaderProgram.setUniformMatrix("u_model_mat", model.getTRS()); //para (es)specular
            shaderProgram.setUniformMatrix("u_model_rotation_mat", model.getR());

            for (Light light : lights) {
                light.getPosition().ifPresent(v -> shaderProgram.setUniform4fv("u_light_position",
                        VecUtils.toVec4f(v), 0, 4));
                light.getDirection().ifPresent(d -> shaderProgram.setUniform4fv("u_light_direction",
                        VecUtils.toVec4f(d), 0, 4));
                shaderProgram.setUniform4fv("u_ambient_color", VecUtils.toVec4f(light.getAmbientColor()), 0, 4);
                shaderProgram.setUniform4fv("u_light_color", VecUtils.toVec4f(light.getColor()), 0, 4);
                light.getAngle().ifPresent(a -> shaderProgram.setUniformf("u_cutoff_angle", a));

                if (shaderProgram.hasUniform("u_cam_pos")) {
                    shaderProgram.setUniform4fv("u_cam_pos", VecUtils.toVec4f(cam.getPosition()), 0, 4);
                }

                Gdx.gl20.glBlendFunc(Gdx.gl20.GL_ONE, Gdx.gl20.GL_ONE);
                model.getMesh().render(shaderProgram, GL20.GL_TRIANGLES);
            }
        }


        shaderProgram.end();
    }
}
