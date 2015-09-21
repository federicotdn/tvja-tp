package com.tvja.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
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
	
	public void render(ViewWorldObject view, List<ModelInstance> models, Vector3 ambient) {
		render(view, models, null, ambient);
	}
	
	public void render(ViewWorldObject view, List<ModelInstance> models, List<Light> lights) {
		render(view, models, lights, null);
	}
	
	public void renderFullscreen(ModelInstance quad, int texture) {
		shaderProgram.begin();
		setUniformi("u_texture", texture);
		quad.getMesh().render(shaderProgram, GL20.GL_TRIANGLES);
		shaderProgram.end();
	}
	
	public void renderShadow(Light light, ViewWorldObject cam, List<ModelInstance> models, int texture) {
		if (light == null || cam == null || models == null || models.isEmpty()) {
			return;
		}
		
		shaderProgram.begin();
		
		setUniformi("u_shadow_map", texture);
		
		for (ModelInstance model : models) {
			setUniformMat4("u_mvp", cam.getViewProjection().mul(model.getTRS()));
			setUniformMat4("u_light_mvp", light.getViewProjection().mul(model.getTRS()));
			setUniformMat4("u_model_mat", model.getTRS());
			setUniformf("u_view_far", light.getFarZ());
			setUniform4fv("u_light_pos", MathUtils.toVec4f(light.getPosition()));
			model.getMesh().render(shaderProgram, GL20.GL_TRIANGLES);
		}
		
		shaderProgram.end();
	}

	public void render(ViewWorldObject view, List<ModelInstance> models, List<Light> lights, Vector3 ambient) {
		if (view == null || models == null || models.isEmpty()) {
			return;
		}

		shaderProgram.begin();

		for (ModelInstance model : models) {
			Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
			model.getTex().bind();

			setUniformMat4("u_mvp", view.getViewProjection().mul(model.getTRS()));
			setUniform4fv("u_cam_pos", MathUtils.toVec4f(view.getPosition()));
			setUniformMat4("u_model_mat", model.getTRS());
			setUniformMat4("u_model_rotation_mat", model.getR());
			setUniformf("u_view_far", view.getFarZ());

			setUniformi("u_texture", 0); // Must match with glActiveTexture call
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
	
	void setUniformf(String name, float val) {
		if (shaderProgram.hasUniform(name)) {
			shaderProgram.setUniformf(name, val);
		}
	}
}
