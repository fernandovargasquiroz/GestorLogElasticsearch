package com.log.elasticsearch.controller;

import com.log.elasticsearch.entity.Logjboss;
import com.log.elasticsearch.service.LogjbossRepositoryService;
import com.log.elasticsearch.service.LogjbossRestTemplateService;
import com.log.elasticsearch.slack.SlackMessageSenderLog;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/logjboss")
@Slf4j
@AllArgsConstructor
public class LogjbossController {

    private final LogjbossRestTemplateService logjbossRestTemplateService;

    private final LogjbossRepositoryService logjbossRepositoryService;

    private final SlackMessageSenderLog slackMessageSenderLog;

    @GetMapping()
    public ResponseEntity<List<Logjboss>> fetchByMessage(@RequestParam(value = "message", required = false) String message) {
        log.info("searching by name {}", message);
        List<Logjboss> logjboss = logjbossRestTemplateService.findLogjbossByMessage(message);
        log.info("logjboss {}", logjboss);
        return ResponseEntity.ok(logjboss);
    }

    @GetMapping("/query")
    public ResponseEntity<List<Logjboss>> findByMessage(@RequestParam(value = "q", required = false) String query) {
        log.info("searching by name {}", query);
        List<Logjboss> logjboss = logjbossRestTemplateService.processSearch(query);
        try {
            slackMessageSenderLog.sendMessageLog(logjboss);
        } catch (Exception ex) {
            Logger.getLogger(LogjbossController.class.getName()).log(Level.SEVERE, null, ex);
        }
        log.info("logjboss {}", logjboss);
        return ResponseEntity.ok(logjboss);
    }

    @PostMapping()
    public ResponseEntity<Logjboss> saveMessage(@RequestBody Logjboss logjboss) {
        log.info("Save a new Logjboss {} ", logjboss);
        return ResponseEntity.ok(logjbossRepositoryService.createLogjbossIndex(logjboss));
    }

}
