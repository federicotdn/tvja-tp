package com.tvja.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.tvja.camera.FPSCamera;
import com.tvja.camera.FPSControllableCamera;
import com.tvja.camera.OrthoFPSCamera;
import com.tvja.render.Light;
import com.tvja.render.ModelInstance;
import com.tvja.render.Shader;

import java.util.ArrayList;
import java.util.List;

public class TPGame extends ApplicationAdapter {
    Texture img, img2;
    Mesh shipMesh;
    Mesh cubeMesh;
    ShaderProgram shaderProgram;
    FPSControllableCamera cam = new FPSCamera(0.1f, 0.01f);

    Shader directionalShader;
    Shader pointShader;
    Shader spotShader;
    Shader earlyZShader;

    List<ModelInstance> models = new ArrayList<>();

    List<Light> directionalLights = new ArrayList<>();
    List<Light> spotLights = new ArrayList<>();
    List<Light> pointLights = new ArrayList<>();

    @Override
    public void create() {
        setupGdx();

        img = new Texture(Gdx.files.internal("models/ship.png"));
        img2 = new Texture(Gdx.files.internal("models/plain.jpg"));

        String vs = Gdx.files.internal("shaders/defaultVS.glsl").readString();
        String fs = Gdx.files.internal("shaders/phong-spotFS.glsl").readString();

        shaderProgram = new ShaderProgram(vs, fs);

        if (!shaderProgram.isCompiled()) {
            System.out.println(shaderProgram.getLog());
        }

        ModelLoader<?> loader = new ObjLoader();
        ModelData shipModel = loader.loadModelData(Gdx.files.internal("models/ship.obj"));

        ModelData cubeModel = loader.loadModelData(Gdx.files.internal("models/cube-textures.obj"));

        shipMesh = new Mesh(true, shipModel.meshes.get(0).vertices.length,
                shipModel.meshes.get(0).parts[0].indices.length, VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));

        shipMesh.setVertices(shipModel.meshes.get(0).vertices);
        shipMesh.setIndices(shipModel.meshes.get(0).parts[0].indices);

        cubeMesh = new Mesh(true, cubeModel.meshes.get(0).vertices.length,
                cubeModel.meshes.get(0).parts[0].indices.length, VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));

        cubeMesh.setVertices(cubeModel.meshes.get(0).vertices);
        cubeMesh.setIndices(cubeModel.meshes.get(0).parts[0].indices);

        directionalShader = new Shader("shaders/defaultVS.glsl", "shaders/phong-directionalFS.glsl");
        pointShader = new Shader("shaders/defaultVS.glsl", "shaders/phong-pointFS.glsl");
        spotShader = new Shader("shaders/defaultVS.glsl", "shaders/phong-spotFS.glsl");
        earlyZShader = new Shader("shaders/defaultVS.glsl", "shaders/early-zFS.glsl");

        models.add(new ModelInstance(shipMesh, img));
        models.add(new ModelInstance(shipMesh, img).translate(2, 0, 0));
        models.add(new ModelInstance(shipMesh, img).translate(4, 0, 0));
        models.add(new ModelInstance(shipMesh, img).translate(6, 0, 0));
        models.add(new ModelInstance(cubeMesh, img2).translate(-1000, -1, -1000).scale(2000, 0.5f, 2000));

        directionalLights.add(Light.newDirectional(new Vector3(1, 1, 1), new Vector3(1,1,1)));
        pointLights.add(Light.newPoint(new Vector3(1, 4, 1), new Vector3(1, 1, 1)));
        spotLights.add(Light.newSpot(new Vector3(0,5,0), new Vector3(0,1,0), new Vector3(0.8f,0.8f,0.8f), (float)Math.PI/10));
    }

    private void setupGdx() {
        Gdx.graphics.setDisplayMode(1000, 1000, false);
        Gdx.input.setCursorCatched(true);
        
        //Gdx.gl20.glDepthMask(true);

        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl20.glDepthFunc(GL20.GL_LEQUAL);
        
        Gdx.gl20.glEnable(GL20.GL_BLEND);
        Gdx.gl20.glBlendFunc(Gdx.gl20.GL_ONE, Gdx.gl20.GL_DST_COLOR);    }

    @Override
    public void render() {
    	//angle += 0.01f;
    	
        checkExit();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        cam.update();

        updateCameraType();

        spotLights.get(0).getDirection().get().rotateRad(new Vector3(1, 0, 1), 0.0002f);

        earlyZShader.render(cam, models, null);
        spotShader.render(cam, models, spotLights);
        pointShader.render(cam, models, pointLights);
        directionalShader.render(cam, models, directionalLights);
    }

    private void updateCameraType() {
        if (Gdx.input.isKeyPressed(Keys.X)) {
            Vector3 pos = cam.getPosition();
            Vector3 ori = cam.getOrientation();
            cam = new FPSCamera(0.1f, 0.01f);
            cam.setOrientation(ori);
            cam.setPosition(pos);
        } else if (Gdx.input.isKeyPressed(Keys.C)) {
            Vector3 pos = cam.getPosition();
            Vector3 ori = cam.getOrientation();
            cam = new OrthoFPSCamera(0.1f, 0.01f);
            cam.setOrientation(ori);
            cam.setPosition(pos);
        }
    }

    private void checkExit() {
        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }
}
