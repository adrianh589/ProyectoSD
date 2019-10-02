package audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import socket.Client;

public class ServerUDP extends Thread {

	private int port;
	private DatagramSocket socket;
	private byte[] buffer = new byte[1024];
	private Client client;

	public ServerUDP(Client client) throws SocketException {
		this.port = 5432;
		this.socket = new DatagramSocket(this.port);
		this.client = client;
	}

	public void run() {
		try {
			DatagramPacket peticion = new DatagramPacket(buffer, buffer.length);

			this.socket.receive(peticion);
			this.client.setFlagChat(true);
			int clientPort = peticion.getPort();
			InetAddress addressClient = peticion.getAddress();
			
			String mensaje = "recibiendo";
			
			System.out.println("Reproduciendo audio");
			
			Clip sonido = AudioSystem.getClip();
			sonido.open(AudioSystem.getAudioInputStream(new ByteArrayInputStream(peticion.getData())));
			
			// Comienza la reproducción
            sonido.start();
            
            // Espera mientras se esté reproduciendo.
            while (sonido.isRunning())
                Thread.sleep(1000);
            
            // Se cierra el clip.
            sonido.close();
			
			DatagramPacket respuesta = new DatagramPacket(mensaje.getBytes(), mensaje.getBytes().length,addressClient,clientPort);
			
			socket.send(respuesta);
			this.client.setFlagChat(false);
			client.ControlMenu();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
