package controller;

import javax.sound.midi.MidiUnavailableException;

public class AkordThread extends Thread {
    private volatile boolean running = true;
    private volatile boolean paused = true;
    private final Object pauseLock = new Object();

    private MidiPlayer midiPlayer = new MidiPlayer();
    private int midBroj;
    private int duzina;

    public AkordThread() throws MidiUnavailableException {
        start();
    }


    public void setMidBroj(int midBroj) {
        this.midBroj = midBroj;
    }

    public void setDuzina(int duzina) {
        this.duzina = duzina;
    }

    public int getMidBroj() {
        return midBroj;
    }

    public int getDuzina() {
        return duzina;
    }

    @Override
    public void run() {
        while (running) {
            synchronized (pauseLock) {
                if (!running) {
                    break;
                }
                if (paused) {
                    try {
                        synchronized (pauseLock) {
                            pauseLock.wait();
                        }
                    } catch (InterruptedException ex) {
                        break;
                    }
                    if (!running) {
                        break;
                    }
                }
            }
            try {
                midiPlayer.play(midBroj,duzina);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            paused=true;
        }
    }

    public void stopp() {
        running = false;
        resumee();
    }

    public void pause() {
        paused = true;
    }

    public void resumee() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }
}
