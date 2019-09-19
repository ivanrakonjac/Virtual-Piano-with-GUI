package controller;

import javax.sound.midi.*;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

public class MidiFormater extends Formater{

    private int index = 0;
    ArrayList<MuzickiSimbol> listaMuzickihSimbola;

    public MidiFormater(String fileName, ArrayList<MuzickiSimbol> listaMuzickihSimbola) {
        super(fileName);
        this.listaMuzickihSimbola=listaMuzickihSimbola;
    }

    @Override
    public void format() {
        try {
            //****  Create a new MIDI sequence with 24 ticks per beat  ****
            Sequence s = new Sequence(javax.sound.midi.Sequence.PPQ,24);

            //****  Obtain a MIDI track from the sequence  ****
            Track t = s.createTrack();

            //****  General MIDI sysex -- turn on General MIDI sound set  ****
            byte[] b = {(byte)0xF0, 0x7E, 0x7F, 0x09, 0x01, (byte)0xF7};
            SysexMessage sm = new SysexMessage();
            sm.setMessage(b, 6);
            MidiEvent me = new MidiEvent(sm,(long)0);
            t.add(me);

            //****  set tempo (meta event)  ****
            MetaMessage mt = new MetaMessage();
            byte[] bt = {0x02, (byte)0x00, 0x00};
            mt.setMessage(0x51 ,bt, 3);
            me = new MidiEvent(mt,(long)0);
            t.add(me);

            //****  set track name (meta event)  ****
            mt = new MetaMessage();
            String TrackName = new String("midifile track");
            mt.setMessage(0x03 ,TrackName.getBytes(), TrackName.length());
            me = new MidiEvent(mt,(long)0);
            t.add(me);

            //****  set omni on  ****
            ShortMessage mm = new ShortMessage();
            mm.setMessage(0xB0, 0x7D,0x00);
            me = new MidiEvent(mm,(long)0);
            t.add(me);

            //****  set poly on  ****
            mm = new ShortMessage();
            mm.setMessage(0xB0, 0x7F,0x00);
            me = new MidiEvent(mm,(long)0);
            t.add(me);

            //****  set instrument to Piano  ****
            mm = new ShortMessage();
            mm.setMessage(0xC0, 0x00, 0x00);
            me = new MidiEvent(mm,(long)0);
            t.add(me);

            for(MuzickiSimbol muzSimb : listaMuzickihSimbola) {
                if(muzSimb instanceof Nota) {
                    mm = new ShortMessage();
                    mm.setMessage(0x90, Integer.parseInt(((Nota) muzSimb).getBroj()), 0x60);
                    me = new MidiEvent(mm, (long)index);
                    t.add(me);

                    if(muzSimb.getTrajanje() == Trajanje.CETVRTINA)
                        index += 60;
                    else
                        index += 30;

                    mm = new ShortMessage();
                    mm.setMessage(0x80, Integer.parseInt(((Nota) muzSimb).getBroj()), 0x40);
                    me = new MidiEvent(mm, (long)index);
                    t.add(me);

                    continue;
                }
                if(muzSimb instanceof Pauza) {
                    if(muzSimb.getTrajanje() == Trajanje.CETVRTINA)
                        index += 60;
                    else
                        index += 30;

                    continue;
                }
                if(muzSimb instanceof Akord) {
                    ArrayList<Nota> nizNota = ((Akord) muzSimb).getNizNota();
                    for(Nota nota : nizNota) {
                        mm = new ShortMessage();
                        mm.setMessage(0x90, Integer.parseInt(((Nota) nota).getBroj()), 0x60);
                        me = new MidiEvent(mm, (long)index);
                        t.add(me);
                    }

                    if(muzSimb.getTrajanje() == Trajanje.CETVRTINA)
                        index += 60;
                    else
                        index += 30;

                    for(MuzickiSimbol nota : nizNota) {
                        mm = new ShortMessage();
                        mm.setMessage(0x80, Integer.parseInt(((Nota) nota).getBroj()), 0x40);
                        me = new MidiEvent(mm, (long)index);
                        t.add(me);
                    }
                }
            }

            //****  set end of track (meta event) 19 ticks later  ****
            mt = new MetaMessage();
            byte[] bet = {}; // empty array
            mt.setMessage(0x2F,bet,0);
            me = new MidiEvent(mt, (long)(index + 19));
            t.add(me);

            //****  write the MIDI sequence to a MIDI file  ****
            File f = new File(this.fileName);
            MidiSystem.write(s,1,f);

        } //try
        catch(Exception e) {
            System.out.println("Exception caught " + e.toString());
        } //catch
    }
}
