package model;


import java.util.Scanner;

import model.GraphLID;
import model.Language;
import dbmanagement.GraphLoader;


public class Main {
    
	/**
	 * The main method from which our application is starting
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		GraphLoader gl = new GraphLoader();
		GraphLID graphLID = new GraphLID();
		
		try {
			gl.makeConnection();
			graphLID.setNodeList(gl.loadNodes());
			graphLID.setEdgeList(gl.loadEdges());
			gl.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
		 		
			// Get language of name from LID
			Language userLang = graphLID.predictLanguage(lowerName);
			System.out.println(testName + " is a " + userLang + " name\n");
			
			// Send language to TTS
			tts.setLanguage(userLang);
			// Synthesize and playback name
			tts.speak(lowerName);
		
		
		

		}
		userIn.close();
		
	}
	
}
