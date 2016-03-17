package dk.aau.ida8.model;


public class TotalCompetition implements CompetitionType {

    @Override
    public double calcScore(Lifter lifter) {
        return lifter.getTotalScore();
    }
}
