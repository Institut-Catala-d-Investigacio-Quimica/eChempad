/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.configurations.Database;

import org.ICIQ.eChempad.entities.Authority;
import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.repositories.ResearcherRepository;
import org.ICIQ.eChempad.services.ResearcherService;
import org.apache.catalina.User;
import org.apache.juli.logging.Log;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * This class contains the method
 * public void onApplicationEvent(final @NotNull ApplicationReadyEvent event)
 * which is executed after the application is "ready", which usually is after all the initializations, but before
 * accepting traffic. In here we can implement ways to load data to the database at the start when we are in dev
 * profile.
 */
@Component
public class DatabaseInitStartup implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private ResearcherService<Researcher, UUID> researcherService;

    @Autowired
    private ResearcherRepository<Researcher, UUID> researcherRepository;

    public DatabaseInitStartup() {}

    @Override
    public void onApplicationEvent(final @NotNull ApplicationReadyEvent event) {
        Researcher res = (Researcher) researcherRepository.loadUserByUsername("eChempad@iciq.es");
        Logger.getGlobal().info("echempad: " + res);
        this.initializeDB();
    }

    private void initializeDB()
    {


        Researcher researcher1 = (Researcher) researcherService.loadUserByUsername("eChempad@iciq.es");
        Logger.getGlobal().info("echempad2: " + researcher1);


    }

}
