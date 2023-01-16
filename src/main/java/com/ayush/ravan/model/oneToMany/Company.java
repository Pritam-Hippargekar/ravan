package com.ayush.ravan.model.oneToMany;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "company")
public class Company implements Serializable {  //(Parent)

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(targetEntity=Branch.class,cascade = CascadeType.ALL , fetch = FetchType.LAZY, mappedBy = "company", orphanRemoval = true)
    private List<Branch> branches = new ArrayList<>();

    public void addBranches(Branch branch){
        if(branch != null){
            if (branches == null) {
                branches = new ArrayList<>();
            }
            branch.setCompany(this);
            branches.add(branch);
        }
    }

    public void removeBranches(Branch branch){
        if(branch != null){
            branch.setCompany(null);
        }
        branches.remove(branch);
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }
}
