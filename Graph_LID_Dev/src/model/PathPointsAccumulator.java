package model;

import model.Language;
import java.util.ArrayList;
import java.util.HashMap;

public class PathPointsAccumulator extends AbstractGraph{

//	private String currentTrigram, nextTrigram;
	private String name;
//	private ArrayList<Node> nodeList = new ArrayList<>();
//	private ArrayList<Edge> edgeList = new ArrayList<>();
	private HashMap<Language, Integer> languageValues = new HashMap<>();
	
//	public PathPointsAccumulator(String currentTrigram) {
//		this.currentTrigram = currentTrigram;
//		this.nextTrigram = "";
//		this.resetLanguageValues(); 
//	}
	
	public PathPointsAccumulator(String name) {
//		this.currentTrigram = currentTrigram;
//		this.nextTrigram = nextTrigram;
		this.name = name;
		this.resetLanguageValues(); 
		this.parseName();
		
	}
	
//	public String getCurrentTrigram() {
//		return this.currentTrigram;
//	}
//	
//	public void setCurrentTrigram(String trigram) {
//		this.currentTrigram = trigram;
//	}
//	
//	public String getNextTrigram() {
//		return this.nextTrigram;
//	}
//	
//	public void setNextTrigram(String trigram) {
//		this.nextTrigram = trigram;
//	}
	
	public void incrementLanguageValue(Language lang) {
		this.increaseLangVal(lang, 1);
	}
	
	//This function will change the value of 'currentTrigram' to that of 'nextTrigram'
//	public void moveForward() {
//		this.currentTrigram = this.nextTrigram;
//	}
	
	public Language getMostLikelyLanguage() {
		Language returnLang = Language.ENGLISH;
		int langVal = 0;
		for (Language lang : Language.values()) {
			int newLangVal = this.getLangVal(lang);
			if (newLangVal > langVal) {
				langVal = newLangVal;
				returnLang = lang;
			}
		}
		return returnLang;
	}
	
	public void resetLanguageValues() {
		for (Language lang : Language.values()) this.languageValues.put(lang, 0);
	}
	
	public void increaseLangVal(Language lang, int incVal) {
		int currentVal = this.languageValues.get(lang);
		currentVal += incVal;
		this.languageValues.put(lang, currentVal);
	}
	
	public int getLangVal(Language lang) {
		return this.languageValues.get(lang);
	}
	
	public String getName() {
		return this.name;
	}
	
	public void parseName() {
		
		int trigramNum = this.name.length();
		
		if (trigramNum == 0) return;
		
		if (trigramNum <= 3) trigramNum = 1;
		else trigramNum -= 2;
		
		String trigram = "";
		
		if (trigramNum == 1) {
			trigram = this.name;
			this.addNode(trigram);
		}
		
		else {
			
			String lastTrigram = this.name.substring(0, 3);
			
			for (int i = 0; i < trigramNum; i++) {
			
				trigram = this.name.substring(i, i+3);
				this.addNode(trigram);
				
				if (i >= 1) {
					this.addEdge(lastTrigram, trigram);
					lastTrigram = trigram;
				}
				
			}
		}
		
	}
	
	public void addNode(String trigram) {
		
		if (this.nodeList.size() == 0 || !this.nodeExists(trigram)) 
			nodeList.add(new Node(trigram));
		
	}
	
	public void addEdge(String lastTrigram, String nextTrigram) {
		
		if (this.edgeList.size() == 0 || !this.edgeExists(lastTrigram, nextTrigram))
			edgeList.add(new Edge(lastTrigram, nextTrigram));
	
	}
	
	//This function was only made for test purposes
	public ArrayList<Node> getNodeList(){
		return this.nodeList;
	}
	
	//This function was only made for test purposes
	public ArrayList<Edge> getEdgeList(){
		return this.edgeList;
	}
	
}
