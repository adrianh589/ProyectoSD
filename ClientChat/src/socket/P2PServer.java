package socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class P2PServer extends Thread {
	private Socket socket = null;
	private ServerSocket server = null;
	DataOutputStream paraElCliente;
	DataInputStream desdeElCliente;
	private boolean flagExit = false;
	/**
	 * @return the flagExit
	 */
	public boolean isFlagExit() {
		return flagExit;
	}

	/**
	 * @param flagExit the flagExit to set
	 */
	public void setFlagExit(boolean flagExit) {
		this.flagExit = flagExit;
	}

	private P2PClient p2pclient;
	private String nameClient;
	private String myName;
	private Client client;

	public P2PServer(int port, String myName, Client client) {
		try {
			this.server = new ServerSocket(port);
			this.myName = myName;
			this.client = client;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void run() {
		try {
			this.socket = server.accept();
			// takes input from the client socket
			this.paraElCliente = new DataOutputStream(socket.getOutputStream());

			// outputs
			this.desdeElCliente = new DataInputStream(socket.getInputStream());
			this.p2pclient = new P2PClient(socket.getLocalAddress().getHostAddress(), 5678, this.myName, this.client, this);
			this.nameClient = this.desdeElCliente.readUTF();
			while (!this.flagExit) {
				System.out.println(this.nameClient + ": " + this.desdeElCliente.readUTF());
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
//			e1.printStackTrace();
		}

	}
}
