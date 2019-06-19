import java.sql.*;

public class DB_Practice {

	private Connection conn;
	private Statement st;
	private ResultSet rs;
	
	public DBConnect() {
		try {
			
			Class.forName("com.mysql.jdbc.Driver");
			
		} catch (Exception e) {
			System.out.println("\nErro: " + e);	
		}
	}
	
	public static void main() {
	
		DBConnect db1 = new DBConnect();
		
	}
	
}
