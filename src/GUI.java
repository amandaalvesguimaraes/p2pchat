
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class GUI implements ActionListener, ListSelectionListener, FocusListener {
	
	//botões
	private JButton clearButton;
	private JButton desfazerButton;
	private JButton sendButton;
	
	private Socket socket1;
	
	private Thread a1;
	
	private JTextArea telaMensagens;
	
	private JTextField msgTexto;
	
	private static int numPorta = 0;
	private static int porta_atual = 0;
	private int counter = 1;
	private Queue<String> fila;
	
	public GUI () {
		
		telaMensagens = new JTextArea();
		telaMensagens.setEditable(false);
		telaMensagens.setBounds(5, 5, 435, 400);
		
		//botões
		sendButton = new JButton("Enviar");
		sendButton.addActionListener(this);
		sendButton.setActionCommand("send_act");
		sendButton.setBounds(155, 520, 80, 30);
		
		clearButton = new JButton("Limpar");
		clearButton.addActionListener(this);
		clearButton.setActionCommand("clear_act");
		clearButton.setBounds(240, 520, 80, 30);
		
		desfazerButton = new JButton("Desfazer");
		desfazerButton.addActionListener(this);
		desfazerButton.setActionCommand("desfazer_act");
		desfazerButton.setBounds(325, 520, 100, 30);
		
		
		//campos de texto para input
		msgTexto = new JTextField(10);
		msgTexto.setLayout(new FlowLayout());
		msgTexto.setSize(20, 5);
		msgTexto.setVisible(true);
		msgTexto.addActionListener(this);
		msgTexto.setEditable(true);
		msgTexto.setBounds(5, 520, 150, 30);
		msgTexto.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if (e.getKeyCode() == KeyEvent.VK_ENTER){
					sendButton.doClick();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					sendButton.doClick();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
		});

		

		JPanel panel = new JPanel(null); //
		panel.add(telaMensagens);
		panel.add(msgTexto);
		panel.add(sendButton);
		panel.add(clearButton);
		panel.add(desfazerButton);
		
		JFrame frame = new JFrame();
		frame.add(panel);
		frame.setTitle("GUI 1");
		frame.setSize(450, 600);
		frame.setVisible(true);
		
		fila = new LinkedList();
	
		
		new Thread () { //thread servidor - receber mensagens
			public void run() {
				try {
					ServerSocket serverSocket = new ServerSocket(porta_atual);
					Socket socket1 = serverSocket.accept();
					DataInputStream entrada1 = new DataInputStream(socket1.getInputStream());
					while (true) {
						String resposta = entrada1.readUTF();
						telaMensagens.setText(telaMensagens.getText()+"AMIGO: "+resposta+"\n");
					}
				} catch (SocketException e) {
					System.out.println("Conexão finalizada");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
		
		try {
			socket1 = new Socket("localhost", numPorta);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	public static void main(String [] args) {
		Scanner in = new Scanner(System.in);
		int portServer = 1;
		String address = "localhost";
		try {
			Socket socket = new Socket(address, portServer);
			System.out.print("Informe o número da porta: ");
			String porta = in.nextLine();
			DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
			saida.writeUTF(porta);
			porta_atual = Integer.parseInt(porta);
			System.out.println("Número da porta enviado. Aguardando conexão com outro cliente...");
			
			DataInputStream entrada1 = new DataInputStream(socket.getInputStream());
			String resposta = entrada1.readUTF();
			
			numPorta = Integer.parseInt(resposta);
			
			socket.close();
			
			System.out.println("Conexão encerrada com o servidor.");
				
		} catch (ConnectException e) {
			System.out.println("Erro de conexão");
		} catch (SocketException e) { 
			System.out.println("Conexão encerrada pelo servidor.");
		} catch (IOException e) {
			System.out.println("Erro: "+ e);
		}
		
		new GUI();
		
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String command = e.getActionCommand();
		if (command.equals("send_act")) {
			new Thread() {
				public void run() {
					try {
						Date date = new Date();
						fila.add(msgTexto.getText());
						telaMensagens.setText(telaMensagens.getText()+"EU: "+" ["+date+"] "+msgTexto.getText()+"\n");
						long tempo = System.currentTimeMillis();
						while ((System.currentTimeMillis() - tempo) < 10000) {
							if ((fila.size() > 1) || (fila.size() == 0)) {
								break;
							}
						}
						if (fila.size() != 0) {
							DataOutputStream saida = new DataOutputStream(socket1.getOutputStream());
							String resposta = fila.poll();
							if (resposta != null) {
								saida.writeUTF(counter+"- ["+date+"] "+resposta);
							}
							counter ++;
						} else {
							fila.clear();
						}
						
					} catch (UnknownHostException e11) {
						e11.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}.start();
		} else if (command.equals("desfazer_act")) {
			fila.remove();
		} else if (command.equals("clear_act")) {
			telaMensagens.setText("");
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub
		
	}


}
