package socket;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerChat {
	// initialize socket and input stream
	private Socket socket = null;
	private ServerSocket servidor = null;
	BufferedReader inFromServer = null;
	// Array para guardar los datos de los usuarios registrados
	public static ArrayList<ClientThread> comunicaciones = new ArrayList<ClientThread>();

	public ServerChat(int port) {
		// starts server and waits for a connection
		try {
			servidor = new ServerSocket(port);
			System.out.println("Server started");

			System.out.println("Waiting for a clients ...");

			while (true) {// Nos quedamos a la escucha de varios clientes
				socket = servidor.accept();
				System.out.println("Client accepted");
				// takes input from the client socket
				DataOutputStream paraElCliente = new DataOutputStream(socket.getOutputStream());

				// outputs
				DataInputStream desdeElCliente = new DataInputStream(socket.getInputStream());

				ClientThread hilo = new ClientThread(socket, desdeElCliente, paraElCliente);
				// Verificar si los clientes estan en el hilo
				
				
				hilo.start();
			}
		} catch (IOException i) {
			System.out.println(i);
		}
	}

	public static void main(String args[]) {
		ServerChat server = new ServerChat(5000);
	}
}
