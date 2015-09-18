package com.tvja.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;

public class AssetUtils {
	public static Mesh meshFromFile(String fileName) {
		ModelLoader<?> loader = new ObjLoader();
		ModelData model = loader.loadModelData(Gdx.files.internal(fileName));
		Mesh mesh = new Mesh(true, model.meshes.get(0).vertices.length,
				model.meshes.get(0).parts[0].indices.length, VertexAttribute.Position(), VertexAttribute.Normal(),
				VertexAttribute.TexCoords(0));
		
		mesh.setVertices(model.meshes.get(0).vertices);
        mesh.setIndices(model.meshes.get(0).parts[0].indices);
		
		return mesh;
	}
	
	public static Mesh loadFullScreenQuad() {
		Mesh mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.TexCoords(0));
		float vertexData[] = {
				1.0f, 1.0f, 0.0f, 1.0f, 1.0f, // vertex 0
				-1.0f, 1.0f, 0.0f, 0.0f, 1.0f, // vertex 1
				1.0f, -1.0f, 0.0f, 1.0f, 0.0f, // vertex 2
				-1.0f, -1.0f, 0.0f, 0.0f, 0.0f, // vertex 3
		};

		mesh.setVertices(vertexData);
		mesh.setIndices(new short[] {1, 2, 3, 2, 3, 4});
		
		return mesh;
	}
	
	public static Texture textureFromFile(String fileName) {
		return new Texture(Gdx.files.internal(fileName));
	}
}
