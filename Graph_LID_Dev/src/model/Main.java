package model;


import java.util.Scanner;

public class Main {
	enum Language 
	{ 
	    MAORI, SAMOAN, ENGLISH; 
	} 
	/**
	 * The main method from which our application is starting
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Placeholder for UI of getting name
		
		Scanner userIn = new Scanner(System.in);
		
		// initialize TTS system
		TextToSpeech tts = new TextToSpeech();
		while (true) { 
		    System.out.flush(); 
			System.out.println("Enter a name you wish to hear, or press 'q' to exit:");
			String testName = userIn.nextLine();
			String lowerName = testName.toLowerCase();
			if (lowerName.equals("q")) break;
		 		
			// LID REMOVED SIMPLY ENTER THE LANGUAGE
			System.out.println("Define name's language, enter 'm' for Maori, 'e' for English, 's' for samoan or press 'q' to exit:");
			String testLang = userIn.nextLine().toLowerCase();
			if (testLang.equals("q")) break;
			Language userLang;
			switch(testLang) {
			case "m": 
				userLang = Language.MAORI;
				break;
			case "s": 
				userLang = Language.SAMOAN;
				break;
			default: 
				userLang = Language.ENGLISH;
				break;
			}
			System.out.println(testName + " is a " + userLang + " name\n");
			
			// Send language to TTS
			tts.setLanguage(userLang);
			// Synthesize and playback name
			tts.speak(lowerName);
		
		
		

		}
		userIn.close();
		
	}
	
}
