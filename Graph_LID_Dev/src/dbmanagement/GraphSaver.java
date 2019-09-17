package dbmanagement;

import java.sql.*;
import model.Language;

public class GraphSaver {

	private Connection conn;
	
	public void makeConnection() throws ClassNotFoundException, SQLException{
		
		Class.forName("org.sqlite.JDBC");
		this.conn = DriverManager.getConnection("jdbc:sqlite:TRAINED_GRAPH.db");
		
	}
	
	public void closeConnection() throws SQLException{
		
		this.conn.close();

		this.conn = null;
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
	
	public double getOverallAccuracy() throws SQLException {
		
		String command = "SELECT OverallAccuracy FROM Accuracies";
		PreparedStatement prep = this.conn.prepareStatement(command);
		ResultSet rs = prep.executeQuery();
		
		double overallAccuracy = rs.getDouble("OverallAccuracy");
		
		rs.close();
		prep.close();
		
		return overallAccuracy;
	}
	
}
