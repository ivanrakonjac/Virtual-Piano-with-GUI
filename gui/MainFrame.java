package gui;

import controller.*;

import javax.sound.midi.MidiUnavailableException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainFrame extends JFrame implements KeyListener {

    private JPanel panel1; //za klavir
    private JPanel panel2; //za ostalo

    private JPanel panel3;
    private JPanel panel4;

    JPanel panelNote = new JPanel(null);
    JPanel panelCrte = new JPanel(null);

    private JCheckBox prikaziTastere;
    private JCheckBox prikaziNote;
    private JButton svirajAutomatski;
    private JButton pauzirajAutomatskoSviranje;
    private JButton nastaviAutomatskoSviranje;
    private JButton snimaj;
    private JButton zavrsiSnimanje;

    private Dugme[] beleDirke;
    private Dugme[] crneDirke;

    private Map<Character,Dugme> mapaDirki;

    private NotesDataLoader ndl;
    private CompositionDataLoader cdl;
    private MidiPlayer midiPlayer;
    private Player player;
    private Snimac snimac=new Snimac();
    private IspisNota ispisNota = new IspisNota(panelNote);

    private MuzickiSimbol prviZaIspis;


    public MainFrame(NotesDataLoader ndl, MidiPlayer midiPlayer,CompositionDataLoader cdl) throws MidiUnavailableException {
        super("Test");
        setSize(1100, 700);
        setLayout(new GridLayout(3,1));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        setVisible(true);

        this.ndl = ndl;
        this.cdl = cdl;
        this.midiPlayer=midiPlayer;

        mapaDirki = new HashMap<>();

        setJMenuBar(createMenuBar());

        //Klavir panel
        panel1 = new JPanel();
        panel1.setFocusable(false);
        panel1.setLayout(null);
        panel1.setBorder(BorderFactory.createEmptyBorder(20,20,10,0));

        //Gornji panel
        panel2 = new JPanel();
        panel2.setFocusable(false);
        panel2.setLayout(new GridLayout(1,1));

        //Panel za dugmad i checkboxove
        panel3 = new JPanel();
        panel3.setFocusable(false);
        panel3.setLayout(new GridLayout(1,3));
        panel3.setBorder(new EmptyBorder(10,10,10,10));
        setPanel3();

        panel4 = new JPanel();
        panel4.setLayout(new GridLayout(2,1));
        panel4.setFocusable(false);
        panel4.setVisible(true);
        dodajElementeZaIscrtavanje();

        add(panel4);
        panel2.add(panel3);

        //Postavljanje crnih dirki
        crneDirke = new Dugme[25];

        int pomeraj = 30;
        for(int i=0;i<25;i++){

            Nota nota = ndl.getNotaFromMapaSimbola("" + ndl.getTasterCrna(i));

            crneDirke[i] = new Dugme(new Button(),Integer.parseInt(nota.getBroj()),true,midiPlayer,nota.getOpis(),nota.getTaster(),snimac);
            crneDirke[i].getButton().setBackground(Color.black);
            crneDirke[i].getButton().setForeground(Color.white);
            crneDirke[i].getButton().setBounds(pomeraj,10,20,100);
            crneDirke[i].getButton().setFocusable(false);

            dodajMouseListener(crneDirke[i].getButton(),nota.getTaster().charAt(0));

            panel1.add(crneDirke[i].getButton());
            mapaDirki.put(ndl.getTasterCrna(i),crneDirke[i]);

            if(i==1 || i==4 || i==6 || i==9 || i==11 || i==14 || i==16 || i==19 || i==21){
                pomeraj+=60;
            }
            else{
                pomeraj+=30;
            }
        }


        //Postavljanje belih dirki
        beleDirke = new Dugme[35];

        pomeraj = 10; //10 da ne bi bilo do ivice
        for(int i=0;i<35;i++){

            Nota nota = ndl.getNotaFromMapaSimbola("" + ndl.getTasterBela(i));

            beleDirke[i] = new Dugme(new Button(),Integer.parseInt(nota.getBroj()),false,midiPlayer,nota.getOpis(),nota.getTaster(),snimac);
            beleDirke[i].getButton().setBackground(Color.white);
            beleDirke[i].getButton().setForeground(Color.black);
            beleDirke[i].getButton().setBounds(pomeraj,10,30,250);
            beleDirke[i].getButton().setFocusable(false);

            dodajMouseListener(beleDirke[i].getButton(),nota.getTaster().charAt(0));

            panel1.add(beleDirke[i].getButton());
            mapaDirki.put(ndl.getTasterBela(i),beleDirke[i]);

            pomeraj+=30;
        }

        add(panel2);
        add(panel1);
        revalidate();
    }

    private JMenuBar createMenuBar(){
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");

        JMenuItem novaKompozicija = new JMenuItem("Ucitaj novu kompoziciju");
        JMenu exportData = new JMenu("Export data");

        fileMenu.add(novaKompozicija);
        fileMenu.add(exportData);

        novaKompozicija.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                jfc.setDialogTitle("Izaberi fajl za ucitavanje kompozicije");

                int returnValue = jfc.showDialog(null, "Izaberi");
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    System.out.println(jfc.getSelectedFile().getPath());
                    cdl.setFilePath(jfc.getSelectedFile().getPath());

                    try {
                        cdl.ucitavanje();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }

                if(cdl.isUcitana()) {
                    System.out.println(Akord.getMaxDuzinaAkorda());
                    try {
                    if(player!=null) player.stopp();

                    //Iscrtavanje
                    ispisNota.setListaMuzickihSimbola(cdl.getListaMuzickihSimbola());
                    ispisNota.setRedniBrZaIspis(0);
                    ispisNota.obrada();

                    player = new Player(ispisNota);
                    player.setListaMuzickihSimbola(cdl.getListaMuzickihSimbola());
                    player.setMapaSimbola(cdl.getMapaSimbola());
                    player.setMapaDirki(mapaDirki);
                    player.setAkordThreadsNumber(Akord.getMaxDuzinaAkorda());
                    } catch (MidiUnavailableException e) {
                        e.printStackTrace();
                    }
                }else {
                    System.out.println("Ucitaj kompoziciju!");
                }


            }
        });

        JMenuItem exportMidi = new JMenuItem("Eksportruj MIDI");
        exportData.add(exportMidi);
        exportMidi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(snimac.isZavrsio()){
                    JFrame parentFrame = new JFrame();
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Specify a file to save");

                    int userSelection = fileChooser.showSaveDialog(parentFrame);

                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        File fileToSave = fileChooser.getSelectedFile();
                        System.out.println("Save as file: " + fileToSave.getAbsolutePath());
                        MidiFormater midiFormater = new MidiFormater(fileToSave.getAbsolutePath()+".MIDI",snimac.getListaUnetihSimbola());
                        midiFormater.format();
                    }
                }
            }
        });

        JMenuItem exportTxt = new JMenuItem("Eksportuj TXT");
        exportData.add(exportTxt);
        exportTxt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(snimac.isZavrsio()){
                    JFrame parentFrame = new JFrame();
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Specify a file to save");

                    int userSelection = fileChooser.showSaveDialog(parentFrame);

                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        File fileToSave = fileChooser.getSelectedFile();
                        System.out.println("Save as file: " + fileToSave.getAbsolutePath());

                        TextFormater textFormater = new TextFormater(fileToSave.getAbsolutePath()+".txt",snimac.getListaUnetihSimbola());
                        try {
                            textFormater.format();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        menuBar.add(fileMenu);
        return menuBar;
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        Nota nota = ndl.getNotaFromMapaSimbola("" + keyEvent.getKeyChar());
        if(nota!=null){
            mapaDirki.get(keyEvent.getKeyChar()).setKeyPressed(true);

            if(cdl.isUcitana()){
                if(ispisNota.getFirst()!=null){

                    prviZaIspis=ispisNota.getFirst();

                    if(prviZaIspis instanceof Nota){
                        if(nota.getTaster()==((Nota) prviZaIspis).getTaster()){
                            ispisNota.removeFirst();
                            ispisNota.incRedniBrZaIspis();
                        }
                    }
                    else if(prviZaIspis instanceof Akord){
                        ((Akord) prviZaIspis).setPritisnuto(nota.getTaster());
                        if(((Akord) prviZaIspis).svePritisnuto()){
                            ispisNota.removeFirst();
                            ispisNota.incRedniBrZaIspis();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        Nota nota = ndl.getNotaFromMapaSimbola("" + keyEvent.getKeyChar());
        if(nota!=null){
            mapaDirki.get(keyEvent.getKeyChar()).setKeyRealesed(true);
        }
    }


    public void dodajMouseListener(Button button,char c){
        button.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                System.out.println("MOUSE PRESSED " + c);

                Nota nota = ndl.getNotaFromMapaSimbola("" + c);
                if(nota!=null){
                    mapaDirki.get(c).setKeyPressed(true);
                }

                if(cdl.isUcitana()){
                    if(ispisNota.getFirst()!=null){

                        prviZaIspis=ispisNota.getFirst();

                        if(prviZaIspis instanceof Nota){
                            if(nota.getTaster()==((Nota) prviZaIspis).getTaster()){
                                ispisNota.removeFirst();
                                ispisNota.incRedniBrZaIspis();
                            }
                        }
                        else if(prviZaIspis instanceof Akord){
                            ((Akord) prviZaIspis).setPritisnuto(nota.getTaster());
                            if(((Akord) prviZaIspis).svePritisnuto()){
                                ispisNota.removeFirst();
                                ispisNota.incRedniBrZaIspis();
                            }
                        }
                    }
                }

            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                System.out.println("MOUSE REALEASED " + c);

                Nota nota = ndl.getNotaFromMapaSimbola("" + c);
                if(nota!=null){
                    mapaDirki.get(c).setKeyRealesed(true);
                }
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });

    }

    public void setPanel3() {

        //Check boxes-------------------------------
        prikaziNote = new JCheckBox();
        prikaziNote.setFocusable(false);

        prikaziTastere = new JCheckBox();
        prikaziTastere.setFocusable(false);

        prikaziTastere.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(prikaziTastere.isSelected()){
                    System.out.println("Prikazi taster cekiran");
                    if(prikaziNote.isSelected()) activeNotaTasterIme();
                    else activeTasterIme();
                }
                else{
                    System.out.println("Prikazi taster uncekiran");
                    if(prikaziNote.isSelected()) activeNotaIme();
                    else deactiveIme();
                }
            }
        });

        prikaziNote.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(prikaziNote.isSelected()){
                    System.out.println("Prikazi note cekiran");
                    if(prikaziTastere.isSelected()) activeNotaTasterIme();
                    else activeNotaIme();
                }
                else{
                    System.out.println("Prikazi note uncekiran");
                    if(prikaziTastere.isSelected()) activeTasterIme();
                    else deactiveIme();
                }
            }
        });

        //Buttons -------------------------------------
        nastaviAutomatskoSviranje=new JButton("PUSTI");
        nastaviAutomatskoSviranje.setFocusable(false);
        nastaviAutomatskoSviranje.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(cdl.isUcitana()){
                    System.out.println(Akord.getMaxDuzinaAkorda());
                    player.resumee();

                    nastaviAutomatskoSviranje.setBackground(Color.orange);
                    nastaviAutomatskoSviranje.setForeground(Color.WHITE);

                    pauzirajAutomatskoSviranje.setBackground(null);
                    pauzirajAutomatskoSviranje.setForeground(Color.black);

                }
                else {
                    System.out.println("Ucitaj kompoziciju!");
                }
            }
        });

        pauzirajAutomatskoSviranje = new JButton("PAUZIRAJ");
        pauzirajAutomatskoSviranje.setFocusable(false);
        pauzirajAutomatskoSviranje.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(cdl.isUcitana()){
                    player.pause();

                    pauzirajAutomatskoSviranje.setBackground(Color.orange);
                    pauzirajAutomatskoSviranje.setForeground(Color.WHITE);

                    nastaviAutomatskoSviranje.setBackground(null);
                    nastaviAutomatskoSviranje.setForeground(Color.black);

                }
            }
        });

        snimaj = new JButton("SNIMAJ");
        snimaj.setFocusable(false);
        snimaj.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                if(snimac.isZavrsio()) snimac.prepapreForNew();
                snimac.setUkljucen(true);

                snimaj.setBackground(Color.ORANGE);
                snimaj.setForeground(Color.white);
            }
        });

        zavrsiSnimanje = new JButton("ZAVRSI");
        zavrsiSnimanje.setFocusable(false);
        zavrsiSnimanje.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                snimac.setZavrsio(true);
                snimac.setUkljucen(false);

                snimaj.setBackground(null);
                snimaj.setForeground(Color.black);

                ArrayList<MuzickiSimbol> lista = snimac.getListaUnetihSimbola();
                for (MuzickiSimbol ms:lista) {
                    System.out.println(ms);
                }

            }
        });

        //Panel za dugmad za sviranje
        JPanel dugmadPanel = new JPanel(new GridLayout(2,1));
        dugmadPanel.setBorder(new EmptyBorder(10,10,10,10));
        dugmadPanel.add(nastaviAutomatskoSviranje);
        dugmadPanel.add(pauzirajAutomatskoSviranje);
        panel3.add(dugmadPanel);

        //Panel za checkBoxove
        JPanel checkBoxPanelOuter = new JPanel(new BorderLayout());
        panel3.add(checkBoxPanelOuter);
        JPanel checkBoxPanel = new JPanel(new GridLayout(2,2));
        checkBoxPanel.setBorder(new EmptyBorder(10,70,10,0));
        checkBoxPanel.add(new JLabel("PRIKAZI NOTE"));
        checkBoxPanel.add(prikaziNote);
        checkBoxPanel.add(new JLabel("PRIKAZI TASTERE"));
        checkBoxPanel.add(prikaziTastere);
        checkBoxPanelOuter.add(checkBoxPanel,BorderLayout.CENTER);

        //Panel za snimanje

            //Panel sa dugmadima za snimanje
            JPanel zaSnimanjePanel = new JPanel(new GridLayout(1,2));
            zaSnimanjePanel.setFocusable(false);
            zaSnimanjePanel.add(snimaj);
            zaSnimanjePanel.add(zavrsiSnimanje);

            //Panel sa seekbarom i dugmadima
            JPanel podesavanjeOsminePanel = new JPanel(new GridLayout(2,1));
            podesavanjeOsminePanel.setFocusable(false);

                //Seekbar panel
                int MIN = 100;
                int MAX = 1000;
                int INIT = 400;

                JSlider osminaPodesavanje = new JSlider(JSlider.HORIZONTAL, MIN, MAX, INIT);
                osminaPodesavanje.setFocusable(false);
                podesavanjeOsminePanel.add(osminaPodesavanje);

                //Panel sa labelom i dugmadima
                JPanel panelSaVrednostima = new JPanel(new GridLayout(1,3));
                panelSaVrednostima.setFocusable(false);
                JLabel trajanjeOsmineText = new JLabel("<html><center>TRAJANJE<br>OSMINE:</center></html>");
                panelSaVrednostima.add(trajanjeOsmineText);

                JLabel vrednostOsmine = new JLabel(""+INIT,SwingConstants.CENTER);
                vrednostOsmine.setFocusable(false);
                vrednostOsmine.setBackground(Color.white);
                vrednostOsmine.setOpaque(true);
                vrednostOsmine.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
                panelSaVrednostima.add(vrednostOsmine);

                JButton promenaVrednostiOsmine = new JButton("Promeni");
                promenaVrednostiOsmine.setFocusable(false);
                promenaVrednostiOsmine.setBorder(BorderFactory.createEmptyBorder(0,2,0,0));
                panelSaVrednostima.add(promenaVrednostiOsmine);
                podesavanjeOsminePanel.add(panelSaVrednostima);


                osminaPodesavanje.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent changeEvent) {
                        vrednostOsmine.setText(""+osminaPodesavanje.getValue());
                        vrednostOsmine.setBackground(Color.white);
                    }
                });

                promenaVrednostiOsmine.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        snimac.setOsmina(osminaPodesavanje.getValue());
                        snimac.setCetvrtina(osminaPodesavanje.getValue()*2);
                        vrednostOsmine.setText(""+osminaPodesavanje.getValue());
                        vrednostOsmine.setBackground(Color.green);
                    }
                });


        JPanel automatskoSviranjePanel = new JPanel(new GridLayout(3,1));
        automatskoSviranjePanel.setFocusable(false);
        automatskoSviranjePanel.setBorder(new EmptyBorder(10,10,10,10));
        automatskoSviranjePanel.add(zaSnimanjePanel);
        automatskoSviranjePanel.add(osminaPodesavanje);
        automatskoSviranjePanel.add(panelSaVrednostima);

        panel3.add(automatskoSviranjePanel);
    }

    public void activeNotaIme(){
        for(int i=0;i<35;i++){
            beleDirke[i].activeNotaIme();
        }
        for(int i=0;i<25;i++){
            crneDirke[i].activeNotaIme();
        }
    }
    public void activeTasterIme(){
        for(int i=0;i<35;i++){
            beleDirke[i].activeTasterIme();
        }
        for(int i=0;i<25;i++){
            crneDirke[i].activeTasterIme();
        }
    }
    public void activeNotaTasterIme(){
        for(int i=0;i<35;i++){
            beleDirke[i].activeNotaTasterIme();
        }
        for(int i=0;i<25;i++){
            crneDirke[i].activeNotaTasterIme();
        }
    }

    public void deactiveIme(){
        for(int i=0;i<35;i++){
            beleDirke[i].deactiveIme();
        }
        for(int i=0;i<25;i++){
            crneDirke[i].deactiveIme();
        }
    }

   public void dodajElementeZaIscrtavanje(){

        int pomeraj=10;
        for(int i=0;i<10;i++){
            JLabel labela = new JLabel();
            labela.setOpaque(true);
            labela.setBounds(pomeraj,10,10,100);
            labela.setText("|");
            labela.setBackground(Color.black);
            panelCrte.add(labela);
            pomeraj=pomeraj+110;
        }


        panel4.add(panelNote);
        panel4.add(panelCrte);
   }

}
