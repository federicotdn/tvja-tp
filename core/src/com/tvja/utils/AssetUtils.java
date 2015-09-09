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
	
	public static Texture textureFromFile(String fileName) {
		return new Texture(Gdx.files.internal(fileName));
	}
}
