package br.inatel.c06.atcsystem;

import br.inatel.c06.atcsystem.aeronave.AeronaveCarga;
import br.inatel.c06.atcsystem.aeronave.AeronaveComercial;
import br.inatel.c06.atcsystem.carga.Carga;
import br.inatel.c06.atcsystem.controle.TorreDeControle;
import br.inatel.c06.atcsystem.pista.Pista;
import br.inatel.c06.atcsystem.voo.Voo;

public class Main
{
    public static void main(String[] args)
    {
        System.out.println("=== SISTEMA DE CONTROLE DE TRÁFEGO AÉREO ===\n");

        // 1. Criar torre de controle
        TorreDeControle torre = new TorreDeControle();

        // 2. Criar pistas
        Pista pista1 = new Pista("Pista 01");
        Pista pista2 = new Pista("Pista 02");
        // Pista pista3 = new Pista("Pista 03");
        torre.adicionarPista(pista1);
        torre.adicionarPista(pista2);
        //torre.adicionarPista(pista3);

        // 3. Criar aeronaves
        AeronaveComercial aviao1 = new AeronaveComercial("Boeing 737", "PT-ABC", 5000, 70000, 2, "Azul", 150);
        AeronaveComercial aviao2 = new AeronaveComercial("Airbus A320", "PT-XYZ", 4500, 65000, 1, "Gol", 120);
        AeronaveCarga cargueiro = new AeronaveCarga("Boeing 747", "PT-CGO", 8000, 200000, 1, 50000);

        // Adicionar cargas ao cargueiro
        cargueiro.adicionarCarga(new Carga(15000, "vacina" , true));  // Carga perecível
        cargueiro.adicionarCarga(new Carga(20000, "moveis" , false));  // Carga sensível (aumenta prioridade)

        // 4. Adicionar aeronaves à torre
        torre.adicionarAeronave(aviao1);
        torre.adicionarAeronave(aviao2);
        torre.adicionarAeronave(cargueiro);

        // 5. Exibir informações das aeronaves
        System.out.println("=== AERONAVES CADASTRADAS ===");
        System.out.println(aviao1.exibirInformacoes());
        System.out.println(aviao2.exibirInformacoes());
        System.out.println(cargueiro.exibirInformacoes());
        System.out.println();

        // 6. Criar voos
        Voo voo1 = new Voo("Voo-001", "Confins", "Guarulhos", Voo.TipoOperacao.DECOLAGEM);
        Voo voo2 = new Voo("Voo-002", "Salvador", "Brasília", Voo.TipoOperacao.POUSO);
        Voo voo3 = new Voo("Voo-003", "Miami", "Viracopos", Voo.TipoOperacao.POUSO);

        // Simular emergência médica no voo2
        //aviao2.emergenciaMedica();

        // 7. Adicionar voos à torre
        torre.adicionarVoo(voo1);
        torre.adicionarVoo(voo2);
        torre.adicionarVoo(voo3);

        // 8. Processar voos
        System.out.println("\n=== INICIANDO PROCESSAMENTO DE VOOS ===");
        torre.processarVoos();

        // Aguardar finalização dos voos
        try {
            Thread.sleep(5000); // Tempo suficiente para todos os voos completarem
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("\n=== SISTEMA FINALIZADO ===");

            System.out.println(aviao1.exibirInformacoes());
            System.out.println(aviao2.exibirInformacoes());
        }
    }
