package model;

import java.io.IOException;

import java.util.Scanner;

import model.GraphLID;
import model.Language;
import dbmanagement.GraphLoader;

import javax.sound.sampled.LineUnavailableException;


public class Main {
	
	/**
	 * The main method from which our application is starting
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		TextToSpeech tts = new TextToSpeech();
		
		
		//Print all the available voices
		
//		tts.getAvailableVoices().stream().forEach(voice -> System.out.println("Voice: " + voice));
		tts.getAvailableVoices().stream().forEach(voice -> System.out.print(""));
		
		
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
		
		while (true) {
			
			System.out.println("Enter a name you wish to hear, or press 'q' to exit:");
			String testName = userIn.nextLine();
			String lowerName = testName.toLowerCase();
			if (lowerName.equals("q")) break;
		 
			// Test code starts here
//			System.out.println("Enter m for Maori or e for English");
//			String userLang = userIn.nextLine();
//			if(userLang.equals("m")) {
//				tts.setVoice("akl_mi_pk_voice1-hsmm"); //set maori voice
//			} else {
//				tts.setVoice("cmu-slt-hsmm"); //set english voice
//			}
			// Test code ends here
			
			
			// Get language of name from LID
			Language userLang = graphLID.predictLanguage(lowerName);
			System.out.println(testName + " is a " + userLang + " name\n");
			
			
			// Set voice according to language
			
			switch(userLang) {
				case SAMOAN: {
					tts.setVoice("akl_mi_pk_voice1-hsmm");
					break;
				}
				case MAORI: {
					tts.setVoice("akl_mi_pk_voice1-hsmm");
					break;
				}
				default: {	
					tts.setVoice("cmu-slt-hsmm"); //set english voice
					break;
				}		
			}
		
		
		
			// TTS output 
			try {
				tts.speak(lowerName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		userIn.close();
		
	}
	
}
