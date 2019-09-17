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
		tts.getAvailableVoices().stream().forEach(voice -> System.out.println("Voice: " + voice));
		
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
		
			// Get language of name from LID
			Language userLang = graphLID.predictLanguage(lowerName);
			System.out.println(testName + " is a " + userLang + " name\n");

			// Set voice according to language
			switch(userLang) {
				case SAMOAN: {
					break;
				}
				case MAORI: {	
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
