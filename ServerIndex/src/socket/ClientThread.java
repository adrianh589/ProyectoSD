package socket;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ClientThread extends Thread {

	private Socket socket;
	private DataInputStream dataInput = null;
	private DataOutputStream dataOutput = null;
	private String nameClient;
	private String ip;

	public ClientThread(Socket socket, DataInputStream in, DataOutputStream out) {
		this.socket = socket;
		this.dataInput = in;
		this.dataOutput = out;
		this.setIp(socket.getInetAddress().getHostAddress());
	}

	public void run() {
		try {
			String message = this.dataInput.readUTF();
			JsonElement parse = new JsonParser().parse(message);
			JsonObject obj = parse.getAsJsonObject();
			String name = obj.get("name").getAsString();
			if (obj.get("disconnect").getAsBoolean()) {
				for (int i = 0; i < ServerChat.comunicaciones.size(); i++) {
					if(ServerChat.comunicaciones.get(i).getNameClient().equals(name) && ServerChat.comunicaciones.get(i).getIp().equals(this.ip)) {
						ServerChat.comunicaciones.remove(i);
						this.dataOutput.writeUTF("Desconexion Exitosa");
						//hacer broadcasting de desconexion
						return;
					}
				}
			}
			if (obj.get("connection").getAsBoolean()) {
				this.dataOutput.writeUTF(this.getJsonClients().toString());
			} else {
				ServerChat.comunicaciones.add(this);
				this.nameClient = name;
				this.dataOutput.writeUTF(this.getJsonClients().toString());
			}
			

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public JsonArray getJsonClients() {
		JsonArray array = new JsonArray();
		for (int i = 0; i < ServerChat.comunicaciones.size(); i++) {
			JsonObject clientJson = new JsonObject();
			clientJson.addProperty("name", ServerChat.comunicaciones.get(i).getNameClient());
			clientJson.addProperty("ip", ServerChat.comunicaciones.get(i).getIp());
			array.add(clientJson);
		}
		return array;
	}

	public String getNameClient() {
		return this.nameClient;
	}

	public void setNameClient(String nombre) {
		this.nameClient = nombre;
	}

	/**
	 * @return the dataInput
	 */
	public DataInputStream getDataInput() {
		return dataInput;
	}

	/**
	 * @param dataInput the dataInput to set
	 */
	public void setDataInput(DataInputStream dataInput) {
		this.dataInput = dataInput;
	}

	/**
	 * @return the dataOutput
	 */
	public DataOutputStream getDataOutput() {
		return dataOutput;
	}

	/**
	 * @param dataOutput the dataOutput to set
	 */
	public void setDataOutput(DataOutputStream dataOutput) {
		this.dataOutput = dataOutput;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}
}
