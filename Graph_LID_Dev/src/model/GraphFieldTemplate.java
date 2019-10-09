package model;

import model.Language;
import java.util.HashMap;

public abstract class GraphFieldTemplate {

	protected HashMap<Language, Integer> langWeights = new HashMap<>();
	
	public void increaseLangVal(Language lang, int incVal) {
		langWeights.put(lang, (langWeights.get(lang) + incVal));
	}
	
	public void resetLanguageValues() {
		for (Language lang : Language.values()) {
			langWeights.put(lang, 0);
		}
	}
	
	public void setLanguageValue(int newVal, Language lang) {
		
		langWeights.put(lang, newVal);
		
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
			this.langWeights.put(lang, null);
		}
		this.langWeights.clear();
	}
	
}