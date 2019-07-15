package dbmanagement;

import model.GraphLID;
import model.Language;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DBManager implements DBManageable{

	private Connection conn;
	
	public void makeConnection() throws SQLException, ClassNotFoundException {
		
		Class.forName("org.sqlite.JDBC");
		this.conn = DriverManager.getConnection("jdbc:sqlite:NamesDB.db");
		
	}
	
	public void closeConnection() throws SQLException {
		this.conn.close();
	}
	
	public boolean nameExists(String name) throws SQLException{
		
		PreparedStatement prep = this.conn.prepareStatement("SELECT name FROM names WHERE name=?");
		prep.setString(1, name);
		ResultSet rs = prep.executeQuery();
		
		System.out.println(rs);
		
		while( rs.next() ) {
			
			String res_name = rs.getString("name");
			if (res_name == name) return true;
			//System.out.println(res_name);
		}
		return false;
		
	}
	
	public ArrayList<String> getNames(Language lang) throws SQLException{
		
		String search_lang = "English";
		
		if (lang == Language.SAMOAN) {
			search_lang = "Samoan";
		} else if (lang == Language.MAORI) {
			search_lang = "Maori";
		}
		
		PreparedStatement prep = this.conn.prepareStatement("SELECT name FROM names WHERE language=?");
		prep.setString(1, search_lang);
		
		ResultSet rs = prep.executeQuery();
		
		ArrayList<String> nameList = new ArrayList<>(); 
		
		while( rs.next() ) {	
			nameList.add(rs.getString("name"));
		}
		return nameList;
	}
	
	public ArrayList<Language> getLanguages(String name) throws SQLException{
		
		PreparedStatement prep = this.conn.prepareStatement("SELECT language FROM names WHERE name=?");
		prep.setString(1, name);
		ResultSet rs = prep.executeQuery();
		
		ArrayList<Language> returnList = new ArrayList<>();
		
		while ( rs.next() ) {
			String testString = rs.getString("language");
			
			if(testString.equals("Samoan")) returnList.add(Language.SAMOAN);
			else if (testString.equals("Maori")) returnList.add(Language.MAORI);
			else if (testString.equals("English")) returnList.add(Language.ENGLISH);
		}
		
		return returnList;
		
	}
	
	public void viewTable() throws SQLException{
		
		PreparedStatement prep = this.conn.prepareStatement("SELECT * FROM names");
		ResultSet rs = prep.executeQuery();
		
		while ( rs.next() ) {
			System.out.println(rs.getString("name") + "\t\t\t" + rs.getString("language"));
		}
		
	}
	
	public void viewTable(String language) throws SQLException{
		
		PreparedStatement prep = this.conn.prepareStatement("SELECT name FROM names WHERE language=?");
		prep.setString(1, language);
		ResultSet rs = prep.executeQuery();
		
		System.out.println(language + ": ");
		while ( rs.next() ) {
			System.out.println("\t" + rs.getString("name"));
		}
		
	}
	
	public int getNumNamesInLanguage(Language lang) throws SQLException{
		
		String languageString = "";
		if (lang == Language.MAORI) languageString = "Maori";
		else if (lang == Language.SAMOAN) languageString = "Samoan";
		else languageString = "English";
		
		PreparedStatement prep = this.conn.prepareStatement("SELECT COUNT(*) AS name_count FROM names WHERE language=?");
		prep.setString(1, languageString);
		ResultSet rs = prep.executeQuery();
		return rs.getInt("name_count");
		
	}
	
	public int getNumNamesStartString(Language lang, String nameStart) throws SQLException{
		
		int numNamesInLang = this.getNumNamesInLanguage(lang);
		int returnNum = 0;
		ArrayList<String> langNames = this.getNames(lang);
		
		for (int i = 0; i < numNamesInLang; i++) {
				
			String curName = langNames.get(i);
				
			if (nameStart.length() > curName.length()) continue;
			boolean isDiff = false;
			
			for (int j = 0; j < nameStart.length(); j++) {
			
				if (nameStart.charAt(j) != curName.charAt(j)) {
					isDiff = true;
					break;
				}
			
			}
			
			if (isDiff == false) 
				returnNum++;
			
		}
		
		return returnNum;
		
	}
	
}