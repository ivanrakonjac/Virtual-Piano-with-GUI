package gui;

import controller.CompositionDataLoader;
import controller.MidiPlayer;
import controller.NotesDataLoader;

import javax.sound.midi.MidiUnavailableException;
import java.io.FileNotFoundException;

public class App {

    public static void main(String[] args) throws FileNotFoundException, MidiUnavailableException {

        NotesDataLoader ndl = new NotesDataLoader();
        ndl.ucitavanje();

        MidiPlayer midiPlayer = new MidiPlayer(0);

        CompositionDataLoader cdl = new CompositionDataLoader(ndl.getMapaSimbola());

        new MainFrame(ndl,midiPlayer,cdl);
    }

}
