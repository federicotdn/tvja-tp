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
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import com.tvja.camera.FPSCamera;
import com.tvja.camera.FPSControllableCamera;
import com.tvja.camera.OrthoFPSCamera;

public class TPGame extends ApplicationAdapter {
	Texture img;
	Mesh shipMesh;
	ShaderProgram shaderProgram;
	//FPSControllableCamera cam = new FPSCamera(0.1f, 0.01f);
	FPSControllableCamera cam = new OrthoFPSCamera(0.1f, 0.01f);
	
	float angle = 0;

	@Override
	public void create() {
		setupGdx();
		
		img = new Texture(Gdx.files.internal("ship.png"));
		String vs = Gdx.files.internal("defaultVS.glsl").readString();
		String fs = Gdx.files.internal("defaultFS.glsl").readString();

		shaderProgram = new ShaderProgram(vs, fs);

		ModelLoader<?> loader = new ObjLoader();
		ModelData shipModel = loader.loadModelData(Gdx.files.internal("ship.obj"));

		shipMesh = new Mesh(true, shipModel.meshes.get(0).vertices.length,
				shipModel.meshes.get(0).parts[0].indices.length, VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));
		
		shipMesh.setVertices(shipModel.meshes.get(0).vertices);
		shipMesh.setIndices(shipModel.meshes.get(0).parts[0].indices);
	}

	private void setupGdx() {
		Gdx.graphics.setDisplayMode(1920, 1080, true);
		Gdx.input.setCursorCatched(true);
		
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glDepthFunc(GL20.GL_LESS);
	}
	
	@Override
	public void render() {
		checkExit();
		updateAngle();
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		cam.update();
		
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
		
		Gdx.gl.glActiveTexture(0);
		img.bind();
		shaderProgram.begin();
		shaderProgram.setUniformi("u_texture", 0);
		
		float sum = 0;
		
		for (int i = 0; i < 10; i++) {
			sum += 0.6f;
			for (int j = 0; j < 10; j++) {
				sum += 0.8f;
				
				Matrix4 modelMat = new Matrix4();
				float diff = (float) Math.sin(angle + sum);
				modelMat.translate((i*2) + (diff * 0.5f), diff, (j*2) + (diff * 0.5f));
				
				modelMat.rotateRad(new Vector3(0, 0, 1), diff);
				
				shaderProgram.setUniformMatrix("u_mvp", cam.getViewProjection().mul(modelMat));
				shipMesh.render(shaderProgram, GL20.GL_TRIANGLES);	
			}
		}
		
		shaderProgram.end();
	}
	
	private void updateAngle() {
		angle += 0.1f;
		while (angle < 0)
			angle += Math.PI * 2;
		while (angle > Math.PI * 2)
			angle -= Math.PI * 2;
	}
	
	private void checkExit() {
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			Gdx.app.exit();
		}
	}
}
