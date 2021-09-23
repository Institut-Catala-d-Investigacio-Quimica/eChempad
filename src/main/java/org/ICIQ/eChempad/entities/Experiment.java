package org.ICIQ.eChempad.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;


/**
 * Model class to store many Documents (files) ideally from a single chemistry assay.
 *
 * An Experiment is composed of many Documents (files) and some metadata (description, name).
 */
@Entity
@Table(name="Experiment", uniqueConstraints = {
        @UniqueConstraint(columnNames = "id")
})
public class Experiment {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description", length = 1000, nullable = false)
    private String description;

    @OneToMany(targetEntity=Document.class, mappedBy="id", fetch=FetchType.EAGER)
    private Set<Document> documents;

    /**
     * Constructor
     * @param name Name used by humans to identify this Experiment. There are no possibility of collisions since we use
     *             the UUID to manage the experiments.
     * @param description String used to describe the contents of this experiment.
     */
    public Experiment(String name, String description) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.documents = new HashSet<>();
    }


    // GETTERS AND SETTERS

    public UUID getUUid() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Document> getDocuments() {
        return this.documents;
    }

    public void setDocuments(Set<Document> documents) {
        this.documents = documents;
    }
}
