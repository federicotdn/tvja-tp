package com.tvja.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created by Hobbit on 10/28/15.
 */
public class AnimationModel extends BaseModel {
    private Model model;
    private Texture tex;

    public AnimationModel(Model model, Texture tex) {
        this.model = model;
        this.tex = tex;
    }

    @Override
    public void render(ShaderProgram shaderProgram, int primitiveType) {
       for (Mesh mesh : model.meshes) {
           mesh.render(shaderProgram, primitiveType);
       }
    }

    @Override
    public void bind() {
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        tex.bind();
    }
}
