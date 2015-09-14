package com.tvja.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.tvja.camera.OpenGLCamera;
import com.tvja.utils.MathUtils;

import java.util.List;

public class Shader {
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
	
	public void render(OpenGLCamera cam, List<ModelInstance> models, Vector3 ambient) {
		render(cam, models, null, ambient);
	}
	
	public void render(OpenGLCamera cam, List<ModelInstance> models, List<Light> lights) {
		render(cam, models, lights, null);
	}

	public void render(OpenGLCamera cam, List<ModelInstance> models, List<Light> lights, Vector3 ambient) {
		if (cam == null || models == null || models.isEmpty()) {
			return;
		}

		shaderProgram.begin();

		for (ModelInstance model : models) {
			Gdx.gl.glActiveTexture(0);
			model.getTex().bind(0);

			setUniformMat4("u_mvp", cam.getViewProjection().mul(model.getTRS()));
			setUniform4fv("u_cam_pos", MathUtils.toVec4f(cam.getPosition()));
			setUniformMat4("u_model_mat", model.getTRS());
			setUniformMat4("u_model_rotation_mat", model.getR());

			setUniformi("u_texture", 0);
			setUniformi("u_shininess", model.getShininess());
			
			if (ambient != null) {
				setUniform4fv("u_ambient_color", MathUtils.toVec4f(ambient));
			}

			if (lights != null && !lights.isEmpty()) {
				for (Light light : lights) {
					setUniform4fv("u_light_position", MathUtils.toVec4f(light.getPosition()));
					setUniform4fv("u_light_direction", MathUtils.toVec4f(light.getDirection()));
					light.getAngle().ifPresent(a -> shaderProgram.setUniformf("u_cutoff_angle", a));
					setUniform4fv("u_light_color", MathUtils.toVec4f(light.getColor()));
	
					model.getMesh().render(shaderProgram, GL20.GL_TRIANGLES);
				}
			} else {
				model.getMesh().render(shaderProgram, GL20.GL_TRIANGLES);
			}
		}

		shaderProgram.end();
	}
	
	void setUniform4fv(String name, float[] val) {
		if (shaderProgram.hasUniform(name)) {
			shaderProgram.setUniform4fv(name, val, 0, 4);
		}
	}
	
	void setUniformi(String name, int val) {
		if (shaderProgram.hasUniform(name)) {
			shaderProgram.setUniformi(name, val);
		}		
	}
	
	void setUniformMat4(String name, Matrix4 val) {
		if (shaderProgram.hasUniform(name)) {
			shaderProgram.setUniformMatrix(name, val);
		}		
	}
}
