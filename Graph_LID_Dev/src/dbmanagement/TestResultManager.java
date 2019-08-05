package dbmanagement;

import java.sql.*;
//import java.util.ArrayList;
import model.Language;

public class TestResultManager {

	private Connection conn;
	
	public TestResultManager() {
		
	}
	
	public void makeConnection() throws ClassNotFoundException, SQLException{
		
		Class.forName("org.sqlite.JDBC");
		this.conn = DriverManager.getConnection("jdbc:sqlite:TestingResults.db");
		
	}
	
	public void closeConnection() throws SQLException{
		
		this.conn.close();

		this.conn = null;
	}
	
	public int getHighestTestNumber() throws SQLException{
		
		//ArrayList<Integer> queryRes = new ArrayList<>();
		int queryRes = 1;
		PreparedStatement prep = this.conn.prepareStatement("SELECT MAX(TestNumber) FROM TestResults");
		ResultSet rs = prep.executeQuery();
		
		while( rs.next() ) {
//			queryRes.add(rs.getInt("TestNumber"));
			queryRes = rs.getInt("TestNumber");
		}
		prep.close();
		rs.close();
		
		prep = null;
		rs = null;
		
		return queryRes;
	}
	
	public void insertEntryToDB(int testNum, Language lang, double accuracy, double testingPercent, double trainingPercent) throws SQLException{
		
		String langString = "English";
		if (lang.equals(Language.MAORI)) {
			langString = "Maori";
		} else if (lang.equals(Language.SAMOAN)) {
			langString = "Samoan";
		}
		
		PreparedStatement prep = this.conn.prepareStatement("INSERT INTO TestResults VALUES(?,?,?,?,?)");
		prep.setInt(1, testNum);
		prep.setString(2, langString);
		prep.setFloat(3, (float)accuracy);
		prep.setFloat(4, (float)testingPercent);
		prep.setFloat(5, (float)trainingPercent);
		prep.execute();
		
		prep.close();
		prep = null;
	}
	
	public double getAccuracyForLanguageInTest(int testNum, Language lang) throws SQLException{
		
		String langString = "English";
		if (lang.equals(Language.MAORI))
			langString = "Maori";
		else if (lang.equals(Language.SAMOAN))
			langString = "Samoan";
		
		PreparedStatement prep = this.conn.prepareStatement("SELECT Accuracy FROM TestResults WHERE TestNumber=? AND Language=?");
		prep.setInt(1, testNum);
		prep.setString(2, langString);
		ResultSet rs = prep.executeQuery();
		
		double retVal = 0.0;
		while ( rs.next() ) {
			retVal = (double)rs.getFloat("Accuracy");
		}
		prep.close();
		rs.close();
		
		prep = null;
		rs = null;
		
		return retVal;
	}
	
}
