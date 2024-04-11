import java.io.*;
import java.util.zip.Deflater;

import javax.swing.JOptionPane;

import javax.sound.sampled.*;

public class ReadAudio {

	public ReadAudio(File file, String waveformFilename, String originalName) throws UnsupportedAudioFileException, IOException {
		
		
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
		AudioFormat format = audioInputStream.getFormat();	
		
		long numSamples = ((audioInputStream.getFrameLength() * format.getFrameSize() * 8) / format.getSampleSizeInBits());
		
		double[] samples = new double[ (int)numSamples ];
		
		double[] leftSamples = new double[ (int)numSamples / format.getChannels()];
		double[] rightSamples = new double[ (int)numSamples / format.getChannels()];
		
		double[] midSamples = new double[ (int)numSamples / format.getChannels()];
		double[] sideSamples = new double[ (int)numSamples / format.getChannels()];
		
		long numBytes = numSamples * (format.getSampleSizeInBits() / 8);
		
		byte[] inBuffer = new byte[(int)numBytes];
		
		audioInputStream.read(inBuffer, 0, inBuffer.length);
		
		samples = decodeBytes(inBuffer, format , samples.length);
		
		
		if(format.getChannels() > 1) {
			for (int i = 0; i < leftSamples.length; i++) {
	            leftSamples[i] = samples[2*i];
	            rightSamples[i] = samples[2*i+1];
	        }  
			
			leftSamples = linearPrediction(format, leftSamples);
			rightSamples = linearPrediction(format, rightSamples);
			
			midSamples = channelCouplingMid(format, leftSamples, rightSamples);
			sideSamples = channelCouplingSide(format, leftSamples, rightSamples);
			
			for (int i = 0; i < leftSamples.length; i++) {
				samples[2*i] = midSamples[i];
				samples[2*i+1] = sideSamples[i];
	        }  
		}
		
		else {
			samples = linearPrediction(format, samples);
			//No need for channel coupling since only 1 channel ( I am assuming that is how this works lol)
		}
		
		String zipMe = "";
		for(int i = 0; i < samples.length; i++) {
			zipMe += samples[i] + ", ";
		}
		
		Deflater zipper = new Deflater();
		zipper.setInput(zipMe.getBytes("UTF-8"));
		zipper.finish();
		
		byte output[] = new byte[(int) numBytes*2];
		int size = zipper.deflate(output);
		
		String msg = "";
		
		msg += "Number of channels is: " + format.getChannels() + "\n";
		msg += "Original Number of Bytes: " + (int) numBytes + "\n";
        msg += "Compressed Number of Bytes:  " + size + "\n";
        
        double compressionRatio = numBytes/(double) size;
        msg += "Compression Ratio: " + compressionRatio + "\n";
        
        PrintWriter writer = new PrintWriter("compressedWav" + Global.audioCount + ".txt", "UTF-8");
        Global.audioCount++;
        writer.print(new String(output));
        writer.close();
        
        popupMessage(msg, "Compression Results");
		
	}
	
	public static void popupMessage(String msg, String title)
    {
        JOptionPane.showMessageDialog(null, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }
	
	public double[] channelCouplingMid(AudioFormat format, double[] inputSamplesLeft, double[] inputSamplesRight) { 
		double[] audioSamples = new double[ inputSamplesLeft.length ];
		
		for(int i = 0; i < inputSamplesLeft.length; i++) {
			audioSamples[i] = (inputSamplesLeft[i] + inputSamplesRight[i])/2;
		}
		
		return audioSamples;
	}
	
	public double[] channelCouplingSide(AudioFormat format, double[] inputSamplesLeft, double[] inputSamplesRight) { 
		double[] audioSamples = new double[ inputSamplesLeft.length ];
		
		for(int i = 0; i < inputSamplesLeft.length; i++) {
			audioSamples[i] = (inputSamplesLeft[i] - inputSamplesRight[i])/2;
		}
		
		return audioSamples;
	}
	
	
	
	//From what I understood from prof is that for the linear predicitve we only have to 
	// get the difference of previous value with current value then store that in current value
	public double[] linearPrediction(AudioFormat format, double[] inputSamples) { 
		double[] audioSamples = new double[ inputSamples.length ];
		
		audioSamples[0] = inputSamples[0];
		
		//Starting from 1 since if we start from 0 then it'll fail cuz no prev sample to get diff
		for(int i = 1; i < inputSamples.length; i++) {
			audioSamples[i] = inputSamples[i] - inputSamples[i-1];
		}
		
		return audioSamples;
	}
	
	
    public double[] decodeBytes(byte[] audioBytes, AudioFormat format, long numSamples) {
    	
    	double[] audioSamples = new double[ (int)numSamples ];
    	
        int sampleSizeInBytes = format.getSampleSizeInBits() / 8;
        
        int[] sampleBytes = new int[sampleSizeInBytes];
        
        int k = 0;
        
        for (int i = 0; i < audioSamples.length; i++) {
            
            // Little Endian cuz it's .wav files
            for (int j = sampleSizeInBytes - 1; j >= 0; j--) {
                sampleBytes[j] = audioBytes[k++];
                if (sampleBytes[j] != 0)
                    j = j + 0;
            }
            
            int ival = 0;
            for (int j = 0; j < sampleSizeInBytes; j++) {
                ival += sampleBytes[j];
                if (j < sampleSizeInBytes - 1) ival <<= 8;
            }
            
            double ratio = Math.pow(2., format.getSampleSizeInBits() - 1);
            double val = ((double) ival) / ratio;
            audioSamples[i] = val;
        }
        
        return audioSamples;
    }
	
    
	

	
}
