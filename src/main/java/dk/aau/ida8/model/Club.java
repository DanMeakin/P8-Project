package dk.aau.ida8.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents one club within the system.
 *
 * Each club has a number of lifters associated with it. These compete on behalf
 * of the club in competitions.
 */
@Entity
public class Club {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    @ManyToOne
    private Address address;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "club_id")
    private List<Lifter> lifters = new ArrayList<>();

    /**
     * Empty constructor required by Hibernate.
     */
    public Club() {
    }

    /**
     * Creates a Club instance.
     *
     * @param name    the name of the club
     * @param address the address of the club
     */
    public Club(String name, Address address) {
        this.name = name;
        this.address = address;
    }

    /**
     * Associates a lifter with this club.
     *
     * @param lifter the lifter to associate with the club
     */
    public void addLifter(Lifter lifter) {
        lifters.add(lifter);
    }

    /**
     * Removes a lifter association with this club.
     *
     * @param lifter the lifter for whom to remove the club association
     */
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

    public List<Lifter> getLifters() {
        return lifters;
    }

    public void setLifters(List<Lifter> lifters) {
        this.lifters = lifters;
    }

    public String toString() {
        return this.name;
    }

}
