import java.io.*;
import java.net.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

import dbConn.DatabaseConn;

public class GameServer {

	private static ServerSocket serverSocket;

	private static final int PORT = 1234;
	private static int clientNo = 1;
	DatabaseConn connectionForInstructions = new DatabaseConn("game_env_db");
	DatabaseConn connectionForAuthPlayer = new DatabaseConn("player_db");

	private static volatile int numberOfPlayers = 0;

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

		private String formatInstructionSet(ResultSet rs) {
			int col1 = 9, col2 = 5;
			String instruction = "";
			try {
				while (rs.next()) {
					for (int i = 2; i <= col1; i++) {

						instruction += rs.getString(i);
						if (i < 6)
							instruction += "|";
						else if (i < col1)
							instruction += ",";

					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return instruction;
		}

		// Takes username of the player as arguments and returns the player
		// current level
		// if the player is unable to authenticate, sends -99 in return

		/**
		 * 
		 * @param username
		 * @ Not authenticating the player at the moment, just returning the
		 * current level without authentication.
		 * 
		 */

		private int authenticatePlayer(String username) {
			ResultSet rs;
			String password = "usman123";

			connectionForAuthPlayer.connect();
			rs = connectionForAuthPlayer.runQuery("SELECT * FROM player where username='" + username + "';");

			try {
				while (rs.next()) {
					System.out.println("Password of user is " + rs.getString(2) + "");
					// if (password.equals(rs.getString(2)))
					// {

					return rs.getInt(3);
					// }

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return -99;
		}
		
		private int addNewPlayer(String username)
		{
		//	ResultSet rs;
			String password = "usman123";
			
			try{
			connectionForAuthPlayer.connect();
			
			connectionForAuthPlayer.executeNonQuery("INSERT INTO player values ('"+username+"','test',1,null,0);");
			}
			catch(Exception e)
			{
				System.out.println("Cannot make connection with DB to add player!!!");
			}
			
			
			return 1;
		}
		
		private void updatePlayerDetails(String username, int level, int score)
		{
			try{
			connectionForAuthPlayer.connect();
			
			connectionForAuthPlayer.executeNonQuery("UPDATE player SET current_Level="+level+" WHERE username='"+username+"';");
			}
			catch(Exception e)
			{
				System.out.println("Cannot make connection with DB to UPDATE player!!!");
			}
		}

		public void run() {
			ResultSet rs;
			int playerLevel = 0;
			String levelInstructions = "-99";

			try {

				stopping = false;
				String instructions, gameMode, clientUsername;

				// Type 1 sample "1|10|80|8|12,34,56,78"
				// Type 2 sample "2|10|108|12|1,8,2,8"

				// Accept message from client on
				// the socket's input stream...

				OutputStream sout = client.getOutputStream();
				// Just converting them to different streams, so that string
				// handling becomes easier.

				DataOutputStream text_to_send = new DataOutputStream(sout);

				instructions = inFromClient.readLine();
				
				clientUsername = instructions;

				System.out.println("Message received from client number " + clientNo + ": " + clientUsername);
				System.out.println("Enter Message: ");
				
				if(clientUsername.charAt(0) == '1')						// Game start request
				{
					clientUsername = clientUsername.substring(1);
					playerLevel = authenticatePlayer(clientUsername);

					if (playerLevel == -99)
					{
						levelInstructions = "-99";
						System.out.println("New Player.");
						playerLevel = addNewPlayer(clientUsername);
						updatePlayerDetails(clientUsername, playerLevel, 400);
					}
					
				}
				else if(clientUsername.charAt(0) == '2')			// Update level request
				{
					clientUsername = clientUsername.substring(1);
					playerLevel = authenticatePlayer(clientUsername);
					playerLevel++;
					updatePlayerDetails(clientUsername, playerLevel, 400);
				}

				connectionForInstructions.connect();
				rs = connectionForInstructions
							.runQuery("SELECT * FROM game_level WHERE level ='" + playerLevel + "';");
				levelInstructions = formatInstructionSet(rs);
				
				System.out.println("Player level = = = " + playerLevel);

				System.out.println("Instructions going to be sent = " + levelInstructions);

				text_to_send.writeUTF(levelInstructions);
				System.out.println("Your message: " + levelInstructions);

				client.close();
			} catch (IOException e) {
			}

		}

		public String waitForPlayerTwo() {
			try {
				Thread.sleep(1000);
				if (numberOfPlayers == 2) {
					numberOfPlayers = 0;
					return "NAME OF PLAYER 2";
				}

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return "";
		}

	}
}

