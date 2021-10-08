
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class TCPServidor {
	public static void main(String [] args) {
		try {
			while (true) {
				System.out.println("- PORTAS RESERVADAS: 1 E 2\n- UTILIZAR PORTAS DIFERENTES PARA CADA CLIENTE");
				ServerSocket serverSocket1 = new ServerSocket(1);
				ServerSocket serverSocket2 = new ServerSocket(2);
				System.out.println("Aguardando as conexões dos clientes...");
				Socket socket1 = serverSocket1.accept();
				Socket socket2 = serverSocket2.accept();
				DataInputStream entrada1 = new DataInputStream(socket1.getInputStream());
				String resposta1 = entrada1.readUTF();
				DataInputStream entrada2 = new DataInputStream(socket2.getInputStream());
				String resposta2 = entrada2.readUTF();
				System.out.println("Portas recebidas. Encaminhando...");
				DataOutputStream saida1 = new DataOutputStream(socket1.getOutputStream());
				saida1.writeUTF(resposta2);
				DataOutputStream saida2 = new DataOutputStream(socket2.getOutputStream());
				saida2.writeUTF(resposta1);
				
				System.out.println("Fechando as conexões com os clientes...\n");
				socket1.close();
				socket2.close();
				
				serverSocket1.close();
				serverSocket2.close();
			}
		} catch (BindException e) {
			System.out.println("Endereço em uso");
		} catch (IOException e) {
			System.out.println("Erro: "+e);
		}
	}
}
