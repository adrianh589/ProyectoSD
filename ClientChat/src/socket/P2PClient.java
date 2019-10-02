package socket;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class P2PClient extends Thread {

	private String address;
	private int port;
	private DataOutputStream haciaElServidor = null;
	private DataInputStream desdeElServidor = null;
	private Socket socket;
	private boolean flagExit = false;
	private BufferedReader desdeElUsuario = null;
	private String name;
	private Client client;
	private P2PServer server;

	public P2PClient(String address, int port, String name, Client client, P2PServer server) {
		try {
			this.client = client;
			this.address = address;
			this.port = port;
			this.name = name;
			this.server = server;
			socket = new Socket(address, port);

			System.out.println("Conectado al chat");

			// lo que viene desde el servidor
			desdeElServidor = new DataInputStream(socket.getInputStream());
			// sends output to the socket
			haciaElServidor = new DataOutputStream(socket.getOutputStream());

			desdeElUsuario = new BufferedReader(new InputStreamReader(System.in));
			haciaElServidor.writeUTF(name);
		} catch (UnknownHostException u) {
			System.out.println(u);
		} catch (IOException i) {
			System.out.println(i);
		}
	}

	public void run() {
		this.client.flagChat = true;
		String line = "";
		try {
			line = desdeElUsuario.readLine();
			while (!line.equals("exit")) {
				haciaElServidor.writeUTF(line);
				line = desdeElUsuario.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			disconnect();
			server.setFlagExit(true);
			this.client.flagChat = false;
			client.ControlMenu();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void disconnect() throws IOException {
		this.flagExit = true;
		this.socket.close();
	}

}
