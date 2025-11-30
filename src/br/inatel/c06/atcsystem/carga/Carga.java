package br.inatel.c06.atcsystem.carga;

public class Carga {
    private String nome;
    private double peso;
    private boolean sensivel;
    private boolean perecivel;

    public Carga(double peso, String nome, boolean sensivel, boolean perecivel) {
        if (peso <= 0) {
            throw new IllegalArgumentException("O peso da carga deve ser positivo.");
        }

        this.peso = peso;
        this.nome = nome;
        this.sensivel = sensivel;
        this.perecivel = perecivel;
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