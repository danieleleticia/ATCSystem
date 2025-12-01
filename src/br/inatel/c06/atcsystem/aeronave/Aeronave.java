package br.inatel.c06.atcsystem.aeronave;

import br.inatel.c06.atcsystem.exceptions.CombustivelInvalidoException;
import br.inatel.c06.atcsystem.exceptions.MatriculaInvalidaException;
import br.inatel.c06.atcsystem.exceptions.PrioridadeInvalidaException;

public abstract class Aeronave {
    protected String modelo;
    protected String matricula;
    protected double combustivel;
    protected double pesoAtual;
    protected double tamanho;
    protected int prioridadeAtual;

    public Aeronave(String modelo, String matricula, double combustivel,double tamanho, double pesoAtual, int prioridadeAtual) {
        if (combustivel < 0) {
            throw new CombustivelInvalidoException("O combustível não pode ser negativo.");
        }

        if (prioridadeAtual < 1 || prioridadeAtual > 5) {
            throw new PrioridadeInvalidaException("A prioridade deve estar entre 1 e 5.");
        }

        if(tamanho <= 0) {
            throw new IllegalArgumentException("Tamanho deve ser positivo.");
        }
        if (pesoAtual < 0) {
            throw new IllegalArgumentException("Peso deve ser positivo.");
        }

        this.modelo = modelo;
        this.matricula = normalizaMatricula(matricula);
        this.combustivel = combustivel;
        this.tamanho = tamanho;
        this.pesoAtual = pesoAtual;
        this.prioridadeAtual = prioridadeAtual;

    }

    public String getMatricula() {
        return matricula;
    }
    public double getTamanho() {return tamanho;}

    private String normalizaMatricula(String matricula) {
        if (matricula == null) {
            throw new MatriculaInvalidaException("A matrícula não pode ser nula.");
        }
        String corrigido = matricula.trim().toUpperCase(); //trim tira espaço em branco, toUpperCase transforma tudo em maiuscula

        if (!corrigido.matches("^(?i)(PT|PR|PP|PS|PU|PH)-[A-Z]{3}$")) {
            throw new MatriculaInvalidaException("Formato inválido de matrícula. Exemplo válido: PT-ABC.");
        }
        return corrigido;
    }

    public abstract void atualizaPrioridade();

    public abstract int getPrioridadePouso();//cada tipo de aeronave tem um tipo especifico de prioridade para a aterissagem

    public abstract String exibirInformacoes();

    public void consumirCombustivel(double quantidade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("Quantidade de combustível a consumir não pode ser negativa.");
        }

        combustivel -= quantidade;
        if (combustivel < 0) {
            combustivel = 0;
        }

    }
}