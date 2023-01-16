package com.ayush.ravan.model.oneToManySelf;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false, exclude = {"children"})
public class City implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column
    protected String name;

    @Column(nullable = false)
    @CreationTimestamp
    protected Timestamp createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    @Setter
    public Timestamp lastModified;

    @Column(nullable = false, columnDefinition = "bigint(20) DEFAULT 1")
    @Version
    @Setter
    public Long version;

    public City(Long version) {
        this.version = version;
    }

    // @JoinColumn is defined on manager making it the relationship owner.
    // cascade.ALL is equivalent to cascade={DETACH, MERGE, PERSIST, REFRESH, REMOVE}
    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "parent_id") // manager_id
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonBackReference(value = "city-locality")
    protected City parent;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)  // manager
    @NotFound(action = NotFoundAction.IGNORE)
    protected Set<City> children;

}

