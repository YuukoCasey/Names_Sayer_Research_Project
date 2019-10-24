package model;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;

import marytts.util.data.audio.MonoAudioInputStream;
import marytts.util.data.audio.StereoAudioInputStream;

/**
 * Audio player that is made per playing of speech synthesis.
 * We make it on runtime because the line and resources used are specific to the 
 * format and size of the synthesized speech to be played. So
 * we only use the resources we need and release it upon use. This 
 * is done to reduce chances of memory leaks.
 * 
 * @author : Joshua Rosairo
 */
public class AudioPlayer extends Thread {
	
	// speech sent from TTS synthesizer in byte stream format
	private AudioInputStream _synthesizedSpeech;
	
	/**
	 * Constructor
	 */
	public AudioPlayer() {
	}
	
	/**
	 * Set what synthesized speech to be said by audioplayer
	 * @param synthesizedSpeech : speech sent from TTS synthesizer in byte stream format
	 */
	public void setSynthesizedSpeech(AudioInputStream synthesizedSpeech) {
		this._synthesizedSpeech = synthesizedSpeech;
	}
	/**
	 * Getter method for what speech the audioplayer will play
	 * @return
	 */
	public AudioInputStream getSynthesizedSpeech() {
		return this._synthesizedSpeech;
	}
	
	/**
	 * method run in user thread that prepares speech for playback, sets up
	 * audio player then plays the speech
	 */
	@Override
	public void run() {

		// get the audio format of the synthesized speech.
		AudioFormat speechFormat = _synthesizedSpeech.getFormat();
		
		// check what channels the audio is using, we do this because different synthesizers
		// may have format variations in their output, so we want to ensure audio
		// is played back correctly regardless of the synthesizer 
		
		// Note: the audioplayer plays AudioInputStream objects which are a part of
		// the javax package. But to distinguish between channels, MARYTTS extends said objects
		// with the StereoAudioInputStream and MonoAudioInputStream objects. So we are using
		// MARYTTS objects to play any and all synthesizer's audio.
		
		switch(speechFormat.getChannels()) {
		case 1: // if speech is single channel audio, we 'convert' it to stereo, to play mono audio through both channels
			_synthesizedSpeech = new StereoAudioInputStream(_synthesizedSpeech, 0); // '0' param means mono audio
			speechFormat = _synthesizedSpeech.getFormat(); //our byte stream changed so must redefine format
			break;
		case 2: // if its dual channel audio we play it as mono, sending same audio to left and right channels
			// this is to ensure we don't get problems where the audio only plays through a single channel
			// despite 2 channels being available
			_synthesizedSpeech = new MonoAudioInputStream(_synthesizedSpeech);
			speechFormat = _synthesizedSpeech.getFormat(); //our byte stream changed so must redefine format
			break;
		default: 
			// at the moment only stereo and mono audio is supported. I.e. no surround sound 
			System.out.println("Synthesized audio format has too many channels and cannot be played");
			break;
		}
		// using SourceDataLine as we will be writing the speech to this line to be sent to mixer
		SourceDataLine line = null; 
		
		try {
			// defining the class specifications of a line that can play our audio format, so that we can 
			// create this line from the javax audio system.
			DataLine.Info lineSpecifications = new DataLine.Info(SourceDataLine.class, speechFormat); 
			// create a line capable of playing the synthesized audio
			line = (SourceDataLine) AudioSystem.getLine(lineSpecifications); 
			// acquire resources incl. buffer needed to operate on the synthesized audios' format
			line.open(speechFormat); 
			// start line so audio can now be sent to i/o
			line.start();
			// set gain to default of 2.0 and cast it to accepted type that we can set gain control to
			((FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN)).setValue((float)2.0f);
			
		} catch (Exception e) {
			// clear buffers, release other resources to reduce possibility of memory leaks
			// (of keeping unplayable byte stream info queued up)
			line.drain(); 
			line.close(); 
			e.printStackTrace();
			return;
		}
		

		int audioStreamIndex = 0;
		// Make byte array and fill array with bytes from synthesized speech
		// to be sent to line to be played through speakers
		// can only send byte arrays to audio player so we set array to maximum size thats a multiple of 4 and 6 
		// so that we only have to go through the byte stream as few times as possible and supporting 16bit and 24bit stereo
		byte[] playableSpeech = new byte[65532]; 
		
		// iterate through byte stream filling byte array, outputting array as audio
		// then continuing from last read point in stream until entire stream
		// has been converted to array form and played.
		while (( audioStreamIndex != -1 )) {
			try {
				// turn synthesized speech into a playable format
				// whilst keeping track of what has been read in in byte stream
				audioStreamIndex = _synthesizedSpeech.read(playableSpeech, 0, playableSpeech.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// play audio if there is legitimate audio left to play
			if (audioStreamIndex >= 0) {
				// play maximum amount of audio we can at once and making next audio continue off the end of that
				line.write(playableSpeech, 0, audioStreamIndex);
			}
		}
		// clear buffers, release other resources to reduce possibility of memory leaks
		// (of keeping unplayable byte stream info queued up)
		line.drain(); 
		line.close(); 
	}
	
}
