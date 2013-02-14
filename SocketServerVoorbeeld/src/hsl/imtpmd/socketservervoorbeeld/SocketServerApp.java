package hsl.imtpmd.socketservervoorbeeld;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class SocketServerApp implements Runnable
{

	public static void main(String[] args)
	{

		try
		{
			ServerSocket serverSocket = new ServerSocket(4444);
			while (true)
			{
				SocketServerApp socketServerApp = new SocketServerApp(serverSocket.accept());
				new Thread(socketServerApp).start();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private Socket		socket;
	private String		message;
	private JTextArea	area;

	public SocketServerApp(Socket socket)
	{
		this.socket = socket;
		area = new JTextArea();

		JPanel panel = new JPanel();
		panel.add(area);

		JFrame frame = new JFrame();
		frame.setBounds(100, 100, 200, 100);
		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	@Override
	public void run()
	{
		area.setText("reading port 4444 for messages");

		InputStreamReader inputStreamReader = null;
		try
		{
			inputStreamReader = new InputStreamReader(socket.getInputStream());
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		OutputStreamWriter outputStreamWriter = null;
		try
		{
			outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
		}
		catch (IOException e2)
		{
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
		PrintWriter writer = new PrintWriter(bufferedWriter, true);

		while (true)
		{
			area.setText("client bericht ontvangen...\n" + area.getText());
			try
			{
				message = bufferedReader.readLine();
			}
			catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			area.setText("client message is: " + message + "\n" + area.getText());

			if (writer != null && writer.checkError() == false)
			{
				area.setText("sending message back to client " + socket.getInetAddress().getHostAddress() + "\n" + area.getText());
				writer.println("server heeft je bericht ontvangen" + message);
				writer.flush();
			}

			try
			{
				Thread.sleep(500);
			}

			catch (InterruptedException e)
			{
				area.setText("Thread interrupted during sleep." + "\n" + area.getText());
				e.printStackTrace();
			}

		}
	}

}
