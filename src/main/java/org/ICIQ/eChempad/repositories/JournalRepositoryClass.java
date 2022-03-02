package org.ICIQ.eChempad.repositories;

import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.entities.Researcher;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.UUID;

@Repository
@Transactional
public class JournalRepositoryClass extends GenericRepositoryClass<Journal, UUID> implements JournalRepository{

    public String getDescription(UUID id) {
        return (String) super.currentSession().createQuery("FROM journal where id=:id").setParameter("id", id).uniqueResult();
    }

    public String getName(UUID id) {
        return (String) super.currentSession().createQuery("FROM researcher where id=:id").setParameter("id", id).uniqueResult();
    }

}