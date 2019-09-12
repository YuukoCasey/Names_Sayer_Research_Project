package dbmanagement;

import java.sql.*;
import java.util.ArrayList;

import model.Language;
import model.Node;
import model.Edge;

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
	
	
//public void insertEntryToDB(int testNum, Language lang, double accuracy, double testingPercent, double trainingPercent) throws SQLException{
//		
//		String langString = "English";
//		if (lang.equals(Language.MAORI)) {
//			langString = "Maori";
//		} else if (lang.equals(Language.SAMOAN)) {
//			langString = "Samoan";
//		}
//		
//		PreparedStatement prep = this.conn.prepareStatement("INSERT INTO TestResults VALUES(?,?,?,?,?)");
//		prep.setInt(1, testNum);
//		prep.setString(2, langString);
//		prep.setFloat(3, (float)accuracy);
//		prep.setFloat(4, (float)testingPercent);
//		prep.setFloat(5, (float)trainingPercent);
//		prep.execute();
//		
//		prep.close();
//		prep = null;
//	}
	

	public void insertNodeToDB(Node node) throws SQLException {
	
		String command = "INSERT INTO Nodes VALUES (?,?,?,?)";
		PreparedStatement prep = this.conn.prepareStatement(command);
	
		prep.setString(1, node.getTrigram());
		prep.setInt(2, node.getLanguageValue(Language.ENGLISH));
		prep.setInt(3, node.getLanguageValue(Language.MAORI));
		prep.setInt(4, node.getLanguageValue(Language.SAMOAN));
		prep.execute();
	
		prep.close();
		prep = null;
	}
	
	
	public void insertEdgeToDB(Edge edge) throws SQLException{
		
		String command = "INSERT INTO Edges VALUES (?,?,?,?,?)";
		PreparedStatement prep = this.conn.prepareStatement(command);
		
		prep.setString(1, edge.getPastNodeTrigram());
		prep.setString(2, edge.getNextNodeTrigram());
		prep.setInt(3, edge.getLanguageValue(Language.ENGLISH));
		prep.setInt(4, edge.getLanguageValue(Language.MAORI));
		prep.setInt(5, edge.getLanguageValue(Language.SAMOAN));
		prep.execute();
		
		prep.close();
		prep = null;
	}
	
	public void updateAccuracies(double overall, double english, double maori, double samoan) throws SQLException{
		
		String command = "UPDATE Accuracies SET OverallAccuracy=?, English=?, Maori=?, Samoan=?";
		PreparedStatement prep = this.conn.prepareStatement(command);
		
		prep.setDouble(1, overall);
		prep.setDouble(2, english);
		prep.setDouble(3, maori);
		prep.setDouble(4, samoan);
		prep.execute();
		
		prep.close();
		
	}

	
	public double getOverallAccuracy() throws SQLException{
		
		PreparedStatement prep = this.conn.prepareStatement("SELECT OverallAccuracy FROM Accuracies");
		ResultSet rs = prep.executeQuery();
		
		double retVal = rs.getDouble("OverallAccuracy");
		
		rs.close();
		prep.close();
		rs = null;
		prep = null;
		
		return retVal;
		
	}
	
	public ArrayList<Node> readNodes() throws SQLException{
		
		String command = "SELECT * FROM Nodes";
		PreparedStatement prep = this.conn.prepareStatement(command);
		ResultSet rs = prep.executeQuery();
		
		ArrayList<Node> retNodeList = new ArrayList<>();
		
		while (rs.next()) {
			
			String trigram = rs.getString("Trigram");
			int english = rs.getInt("English");
			int maori = rs.getInt("Maori");
			int samoan = rs.getInt("Samoan");
			
			Node newNode = new Node(trigram);
			newNode.setLanguageValue(english, Language.ENGLISH);
			newNode.setLanguageValue(maori, Language.MAORI);
			newNode.setLanguageValue(samoan, Language.SAMOAN);
			
			retNodeList.add(newNode);
			
		}
		
		rs.close();
		prep.close();
		
		return retNodeList;
		
	}
	
	public ArrayList<Edge> readEdges() throws SQLException {
		
		ArrayList<Edge> retEdgeList = new ArrayList<>();
		
		String command = "SELECT * FROM Edges";
		PreparedStatement prep = this.conn.prepareStatement(command);
		ResultSet rs = prep.executeQuery();
		
		while (rs.next()) {
			
			String pastTrigram = rs.getString("PastTrigram");
			String nextTrigram = rs.getString("NextTrigram");
			int english = rs.getInt("English");
			int maori = rs.getInt("Maori");
			int samoan = rs.getInt("Samoan");
			
			Edge edge = new Edge(pastTrigram, nextTrigram);
			edge.setLanguageValue(english, Language.ENGLISH);
			edge.setLanguageValue(maori, Language.MAORI);
			edge.setLanguageValue(samoan, Language.SAMOAN);
			
			retEdgeList.add(edge);
			
		}
		
		rs.close();
		prep.close();
		
		return retEdgeList;
	}
	
}