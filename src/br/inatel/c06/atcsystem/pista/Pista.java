package br.inatel.c06.atcsystem.pista;

import br.inatel.c06.atcsystem.aeronave.Aeronave;

public class Pista {

    private String id;
    private double tamanhoMaximoPista;
    private boolean ocupada;

    public Pista(String id, double tamanhoMaximoPista) {
        if (tamanhoMaximoPista <= 0) {
            throw new IllegalArgumentException("O tamanho máximo da pista deve ser positivo.");
        }

        this.id = id;
        this.tamanhoMaximoPista = tamanhoMaximoPista;
        this.ocupada = false;
    }

    public String getId() {
        return id;
    }

    public double getTamanhoMaximoPista() {
        return tamanhoMaximoPista;
    }

    public boolean estaOcupada() {
        return ocupada;
    }

    public boolean suportaTamanho(double tamanhoAeronave) {
        return tamanhoAeronave <= tamanhoMaximoPista;
    }

    public boolean suportaAeronave(Aeronave aeronave) {
        return aeronave != null && suportaTamanho(aeronave.getTamanho());
    }

    public synchronized boolean ocupar() {
        if (ocupada) {
            return false;
        }
        ocupada = true;
        return true;
    }

    public synchronized boolean ocupar(Aeronave aeronave) {
        if (ocupada) {
            return false;
        }
        if (!suportaAeronave(aeronave)) {
            return false;
        }
        ocupada = true;
        return true;
    }


    public synchronized void liberar() {
        ocupada = false;
    }

    @Override
    public String toString() {
        String status = ocupada ? "ocupada" : "desocupada";
        return "Pista " + id +
                " (tamanho máximo: " + tamanhoMaximoPista + "), " +
                status;
    }
}
