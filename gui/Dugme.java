package gui;

import controller.MidiPlayer;
import controller.Nota;
import controller.Snimac;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Dugme extends Thread{
    private Button button;

    private boolean bool; //za sviranje
    private boolean crna;

    private long start;
    private long end;

    private int midiNumber;
    private MidiPlayer midiPlayer;

    private boolean keyPressed;
    private boolean keyRealesed;

    private volatile boolean running = true;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();

    private String tasterIme;
    private String notaIme;
    private String notaTasterIme;

    private Snimac snimac;


    public Dugme(Button button, int midiNumber,boolean crna,MidiPlayer midiPlayer,String notaIme,String tasterIme,Snimac snimac){
        this.button=button;
        this.bool=true;
        this.crna = crna;
        this.start = 0;
        this.end = 0;
        this.midiNumber=midiNumber;
        this.midiPlayer=midiPlayer;

        keyRealesed=false;
        keyPressed=false;

        if(crna) this.notaIme ="" + notaIme.charAt(0) + notaIme.charAt(1);
        else this.notaIme = notaIme;
        this.tasterIme = tasterIme;
        this.notaTasterIme = notaIme + " " + tasterIme;

        this.snimac=snimac;

        start();
        pause();
    }

    public Button getButton() {
        return button;
    }

    public synchronized void setKeyPressed(boolean keyPressed) {
        System.out.println("setKeyPressed " + tasterIme);
        this.keyPressed = keyPressed;
        resumee();
    }

    public synchronized void setKeyRealesed(boolean keyRealesed) {
        this.keyRealesed = keyRealesed;
    }

    public synchronized void keyPressed(){
        if(bool){
            button.setBackground(Color.red);
            start = System.nanoTime();
            bool=false;
        }
        else{
            button.setBackground(Color.red);
            //smidiPlayer.play(midiNumber);
        }

        keyPressed=false;

        System.out.println(tasterIme + " typed");
    }

    public synchronized void keyRealesed(){
        if(crna) button.setBackground(Color.black);
        else button.setBackground(Color.white);

        end = System.nanoTime();
        bool=true;

        long cnt = TimeUnit.NANOSECONDS.toMillis(end-start);
        if(cnt<350) cnt = 350;

        try {
            midiPlayer.play(midiNumber,cnt);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(snimac!=null){
            if(snimac.isUkljucen()){
                snimac.dodaj(cnt,System.nanoTime(),new Nota(tasterIme,notaIme,""+midiNumber,crna));
            }
        }

        keyRealesed=false;
        pause();

        System.out.println(tasterIme + " realesed " + cnt);
    }

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
            if(keyPressed == true) keyPressed();
            if(keyRealesed == true) keyRealesed();
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

    public void activeNotaIme(){
        button.setLabel(notaIme);
    }
    public void activeTasterIme(){
        button.setLabel(tasterIme);
    }
    public void activeNotaTasterIme(){
        button.setLabel(notaTasterIme);
    }

    public void deactiveIme(){
        button.setLabel("");
    }
}
