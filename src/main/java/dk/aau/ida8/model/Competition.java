package dk.aau.ida8.model;

import java.util.*;


public class Competition {

    private long id;
    private CompetitionType type;
    private ArrayList<Lifter> lifters;
    private String competitionName;
    private String address;
    private Date date;
    private String clubName;
    private boolean weightInCompleted;
    private boolean snatchCompleted;
    private boolean cleanAndJerkCompleted;
    private boolean finished;

    public Competition() {
    }

    public Competition(String competitionName, CompetitionType type, String address, Date date, String clubName, long id) {
        this.competitionName = competitionName;
        this.type = type;
        this.address = address;
        this.date = date;
        this.clubName = clubName;
        this.id = id;
    }


    /**
     * Adds a new lifter to the competition
     *
     */

    public void addLifterToCompetition(int startWeightCj, int startWeightSnatch){
        getLifters().add(new Lifter(startWeightCj, startWeightSnatch));
    }

    /**
     * Removes a competitor from the list of lifters
     * @param lifter who is to be removed
     */
    public void removeLifterFromCompetition (Lifter lifter){
        getLifters().remove(getLifters().indexOf(lifter));
    }

    /**
     * finds a lister based on his index in the ArrayList lifters
     * @param index in the lifters ArrayList
     * @return
     */
    public Lifter getLifter(int index){
        return getLifters().get(index);
    }

    public void ArrangeStartingOrderOfLifts(){
        Collections.sort(getLifters(), new Comparator<Lifter>() {
            @Override public int compare(Lifter l1, Lifter l2) {
                return l1.nextLift(l1.getSnatches()).getWeight() - l2.nextLift(l2.getSnatches()).getWeight(); // Ascending
            }
        });
    }


    public ArrayList<Lifter> arrangeOrderOfLifts() {
        //find the lowest startWeight


        //find the order of the lifters based on weightTry


        //arrange the
    }



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CompetitionType getType() {
        return type;
    }

    public void setType(CompetitionType type) {
        this.type = type;
    }

    public ArrayList<Lifter> getLifters() {
        return lifters;
    }

    public void setLifters(ArrayList lifters) {
        this.lifters = lifters;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public boolean isWeightInCompleted() {
        return weightInCompleted;
    }

    public void setWeightInCompleted(boolean weightInCompleted) {
        this.weightInCompleted = weightInCompleted;
    }

    public boolean isSnatchCompleted() {
        return snatchCompleted;
    }

    public void setSnatchCompleted(boolean snatchCompleted) {
        this.snatchCompleted = snatchCompleted;
    }

    public boolean isCleanAndJerkCompleted() {
        return cleanAndJerkCompleted;
    }

    public void setCleanAndJerkCompleted(boolean cleanAndJerkCompleted) {
        this.cleanAndJerkCompleted = cleanAndJerkCompleted;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
