package model;

import model.Language;
import java.util.HashMap;

public abstract class GraphFieldTemplate {

	protected HashMap<Language, Integer> langWeights = new HashMap<>();
	
	public void incrementLangVal(Language lang) {
		langWeights.put(lang, (langWeights.get(lang) + 1));
	}
	
	public int getLangVal(Language lang) {
		return langWeights.get(lang);
	}
	
}
