package model;

import java.util.HashMap;

//import java.util.HashMap;

public class Edge extends GraphFieldTemplate{

	private String pastNodeTrigram;
	private String nextNodeTrigram;
	//private HashMap<Language, Integer> langWeights = new HashMap<>();
	
	public Edge(Language language, String pastNodeTrigram, String nextNodeTrigram) {
		this.pastNodeTrigram = pastNodeTrigram;
		this.nextNodeTrigram = nextNodeTrigram;
		for (Language lang : Language.values()) {
			
			if (lang.equals(language)) langWeights.put(lang, 1);
			else langWeights.put(lang, 0);
			
		}
		
	}
	
	public Edge(String pastNodeTrigram, String nextNodeTrigram) {
		this.pastNodeTrigram = pastNodeTrigram;
		this.nextNodeTrigram = nextNodeTrigram;
		for (Language lang : Language.values()) langWeights.put(lang, 0);
	}
	
	public Edge(String pastTrigram, String nextTrigram, HashMap<Language, Integer> langValues) {
		this.pastNodeTrigram = pastTrigram;
		this.nextNodeTrigram = nextTrigram;
		for (Language lang : Language.values())
			this.langWeights.put(lang, langValues.get(lang));
	}
	
	public String getPastNodeTrigram() {
		return this.pastNodeTrigram;
	}
	
	public String getNextNodeTrigram() {
		return this.nextNodeTrigram;
	}
	
	public boolean hasSameNames(Edge edge) {
		if (edge.getPastNodeTrigram().equals(this.getPastNodeTrigram()) 
				&& edge.getNextNodeTrigram().equals(this.getNextNodeTrigram())) 
			return true;
		return false;
	}
	
	public int getLanguageValue(Language lang) {
		return this.langWeights.get(lang);
	}
	
	
}
