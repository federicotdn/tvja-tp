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
	Texture img, img2;
	Mesh shipMesh;
	Mesh cubeMesh;
	ShaderProgram shaderProgram;
	FPSControllableCamera cam = new FPSCamera(0.1f, 0.01f);
	//FPSControllableCamera cam = new OrthoFPSCamera(0.1f, 0.01f);
	
	float angle = 0;

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
	}

	private void setupGdx() {
		Gdx.graphics.setDisplayMode(1000, 1000, false);
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
		
		Vector3 cam_pos = cam.getPosition();
		float[] cam_pos_4 = new float[] { cam_pos.x, cam_pos.y, cam_pos.z, 1 };
		
		Vector3 cam_ori_angles = new Vector3(cam.getOrientation());
		Vector3 cam_ori = new Vector3();
		cam_ori.rotateRad(new Vector3(1, 0, 0), cam_ori_angles.x);
		cam_ori.rotateRad(new Vector3(0, 1, 0), cam_ori_angles.y);
		cam_ori.rotateRad(new Vector3(0, 0, 1), cam_ori_angles.z);
		cam_ori.nor();
		float[] cam_ori_4 = new float[] { cam_ori.x, cam_ori.y, cam_ori.z, 1 };
		
		shaderProgram.begin();
		shaderProgram.setUniformi("u_texture", 0);
		shaderProgram.setUniformf("u_cutoff_angle", (float) (Math.PI / 10.0f));
		shaderProgram.setUniform4fv("u_light_direction", new float[]{1, 1, 1, 1}, 0, 4); //directional
		shaderProgram.setUniform4fv("u_light_position", new float[]{50, 1, 50, 1}, 0, 4); //point
		shaderProgram.setUniform4fv("u_light_color", new float[]{1, 1, 1, 1}, 0, 4);
		shaderProgram.setUniform4fv("u_ambient_color", new float[]{0.1f, 0.1f, 0, 1}, 0, 4);
		shaderProgram.setUniform4fv("u_cam_pos", cam_pos_4, 0, 4);
		
		drawMoving();
		//drawCube();
		
		shaderProgram.end();
	}
	
	private void drawCube() {
		Matrix4 t = new Matrix4();
		Matrix4 r = new Matrix4();
		Matrix4 s = new Matrix4();
		
		t.translate(-1, 0, -1);
		r.rotateRad(new Vector3(0, 1, 0), angle);
		
		Matrix4 m = s.mul(r).mul(t);
		
		shaderProgram.setUniformMatrix("u_mvp", cam.getViewProjection().mul(m));
		shaderProgram.setUniformMatrix("u_model_mat", m); //para (es)specular
		shipMesh.render(shaderProgram, GL20.GL_TRIANGLES);	
	}
	
	private void drawMoving() {
		float sum = 0;
		
		for (int i = 0; i < 10; i++) {
			sum += 0.6f;
			for (int j = 0; j < 10; j++) {
				sum += 0.8f;

				Matrix4 modelMat = new Matrix4();
				float diff = (float) Math.sin(angle + sum);
				modelMat.translate((i * 2) + (0 * 0.5f), 0, (j * 2) + (0 * 0.5f));

				Matrix4 modelMatRot = new Matrix4();
				//modelMatRot.rotateRad(new Vector3(0, 0, 1), diff);

				modelMat.mul(modelMatRot);

				shaderProgram.setUniformMatrix("u_mvp", cam.getViewProjection().mul(modelMat));
				shaderProgram.setUniformMatrix("u_model_mat", modelMat); //para (es)specular
				shaderProgram.setUniformMatrix("u_model_rotation_mat", modelMatRot);

				shipMesh.render(shaderProgram, GL20.GL_TRIANGLES);
			}
		}
		
		img2.bind(1);
		shaderProgram.setUniformi("u_texture", 1);
		
		Matrix4 t = new Matrix4();
		Matrix4 r = new Matrix4();
		Matrix4 s = new Matrix4();
		s.scale(2000, 0.2f, 2000);
		t.translate(new Vector3(-50, -1, -50));
		
		Matrix4 trs = new Matrix4(t).mul(r).mul(s);
		
		shaderProgram.setUniformMatrix("u_mvp", cam.getViewProjection().mul(trs));
		shaderProgram.setUniformMatrix("u_model_mat", trs); //para (es)specular
		shaderProgram.setUniformMatrix("u_model_rotation_mat", r);
		
		cubeMesh.render(shaderProgram, GL20.GL_TRIANGLES);
	}
	
	private void updateAngle() {
		angle += 0.05f;
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
