package br.inatel.c06.atcsystem.carga;

public class Carga {
    private String tipo;
    private double peso;
    private boolean sensivel;
    private boolean perecivel;

    public String getTipo() {
        return tipo;
    }

    public double getPeso() {
        return peso;
    }

    public boolean isSensivel() {
        return sensivel;
    }

    public boolean isPerecivel() {
        return perecivel;
    }
}