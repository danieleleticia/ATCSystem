package br.inatel.c06.atcsystem.carga;

public class Carga {
    private String nome;
    private double peso;
    private boolean sensivel;
    private boolean perecivel;

    public Carga(double peso, String nome, boolean sensivel)
    {
        this.peso = peso;
        this.nome = nome;
        this.sensivel = sensivel;
    }

    public String getNome() {
        return nome;
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

    @Override
    public String toString() {
        return "Carga: " + nome  +
                ", peso=" + peso +
                ", sensivel=" + sensivel +
                ", perecivel=" + perecivel;
    }

}