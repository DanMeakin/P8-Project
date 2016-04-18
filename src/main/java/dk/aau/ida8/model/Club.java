package dk.aau.ida8.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Club {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    @ManyToOne
    private Address address;

    @OneToMany(cascade = {CascadeType.ALL})
    private List<Lifter> lifters;

    public Club() {

    }

    public Club(String name, Address address) {
        this.lifters = new ArrayList<Lifter>();
        this.name = name;
        this.address = address;
    }

    public void addLifter(Lifter lifter) {
        lifters.add(lifter);
    }

    public void removeLifter(Lifter lifter) {
        lifters.remove(lifter);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

}
