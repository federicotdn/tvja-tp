package com.tvja.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.tvja.camera.PerspectiveCamera;
import com.tvja.camera.PlayerInput;
import com.tvja.net.*;
import com.tvja.render.BaseModel;
import com.tvja.render.Light;
import com.tvja.render.ModelInstance;
import com.tvja.utils.AssetUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MultiplayerGame extends TPGameBase {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int READBUFFER_LEN = 4096;
    private DatagramSocket socket;
    private ByteArrayOutputStream bos;
    private ByteArrayInputStream bis;
    private Vector3 ambientLight;
    private byte[] readBuffer;
    private Integer id;
    
    private Texture img, img2;
	private Mesh shipMesh;
	private Mesh cubeMesh;

    private Kryo kryo;
    private Output out;
    private Input in;

    private PlayerInput input;
    
    Map<NetworkObject, BaseModel> modelInstances;

    @Override
    protected void init() {
        ambientLight = new Vector3(0.05f, 0.05f, 0.05f);
        modelInstances = new HashMap<NetworkObject, BaseModel>();
        	
		img = AssetUtils.textureFromFile("models/ship.png");
		img2 = AssetUtils.textureFromFile("models/plain.jpg");

		shipMesh = AssetUtils.meshFromFile("models/ship.obj");
		cubeMesh = AssetUtils.meshFromFile("models/cube-textures.obj");
		
		directionalLights.add(Light.newDirectional(new Vector3(-(float) (3 * Math.PI / 4), 3.5199971f, 0), new Vector3(1f, 0.99f, 0.8f), 0.75f));
		spotLights.add(Light.newSpot(new Vector3(0, 10, 0), new Vector3((float) -Math.PI / 2, 0, 0), new Vector3(1, 1, 1),
				(float) Math.PI / 7, 0.8f));
		
		ModelInstance mi = new ModelInstance(cubeMesh, img2);
		mi.translate(-22.5f, -1, -22.5f);
		mi.scale(45, 0.5f, 45);
		mi.setShininess(4);
		models.add(mi);
		
		cam = new PerspectiveCamera();

        try {
            socket = new DatagramSocket(new InetSocketAddress("0.0.0.0", Protocol.CLIENT_PORT));
            socket.setSoTimeout(15);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        bos = new ByteArrayOutputStream();

        bos.reset();
        kryo = new Kryo();
        kryo.register(ServerPacket.class);
        kryo.register(ClientPacket.class);
        kryo.register(LinkedList.class);
        kryo.register(ArrayList.class);
        kryo.register(PlayerInput.class);

        out = new Output(bos);

        ClientPacket cp = new ClientPacket(Protocol.Code.NEW_CLIENT);
        kryo.writeObject(out, cp);
        out.flush();

        byte[] data = bos.toByteArray();
        readBuffer = new byte[READBUFFER_LEN];
        while (true) {
            DatagramPacket outPakcet = new DatagramPacket(data, data.length,
                    new InetSocketAddress(SERVER_ADDRESS, Protocol.SERVER_PORT));
            try {
                socket.send(outPakcet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            readBuffer[0] = 0;
            boolean received = false;

            DatagramPacket packet = new DatagramPacket(readBuffer, READBUFFER_LEN);
            try {
                socket.receive(packet);
                received = true;
            } catch (SocketTimeoutException e) {
            /* DO NOTHING */
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (received) {
                bis = new ByteArrayInputStream(readBuffer);
                in = new Input(bis);
                ServerPacket sp = kryo.readObject(in, ServerPacket.class);
                if (sp.getCode() == Protocol.Code.ACK_CLIENT) {
                    id = sp.getId();
                    System.out.println("Connected with id: " + id);
                    break;
                }
            }
        }

        input = new PlayerInput();
    }

    @Override
    protected void update() {
        boolean received = false;

        DatagramPacket packet = new DatagramPacket(readBuffer, READBUFFER_LEN);
        try {
            socket.receive(packet);
            received = true;
        } catch (SocketTimeoutException e) {
        	/* DO NOTHING */
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (received) {
            bis = new ByteArrayInputStream(readBuffer);
            in = new Input(bis);
            ServerPacket sp = kryo.readObject(in, ServerPacket.class);

            if (sp.getCode() == Protocol.Code.NET_OBJECTS) {
            	for (NetworkObject no : sp.getNetworkObjects()) {
                    if (no.getID() != sp.getClientAvatar()) {
            			if (!modelInstances.containsKey(no)) {
            				ModelInstance mi = new ModelInstance(shipMesh, img);
            				modelInstances.put(no, mi);
            				models.add(mi);
            				groupModels();
            			}

                        BaseModel bm = modelInstances.get(no);
                        bm.updateWith(no);
            		} else {
                        cam.updateWith(no);
                    }
            	}
            }
            
        }

        input.grabInputs();
        if (input.hasInputs()) {
        	bos.reset();
        	ClientPacket cp = new ClientPacket(Protocol.Code.INPUT, input);
        	kryo.writeObject(out, cp);
        	out.flush();
        	
        	byte[] data = bos.toByteArray();
        	
        	DatagramPacket outPakcet = new DatagramPacket(data, data.length,
        			new InetSocketAddress(SERVER_ADDRESS, Protocol.SERVER_PORT));
        	try {
        		socket.send(outPakcet);
        	} catch (IOException e) {
        		e.printStackTrace();
        	}        	
        }
        
		Light spot = spotLights.get(0);
		
		if (Gdx.input.isKeyPressed(Keys.V)) {
			spot.setPosition(cam.getPosition());
			spot.setOrientation(cam.getOrientation());
		}
    }

    @Override
    protected Vector3 getAmbientLight() {
        return ambientLight;
    }

}
