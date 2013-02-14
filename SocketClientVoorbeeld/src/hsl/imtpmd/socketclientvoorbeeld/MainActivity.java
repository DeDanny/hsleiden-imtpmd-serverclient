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
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements Runnable
{

	private Socket			serverSocket;

	private PrintWriter		serverWriter;
	private BufferedReader	serverReader;

	private String			serverMessage;
	private String			messageToSend;
	private boolean			interactWithServer;

	private TextView		feedbackText;
	private TextView		feedbackText2;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		feedbackText = (TextView) findViewById(R.id.feedbacktext);
		feedbackText2 = (TextView) findViewById(R.id.feedbacktext2);

		interactWithServer = true;
		Thread connectToServerThread = new Thread(this);
		connectToServerThread.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void verzendVraag(View view)
	{
		messageToSend = ((EditText) findViewById(R.id.editText1)).getText().toString();
		sendFeedbackToUI("message set up for sending: " + messageToSend);
	}

	@Override
	public void run()
	{
		//maak eerst connectie met de server
		connectToServer();

		if (serverSocket != null)
		{
			sendFeedbackToUI("starting to read messages from server ....");

			serverReader = null;
			serverWriter = null;

			try
			{
				sendFeedbackToUI("grabbing writer from server");
				serverReader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

				serverWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream())), true);
			}

			catch (IOException e)
			{
				Log.e("debug", e.toString(), e);
				sendFeedbackToUI("failed to grab outputstream from server socket: " + e.getMessage());
				interactWithServer = false;
			}

			int aliveCounter = 0;
			sendFeedbackToUI("Go");
			while (interactWithServer == true)
			{
				sendFeedbackToUI2("server interaction alive (" + aliveCounter + " seconds)");
				aliveCounter++;

				if (messageToSend != null)
				{
					sendFeedbackToUI("attempting to send message");

					serverWriter.println(messageToSend);
					sendFeedbackToUI("message written");

					messageToSend = null;

					try
					{
						if (serverReader != null)
						{
							sendFeedbackToUI("reading server text");
							serverMessage = serverReader.readLine();

							if (serverMessage != null)
							{
								sendFeedbackToUI("server: " + serverMessage);

							}
							sendFeedbackToUI("done reading server text");
						}
					}

					catch (IOException e)
					{
						sendFeedbackToUI("failed to read message from server: " + e.getMessage());
					}
				}
				try
				{
					Thread.sleep(1000);
					sendFeedbackToUI2("done sleeping,  action time !!");
				}

				catch (InterruptedException e)
				{
					sendFeedbackToUI("Pausing thread failed: " + e.getMessage());
					e.printStackTrace();
				}
			}

			//while loop finished, close server
			try
			{
				serverSocket.close();
				sendFeedbackToUI("server connection shut down");
			}

			catch (IOException e)
			{
				sendFeedbackToUI("failed to shut down server connection correctly");
			}
		}
	}

	public void sendFeedbackToUI(final String message)
	{
		runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				feedbackText.setText(message + "\n" + feedbackText.getText());
			}
		});
	}

	public void sendFeedbackToUI2(final String message)
	{
		runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				feedbackText2.setText(message + "\n" + feedbackText2.getText());
			}
		});
	}

	public void connectToServer()
	{
		if (serverSocket == null)
		{
			//verbinding met server maken
			sendFeedbackToUI("contacting server...");

			//nieuwe socket kan een IOException gooien
			serverSocket = new Socket();

			try
			{
				String ip = "145.97.16.205";
				ip = "195.169.157.121";

				sendFeedbackToUI("connecting to  " + ip + ":4444");

				serverSocket.connect(new InetSocketAddress(ip, 4444), 4000);
			}

			catch (IOException e)
			{
				sendFeedbackToUI("creating socket to server failed: " + e.getMessage());
				e.printStackTrace();
			}

			sendFeedbackToUI("connecting sequence completed");
		}
	}
}
