package com.log.elasticsearch.controller;

import com.log.elasticsearch.docker.Sincronizar;
import com.log.elasticsearch.entity.Entidad;
import com.log.elasticsearch.service.EntidadRepositoryService;
import com.log.elasticsearch.service.EntidadRestTemplateService;
import com.log.elasticsearch.slack.SlackMessageSenderLog;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/entidad")
@Slf4j
@AllArgsConstructor
public class EntidadController {

    private final EntidadRestTemplateService entidadRestTemplateService;
    private final EntidadRepositoryService entidadRepositoryService;
    private final SlackMessageSenderLog slackMessageSenderLog;

    @GetMapping()
    public ResponseEntity<List<Entidad>> fetchByMessage(@RequestParam(value = "message", required = false) String message) {
        log.info("searching by message {}", message);
        List<Entidad> entidads = entidadRestTemplateService.findEntidadByMessage(message);
        log.info("entidads {}", entidads);
        return ResponseEntity.ok(entidads);
    }

    @GetMapping("/query")
    public ResponseEntity<List<Entidad>> findByMessage(@RequestParam(value = "q", required = false) String query) {
        log.info("searching by message {}", query);
//        sincronizar.sincronizarLogsALocal();
//        sincronizar.sincronizarLogsAFileBeat();
        List<Entidad> entidads = entidadRestTemplateService.processSearch(query);
        List<Entidad> entidadesNuevas;
        entidadesNuevas = new ArrayList<Entidad>();
        for (Entidad entidad : entidads) {
            try {
                Optional<Entidad> entidadEncontrada = null;
                try {
                    entidadEncontrada = entidadRepositoryService.findById(entidad.getId());
                } catch (Exception e) {
                }

                if (entidadEncontrada == null) {
                    entidadesNuevas.add(entidad);
                    entidadRepositoryService.save(entidad);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
        try {
            slackMessageSenderLog.sendMessageLogEntidad(entidadesNuevas);

        } catch (Exception ex) {
            Logger.getLogger(LogjbossController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ResponseEntity.ok(entidads);
    }

}
