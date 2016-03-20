package dk.aau.ida8.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Address {
    @Id
    @GeneratedValue
    private long id;

    private String building;
    private String street;
    private String postcode;
    private String town;

    public Address(String building, String street, String postcode, String town) {
        this.building = building;
        this.street = street;
        this.postcode = postcode;
        this.town = town;
    }

    public String getBuilding() {
        return building;
    }

    public String getStreet() {
        return street;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getTown() {
        return town;
    }
}
