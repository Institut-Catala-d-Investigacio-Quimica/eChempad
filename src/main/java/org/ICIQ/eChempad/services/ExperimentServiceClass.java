/**
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.Authority;
import org.ICIQ.eChempad.entities.Experiment;
import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.repositories.ExperimentRepository;
import org.ICIQ.eChempad.repositories.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class ExperimentServiceClass extends GenericServiceClass<Experiment, UUID> implements ExperimentService {

    @Autowired
    SecurityService securityService;

    @Autowired
    JournalRepository journalRepository;

    @Autowired
    public ExperimentServiceClass(ExperimentRepository experimentRepository) {
        super(experimentRepository);
        //       this.researcherRepository = (ResearcherRepository) genericRepository;
    }

    /**
     * Adds a new experiment into a certain journal, provided as a URL parameter.
     *
     * @param experiment   Data of the addition of the experiment.
     * @param journal_uuid Journal where er are adding the new experiment. We must have WRITE permissions
     * @throws ResourceNotExistsException  The resource does not exist.
     * @throws NotEnoughAuthorityException Thrown if we do not have the required authority to perform the operation in the journal
     */
    @Override
    public void addExperimentToJournal(Experiment experiment, UUID journal_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException {
        // TODO query journal when we know that we have permissions.
        Journal journal = this.journalRepository.get(journal_uuid);

        // Check permissions
        if (this.securityService.isResearcherAuthorized(Authority.WRITE, journal_uuid, Journal.class))
        {
            // experiment points to the journal where it is in
            experiment.setJournal(journal);
            // Save the experiment
            this.genericRepository.saveOrUpdate(experiment);
            // Then add to experiment the new doc
            journal.getExperiments().add(experiment);
            // And save (update) experiment
            this.journalRepository.saveOrUpdate(journal);

            this.securityService.saveElementWorkspace(experiment);
        }
        else
        {
            throw new NotEnoughAuthorityException("Not enough authority to perform operation");
        }


    }

    /**
     * gets the experiments of a certain journal if we have enough privileges to do so.
     *
     * @param journal_uuid UUID of the journal we are referring
     * @return Returns all the experiments under this journal if they exist.
     * @throws ResourceNotExistsException  Thrown if this journal does not exist.
     * @throws NotEnoughAuthorityException Thrown if we do not have enough authority to read the journal.
     */
    @Override
    public Set<Experiment> getExperimentsFromJournal(UUID journal_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException {
        Journal journal = this.journalRepository.get(journal_uuid);
        if (this.securityService.isResearcherAuthorized(Authority.READ, journal.getUUid(), Journal.class))
        {
            return journal.getExperiments();
        }
        else
        {
            throw new NotEnoughAuthorityException("You do not have authority to read this journal");
        }
    }

    @Override
    public Set<Experiment> getAll() {
        return this.securityService.getAuthorizedExperiment(Authority.READ);
    }



}
