package br.inatel.c06.atcsystem.pista;

import br.inatel.c06.atcsystem.aeronave.Aeronave;

public class Pista {

    private String id;
    // Tamanho máximo que a pista suporta (por exemplo, em metros)
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

    /**
     * Verifica se um determinado tamanho de aeronave pode usar esta pista.
     */
    public boolean suportaTamanho(double tamanhoAeronave) {
        return tamanhoAeronave <= tamanhoMaximoPista;
    }

    /**
     * Verifica se a aeronave pode usar esta pista com base no seu tamanho.
     */
    public boolean suportaAeronave(Aeronave aeronave) {
        return aeronave != null && suportaTamanho(aeronave.getTamanho());
    }

    /**
     * Ocupa a pista sem verificar tamanho de aeronave (mantido para compatibilidade).
     */
    public synchronized boolean ocupar() {
        if (ocupada) {
            return false;
        }
        ocupada = true;
        return true;
    }

    /**
     * Versão de ocupar que já verifica se a aeronave cabe na pista.
     * Se não couber ou já estiver ocupada, retorna false.
     */
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

    /**
     * Libera a pista.
     */
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
