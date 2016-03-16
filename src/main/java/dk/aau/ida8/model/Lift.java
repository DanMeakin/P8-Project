package dk.aau.ida8.model;

public class Lift {
    private boolean isAccepted;
    private boolean isPerformed;
    private int weight;

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
