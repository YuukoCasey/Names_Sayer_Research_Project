package model;

import model.Language;
import java.util.ArrayList;
import java.util.HashMap;

public class PathPointsAccumulator extends AbstractGraph{

	private String name;
//	private HashMap<Language, Integer> languageValues = new HashMap<>();
	private HashMap<Language, Double> languageValues = new HashMap<>();
	
	public PathPointsAccumulator(String name) {
//		this.currentTrigram = currentTrigram;
//		this.nextTrigram = nextTrigram;
		this.name = name;
		this.resetLanguageValues(); 
		this.parseName();
		
	}
	
//	public void incrementLanguageValue(Language lang) {
//		this.increaseLangVal(lang, 1);
//	}
	
	public Language getMostLikelyLanguage() {
		Language returnLang = Language.ENGLISH;
		double langVal = 0;
		for (Language lang : Language.values()) {
			double newLangVal = this.getLangVal(lang);
			if (newLangVal > langVal) {
				langVal = newLangVal;
				returnLang = lang;
			}
		}
		return returnLang;
	}
	
	public void resetLanguageValues() {
		for (Language lang : Language.values()) this.languageValues.put(lang, 0.0);
	}
	
	public void increaseLangVal(Language lang, int incVal, int valSum) {
		
		// TODO: Normalise this function
		
				
		double currentVal = this.languageValues.get(lang);
		currentVal += ((double)incVal / (double)valSum);
		this.languageValues.put(lang, currentVal);
	}
	
	public double getLangVal(Language lang) {
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
