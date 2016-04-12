package dk.aau.ida8.model;


public class TotalStrategy implements ScoreStrategy {

    @Override
    public double calculateScore(Participant participant) {
        return participant.getBestCleanAndJerk() +
                participant.getBestSnatch();
    }
}
