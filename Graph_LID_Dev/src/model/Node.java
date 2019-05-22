package model;

import java.util.HashMap;
import model.Language;

public class Node extends GraphFieldTemplate{

//	private HashMap<Language, Integer> langWeights = new HashMap<>();
	private String trigram;
	
	public Node(Language language, String trigram) {
		
		for (Language lang : Language.values()) {
			
			if (lang.equals(language)) langWeights.put(lang, 1);
			
			else langWeights.put(lang, 0);	
		}
		this.setTrigram(trigram);
		
	}
	
	public Node(String trigram) {
		this.setTrigram(trigram);
		for (Language lang : Language.values()) langWeights.put(lang, 0);
	}
	
	public void setTrigram(String trigram) {
		if (trigram.length() == 3) this.trigram = trigram;
	}
	
	public int getLangVal(Language lang) {
		return langWeights.get(lang);
	}
	
	public HashMap<Language, Integer> getAllLangValues(){
		return this.langWeights;
	}
	
	public String getTrigram() {
		return this.trigram;
	}
	
//	public void incrementLangVal(Language lang) {
//		langWeights.put(lang, (langWeights.get(lang) + 1));
//	}
		
}