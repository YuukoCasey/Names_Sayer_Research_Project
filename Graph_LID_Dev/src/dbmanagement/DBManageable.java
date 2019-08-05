package dbmanagement;

//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.Statement;
import model.Language;
import java.util.ArrayList;

public interface DBManageable {

	public void viewTable() throws SQLException;
	public void viewTable(String language) throws SQLException;
	public boolean nameExists(String name) throws SQLException;
	public ArrayList<Language> getLanguages(String name) throws SQLException;
	public ArrayList<String> getNames(Language lang) throws SQLException;
	public void makeConnection() throws SQLException, ClassNotFoundException;
	public void closeConnection() throws SQLException;
	
}
