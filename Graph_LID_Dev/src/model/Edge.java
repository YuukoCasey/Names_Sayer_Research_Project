package model;

import java.util.HashMap;

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
	
	public String getPastNodeTrigram() {
		return this.pastNodeTrigram;
	}
	
	public String getNextNodeTrigram() {
		return this.nextNodeTrigram;
	}
	
//	public int getLangVal(Language lang) {
//		return this.langWeights.get(lang);
//	}
	
//	public void incrementLangVal(Language lang) {
//		this.langWeights.put(lang, (this.langWeights.get(lang) + 1));
//	}
	
}
