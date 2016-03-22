package dk.aau.ida8.model;

import org.omg.PortableServer.POAPackage.ServantNotActiveHelper;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * This class represents one weightlifter.
 *
 * Each weightlifter must be the member of a club to participate in a
 * competition. Their gender, weight and name must be stored.
 *
 * Lifter objects are used in the {@link Participation Participation} class,
 * representing an individual's participation within a particular competition.
 */
@Entity
public class Lifter {

    /**
     * Defines genders options for a Lifter.
     */
    public enum Gender {
        MALE, FEMALE
    }

    @Id
    @GeneratedValue
    private long id;

    private String forename;
    private String surname;
    private Club club;
    private Gender gender;
    private double bodyWeight;

    public Lifter(String forename, String surname, Club club, Gender gender, float bodyWeight) {
        this.forename = forename;
        this.surname = surname;
        this.club = club;
        this.gender = gender;
        this.bodyWeight = bodyWeight;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFullName() {
        return getForename() + " " + getSurname();
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public double getBodyWeight() {
        return bodyWeight;
    }

    public void setBodyWeight(double bodyWeight) {
        this.bodyWeight = bodyWeight;
    }
}
