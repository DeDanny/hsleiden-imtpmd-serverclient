package hsl.imtpmd.socketservervoorbeeld;

import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class SocketServerApp
{
	private static ServerSocket serverSocket;
	private static Socket clientSocket;
	
	private static String message;
	private static JTextArea area;
	
	
	public static void main( String[] args )
	{
		area = new JTextArea();
		
		JPanel panel = new JPanel();
		panel.add( area );
		
		JFrame frame = new JFrame();
		frame.setBounds( 100, 100, 200, 100 );
		frame.add( panel );
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
		frame.setVisible( true );
		
		try
		{
			serverSocket = new ServerSocket( 4444 );
			runServer();
		}
		
		catch( IOException e )
		{
			area.setText( "Kan niet naar port 4444 luisteren" );
		}
	}
	
	
	public static void runServer()
	{
		area.setText( "reading port 4444 for messages" );
		
		while( true )
		{
			try
			{
				clientSocket = serverSocket.accept();
				
				InputStreamReader inputStreamReader = new InputStreamReader( clientSocket.getInputStream() );
				BufferedReader bufferedReader = new BufferedReader( inputStreamReader );
				
				message = bufferedReader.readLine();
				area.setText( message + "\n" + area.getText() );
				
				
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter( clientSocket.getOutputStream() );
				BufferedWriter bufferedWriter = new BufferedWriter( outputStreamWriter );
				PrintWriter writer = new PrintWriter( bufferedWriter, true );
				
				if( writer != null && writer.checkError() == false )
				{
					area.setText( "sending message back to client " + clientSocket.getInetAddress().getHostAddress() + "\n" + area.getText() );
					writer.println( "server heeft je bericht ontvangen\r\n" );
				}

				writer.close();
			}
			
			catch( IOException e )
			{
				area.setText( "Kan message niet lezen" + "\n" + area.getText() );
				System.out.println( "Kan message niet lezen" );
			}
			
			catch( Exception e )
			{
				area.setText( e.getMessage() );
				System.out.println( e.getMessage() );
			}
			
			
			
			try
			{
				Thread.sleep(500);
			}
			
			catch (InterruptedException e)
			{
				area.setText( "Thread interrupted during sleep." + "\n" + area.getText() );
				e.printStackTrace();
			}
		}
	}
}
