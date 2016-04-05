package dk.aau.ida8.model;

import org.omg.PortableServer.POAPackage.ServantNotActiveHelper;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne
    private Club club;
    private Gender gender;
    private double bodyWeight;

    @OneToMany
    private List<Participation> participations;

    public Lifter() {

    }

    public Lifter(String forename, String surname, Club club, Gender gender, double bodyWeight) {
        this.forename = forename;
        this.surname = surname;
        this.club = club;
        this.gender = gender;
        this.bodyWeight = bodyWeight;
        this.participations = new ArrayList<>();
    }

    /**
     * Adds a Participation instance for this Lifter.
     *
     * A Participation instance represents the participation of a lifter within
     * a given competition. See {@link Participation Participation} for details.
     *
     * @param participation the participation to aggregate to this lifter
     */
    public void addParticipation(Participation participation) {
        participations.add(participation);
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

    public List<Participation> getParticipations() {
        return participations;
    }
}
