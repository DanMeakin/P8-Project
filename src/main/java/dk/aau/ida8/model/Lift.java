package dk.aau.ida8.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Lift implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private boolean isAccepted;
    private boolean isPerformed;
    private int weight;

    public Lift() {

    }

    public Lift(int weight) {
        this.isAccepted = false;
        this.isPerformed = false;
        this.weight = weight;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public boolean isPerformed() {
        return isPerformed;
    }

    public void setPerformed(boolean performed) {
        isPerformed = performed;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
