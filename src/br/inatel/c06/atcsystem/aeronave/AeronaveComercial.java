package br.inatel.c06.atcsystem.aeronave;

public class AeronaveComercial extends Aeronave{
    private String companhia;
    private int qtdPassageiros;
    private boolean emergencia = false;

    public AeronaveComercial(String modelo, String matricula, double combustivel, double pesoAtual, double tamanho, int prioridadeAtual, String companhia, int qtdPassageiros) {

        super(modelo, matricula, combustivel, pesoAtual, tamanho  , prioridadeAtual);

        if (qtdPassageiros < 0) {
            throw new IllegalArgumentException("A quantidade de passageiros não pode ser negativa.");
        }

        this.companhia = companhia;
        this.qtdPassageiros = qtdPassageiros;
    }

    public String getCompanhia() {
        return companhia;
    }

    public int getQtdPassageiros() {
        return qtdPassageiros;
    }

    public void emergenciaMedica() {
        this.emergencia = true;
        atualizaPrioridade();
    }

    @Override
    public void atualizaPrioridade() {
        if(emergencia)
            this.prioridadeAtual = 5;
    }

    @Override
    public int getPrioridadePouso() {
        return this.prioridadeAtual;
    }

    @Override
    public String exibirInformacoes() {
        return  "Aeronave Comercial [" + modelo + " - " + matricula + ", Companhia: " + companhia + ", Passageiros: " + qtdPassageiros + "]";
    }
}