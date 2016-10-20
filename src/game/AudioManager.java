package game;

import java.io.*;
import javax.sound.sampled.*;

public class AudioManager implements Runnable {
	String fileLocation = "gamejamcowlevelmusic.wav";

	public AudioManager() {
		for(int i = 0; i < Sound.values().length; i++){
			
		}
	}

	private enum Sound {
		mainmenu, level1
	}

	Sound sound;

	public void play(String fileName) {
		Thread t = new Thread(this);
		fileLocation = fileName;
		t.start();
	}

	public void run() {
		playSound(fileLocation);
	}

	AudioFormat audioFormat;

	private void playSound(String fileName) {
		File soundFile = new File(fileName);
		AudioInputStream audioInputStream = null;
		SourceDataLine line = null;
		try {
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);
			audioFormat = audioInputStream.getFormat();
		} catch (Exception e) {
			e.printStackTrace();
		}
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		try {
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(audioFormat);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		line.start();
		int nBytesRead = 0;
		byte[] abData = new byte[128000];
		while (nBytesRead != -1) {
			try {
				nBytesRead = audioInputStream.read(abData, 0, abData.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (nBytesRead >= 0) {
				// int nBytesWritten = line.write(abData, 0, nBytesRead);
				line.write(abData, 0, nBytesRead);
			}
		}

		line.drain();
		line.close();
	}
}
