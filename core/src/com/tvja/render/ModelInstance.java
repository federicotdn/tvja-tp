package com.tvja.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ModelInstance extends BaseModel {

    private Mesh mesh;
    private Texture tex;

    public ModelInstance(Mesh mesh, Texture tex) {
        this.mesh = mesh;
        this.tex = tex;
    }

    public Mesh getMesh() {
        return mesh;
    }
    
    public Texture getTex() {
        return tex;
    }


    @Override
    public void render(ShaderProgram shaderProgram, int primitiveType, ViewWorldObject view) {
        mesh.render(shaderProgram, primitiveType);
    }

    @Override
    public void bind() {
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        tex.bind(0);
    }
}
