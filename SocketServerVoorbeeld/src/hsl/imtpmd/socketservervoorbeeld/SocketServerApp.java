package hsl.imtpmd.socketservervoorbeeld;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
import javax.swing.WindowConstants;

public class SocketServerApp implements Runnable, WindowListener
{

	public static void main(String[] args)
	{
		JTextArea area = new JTextArea();

		JPanel panel = new JPanel();
		panel.add(area);

		JFrame frame = new JFrame();
		frame.setBounds(100, 100, 200, 100);
		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		area.setText("Server Active");
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
	private JFrame		frame;
	boolean				loop	= true;

	public SocketServerApp(Socket socket)
	{
		this.socket = socket;
		area = new JTextArea();

		JPanel panel = new JPanel();
		panel.add(area);

		frame = new JFrame();
		frame.setBounds(100, 100, 200, 100);
		frame.add(panel);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.addWindowListener(this);
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

		while (loop)
		{
			area.setText("client bericht ontvangen...\n" + area.getText());
			try
			{
				message = bufferedReader.readLine();
			}
			catch (IOException e1)
			{
				loop = false;
				e1.printStackTrace();
			}
			if (message == null)
			{
				loop = false;
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
				loop = false;
				area.setText("Thread interrupted during sleep." + "\n" + area.getText());
				e.printStackTrace();
			}

		}

		frame.setVisible(false);
		frame.dispose();
	}

	@Override
	public void windowActivated(WindowEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent arg0)
	{
		loop = false;
	}

	@Override
	public void windowClosing(WindowEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent arg0)
	{
		// TODO Auto-generated method stub

	}
}
