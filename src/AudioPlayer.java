import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;


public class AudioPlayer implements Runnable{
	private final int EXTERNAL_BUFFER_SIZE = 524288;
	
    SourceDataLine auline;
    AudioInputStream audioInputStream;
    String audioFile;
    
	AudioPlayer(String audioFile)
	{
		this.audioFile = audioFile;
	}
	
	public void load()
	{
        File soundFile = new File(audioFile);
        if (!soundFile.exists()) { 
            System.err.println("Wave file not found: " + audioFile);
            return;
        } 
 
        try { 
            audioInputStream = AudioSystem.getAudioInputStream(soundFile);
        } catch (UnsupportedAudioFileException e1) { 
            e1.printStackTrace();
            return;
        } catch (IOException e1) { 
            e1.printStackTrace();
            return;
        }
 
        AudioFormat format = audioInputStream.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
 
        try { 
            auline = (SourceDataLine) AudioSystem.getLine(info);
            auline.open(format);
        } catch (LineUnavailableException e) { 
            e.printStackTrace();
            return;
        } catch (Exception e) { 
            e.printStackTrace();
            return;
        } 
	}
	
	@Override
	public void run() {
        auline.start();
        int nBytesRead = 0;
        byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];
 
        try { 
            while (nBytesRead != -1) { 
                nBytesRead = audioInputStream.read(abData, 0, abData.length);
                if (nBytesRead >= 0) 
                    auline.write(abData, 0, nBytesRead);
            } 
        } catch (IOException e) { 
            e.printStackTrace();
            return;
        }
        finally
        {
          auline.drain();
          auline.close();
        }
	}
}
