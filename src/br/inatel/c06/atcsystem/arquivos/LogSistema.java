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

    // Método base: escreve uma linha no arquivo (com append)
    private static synchronized void escreverLinha(String linha) {
        try {
            Files.writeString(
                    CAMINHO_ARQUIVO,
                    linha + System.lineSeparator(),
                    StandardOpenOption.CREATE,   // cria o arquivo se não existir
                    StandardOpenOption.APPEND    // adiciona no final
            );
        } catch (IOException e) {
            System.err.println("Erro ao escrever no log: " + e.getMessage());
        }
    }

    // ---------- FILA DE AERONAVES ----------

    private static String formatarAeronave(Aeronave a) {
        return "[PRIO=" + a.getPrioridadePouso() + "] Aeronave "
                + a.getMatricula() + " (" + a.getModelo() + ")";
    }

    public static void registrarFilaInicial(List<Aeronave> fila) {
        escreverLinha("==== Fila inicial de prioridades (Aeronaves) ====");
        int pos = 1;
        for (Aeronave a : fila) {
            escreverLinha(pos + ") " + formatarAeronave(a));
            pos++;
        }
        escreverLinha("================================================");
        escreverLinha("");
    }

    public static void registrarFilaFinal(List<Aeronave> fila) {
        escreverLinha("");
        escreverLinha("==== Fila final de prioridades (Aeronaves) ====");
        int pos = 1;
        for (Aeronave a : fila) {
            escreverLinha(pos + ") " + formatarAeronave(a));
            pos++;
        }
        escreverLinha("===============================================");
        escreverLinha("");
    }

    // esse pode continuar usando Aeronave, porque registra alocação:
    public static void registrarAlocacao(Aeronave a, Voo v) {
        escreverLinha("[ALOCACAO] Aeronave " + a.getMatricula()
                + " (prio=" + a.getPrioridadePouso() + ") -> Voo " + v.getCodigo());
    }

    // Se quiser registrar qualquer mensagem solta:
    public static void registrarMensagem(String mensagem) {
        escreverLinha("[LOG] " + mensagem);
    }

    public static void limparLog() {
        try {
            // sobrescreve o arquivo com string vazia (sem APPEND)
            Files.writeString(
                    CAMINHO_ARQUIVO,
                    "",
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            System.err.println("Erro ao limpar o log: " + e.getMessage());
        }
    }

}
