package my_package;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLiteTest {
	
	private static Connection con;
	private static boolean hasData = false;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		MySQLiteTest dbCreator = new MySQLiteTest();
		try {
			dbCreator.createDB();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void createDB() throws SQLException, ClassNotFoundException {
		
		Class.forName("org.sqlite.JDBC");
		con = DriverManager.getConnection("jdbc:sqlite:MySQLiteTest.db");
		
		if ( !hasData ) {
			hasData = true;
			
			Statement state = con.createStatement();
			ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='user'");
			
//			if ( res.next() ) {
//				System.out.println("Building the User table");
//				
//				Statement state2 = con.createStatement();
//				state2.execute("CREATE TABLE user(id integer, fNane varchar(60), lName varchar(60), primary key(id));");
//				
//				PreparedStatement prep = con.prepareStatement("INSERT INTO user valus (?,?,?);");
//				prep.setString(2, "John");
//				prep.setString(3, "McNeil");
//				prep.execute();
//				
//				ResultSet testSet;
//				testSet = state2.executeQuery("SHOW DATABASES LIKE 'MySQLiteTest.db';");
//				System.out.println(testSet);
//				
//			} else {
//				System.out.println("Section of code was skipped");
//				System.out.println(res);
//			}
			
			state.execute("CREATE TABLE IF NOT EXISTS user(id integer, fName varchar(60), lName varchar(60), primary key(id));");
			
			PreparedStatement prep = con.prepareStatement("INSERT INTO user (fName, lName) values (?,?);");
			prep.setString(1, "John");
			prep.setString(2, "McNeil");
			prep.execute();
			
			prep = con.prepareStatement("INSERT INTO user (fName, lName) values (?,?);");
			prep.setString(1, "Yuuko");
			prep.setString(2, "Casey");
			prep.execute();
			
		}
		
		viewTable();
		
		con.close();
		
	}
	
	public static void viewTable() throws SQLException{
		
		String dbName = "MySQLiteTest.db";
		
		Statement stmt = null;
		String query = "SELECT fName FROM user";
		
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while ( rs.next() ) {
				
				String firstName = rs.getString("fName");
				System.out.println("Name is " + firstName);
			}
			
		} finally {
			if (stmt != null) stmt.close();
		}
		
	}
	
}
