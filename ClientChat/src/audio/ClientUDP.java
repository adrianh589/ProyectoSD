package audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ClientUDP {

	private DatagramSocket socket;
	private byte[] buffer = new byte[1024];
	private InetAddress address;
	private int portServer = 5432;

	public ClientUDP(String address) {
		try {
			socket = new DatagramSocket();
			this.address = InetAddress.getByName(address);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendAudio(String audio) {
		try {
//			String mensaje = "enviando audio";
			File file = new File("no-tears-left-to-cry.mp3");
			this.buffer = readFileToByteArray(file);
			DatagramPacket pregunta = new DatagramPacket(this.buffer, this.buffer.length, this.address,
					this.portServer);
			System.out.println("Enviando informacion");
			this.socket.send(pregunta);
			DatagramPacket respuesta = new DatagramPacket(this.buffer, this.buffer.length);

			this.socket.receive(respuesta);
			System.out.println(new String(respuesta.getData()));

			socket.close();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 private static byte[] readFileToByteArray(File file){
	        FileInputStream fis = null;
	        // Creating a byte array using the length of the file
	        // file.length returns long which is cast to int
	        byte[] bArray = new byte[(int) file.length()];
	        try{
	            fis = new FileInputStream(file);
	            fis.read(bArray);
	            fis.close();        
	            
	        }catch(IOException ioExp){
	            ioExp.printStackTrace();
	        }
	        return bArray;
	    }
}
