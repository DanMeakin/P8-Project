package dk.aau.ida8.model;

import javax.persistence.*;

@Entity
public class Club {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    @ManyToOne
    private Address address;

    public Club() {

    }

    public Club(String name, Address address) {
        this.name = name;
        this.address = address;
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
