package model;

import java.io.IOException;


import java.util.Collection;

import javax.sound.sampled.*;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;


import marytts.LocalMaryInterface;
import marytts.MaryInterface;

import marytts.exceptions.SynthesisException;
import marytts.modules.synthesis.Voice;

/**
 * 
 * @author josh
 *	This is a single class called TextToSpeech,  it holds the interface adapter for MaryTTS as well
 *	the audioplayer. Its methods primarily take speech, send it to the adapter and then play the resultant
 *	audio using javax.
 */

public class TextToSpeech {
	
	private MaryInterface marytts;
	
	/**
	 * Constructor initialises the interface adapter for mary
	 * 
	 */
	public TextToSpeech() {
		try {
			marytts = new LocalMaryInterface();		
		} catch (Exception e) {
			;
		}
	}
	
	public void speak(String text) throws IOException, 
	   LineUnavailableException, InterruptedException {
		
		class AudioListener implements LineListener {
		    private boolean done = false;
		    @Override public synchronized void update(LineEvent event) {
		      Type eventType = event.getType();
		      if (eventType == Type.STOP || eventType == Type.CLOSE) {
		        done = true;
		        notifyAll();
		      }
		    }
		    public synchronized void waitUntilDone() throws InterruptedException {
		      while (!done) { wait(); }
		    }
		  }
		
		AudioInputStream audio = null;
		try {
			audio = marytts.generateAudio(text);
		} catch (SynthesisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AudioListener listener = new AudioListener();
		
			try {
				Clip clip = AudioSystem.getClip();
				clip.addLineListener(listener);
				clip.open(audio);
			    try {
			    	clip.start();
			    	listener.waitUntilDone();
			    } finally {
			    	clip.close();
			    }
			} finally {
			    audio.close();
			}
	}
	
	//Getters
	
	/**
	 * Gets all voices installed for MaryTTS
	 * 
	 * @return the available voices for MaryTTS as a collection of strings
	 */
	public Collection<Voice> getAvailableVoices() {
		return Voice.getAvailableVoices();
	}
	
	/**
	 * @return the marytts interface (used for changing marytts settings such as voice etc.)
	 */
	public MaryInterface getMarytts() {
		return marytts;
	}
	
	
	//Setters
	
	/**
	 * Change the voice
	 * 
	 * @param String voice: the string representation of chosen voice for Mary TTS
	 */
	public void setVoice(String voice) {
		marytts.setVoice(voice);
	}
	
}
