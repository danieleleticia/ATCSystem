package br.inatel.c06.atcsystem.aeronave;

import br.inatel.c06.atcsystem.exceptions.CombustivelInvalidoException;
import br.inatel.c06.atcsystem.exceptions.MatriculaInvalidaException;
import br.inatel.c06.atcsystem.exceptions.PrioridadeInvalidaException;

public abstract class Aeronave {
    protected String modelo;
    protected String matricula;
    protected double combustivel;
    protected double combustivelInicial;
    protected double pesoAtual;
    protected int prioridadeAtual;


    public Aeronave(String modelo, String matricula, double combustivel,double combustivelInicial, double pesoAtual, int prioridadeAtual) {

        if (combustivel < 0) {
            throw new CombustivelInvalidoException("O combustível não pode ser negativo.");
        }

        if (prioridadeAtual < 1 || prioridadeAtual > 5) {
            throw new PrioridadeInvalidaException("A prioridade deve estar entre 1 e 5.");
        }

        this.modelo = modelo;
        this.matricula = normalizaMatricula(matricula);
        this.combustivel = combustivel;
        this.pesoAtual = pesoAtual;
        this.prioridadeAtual = prioridadeAtual;
    }

    //sobrecarga
    public Aeronave(String modelo, String matricula, double combustivel, double pesoAtual, int prioridadeAtual) {

        if (combustivel < 0) {
            throw new CombustivelInvalidoException("O combustível não pode ser negativo.");
        }

        if (prioridadeAtual < 1 || prioridadeAtual > 5) {
            throw new PrioridadeInvalidaException("A prioridade deve estar entre 1 e 5.");
        }

        this.modelo = modelo;
        this.matricula = normalizaMatricula(matricula);
        this.combustivel = combustivel;
        this.combustivelInicial = combustivel;
        this.pesoAtual = pesoAtual;
        this.prioridadeAtual = prioridadeAtual;
    }

    public String getModelo() {
        return modelo;
    }
    public String getMatricula() {
        return matricula;
    }
    public double getCombustivel() {
        return combustivel;
    }
    public double getPesoAtual() {
        return pesoAtual;
    }
    public int getPrioridadeAtual() {
        return prioridadeAtual;
    }
    public double getCombustivelInicial() {return combustivelInicial;}

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

        if (isCombustivelCritico()) {
            emergenciaCombustivel();
        }
    }

    public boolean isCombustivelCritico() {
        if (combustivelInicial<= 0) {
            return false; // evita divisão esquisita
        }
        return combustivel <= combustivelInicial * 0.10;
    }

    public void emergenciaCombustivel() {
        prioridadeAtual = 5;
        System.out.println("Aeronave " + matricula + " em EMERGÊNCIA por combustível crítico!");
    }

}