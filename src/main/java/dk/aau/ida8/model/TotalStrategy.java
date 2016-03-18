package dk.aau.ida8.model;


public class TotalStrategy implements ScoreStrategy {

    @Override
    public double calculateScore(Participation participation) {
        return participation.getBestCleanAndJerk().getScore() +
                participation.getBestSnatch().getScore();
    }
}
