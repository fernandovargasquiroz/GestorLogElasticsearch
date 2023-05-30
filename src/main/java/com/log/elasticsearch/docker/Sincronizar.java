package com.log.elasticsearch.docker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author fernando.vargas
 */
@Service
@RequiredArgsConstructor
public class Sincronizar {

    public void sincronizarLogsALocal() {
                try {
            String dockerCommand = "docker cp 74ff6efdd98f4854accac9459fd0b9da4cf4b6c5bb22e528b21f1dd666e29741:/usr/local/tomcat/logs/ D:/fv/OrigenLogs";
           ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("cmd.exe", "/c", dockerCommand);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = process.waitFor();
        } catch (IOException| InterruptedException e) {
            e.printStackTrace();
        }
    }
        public void sincronizarLogsAFileBeat() {
                try {
            // Copia a Local
            String dockerCommand = "docker cp D:/fv/OrigenLogs/logs c3a10db4acd1e29c59f13f89b7d1b5493f9abed41dcdf301487348cc2d85bc8d:/usr/share/filebeat/logs";

            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("cmd.exe", "/c", dockerCommand);

            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Esperar a que el proceso termine
            int exitCode = process.waitFor();
            System.out.println("Comando finalizado con código de salida: " + exitCode);
        } catch (IOException| InterruptedException e) {
            e.printStackTrace();
        }
    }
}
