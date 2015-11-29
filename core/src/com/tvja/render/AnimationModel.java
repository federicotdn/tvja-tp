package com.tvja.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by Hobbit on 10/28/15.
 */
public class AnimationModel extends BaseModel {
    private com.badlogic.gdx.graphics.g3d.ModelInstance modelInstance;
    private Texture tex;
    private AnimationController animationController;
    private float[] bones;

    public AnimationModel(ModelInstance modelInstance, Texture tex) {
        super("shaders/animated-VS.glsl", "shaders/animated-shadow-VS.glsl");
        this.modelInstance = modelInstance;
        this.tex = tex;
        animationController = new AnimationController(modelInstance);
        animationController.animate(modelInstance.animations.get(0).id, -1, 1f, null, 0.2f);
    }

    public void setBones(float[] bones) {
        this.bones = bones;
    }

    @Override
    public void render(ShaderProgram shaderProgram, int primitiveType, ViewWorldObject view) {
        Array<Renderable> renderables = new Array<Renderable>();
//        shaderProgram.setUniform4fv("u_ambient_color", new float[] {0.3f,1,1,1}, 0, 4);
        final Pool<Renderable> pool = new Pool<Renderable>() {
            @Override
            protected Renderable newObject() {
                return new Renderable();
            }

            @Override
            public Renderable obtain() {
                Renderable renderable = super.obtain();
                renderable.material = null;
                renderable.mesh = null;
                renderable.shader = null;
                return renderable;
            }
        };

        modelInstance.getRenderables(renderables, pool);
        Matrix4 idtMatrix = new Matrix4().idt();
        float[] bones = new float[32 * 16];
        for (int i = 0; i < bones.length; i++)
            bones[i] = idtMatrix.val[i % 16];
        for (Renderable render : renderables) {
                shaderProgram.setUniformMatrix("u_mvp", view.getViewProjection().mul(render.worldTransform));
            for (int i = 0; i < bones.length; i++) {
                final int idx = i / 16;
                bones[i] = (render.bones == null || idx >= render.bones.length || render.bones[idx] == null) ?
                        idtMatrix.val[i % 16] : render.bones[idx].val[i % 16];
            }
                shaderProgram.setUniformMatrix4fv("u_bones", bones, 0, bones.length);
            render.mesh.render(shaderProgram, render.primitiveType, render.meshPartOffset, render.meshPartSize);

        }
    }

    @Override
    public Matrix4 getTRS() {
        return modelInstance.transform;
    }


    @Override
    public void bind() {
        if (tex != null) {
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
            tex.bind();
        }
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    public AnimationController getAnimationController() {
        return animationController;
    }
}
