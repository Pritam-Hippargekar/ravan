package com.ayush.ravan.model.oneToMany;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "branch")
public class Branch {  // (Child)

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToOne(targetEntity=Company.class, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="company_id",nullable = false, foreignKey = @ForeignKey(name = "fk_name"))//Optional
    //@JsonIgnore
    //@JsonIgnoreProperties("branches")  // for department field employee
    private Company company;

    //Accessors...

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    // @Override equals(Object o) and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Branch branch = (Branch) o;
        return id.equals(branch.id) && name.equals(branch.name) && company.equals(branch.company);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, company);
    }
}
