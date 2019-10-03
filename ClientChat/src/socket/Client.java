/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package socket;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import audio.ClientUDP;
import audio.ServerUDP;

/**
 *
 * @author Alexis Holguin
 */
public class Client {
	// initialize socket and input output streams
	private Socket socket = null;
	private BufferedReader desdeElUsuario = null;
	private DataOutputStream haciaElServidor = null;
	private DataInputStream desdeElServidor = null;
	private String name;
	private String address;
	private int port;
	protected P2PClient p2pClient;
	private P2PServer p2pSever;
	private boolean flagExit = false;
	protected boolean flagChat = false;
	/**
	 * @return the flagChat
	 */
	public boolean isFlagChat() {
		return flagChat;
	}

	/**
	 * @param flagChat the flagChat to set
	 */
	public void setFlagChat(boolean flagChat) {
		this.flagChat = flagChat;
	}

	private ServerUDP serverUDP;

	// constructor to put ip address and port
	public Client(String address, int port) {
		// establish a connection
		try {
			this.address = address;
			this.port = port;
			socket = new Socket(address, port);

			System.out.println("Connected");

			// takes input from terminal
			desdeElUsuario = new BufferedReader(new InputStreamReader(System.in));

			// lo que viene desde el servidor
			desdeElServidor = new DataInputStream(socket.getInputStream());

			// sends output to the socket
			haciaElServidor = new DataOutputStream(socket.getOutputStream());

		} catch (UnknownHostException u) {
			System.out.println(u);
		} catch (IOException i) {
			System.out.println(i);
		}
	}

	public void init() {
		System.out.println("Bienvenido, ingrese su nombre de Usuario");
		try {
			this.name = desdeElUsuario.readLine();
			p2pSever = new P2PServer(5678, this.name, this);
			p2pSever.start();
			this.serverUDP = new ServerUDP(this);
			this.serverUDP.start();
			// envio de mensaje al server

			JsonObject message = new JsonObject();
			message.addProperty("name", this.name);
			message.addProperty("connection", false);
			message.addProperty("disconnect", false);
			haciaElServidor.writeUTF(message.toString());
			System.out.println("Se ha establecido con exito su nombre!");

			System.out.println("Clientes Conectados:");
			String response = this.desdeElServidor.readUTF();
			JsonElement parse = new JsonParser().parse(response);
			JsonArray obj = parse.getAsJsonArray();
			for (int i = 0; i < obj.size(); i++) {
				System.out.println((i + 1) + ". " + obj.get(i).getAsJsonObject().get("name").getAsString());
			}

			socket.close();

			ControlMenu();

		} catch (IOException i) {
			System.out.println(i);
		}
	}

	public void ControlMenu() throws IOException {
		ShowMenu();
		while (!this.flagChat) {
			String line = desdeElUsuario.readLine();
			if (line.equals("1")) {
				listClients();
			} else if (line.equals("2")) {
				System.out.println("Selccione un cliente:");
				listClients();
				System.out.println("Ingrese el usuario al que se desea conectar:");
				selectClient(Integer.parseInt(desdeElUsuario.readLine()));
				break;
			} else if (line.equals("3")) {
				listClients();
				System.out.println("Ingrese el usuario al que se desea conectar:");
				selectClient(Integer.parseInt(desdeElUsuario.readLine()));
			} else if (line.equals("4")) {
				this.flagExit = true;
				break;
			}else {
				System.out.println("Opcion incorrecta");
			}
			if (!this.flagChat) {
				ShowMenu();
			}
		}
		if (this.flagExit) {
			disconnect();
		}

	}

	public void selectClientAudio(int client) {
		JsonArray clients;
		try {
			clients = getClientsJson();
			JsonObject clientObj = clients.get(client - 1).getAsJsonObject();
			ClientUDP clientUDP = new ClientUDP(clientObj.get("ip").getAsString());
			clientUDP.sendAudio("as");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void selectClient(int client) throws IOException {
		JsonArray clients = getClientsJson();
		JsonObject clientObj = clients.get(client - 1).getAsJsonObject();
		p2pClient = new P2PClient(clientObj.get("ip").getAsString(), 5678, this.name, this, this.p2pSever);
		this.p2pSever.setP2pclient(p2pClient);
		p2pClient.start();
	}

	public void listClients() throws UnknownHostException, IOException {
		System.out.println("Clientes Conectados:");
		JsonArray obj = getClientsJson();
		for (int i = 0; i < obj.size(); i++) {
			System.out.println((i + 1) + ". " + obj.get(i).getAsJsonObject().get("name").getAsString());
		}
	}

	public JsonArray getClientsJson() throws IOException {
		this.socket = new Socket(this.address, this.port);

		// lo que viene desde el servidor
		desdeElServidor = new DataInputStream(socket.getInputStream());

		// sends output to the socket
		haciaElServidor = new DataOutputStream(socket.getOutputStream());

		JsonObject message = new JsonObject();
		message.addProperty("name", this.name);
		message.addProperty("connection", true);
		message.addProperty("disconnect", false);
		haciaElServidor.writeUTF(message.toString());
		String response = this.desdeElServidor.readUTF();
		JsonElement parse = new JsonParser().parse(response);
		JsonArray obj = parse.getAsJsonArray();
		return obj;
	}

	public void ShowMenu() {
		System.out.println("Eliga opcion:");
		System.out.println("1. Listar Cliente");
		System.out.println("2. Conectarse con un cliente");
		System.out.println("3. Enviar audio a cliente");
		System.out.println("4. Salir del chat");
	}

	public void disconnect() throws UnknownHostException, IOException {

		this.socket = new Socket(this.address, this.port);

		// lo que viene desde el servidor
		desdeElServidor = new DataInputStream(socket.getInputStream());

		// sends output to the socket
		haciaElServidor = new DataOutputStream(socket.getOutputStream());

		JsonObject message = new JsonObject();
		message.addProperty("name", this.name);
		message.addProperty("connection", true);
		message.addProperty("disconnect", true);
		try {
			haciaElServidor.writeUTF(message.toString());
			System.out.println(this.desdeElServidor.readUTF());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		Client client = new Client(args[0], 5000);
		client.init();
	}

}
