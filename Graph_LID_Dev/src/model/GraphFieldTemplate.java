package model;

import model.Language;
import java.util.HashMap;

public abstract class GraphFieldTemplate {

	protected HashMap<Language, Integer> langWeights = new HashMap<>();
//	private String trigram;
//	private String name;
	
//	public void setName(String name) {
//		this.name = name;
//	}
//	
//	public String getName() {
//		return this.name;
//	}
	
	
//	public void setTrigram(String trigram) {
//		while(trigram.length() < 3) trigram += "_";
//		if (trigram.length() == 3) this.trigram = trigram;
//	}
//	
//	public String getTrigram() {
//		return this.trigram;
//	}
	
	public void increaseLangVal(Language lang, int incVal) {
		langWeights.put(lang, (langWeights.get(lang) + incVal));
	}
	
	public void resetLanguageValues() {
		for (Language lang : Language.values()) {
			langWeights.put(lang, 0);
		}
	}
	
	public int getLangVal(Language lang) {
		return langWeights.get(lang);
	}
	
	public static boolean canMakeNextTrigram(int index, String name) {
		if (index >= name.length()-3) return false;
		return true;
	}
	
	public static String makeNextTrigram(int index, String name) {
		if (!canMakeNextTrigram(index, name)) return "";
		return name.substring(index, index+3);
	}
	
	public void close() {
		for (Language lang : Language.values()) {
			this.langWeights.remove(lang);
		}
	}
	
}
