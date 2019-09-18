package controller;

public class MuzickiSimbol {
    Trajanje trajanje;

    public MuzickiSimbol(){};

    public MuzickiSimbol(Trajanje trajanje){
        this.trajanje=trajanje;
    }

    public Trajanje getTrajanje() {
        return trajanje;
    }

    public void setTrajanje(Trajanje trajanje) {
        this.trajanje = trajanje;
    }
}
