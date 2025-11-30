package br.inatel.c06.atcsystem.aeronave;

import java.util.ArrayList;
import br.inatel.c06.atcsystem.exceptions.PrioridadeInvalidaException;
import br.inatel.c06.atcsystem.carga.Carga;

public class AeronaveCarga extends Aeronave {

    private double capacidadeMaxCarga;
    private ArrayList<Carga> listaCargas;

    public AeronaveCarga(String modelo, String matricula, double combustivel, double pesoAtual, int prioridadeAtual, double capacidadeMaxCarga) {

        super(modelo, matricula, combustivel, pesoAtual, prioridadeAtual);

        if (capacidadeMaxCarga <= 0) {
            throw new IllegalArgumentException("A capacidade máxima de carga deve ser positiva.");
        }

        this.capacidadeMaxCarga = capacidadeMaxCarga;
        this.listaCargas = new ArrayList<Carga>();
    }


    public double getCapacidadeMaxCarga() {
        return capacidadeMaxCarga;
    }

    public ArrayList<Carga> getListaCargas() {
        return listaCargas;
    }

    public void adicionarCarga(Carga novaCarga) {

        if (novaCarga == null) {
            throw new IllegalArgumentException("A carga não pode ser nula.");
        }

        double pesoNovo = calcularPesoTotalCarga() + novaCarga.getPeso();

        if (pesoNovo > capacidadeMaxCarga) {
            throw new IllegalArgumentException("Sobrecarga: peso total excede a capacidade máxima de carga.");
        }

        listaCargas.add(novaCarga);

        this.pesoAtual += novaCarga.getPeso();

        atualizaPrioridade();
    }

    public void removerCarga(Carga removida) {

        if (removida == null) {
            throw new IllegalArgumentException("A carga não pode ser nula.");
        }
        if (!listaCargas.remove(removida)) {
            throw new IllegalArgumentException("A carga informada não existe na aeronave.");
        }

        this.pesoAtual -= removida.getPeso();
        if (this.pesoAtual < 0) {
            this.pesoAtual = 0;
        }

        atualizaPrioridade();
    }

    public double calcularPesoTotalCarga() {
        double soma = 0;
        for (Carga carga : listaCargas) {
            soma += carga.getPeso();
        }
        return soma;
    }

    public boolean possuiCargaSensivel() {
        for (Carga carga : listaCargas)
            if (carga.isSensivel())
                return true;
        return false;
    }

    public boolean possuiCargaPerecivel() {
        for (Carga carga : listaCargas)
            if (carga.isPerecivel())
                return true;
        return false;
    }

    @Override
    public void atualizaPrioridade() {

        int prioridade = this.prioridadeAtual;

        if (possuiCargaSensivel()) {
            prioridade = 5;
        } else if (possuiCargaPerecivel()) {
            if (prioridade < 3) {
                prioridade = 3;
            }
        } else
            prioridade = 1;

        if (prioridade < 1 || prioridade > 5) {
            throw new PrioridadeInvalidaException("A prioridade calculada ficou fora do intervalo 1..5.");
        }
        this.prioridadeAtual = prioridade;
    }

    @Override
    public int getPrioridadePouso() {
        return this.prioridadeAtual;
    }

    @Override
    public String exibirInformacoes() {
        return "AeronaveCarga{" +
                "modelo='" + modelo +
                ", matricula='" + matricula +
                ", combustivel=" + combustivel +
                ", pesoAtual=" + pesoAtual +
                ", prioridadeAtual=" + prioridadeAtual +
                ", capacidadeMaxCarga=" + capacidadeMaxCarga +
                ", pesoTotalCarga=" + calcularPesoTotalCarga() +
                ", cargas=" + listaCargas + '}';
    }
}