package main.java;

import javax.sound.sampled.*;

public class SoundBarApi {

    SoundBarApi() {
        captureAudio();
    }

    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    private boolean shouldStopCapturingAudio = false;

    public static void main(String[] args) {
        new SoundBarApi();

    }

    public void start(){
        if(shouldStopCapturingAudio) {
            resume();
            return;
        }
        captureAudio();
    }

    public void stop(){
        shouldStopCapturingAudio = true;
    }

    public void resume(){
        if(!shouldStopCapturingAudio) return;
        shouldStopCapturingAudio = false;
        captureAudio();
    }

    public void close(){
      try {
        shouldStopCapturingAudio = true;
        targetDataLine.stop();
        targetDataLine.flush();
        targetDataLine.close();  
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    // Capture data from microphone in a thread
    private void captureAudio() {
        try {
            audioFormat = new AudioFormat(44100.0F, 8, 1, true, false);
            DataLine.Info dataLineInfo = new DataLine.Info(
                    TargetDataLine.class,
                    audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);

            new CaptureThread().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Inner class to capture data from microphone
    // and find the peak frequency for each chunk.
    class CaptureThread extends Thread {
        public void run() {

            try {
                targetDataLine.open(audioFormat);

                int numBytesRead;
                byte[] data = new byte[targetDataLine.getBufferSize() / 5];

                // Begin audio capture.
                targetDataLine.start();

                while (!shouldStopCapturingAudio) {
                    numBytesRead = targetDataLine.read(data, 0, data.length);
                    System.out.println("NUMBER OF BYTES READ -> "+ numBytesRead);
                    if (numBytesRead > 0) {
                        System.out.println("PEAK -> " + max(data));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

   private byte max(byte[] arr) {
        byte max = -127;

        for (byte b : arr) {
            if (b > max) {
                max = b;
            }
        }

        return max;
    }
}
