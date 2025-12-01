package br.inatel.c06.atcsystem.arquivos;

import br.inatel.c06.atcsystem.aeronave.Aeronave;
import br.inatel.c06.atcsystem.voo.Voo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class LogSistema {

    private static Path CAMINHO_ARQUIVO = Paths.get("src/log_sistema.txt");

    private static synchronized void escreverLinha(String linha) {
        try {
            Files.writeString(
                    CAMINHO_ARQUIVO,
                    linha + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            System.err.println("ERRO CRÍTICO AO GRAVAR LOG: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String formatarVoo(Voo v) {
        return "[PRIO=" + v.getPrioridade() + "] Voo " + v.getCodigo()
                + " - Aeronave " + v.getAeronave().getMatricula();
    }

    public static void registrarFilaInicial(List<Voo> filaVoos) {
        escreverLinha("==== Fila inicial de Voos ====");
        int pos = 1;
        for (Voo v : filaVoos) {
            escreverLinha(pos + ") " + formatarVoo(v));
            pos++;
        }
        escreverLinha("==============================");
        escreverLinha("");
    }

    public static void registrarAlocacao(Aeronave a, Voo v) {
        escreverLinha("[ALOCACAO] " + v.getCodigo() + " assumiu a pista " + v.getPista()+ "com " + a.getMatricula());
    }

    public static void registrarMensagem(String mensagem) {
        escreverLinha("[LOG] " + mensagem);
    }

    public static void limparLog() {
        try {
            Files.writeString(
                    CAMINHO_ARQUIVO,
                    "",
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            System.err.println("Erro ao limpar log: " + e.getMessage());
    }
    }
}