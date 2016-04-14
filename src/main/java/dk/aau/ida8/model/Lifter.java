package dk.aau.ida8.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class represents one weightlifter.
 *
 * Each weightlifter must be the member of a club to participate in a
 * competition. Their gender, weight and name must be stored.
 *
 * Lifter objects are used in the {@link Participant Participant} class,
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

    @DateTimeFormat(pattern = "dd-mm-yyyy")
    private Date dateOfBirth;

    @OneToMany
    private List<Participant> participants;

    public Lifter() {

    }

    public Lifter(String forename, String surname, Club club, Gender gender, Date dateOfBirth, double bodyWeight) {
        this.forename = forename;
        this.surname = surname;
        this.club = club;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.bodyWeight = bodyWeight;
        this.participants = new ArrayList<>();
    }

    /**
     * Adds a Participant instance for this Lifter.
     *
     * A Participant instance represents the participant of a lifter within
     * a given competition. See {@link Participant Participant} for details.
     *
     * @param participant the participant to aggregate to this lifter
     */
    public void addParticipation(Participant participant) {
        participants.add(participant);
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

    public String getGenderInitial() {
        return getGender().toString().substring(0, 1);
    }

    public double getBodyWeight() {
        return bodyWeight;
    }

    public void setBodyWeight(double bodyWeight) {
        this.bodyWeight = bodyWeight;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public List<Participant> getParticipants() {
        return participants;
    }
}
