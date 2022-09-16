package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.Authority;
import org.ICIQ.eChempad.entities.GenericEntity;
import org.ICIQ.eChempad.entities.IEntity;
import org.ICIQ.eChempad.entities.Journal;

import java.io.Serializable;
import java.util.UUID;

public interface AuthorityController<T extends GenericEntity, S extends Serializable> extends GenericController<Authority, UUID> {
}
