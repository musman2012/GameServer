package dbConn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseConn {
	
	private Connection conn = null;
	private Statement st = null;
	private ResultSet rs = null;
	private String connString = "jdbc:mysql://gameenv.cccaikjwogxr.ap-northeast-1.rds.amazonaws.com:3306/"; 
	
	public DatabaseConn(String dbName) {
		
	//	String dbName = System.getProperty("RDS_DB_NAME");
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// game_environment_db
		connString += dbName;	// completes the connection string
//		connect();
	}
	
	public void connect() {
		try {
			conn = DriverManager.getConnection(connString,"root","hazrat2012");
			st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			System.out.println("Connected!!");
		} catch (SQLException e) {
//			e.printStackTrace();
			e.printStackTrace();
			System.out.println("Unable to make connection with the Database"+connString);
			System.out.println(e.getStackTrace());
		}
	}
	
	public void executeNonQuery(String query){
		try {
			st.execute(query);
			
		} catch (SQLException e) {
//			e.printStackTrace();
			System.out.println("Unable to run UPDATE query ");		
		}
	}
	
	public ResultSet runQuery(String query){
		
		
		try {
			rs = st.executeQuery(query);

			if(rs == null) System.out.println("null"); 
			
		} catch (SQLException e) {
//			e.printStackTrace();
			System.out.println("Unable to run SELECT query ");		
		}
		return rs;
	}
	
/*	public static void main(String[] args) {
		DatabaseConn db = new DatabaseConn("playerdb");
		db.connect();
		db.runQuery("SELECT * FROM player WHERE username='usman123';");
	}
	
	*/
}
