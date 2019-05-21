package model;

import model.Node;
import java.util.ArrayList;
import model.Language;
import java.util.HashMap;

public class GraphLID {

	private ArrayList<Node> graphNodes = new ArrayList<>();
	private ArrayList<Edge> graphEdges = new ArrayList<>();
	
	public GraphLID() {

	}
	
	public void addNode(Language lang, String trigram) {
//			int index = this.getNodeIndex(trigram);
//			if (index == -1) graphNodes.add(new Node(lang, trigram)); //If the node does not exist
//			else this.incrementLangVal(lang, trigram);
		if (this.graphNodes.size() == 0 || !this.nodeExists(trigram)) {
			//Node newNode = new Node(lang, trigram);
			graphNodes.add(new Node(lang, trigram));
		} else this.incrementLangValNode(lang, trigram);
	}
	
	public void addEdge(Language lang, String pastNodeTrigram, String nextNodeTrigram) {
		if (this.graphEdges.size() == 0 || !this.edgeExists(pastNodeTrigram, nextNodeTrigram)) {
			graphEdges.add(new Edge(lang, pastNodeTrigram, nextNodeTrigram));
		} else this.incrementLangValEdge(lang, pastNodeTrigram, nextNodeTrigram);
	}
	
	public int getNodeIndex(String trigram) {
		for (int i = 0; i < graphNodes.size(); i++) {
			if (graphNodes.get(i).getTrigram().equals(trigram)) return i;
		}
		return -1; //Incase the node does not exist
	}
	
//	public Node getNodeByTrigram(Language lang, String trigram) {
//		Node returnNode = new Node(lang, trigram);
//		
//		for (int i = 0; i < this.graphNodes.size(); i++) {
//			if 
//		}
//		
//		return returnNode;
//	}
	
	public int getEdgeIndex(String pastNodeTrigram, String nextNodeTrigram) {
		for (int i = 0; i < graphEdges.size(); i++) {
			if (graphEdges.get(i).getPastNodeTrigram().equals(pastNodeTrigram) && graphEdges.get(i).getNextNodeTrigram().equals(nextNodeTrigram)) return i;
		}
		return -1;
	}
	
	public void incrementLangValNode(Language lang, String trigram) {
		int index = this.getNodeIndex(trigram);
		Node incrNode = graphNodes.get(index);
		incrNode.increaseLangVal(lang, 1);
		graphNodes.set(index, incrNode);
	}
	
	public void incrementLangValEdge(Language lang, String pastNodeTrigram, String nextNodeTrigram) {
		int index = this.getEdgeIndex(pastNodeTrigram, nextNodeTrigram);
		Edge incrEdge = graphEdges.get(index);
		incrEdge.increaseLangVal(lang, 1);
		graphEdges.set(index, incrEdge);
	}
	
	public Node getNode(int index) {
		return graphNodes.get(index);
	}
	
	public Edge getEdge(int index) {
		return graphEdges.get(index);
	}
	
	
	public boolean nodeExists(String trigram) {
		for (int i = 0; i < graphNodes.size(); i++) {
			if (graphNodes.get(i).getTrigram().equals(trigram)) return true;
//			Node tempNode = this.getNode(i);
//			String tempTrigram = tempNode.getTrigram();
//			System.out.println("\ntempTrigram = " + tempTrigram);
//			System.out.println("Length of tempTrigram is " + tempTrigram.length());
//			System.out.println("Is tempTrigram equal to 'Yuu'? " + (tempTrigram.equals("Yuu")));
		}
		return false;
	}
	
	public boolean edgeExists(String pastNodeTrigram, String nextNodeTrigram) {
		for (int i = 0; i < graphEdges.size(); i++) {
			
			if (graphEdges.get(i).getPastNodeTrigram().equals(pastNodeTrigram) && graphEdges.get(i).getNextNodeTrigram().equals(nextNodeTrigram)) 
				return true;
			
		}
		return false;
	}
	
	public int getNodeListSize() {
		return this.graphNodes.size();
	}
	
	public int getEdgeListSize() {
		return this.graphEdges.size();
	}
	
	public Language predictLanguage(String name) {
		//This function should be able to iterate through an input string to see if there is
		//any relevant node for each trigram. If there is, that node's weight for each language
		//is added to the PointAccumulator. 
		
		Language predictLanguage = Language.ENGLISH;
		int nameLength = name.length();
		if (nameLength == 0) return predictLanguage;
		
		int numTrigrams = 0;
		String pastTrigram = "";
		String nextTrigram = "";
		
		if (nameLength <= 3) {
			numTrigrams = 1;
			pastTrigram = name;
		}
		else {
			numTrigrams = nameLength - 2;
			nextTrigram = GraphFieldTemplate.makeNextTrigram(0, name);
			pastTrigram = name.substring(0, 3);
		}
		
		PathPointsAccumulator langPointAccumulator = new PathPointsAccumulator(pastTrigram, nextTrigram);
		
		System.out.println();
		System.out.println("TESTING FUNCTIONALITY OF 'predictLanguage' FUNCTION");
		
		for (int i = 0; i < numTrigrams; i++) {
			//For each trigram in the name, check whether or not it already exists in the graph
			//Then increment the value for each language where relevant
			
			//Following that, do the same with the edge between adjacent trigrams
			String currentTrigram = langPointAccumulator.getCurrentTrigram();
			boolean nodeTrigramExists = this.nodeExists(currentTrigram); //Checks to see if the 
																		 //node already exists
			
			System.out.println("\nThe current trigram is " + currentTrigram);
			System.out.println("Does this trigram exist in the graph? " + nodeTrigramExists);
			
			if (nodeTrigramExists) {
				int nodeIndex = this.getNodeIndex(currentTrigram);
				
				Node currentNode = this.getNode(nodeIndex);
				
				for (Language lang : Language.values()) {
					System.out.println("The ");
					langPointAccumulator.increaseLangVal(lang, currentNode.getLangVal(lang));
				}
				
			}
			
			//Now check that the relative edge exists and process similarly to node
			if (nextTrigram != currentTrigram && nextTrigram != "") {
				
				boolean edgeTrigramExists = this.edgeExists(currentTrigram, nextTrigram);
				
				if (edgeTrigramExists) {
					
					int edgeIndex = this.getEdgeIndex(currentTrigram, nextTrigram);
					
					Edge currentEdge = this.getEdge(edgeIndex);
					
					for (Language lang : Language.values())
						langPointAccumulator.increaseLangVal(lang, currentEdge.getLangVal(lang));
					
				}	
				
			}
			
			langPointAccumulator.moveForward();
			if (i < numTrigrams-1) {
				nextTrigram = GraphFieldTemplate.makeNextTrigram(i+1, name);
				langPointAccumulator.setNextTrigram(nextTrigram);
			}
		}
		
		predictLanguage = langPointAccumulator.getMostLikelyLanguage();
		
		return predictLanguage;
			
		
	}
	
	public void parseName(String name, Language lang) {
		//This function will take a name and a language as inputs, and add them to the graph
		
		if (name.length() == 0) return;
		
		int numOfTrigrams = 0;
		if (name.length() <= 3) numOfTrigrams = 1; //Trigram is whole name
		else numOfTrigrams = name.length() - 2;
		
		String trigram = "";
		
		if (numOfTrigrams == 1) {
			trigram = name;
			this.addNode(lang, trigram);
		}
		
		else {
//			Node lastNode = this.graphNodes.get(this.getNodeIndex(trigram));
			
			String lastTrigram = name.substring(0, 3); //Set lastTrigram to the first trigram
			for (int i = 0; i < numOfTrigrams; i++) {
				trigram = name.substring(i, i+3);
				this.addNode(lang, trigram);
				if (i >= 1) {
//					Edge newEdge = new Edge(lang, lastTrigram, trigram);
					this.addEdge(lang, lastTrigram, trigram);
					lastTrigram = trigram;
				}
				
			}
			
		}
		
		
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
//		GraphLID testGraph = new GraphLID();
//		testGraph.addNode(Language.ENGLISH, "the");
//		
//		System.out.println("Size of node list is " + testGraph.getNodeListSize());
//		
//		Node testNode = testGraph.getNode(testGraph.getNodeListSize()-1);
//		HashMap<Language, Integer> testHashMap = testNode.getAllLangValues();
//		
//		for (Language lang : Language.values()) {
//			int langVal = testHashMap.get(lang);
//			System.out.println("Trigram is 'the'. Lang val for " + lang + " is " + langVal);
//		}
//		
//		
		
//		
//		Node testNode = new Node(Language.RUSSIAN, "eto");
//		System.out.println("testNode has a trigram of " + testNode.getTrigram());
		
//		String name = "Yuuko";
//		String nameSubString = name.substring(0, 3);
//		System.out.println(nameSubString);

		
//		System.out.println("Input name is Yuuko. Yuuko has been input as a JAPANESE name");
		
//		int nodeListSize = testGraph.getNodeListSize();
//		for (int i = 0; i < nodeListSize; i++) {
//			
//			Node tempNode = testGraph.getNode(i);
//			System.out.println("\nNode is " + tempNode.getTrigram());
//			
//			for (Language lang : Language.values()) {
//				System.out.println("For this node, the value of " + lang + " is " + tempNode.getLangVal(lang));
//			}
//			
//		}
		
		
//		nodeListSize = testGraph.getNodeListSize();
//		System.out.println("NODE LIST SIZE IS " + nodeListSize);
		
//		System.out.println("\nThere is a node with the trigram 'Yuu': " + testGraph.nodeExists("Yuu") + "\n");
		
//		System.out.println("Input name is Yuuko. Yuuko has been input as an ENGLISH name");
//		
//		nodeListSize = testGraph.getNodeListSize();
//		for (int i = 0; i < nodeListSize; i++) {
//			
//			Node tempNode = testGraph.getNode(i);
//			System.out.println("\nNode is " + tempNode.getTrigram());
//			
//			for (Language lang : Language.values()) {
//				if (lang == Language.JAPANESE || lang == Language.ENGLISH) System.out.println("For this node, the value of " + lang + " is " + tempNode.getLangVal(lang));
//			}
//			
//		}
//		
////		System.out.println("\nNumber of nodes after parsing the name 'Yuuko' as both ENGLISH and JAPANESE is " + nodeListSize);
//		
		
		GraphLID testGraph = new GraphLID();
		testGraph.parseName("Yuuko", Language.JAPANESE);
//		testGraph.parseName("Yuuko", Language.ENGLISH);
		testGraph.parseName("Casey", Language.IRISH);
		testGraph.parseName("Yumika", Language.JAPANESE);
		testGraph.parseName("Yuu", Language.JAPANESE);		
		testGraph.parseName("Takayama", Language.JAPANESE);
		testGraph.parseName("Takahashi", Language.JAPANESE);
		testGraph.parseName("Takahara", Language.JAPANESE);
		testGraph.parseName("Uehara", Language.JAPANESE);
		testGraph.parseName("Matsuyama", Language.JAPANESE);
		testGraph.parseName("Tretiakova", Language.RUSSIAN);
		testGraph.parseName("Tchaikovsky", Language.RUSSIAN);
		testGraph.parseName("Piotr", Language.RUSSIAN);
		testGraph.parseName("Gagarin", Language.RUSSIAN);
//		testGraph.parseName("Hinako", Language.JAPANESE);
//		testGraph.parseName(name, lang);
		
		int nodeListSize = testGraph.getNodeListSize();
		int edgeListSize = testGraph.getEdgeListSize();
		System.out.println("Number of nodes is now " + nodeListSize);
		System.out.println("Number of edges is now " + edgeListSize);
		
		for (int i = 0; i < nodeListSize; i++) {
			
			Node tempNode = testGraph.getNode(i);
			String nodeTrigram = tempNode.getTrigram();
			System.out.println("\nFor this node, the trigram is " + nodeTrigram);
			
			for (Language lang : Language.values()) {
				int langVal = tempNode.getLangVal(lang);
//				if (lang == Language.ENGLISH || lang == Language.JAPANESE) System.out.println("For the node " + nodeTrigram + " the weight for " + lang + " is " + langVal);
				System.out.println("For the node " + nodeTrigram + " the weight for " + lang + " is " + langVal);
			}
			
		}
		
		System.out.println();
		
		for (int i = 0; i < edgeListSize; i++) {
			
			Edge tempEdge = testGraph.getEdge(i);
			String edgePastTrigram = tempEdge.getPastNodeTrigram();
			String edgeNextTrigram = tempEdge.getNextNodeTrigram();
			
			for (Language lang : Language.values()) {
				int langVal = tempEdge.getLangVal(lang);
//				if (lang.equals(Language.ENGLISH) || lang.equals(Language.JAPANESE)) System.out.println("For the edge from " + edgePastTrigram + " to " + edgeNextTrigram + " the weight of " + lang + " is " + langVal);
				System.out.println("For the edge from " + edgePastTrigram + " to " + edgeNextTrigram + " the weight of " + lang + " is " + langVal);
			}
			System.out.println();
		}
		
		Language kanakoLanguage = testGraph.predictLanguage("Kanako");
		
		System.out.println("The language of origin for the name 'Kanako' was tested");
		System.out.println("'Kanako' is a " + kanakoLanguage + " name");
		
	}

}
