package dk.aau.ida8.model;

public class SinclairStrategy implements ScoreStrategy {

    // The coefficient for this Olympic cycle
    private static final double MALE_COEFFICIENT = 0.704358141;
    private static final double FEMALE_COEFFICIENT = 0.897260740;

    //male and female world record holder's bodyweight (in the heaviest category)
    private static final double MALE_WRH_BODYWEIGHT = 174.393;
    private static final double FEMALE_WRH_BODYWEIGHT = 148.026;

    /**
     * Calculates the score for a participant of a given lifter in a
     * competition.
     *
     * The Sinclair scoring strategy involves the calculation of a coefficient
     * for a given lifter. This is calculated using the sinclairCoefficient
     * method.
     *
     * The coefficient is then multiplied by the actual total the lifter
     * achieved, to determine the "Sinclair" total.
     *
     * @param participant the Participant object representing a Lifter's
     *                      participant in a given Competition
     * @return the "Sinclair" total score for the competition
     */
    @Override
    public double calculateScore(Participant participant){
        Lifter lifter = participant.getLifter();
        int rawScore = participant.getBestCleanAndJerk() +
                participant.getBestSnatch();
        return rawScore * sinclairCoefficient(lifter);
    }

    /**
     * Calculates the Sinclair coefficient for a given lifter.
     *
     * The calculation is described by the formula: 10^(A log10(x/b)^2, where
     *
     *  x is the lifter's body weight
     *  A is the official coefficient for this Olympic cycle, where this
     *    coefficient is less than the lifter's bodyweight, otherwise it is set
     *    to 1.0
     *  b is the bodyweight of the world record holder in the heaviest category
     *
     * @param lifter the lifter for whom to calculate the Sinclair coefficient
     * @return the Sinclair coefficient for the passed lifter
     */
    private static double sinclairCoefficient(Lifter lifter) {
        double genderCoefficient;
        double genderBodyweight;
        if (lifter.getGender().equals("M")) {
            genderCoefficient = MALE_COEFFICIENT;
            genderBodyweight = MALE_WRH_BODYWEIGHT;
        } else if (lifter.getGender().equals("F")) {
            genderCoefficient = FEMALE_COEFFICIENT;
            genderBodyweight = FEMALE_WRH_BODYWEIGHT;
        } else {
            throw new IllegalArgumentException("unknown gender for lifter: " + lifter);
        }
        return Math.pow(10, genderCoefficient * Math.pow(Math.log10(lifter.getBodyWeight() / genderBodyweight), 2));
    }

}
