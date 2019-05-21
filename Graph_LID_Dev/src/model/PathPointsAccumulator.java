package model;

import model.Language;

public class PathPointsAccumulator extends GraphFieldTemplate{

	private String currentTrigram, nextTrigram;
	
	public PathPointsAccumulator(String currentTrigram) {
		this.currentTrigram = currentTrigram;
		this.nextTrigram = "";
		this.resetLanguageValues(); 
	}
	
	public PathPointsAccumulator(String currentTrigram, String nextTrigram) {
		this.currentTrigram = currentTrigram;
		this.nextTrigram = nextTrigram;
		this.resetLanguageValues(); 
	}
	
	public String getCurrentTrigram() {
		return this.currentTrigram;
	}
	
	public void setCurrentTrigram(String trigram) {
		this.currentTrigram = trigram;
	}
	
	public String getNextTrigram() {
		return this.nextTrigram;
	}
	
	public void setNextTrigram(String trigram) {
		this.nextTrigram = trigram;
	}
	
	public void incrementLanguageValue(Language lang) {
		this.increaseLangVal(lang, 1);
	}
	
	//This function will change the value of 'currentTrigram' to that of 'nextTrigram'
	public void moveForward() {
		this.currentTrigram = this.nextTrigram;
	}
	
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
	
}
