package model;

import model.Node;
import model.Edge;
import model.Language;
import java.util.ArrayList;

public abstract class AbstractGraph {

	protected ArrayList<Node> nodeList = new ArrayList<>();
	protected ArrayList<Edge> edgeList = new ArrayList<>();
	
	public boolean nodeExists(String trigram) {
		for (int i = 0; i < nodeList.size(); i++) {
			if (nodeList.get(i).getTrigram().equals(trigram)) return true;
		}
		return false;
	}
	
	public boolean edgeExists(String pastNodeTrigram, String nextNodeTrigram) {
		for (int i = 0; i < edgeList.size(); i++) {
			
			if (edgeList.get(i).getPastNodeTrigram().equals(pastNodeTrigram) 
					&& edgeList.get(i).getNextNodeTrigram().equals(nextNodeTrigram)) 
				return true;
			
		}
		return false;
	}
	
	public void addNode(Language lang, String trigram) {
		if (this.nodeList.size() == 0 || !this.nodeExists(trigram)) {
			nodeList.add(new Node(lang, trigram));
		} else {
			this.incrementLangValNode(lang, trigram);
		}
	}
	
	public void addEdge(Language lang, String pastNodeTrigram, String nextNodeTrigram) {
		if (this.edgeList.size() == 0 || !this.edgeExists(pastNodeTrigram, nextNodeTrigram)) {
			edgeList.add(new Edge(lang, pastNodeTrigram, nextNodeTrigram));
		} else this.incrementLangValEdge(lang, pastNodeTrigram, nextNodeTrigram);
	}
	
	public void incrementLangValNode(Language lang, String trigram) {
		int index = this.getNodeIndex(trigram);
		if (index == -1) {
			return;
		}
		
		Node incrNode = nodeList.get(index);
		incrNode.increaseLangVal(lang, 1);
		nodeList.set(index, incrNode);
	}
	
	public void incrementLangValEdge(Language lang, String pastNodeTrigram, String nextNodeTrigram) {
		int index = this.getEdgeIndex(pastNodeTrigram, nextNodeTrigram);
		Edge incrEdge = edgeList.get(index);
		incrEdge.increaseLangVal(lang, 1);
		edgeList.set(index, incrEdge);
	}
	
	public int getNodeIndex(String trigram) {
		for (int i = 0; i < nodeList.size(); i++) {
			if (nodeList.get(i).getTrigram().equals(trigram)) return i;
		}
		return -1; //Incase the node does not exist
	}
	
	public int getEdgeIndex(String pastNodeTrigram, String nextNodeTrigram) {
		for (int i = 0; i < edgeList.size(); i++) {
			if (edgeList.get(i).getPastNodeTrigram().equals(pastNodeTrigram) 
					&& edgeList.get(i).getNextNodeTrigram().equals(nextNodeTrigram)) 
				return i;
		}
		return -1;
	}
	
	public Node getNode(int index) {
		return nodeList.get(index);
	}
	
	public Edge getEdge(int index) {
		return edgeList.get(index);
	}
	
	public int getNodeListSize() {
		return this.nodeList.size();
	}
	
	public int getEdgeListSize() {
		return this.edgeList.size();
	}
	
	public boolean nodeExists(Node inputNode) {
		
		String trigram = inputNode.getTrigram();
		
		for (int i = 0; i < nodeList.size(); i++) {
			if (nodeList.get(i).getTrigram().equals(trigram)) return true;
		}
		
		return false;
	}
	
	public boolean edgeExists(Edge inputEdge) {
		
		String pastNodeTrigram = inputEdge.getPastNodeTrigram();
		String nextNodeTrigram = inputEdge.getNextNodeTrigram();
		
		for (int i = 0; i < edgeList.size(); i++) {
			
			if (edgeList.get(i).getPastNodeTrigram().equals(pastNodeTrigram) 
					&& edgeList.get(i).getNextNodeTrigram().equals(nextNodeTrigram)) 
				return true;
			
		}
		return false;
		
	}
	
}