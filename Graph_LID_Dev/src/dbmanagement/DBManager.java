package dbmanagement;

//import model.GraphLID;
import model.Language;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.Statement;
import java.util.ArrayList;

public class DBManager implements DBManageable{

	private Connection conn; //The object's connection to a database
	
	public void makeConnection() throws SQLException, ClassNotFoundException {
		
		/**********************************************************************
		 *                                                                    *
		 * This function serves to make this object connect to the NamesDB.db *
		 * database                                                           *
		 *                                                                    *
		 **********************************************************************/
		
		Class.forName("org.sqlite.JDBC");
		this.conn = DriverManager.getConnection("jdbc:sqlite:NamesDB.db");
		
	}
	
	public void closeConnection() throws SQLException {
		
		/*************************************************************
		 *                                                           *
		 * This function is used to close a connection to NamesDB.db *
		 *                                                           *
		 *************************************************************/
		
		this.conn.close();
		this.conn = null;
	}
	
	public boolean nameExists(String name) throws SQLException{
		
		/******************************************************************
		 *                                                                *
		 * This function serves to check whether or not a name is already *
		 * in the NamesDB.db database                                     *
		 *                                                                *
		 ******************************************************************/
		
		PreparedStatement prep = this.conn.prepareStatement("SELECT name FROM names WHERE name=?");
		prep.setString(1, name);
		ResultSet rs = prep.executeQuery();
		
		while( rs.next() ) {
			
			String res_name = rs.getString("name");
			if (res_name == name) {
				prep.close();
				rs.close();
				prep = null;
				rs = null;
				return true;
			}
		}
		//Closing PreparedStatements and ResultSets is being done
		//to try and find and close any memory leaks
		prep.close();
		rs.close();
		prep = null;
		rs = null;
		return false;
		
	}
	
	public ArrayList<String> getNames(Language lang) throws SQLException{
		
		/*************************************************************
		 *                                                           *
		 * This function serves to retrieve every name stored in the *
		 * NamesDB.db database for a given language                  *
		 *                                                           *
		 * Input:                                                    *
		 * 	The language for which you wish to retrieve all names    *
		 *                                                           *
		 * Output:                                                   *
		 *  An ArrayList of all names for a given language           *
		 *                                                           *
		 *************************************************************/
		
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
		prep.close();
		rs.close();
		
		prep = null;
		rs = null;
		
		return nameList;
	}
	
	public ArrayList<Language> getLanguages(String name) throws SQLException{
		
		/**********************************************************************
		 *                                                                    *
		 * This function serves to retrieve a list of all languages for which *
		 * the database has a given name stored                               *
		 *                                                                    *
		 * Input:                                                             *
		 * 	A name you are searching for in the database                      *
		 *                                                                    *
		 * Output:                                                            *
		 * 	A list of the languages for which this name has been stored       *
		 *                                                                    *
		 **********************************************************************/
		
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
		prep.close();
		rs.close();
		
		prep = null;
		rs = null;
		
		return returnList;
		
	}
	
	public void viewTable() throws SQLException{
		
		/**********************************************************************
		 *                                                                    *
		 * Sources aside, this function serves to display all the data in the *
		 * names table of NamesDB.db                                          *
		 *                                                                    *
		 **********************************************************************/
		
		PreparedStatement prep = this.conn.prepareStatement("SELECT * FROM names");
		ResultSet rs = prep.executeQuery();
		
		while ( rs.next() ) {
			System.out.println(rs.getString("name") + "\t\t\t" + rs.getString("language"));
		}
		
		prep.close();
		rs.close();
		
		prep = null;
		rs = null;
		
	}
	
	public void viewTable(String language) throws SQLException{
		
		/***************************************************************************
		 *                                                                         *
		 * This function serves to display all of the names stored in the database *
		 * for a particular language                                               *
		 *                                                                         *
		 ***************************************************************************/
		
		PreparedStatement prep = this.conn.prepareStatement("SELECT name FROM names WHERE language=?");
		prep.setString(1, language);
		ResultSet rs = prep.executeQuery();
		
		System.out.println(language + ": ");
		while ( rs.next() ) {
			System.out.println("\t" + rs.getString("name"));
		}
		
		prep.close();
		rs.close();
		
		prep = null;
		rs = null;
		
	}
	
	public int getNumNamesInLanguage(Language lang) throws SQLException{
		
		/******************************************************************
		 *                                                                *
		 * This function serves to find the number of names stored in the *
		 * database for a given language                                  *
		 *                                                                *
		 ******************************************************************/
		
		String languageString = "";
		if (lang == Language.MAORI) languageString = "Maori";
		else if (lang == Language.SAMOAN) languageString = "Samoan";
		else languageString = "English";
		
		PreparedStatement prep = this.conn.prepareStatement("SELECT COUNT(*) AS name_count FROM names WHERE language=?");
		prep.setString(1, languageString);
		ResultSet rs = prep.executeQuery();
		int retVal = rs.getInt("name_count");
		
		prep.close();
		rs.close();
		
		prep = null;
		rs = null;
		
		return retVal;
		
	}
	
	public int getNumNamesStartString(Language lang, String nameStart) throws SQLException{
		
		/**************************************************************************
		 *                                                                        *
		 * This function serves to return the number of names in a given language *
		 * that start with a given string                                         *
		 *                                                                        *
		 **************************************************************************/
		
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
	
	public int getTrainingSize(double trainingPercent, Language lang) throws Exception{
		
		/************************************************************************
		 *                                                                      *
		 * This function serves to find the number of names that should be used *
		 * to train the AI. It analyses the number of names for each language,  *
		 * and finds the lowest number, and returns half of that number         *
		 *                                                                      *
		 ************************************************************************/
		
		int smallestLangNum = this.getNumNamesInLanguage(lang);
		
//		for (Language lang : Language.values()) {
//			int temp = this.getNumNamesInLanguage(lang);
//			if (temp < smallestLangNum && temp > 0) 
//				smallestLangNum = temp;
//		}

		
		double intermediate_double = (double)smallestLangNum * trainingPercent / 100.0;
		
		smallestLangNum = (int)Math.round(intermediate_double);
		
		return smallestLangNum;
	}
	
	public ArrayList<String> getNamesStartString(String nameStart, Language lang) throws SQLException{
		
		/**************************************************************************
		 * This function serves to find any name in the database that starts with *
		 * a given string for a given language                                    *
		 *                                                                        *
		 * Inputs:                                                                *
		 * 	-The String a desired name will start with                            *
		 *  -The Language from which the name should be from                      *
		 *                                                                        *
		 * Output:                                                                *
		 * 	-An ArrayList of all the names in the database that start with the    *
		 * 		start string for this language                                    *
		 **************************************************************************/
		
		ArrayList<String> returnList = new ArrayList<>();
		
		String searchLanguage = "English";
		if (lang == Language.MAORI) 
			searchLanguage = "Maori";
		else if (lang == Language.SAMOAN)
			searchLanguage = "Samoan";
		
		PreparedStatement prep = this.conn.prepareStatement("SELECT name FROM names WHERE language=?");
		prep.setString(1, searchLanguage);
		ResultSet rs = prep.executeQuery();
		
		ArrayList<String> nameList = new ArrayList<>(); 
		
		while( rs.next() ) {	
			nameList.add(rs.getString("name"));
		}
		
		//The NameIterationLoop will be used to look at each name and determine whether
		//it starts with the nameStart String. If so, the name will be added to the
		//returnList
		NameIterationLoop:
		for (int i = 0; i < nameList.size(); i++) {
			String examineName = nameList.get(i);
			if (nameStart.length() == 0) returnList.add(examineName);
			else{
				if (examineName.length() < nameStart.length())
					continue NameIterationLoop;
				for (int j = 0; j < nameStart.length(); j++) {
					if (examineName.charAt(j) != nameStart.charAt(j))
						continue NameIterationLoop;
					
				}
				//If the loop has not resulted in skipping to the next iteration of the 
				//'NameIterationLoop' then the nameStart should be suitable for
				//'examineName'
				returnList.add(examineName);
			}
		}
		prep.close();
		rs.close();
		
		prep = null;
		rs = null;
		
		return returnList;
		
	}
	
	public Language getLangWithMostNames() throws SQLException{
		Language retLang = Language.SAMOAN;
		int maxNames = this.getNumNamesInLanguage(retLang);
		for (Language lang : Language.values()) {
			
			int examineNum = this.getNumNamesInLanguage(lang);
			if (examineNum > maxNames) {
				retLang = lang;
				maxNames = examineNum;
			}
			
		}
		return retLang;
	}
	
}
