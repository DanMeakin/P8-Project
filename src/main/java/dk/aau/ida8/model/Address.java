package dk.aau.ida8.model;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents an address stored within the system.
 */
@Entity
public class Address {
    @Id
    @GeneratedValue
    private long id;

    private String building;
    private String street;
    private String postcode;
    private String town;


    /**
     * Empty constructor required by Hibernate.
     */
    public Address() {
    }

    /**
     * Creates an Address instance.
     *
     * @param building  the name/number of the building
     * @param street    the street
     * @param postcode  the postcode
     * @param town      the town
     */
    public Address(String building, String street, String postcode, String town) {
        this.building = building;
        this.street = street;
        this.postcode = postcode;
        this.town = town;
    }

    /**
     * Creates a string representation of an Address.
     *
     * @return string representation of an Address.
     */
    public String toString() {
        return getStreet() + " " + getBuilding() + ", " +
                getPostcode() + " " + getTown();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }
}
