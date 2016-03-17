package dk.aau.ida8.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.List;

public class Lifter {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private String club;
    private String gender;
    private int lifterNumber;
    private float bodyWeight;
    private int bestCj;
    private int bestSnatch;
    private int totalScore;
    private Double sinclairScore;
    private List<Lift> cleanJerks;
    private List<Lift> snatches;
    private boolean isDoneWithSnatch;
    private boolean isDoneWithCj;


    public Lifter() {
    }

    public Lifter(int startWeightCj, int startWeightSnatch) {
        this.cleanJerks = Arrays.asList(
                new Lift(startWeightCj),
                new Lift(0),
                new Lift(0)
        );
        this.snatches = Arrays.asList(
                new Lift(startWeightSnatch),
                new Lift(0),
                new Lift(0)
        );
    }

    public Lifter(String name, String club, String gender, int lifterNumber, float bodyWeight, int startWeightCj, int startWeightSnatch) {
        this.name = name;
        this.club = club;
        this.gender = gender;
        this.lifterNumber = lifterNumber;
        this.bodyWeight = bodyWeight;
        this.bestCj = 0;
        this.bestSnatch = 0;
        this.totalScore = 0;
        this.sinclairScore = 0.0;
        this.cleanJerks = Arrays.asList(
                new Lift(startWeightCj),
                new Lift(0),
                new Lift(0)
        );
        this.snatches = Arrays.asList(
                new Lift(startWeightSnatch),
                new Lift(0),
                new Lift(0)
        );
        this.isDoneWithCj = false;
        this.isDoneWithSnatch = false;
    }

    public boolean isDoneWithSnatch() {
        return isDoneWithSnatch;
    }

    public void setDoneWithSnatch(boolean doneWithSnatch) {
        isDoneWithSnatch = doneWithSnatch;
    }

    public boolean isDoneWithCj() {
        return isDoneWithCj;
    }

    public void setDoneWithCj(boolean doneWithCj) {
        isDoneWithCj = doneWithCj;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getLifterNumber() {
        return lifterNumber;
    }

    public void setLifterNumber(int lifterNumber) {
        this.lifterNumber = lifterNumber;
    }

    public float getBodyWeight() {
        return bodyWeight;
    }

    public void setBodyWeight(float bodyWeight) {
        this.bodyWeight = bodyWeight;
    }

    public int getBestCj() {
        return bestCj;
    }

    public void setBestCj(int bestCj) {
        this.bestCj = bestCj;
    }

    public int getBestSnatch() {
        return bestSnatch;
    }

    public void setBestSnatch(int bestSnatch) {
        this.bestSnatch = bestSnatch;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public Double getSinclairScore() {
        return sinclairScore;
    }

    public void setSinclairScore(Double sinclairScore) {
        this.sinclairScore = sinclairScore;
    }

    public List<Lift> getCleanJerks() {
        return cleanJerks;
    }

    public void setCleanJerks(List<Lift> cleanJerks) {
        this.cleanJerks = cleanJerks;
    }

    public List<Lift> getSnatches() {
        return snatches;
    }

    public void setSnatches(List<Lift> snatches) {
        this.snatches = snatches;
    }

    /**
     * This method returns the next lift that is to be performed by the lifter
     * @param listOfLifts is the list containing the 3 lifts withing the lift categories
     * @return the lift object
     */
    public Lift nextLift(List<Lift> listOfLifts){

        for (Lift lift : listOfLifts) {
            if (lift.isPerformed() == false){
                return lift;
            }
        }
        return null;
    }

    /**
     * Sets if the chosen lift is accepted or rejected, and also marks the lift as performed.
     * Assumes that the liftnumber stars from 1 in the category of CJ or Snatch
     * @param listOfLift is the list containing the 3 lifts withing the lift categories
     * @param numberOfLift is the number of the specific lift in the category
     * @param isAccepted true if the lift is accepted, false otherwise
     */
    public void updateLiftStatus(List<Lift> listOfLift, int numberOfLift, boolean isAccepted){
        Lift l = listOfLift.get(numberOfLift - 1);
        l.setAccepted(isAccepted);
        l.setPerformed(true);

        // call methods to update state based on the lift performed
        this.setNextWeightAutomatically(listOfLift, numberOfLift, isAccepted);
        this.calculateIfDone();
    }

    private void calculateIfDone(){

        if (snatches.get(snatches.size() - 1).isPerformed() == true) {
            setDoneWithSnatch(true);
        }

        if (cleanJerks.get(cleanJerks.size() - 1).isPerformed() == true) {
            setDoneWithCj(true);
        }

    }

    private void setNextWeightAutomatically(List<Lift> listOfLifts, int numberOfPreviousLift, boolean isAccepted){

        if(numberOfPreviousLift != 3) {
            Lift nextLift = listOfLifts.get(numberOfPreviousLift);
            if(isAccepted){
                nextLift.setWeight(listOfLifts.get(numberOfPreviousLift - 1).getWeight() + 1);
            } else {
                nextLift.setWeight(listOfLifts.get(numberOfPreviousLift - 1).getWeight());
            }

        }
    }

    /**
     * Updates the weight of the chosen lift
     * @param listOfLift is the list containing the 3 lifts withing the lift categories
     * @param numberOfLift is the number of the specific lift in the category
     * @param weight the desired weight to update the lift with
     */
    public void updateLiftWeight(List<Lift> listOfLift, int numberOfLift, int weight){
        Lift l = listOfLift.get(numberOfLift - 1);
        l.setWeight(weight);
    }

}
