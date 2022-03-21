package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.exceptions.ExceptionResourceNotExists;
import org.ICIQ.eChempad.services.JournalService;
import org.ICIQ.eChempad.services.JournalServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/journal")
public class JournalControllerClass implements JournalController{
    // https://blog.marcnuri.com/inyeccion-de-campos-desaconsejada-field-injection-not-recommended-spring-ioc
    private JournalService journalService;

    @Autowired
    public JournalControllerClass(JournalServiceClass journalServiceClass) {
        this.journalService = journalServiceClass;
    }

    @Override
    @GetMapping(
            value = "",
            produces = "application/json"
    )
    public ResponseEntity<Set<Journal>> getAllJournals() {
        HashSet<Journal> journals = new HashSet<>(this.journalService.getAll());

        return ResponseEntity.ok(journals);
    }

    // https://stackoverflow.com/questions/30967822/when-do-i-use-path-params-vs-query-params-in-a-restful-api
    // https://restfulapi.net/http-methods/
    @Override
    @GetMapping(
            value = "/{id}",
            produces = "application/json"
    )
    public ResponseEntity<Journal> getJournal(@PathVariable(value = "id") UUID uuid) throws ExceptionResourceNotExists {
        Journal journal = this.journalService.get(uuid);
        return ResponseEntity.ok().body(journal);
    }


    @PostMapping(
            value = "",
            consumes = "application/json"
    )
    public void addJournal(@Validated @RequestBody Journal journal) {
        this.journalService.saveOrUpdate(journal);
    }


    @DeleteMapping(
            value = "/{id}",
            produces = "application/json"
    )
    public void removeJournal(@PathVariable(value = "id") UUID uuid) throws ExceptionResourceNotExists {
        this.journalService.remove(uuid);
    }

    @PutMapping(
            value = "/{id}",
            produces = "application/json",
            consumes = "application/json"
    )
    @Override
    public void putJournal(@Validated @RequestBody Journal journal, @PathVariable(value = "id") UUID uuid) throws ExceptionResourceNotExists {
        this.journalService.update(journal, uuid);
    }
}
