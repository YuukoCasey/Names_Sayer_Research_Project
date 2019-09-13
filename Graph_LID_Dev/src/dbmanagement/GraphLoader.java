package dbmanagement;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import model.Language;
import model.Node;
import model.Edge;

public class GraphLoader {

	private Connection conn;
	
	public void makeConnection() throws ClassNotFoundException, SQLException{
		
		Class.forName("org.sqlite.JDBC");
		this.conn = DriverManager.getConnection("jdbc:sqlite:TRAINED_GRAPH.db");
		
	}
	
	public void closeConnection() throws SQLException{
		
		this.conn.close();
		this.conn = null;
		
	}
	
	public ArrayList<Node> loadNodes() throws SQLException{
		
		String command = "SELECT * FROM Nodes";
		PreparedStatement prep = this.conn.prepareStatement(command);
		ResultSet rs = prep.executeQuery();
		
		ArrayList<Node> nodeList = new ArrayList<>();
		
		while(rs.next()) {
			
			String trigram = rs.getString("Trigram");
			HashMap<Language, Integer> langValues = new HashMap<>();
			int langVal = rs.getInt("English");
			langValues.put(Language.ENGLISH, langVal);
			langVal = rs.getInt("Maori");
			langValues.put(Language.MAORI, langVal);
			langVal = rs.getInt("Samoan");
			langValues.put(Language.SAMOAN, langVal);
			
			nodeList.add(new Node(trigram, langValues));
			
		}
		
		rs.close();
		prep.close();
		
		return nodeList;
	}
	
	public ArrayList<Edge> loadEdges() throws SQLException {
		
		String command = "SELECT * FROM Edges";
		PreparedStatement prep = this.conn.prepareStatement(command);
		ResultSet rs = prep.executeQuery();
		
		ArrayList<Edge> edgeList = new ArrayList<>();
		
		while(rs.next()) {
			
			String pastTrigram = rs.getString("PastTrigram");
			String nextTrigram = rs.getString("NextTrigram");
			
			HashMap<Language, Integer> langValues = new HashMap<>();
			int langVal = rs.getInt("English");
			langValues.put(Language.ENGLISH, langVal);
			langVal = rs.getInt("Maori");
			langValues.put(Language.MAORI, langVal);
			langVal = rs.getInt("Samoan");
			langValues.put(Language.SAMOAN, langVal);
			
			edgeList.add(new Edge(pastTrigram, nextTrigram, langValues));
			
		}
		
		rs.close();
		prep.close();
		
		return edgeList;
		
	}
	
}
