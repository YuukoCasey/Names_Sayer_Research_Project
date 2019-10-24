package model;

import java.io.File;
import java.io.IOException;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import model.Language;
import com.sun.speech.freetts.*;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;

/**
 * 
 *	This class is the entry point for dealing with the TTS systems and the audioplayer
 *	It takes the name to be pronounced and the identified language of the name 
 *	and synthesizes the appropriate speech and then creates an audioplayer 
 *	that is built to play that speech as intended (this is to introduce compatibility
 *	with different synthesizers which have different output specifications)
 *	The audioplayer is created anew for each TTS synthesis for this reason.
 *	Synthesizers used are MARYTTS for Maori, and FreeTTS for English
 *
 * * @author Joshua Rosairo
 */
public class TextToSpeech {
	
	// define audioplayer
	private AudioPlayer _audioPlayer;
	
	// TTS synthesizers used
	private MaryInterface _marytts; // Maori
	private com.sun.speech.freetts.Voice _freetts; // English
	
	private Language _nameLang; // language of the name
	private String _name; // ASCII text of the name
	
	private static final String ENGLISHVOICENAME = "kevin16"; // English voice used by FreeTTS
	private static final String MAORIVOICENAME = "akl_mi_pk_voice1-hsmm"; // Maori voice used by MARYTTS
	private static final String SAMOANVOICENAME = "akl_mi_pk_voice1-hsmm"; // Placeholder
	
	private File englishSynthesizedAudio = new File("english.wav"); // file holding english speech synthesized by FreeTTS 
	private String englishSynthesizedAudioFileName = "english"; // file name of " " excluding .wav
    
	/**
	 * Constructor initializes the MaryTTS and FreeTTS synthesizers
	 * and assigns the English voice to FreeTTS
	 */
	public TextToSpeech() {
		try {
			_marytts = new LocalMaryInterface();
	        VoiceManager voiceManager = VoiceManager.getInstance();
	        _freetts = voiceManager.getVoice(ENGLISHVOICENAME);
		} catch (MaryConfigurationException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Takes the name to be said, and calls the synthesizeSpeech method
	 * then creates an audio player to playback the speech that was synthesized
	 * @param text : the name to be said
	 */
	public void speak(String text) {
		_name = text; // initialise name here so it can be used by synthesizeSpeech() method
		
		// synthesize speech and return it as an AudioInputStream so it can be processed by audioplayer
		try (AudioInputStream synthesizedSpeech = synthesizeSpeech()) {
			
			// new audioplayer instance initialised everytime, (this is because the audioplayer
			// specifies the line and resources it will use according to the synthesized speech
			// which is different for each name/lang combination
			_audioPlayer = new AudioPlayer();
			
			// send synthesized speech to audioplayer
			_audioPlayer.setSynthesizedSpeech(synthesizedSpeech);
			
			// process and play the synthesized speech according to user's speaker setup
			_audioPlayer.start();
			_audioPlayer.join(); // thread waits until speech is finished talking
			
			// cleanup any files that were created in during this iteration
			englishSynthesizedAudio.delete();
		} catch (SynthesisException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			_audioPlayer.interrupt(); // ensure only one playback at any time
			e.printStackTrace();
		}
	}
	
	/**
	 * Determines which synthesizer to use according to identified language. Then synthesizes
	 * the name and returns the stream for use by audioplayer
	 * @return the synthesized speech as an AudioInputStream object so the audioplayer can
	 * process it
	 * @throws SynthesisException
	 */
	private AudioInputStream synthesizeSpeech() throws SynthesisException {
			// Choose synthesizer according to language
			switch(_nameLang) {
				// Samoan not implemented as no access to an existing Samoan TTS synthesizer
				case SAMOAN: { 
					_marytts.setVoice(SAMOANVOICENAME);
					return _marytts.generateAudio(_name);
				}
				// Sets the maori voice for MARYTTS to use then synthesizes the Maori pronunciation of the name
				case MAORI: {
					_marytts.setVoice(MAORIVOICENAME);
					return _marytts.generateAudio(_name);
				}
				// Synthesizes English pronunciation of the name
				case ENGLISH: {
					// allocate resources to FreeTTS and prepare for synthesize
			        _freetts.allocate();
			        // Change audioplayer to a filewriter so that the file can be converted into
			        // a format that our AudioPlayer can use
			        SingleFileAudioPlayer fileWriter = new SingleFileAudioPlayer(englishSynthesizedAudioFileName,Type.WAVE);
			        _freetts.setAudioPlayer(fileWriter);
			        
			        // Synthesizes the english speech and saves it as englishSynthesizedAudioFileName with .wav extension
			        _freetts.speak(_name);
			        
			        // close the filewriter so the file gets saved and can be opened by other i/o
			        fileWriter.close();
			        
			        // try to convert the file holding the synthesized english audio into 
			        // a format that our audio player can use
			        try {
						return AudioSystem.getAudioInputStream(englishSynthesizedAudio);
					} catch (UnsupportedAudioFileException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				// Placeholder default for when working on LID for new languages not yet supported here.
				default : return null;
			}
	}
	
	// Getters and Setters for fields that are used outside the class //
	
	/**
	 * Sets the language chosen which determines which synthesizer to use.
	 * @param identifiedLang = the language identified as belonging to the name entered
	 */
	public void setLanguage(Language identifiedLang) {
		this._nameLang = identifiedLang;
	}
	
}
