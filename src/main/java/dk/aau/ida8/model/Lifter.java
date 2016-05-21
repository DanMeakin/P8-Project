package dk.aau.ida8.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
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
        MALE, FEMALE;

        public String toString() {
            switch(this) {
                case MALE: return "M";
                case FEMALE: return "F";
                default: return "N/A";
            }
        }
    }

    @Id
    @GeneratedValue
    private long id;

    private String forename;
    private String surname;
    private boolean active;

    @ManyToOne
    private Club club;
    private Gender gender;
    private double bodyWeight;

    @DateTimeFormat(pattern = "dd-mm-yyyy")
    private Date dateOfBirth;

    @OneToMany(cascade = {CascadeType.ALL})
    private List<Participant> participants = new ArrayList<>();

    /**
     * Empty constructor required by Hibernate.
     */
    public Lifter() {
        this.active = true;
    }

    /**
     * Creates a lifter instance.
     *
     * @param forename    the lifter's forname
     * @param surname     the lifter's surname
     * @param club        the club of which the lifter is a member
     * @param gender      the lifter's gender
     * @param dateOfBirth the lifter's date of birth
     * @param bodyWeight  the lifter's body weight in kilograms
     */
    public Lifter(String forename, String surname, Club club, Gender gender, Date dateOfBirth, double bodyWeight) {
        this.forename = forename;
        this.surname = surname;
        this.club = club;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.bodyWeight = bodyWeight;
        this.active = true;
    }

   /**
     * Determines the equality of two objects.
     *
     * A lifter is equal to another object only if that other object is
     * a lifter, and it is equal in terms of the
     * {@link #equals(Lifter)} method.
     *
     * @param o the other object to test for equality
     * @return true, if this is equal to o, else false
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof Lifter && equals((Lifter) o);
    }

    /**
     * Determines the equality of two lifters.
     *
     * Two lifters are equal only if they both share the same ID.
     *
     * @param l the other lifter to test for equality
     * @return true, if this is equal to p, else false
     */
    public boolean equals(Lifter l) {
        return getId() == l.getId();
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

    public String getClubName() {
        if (getClub() == null) {
            return "";
        } else {
            return getClub().getName();
        }
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

    public boolean isActive() {
        return active;
    }

    /**
     * Sets this lifter's active state.
     *
     * @param active the value to set this lifter's active state to
     */
    public void setActive(boolean active) {
        this.active = active;
    }

}
