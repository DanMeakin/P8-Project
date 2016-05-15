package dk.aau.ida8.util;

import dk.aau.ida8.model.Lifter;
import dk.aau.ida8.model.Participant;

import java.util.function.Function;

public class SinclairCalculator implements Function<Participant, Double> {

    public Double apply(Participant participant){
        return calculateScore(participant);
    }

    /**
     * Calculates the Sinclair coefficient for a given participant.
     *
     * The calculation is described by the formula: 10^(A log10(x/b)^2, where
     *
     *  x is the participant's body weight
     *  A is the official coefficient for this Olympic cycle, where this
     *    coefficient is less than the participant's bodyweight, otherwise it is set
     *    to 1.0
     *  b is the bodyweight of the world record holder in the heaviest category
     *
     * @param participant the participant for whom to calculate the Sinclair coefficient
     * @return the Sinclair coefficient for the passed participant
     */
    private static double sinclairCoefficient(Participant participant) {
        double genderCoefficient;
        double genderBodyweight;
        if (participant.getGender().equals(Lifter.Gender.MALE)) {
            genderCoefficient = SinclairCoefficient.getMaleCoefficient();
            genderBodyweight = SinclairCoefficient.getMaleWrhBodyweight();
        } else if (participant.getGender().equals(Lifter.Gender.FEMALE)) {
            genderCoefficient = SinclairCoefficient.getFemaleCoefficient();
            genderBodyweight = SinclairCoefficient.getFemaleWrhBodyweight();
        } else {
            throw new IllegalArgumentException("unknown gender for lifter: " + participant);
        }
        return Math.pow(10, genderCoefficient * Math.pow(Math.log10(participant.getBodyWeight() / genderBodyweight), 2));
    }

    /**
     * Calculates the score for a participant in a
     * competition.
     *
     * The Sinclair scoring strategy involves the calculation of a coefficient
     * for a given participant. This is calculated using the sinclairCoefficient
     * method.
     *
     * The coefficient is then multiplied by the actual total the participant
     * achieved, to determine the "Sinclair" total.
     *
     * @param participant the Participant object representing a participant's
     *                      participation in a given Competition
     * @return the "Sinclair" total score for the competition
     */
    public double calculateScore(Participant participant) {
        int rawScore = participant.getBestCleanAndJerk() +
                participant.getBestSnatch();
        return participant.getTotalScore() * sinclairCoefficient(participant);
    }
}
