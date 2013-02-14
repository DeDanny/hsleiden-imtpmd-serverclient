package hsl.imtpmd.socketclientvoorbeeld;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity implements Runnable
{
	private Socket serverSocket;
	
	private PrintWriter serverWriter;
	private BufferedReader serverReader;
	
	private String serverMessage;
	private String messageToSend;
	private boolean interactWithServer;
	
	private TextView feedbackText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.feedbackText = (TextView) this.findViewById( R.id.feedbacktext );
		
		this.interactWithServer = true;
		Thread connectToServerThread = new Thread( this );
		connectToServerThread.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	
	public void verzendVraag( View view )
	{
		messageToSend = "This is a test";
		sendFeedbackToUI( "message set up for sending" );
	}
	

	@Override
	public void run()
	{
		//maak eerst connectie met de server
		connectToServer();
		
		if( serverSocket != null )
		{
			sendFeedbackToUI( "starting to read messages from server ...." );
	
			serverReader = null;
			serverWriter = null;
			
			try
			{
				sendFeedbackToUI( "grabbing writer from server" );		
				serverReader = new BufferedReader( new InputStreamReader( serverSocket.getInputStream() ) );
						
				serverWriter = new PrintWriter( new BufferedWriter( new OutputStreamWriter( serverSocket.getOutputStream() ) ), true );
			}
			
			catch (IOException e)
			{
				sendFeedbackToUI( "failed to grab outputstream from server socket: " + e.getMessage() );
			}
	
			int aliveCounter = 0;
			while( interactWithServer == true )
			{
				sendFeedbackToUI( "server interaction alive (" + aliveCounter + " seconds)" );
				aliveCounter++;
				
				try
				{
					if( serverReader != null )
					{	
						sendFeedbackToUI( "reading server text" );
						serverMessage = serverReader.readLine();
						
						if( serverMessage != null )
						{
							sendFeedbackToUI( serverMessage );
						}
						sendFeedbackToUI( "done reading server text" );
					}
				}	
				
				catch (IOException e)
				{
					sendFeedbackToUI( "failed to read message from server: " + e.getMessage() );
				}
			
				
				if( serverWriter != null )
				{
					if( messageToSend != null )
					{
						sendFeedbackToUI( "attempting to send message" );
						
						serverWriter.println( "test message" );
						sendFeedbackToUI( "message written" );
						
						messageToSend = null;
					}
				}
				
				try
				{
					Thread.sleep( 1000 );
				}
				
				catch (InterruptedException e)
				{
					sendFeedbackToUI( "Pausing thread failed: " + e.getMessage() );
					e.printStackTrace();
				}
			}
			
			//while loop finished, close server
			try
			{
				serverSocket.close();
				sendFeedbackToUI( "server connection shut down" );
			}
			
			catch (IOException e)
			{
				sendFeedbackToUI( "failed to shut down server connection correctly" );
			}
		}
	}
	
	
	public void sendFeedbackToUI( final String message )
	{
		this.runOnUiThread( new Runnable()
		{	
			@Override
			public void run()
			{
				feedbackText.setText( message + "\n" + feedbackText.getText() );
			}
		});
	}
	
	
	public void connectToServer()
	{
		if( serverSocket == null )
		{
			//verbinding met server maken
			sendFeedbackToUI( "contacting server..." );
			
			//nieuwe socket kan een IOException gooien
			serverSocket = new Socket();
			
			try
			{
				sendFeedbackToUI( "connecting to 145.97.16.205:4444" );				
				serverSocket.connect( new InetSocketAddress("145.97.16.205", 4444), 4000 );
			}
			
			catch (IOException e)
			{
				sendFeedbackToUI( "creating socket to server failed: " + e.getMessage() );
				e.printStackTrace();
			}

			sendFeedbackToUI( "connecting sequence completed" );		
		}
	}
}