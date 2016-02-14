<<<<<<< HEAD
//package server;
=======
>>>>>>> 9e6d3a48d45ae9525cb5e5102d12ec27948cd0e1

import java.io.*;
import java.net.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GameServer {

	private static ServerSocket serverSocket;

	private static final int PORT = 1234;
	private static int clientNo = 1;
	DatabaseConn connectionForInstructions =  new DatabaseConn("game_env_db");
	DatabaseConn connectionForAuthPlayer =  new DatabaseConn("player_db");

	public static void main(String argv[]) throws Exception {
		System.out.println("Started!!!");

		System.out.println("Opening port...\n");

		try {
			serverSocket = new ServerSocket(PORT);

		} catch (IOException e) {
		}

		do {

			Socket client = serverSocket.accept();
			System.out.println("\nNew client!!!\n");

			// Create a thread to handle communication with
			// this client and pass the constructor for this
			// thread a reference to the relevant socket...

			GameServer.ClientHandler handler = new GameServer().new ClientHandler(client, clientNo);

			handler.start(); // this method calls run.

			clientNo++;

		} while (true);
	}

	class ClientHandler extends Thread {
		private Socket client;
		private BufferedReader inFromClient;
		private BufferedReader text_to_Client;

		private DataOutputStream outToClient;
		public int clientNo;

		public boolean stopping;

		public ClientHandler(Socket socket, int clientNos) {
			// Set up reference to associated socket...
			client = socket;
			clientNo = clientNos;

			try {
				inFromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
				text_to_Client = new BufferedReader(new InputStreamReader(System.in));

				outToClient = new DataOutputStream(client.getOutputStream());
			} catch (IOException e) {
				
			}
		}
		
		private String formatInstructionSet(ResultSet rs)
		{
			int col1 = 9, col2 = 5;
			String instruction = "";
			try {
				while(rs.next()){
				for(int i = 2; i <= col1; i++)
				{
					
						instruction += rs.getString(i);
						if(i < 6)
							instruction += "|";
						else if(i < col1)
							instruction += ",";
					
				}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return instruction;
		}
		
		
		// Takes username of the player as arguments and returns the player current level
		// if the player is unable to authenticate, sends -99 in return

		/**
		 * 
		 * @param username
		 * @ Not authenticating the player at the moment,
		 * just returning the current level without authentication.
		 * 
		 */
		
		private int authenticatePlayer(String username)
		{
			ResultSet rs; String password = "usman123";
			
			connectionForAuthPlayer.connect();
			rs = connectionForAuthPlayer.runQuery("SELECT * FROM player where username='"+username+"';");
			
			try {
				while(rs.next()){
					System.out.println("Password of user is "+rs.getString(2)+"");
				//	if (password.equals(rs.getString(2)))
				//	{
						
						return rs.getInt(3);
				//	}
					
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			return -99;
		}
		
		
		
		public void run() {
			ResultSet rs;
			int playerLevel; String levelInstructions = "-99";
			System.out.println("Break Point!!!");

			try {
				
				stopping = false;
				String clientUsername;
				
			//	Thread mythread = Thread.currentThread();
				
				// Type 1 sample "1|10|80|8|12,34,56,78"
				// Type 2 sample "2|10|108|12|1,8,2,8"

				// Accept message from client on
				// the socket's input stream...

				OutputStream sout = client.getOutputStream();
				// Just converting them to different streams, so that string
				// handling becomes easier.

				DataOutputStream text_to_send = new DataOutputStream(sout);

				clientUsername = inFromClient.readLine();
				
				System.out.println("Message received from client number " + clientNo + ": " + clientUsername);
				System.out.println("Enter Message: ");
				playerLevel = authenticatePlayer(clientUsername);
//				playerLevel = -99;
				if(playerLevel == -99)
<<<<<<< HEAD
					levelInstructions = "1|10|80|8|12,34,56,78";
=======
					levelInstructions = "-99";
>>>>>>> 9e6d3a48d45ae9525cb5e5102d12ec27948cd0e1
				else
				{
					connectionForInstructions.connect();
					rs = connectionForInstructions.runQuery("SELECT * FROM game_level WHERE level ='"+playerLevel+"';");
					levelInstructions = formatInstructionSet(rs);
				}
				System.out.println("Player level = = = "+playerLevel);
				
				System.out.println("Instructions going to be sent = "+levelInstructions);
				
				
				// clientUsername = text_to_Client.readLine();
				// capitalizedSentence = clientSentence.toUpperCase() + '\n';
				// int charCount = clientSentence.length();
				text_to_send.writeUTF(levelInstructions);
				System.out.println("Your message: " + levelInstructions);
				// outToClient.write(charCount);

				// System.out.println("\nlength: " + charCount);

				client.close();
			} catch (IOException e) {
			}

		}
	}
}
