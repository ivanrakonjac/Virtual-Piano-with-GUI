package controller;

public class Nota extends MuzickiSimbol{

    private String taster;
    private String opis;
    private String broj;
    private boolean povisena;
    private boolean jedina=false;

    public Nota(String taster,String opis,String broj,boolean povisena){
        this.taster=taster;
        this.opis=opis;
        this.broj=broj;
        this.povisena=povisena;
    }

    public boolean isJedina() {
        return jedina;
    }

    public void setJedina(boolean jedina) {
        this.jedina = jedina;
    }

    public String getOpis() {
        return opis;
    }

    public char getCharOpis(){
        return opis.charAt(0);
    }

    public String getTaster() {
        return taster;
    }

    public String getBroj() {
        return broj;
    }

    public boolean isPovisena() {
        return povisena;
    }

    @Override
    public String toString() {
        return "Nota{" +
                "taster='" + taster + '\'' +
                ", opis='" + opis + '\'' +
                ", broj='" + broj + '\'' +
                ", povisena=" + povisena +
                ", trajanje=" + trajanje +
                ", jedina=" + jedina +
                '}';
    }
}
