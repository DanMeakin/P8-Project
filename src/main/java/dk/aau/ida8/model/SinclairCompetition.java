package dk.aau.ida8.model;

public class SinclairCompetition implements CompetitionType {

    /**
     * ACTUAL TOTAL × SINCLAIR COEFFICIENT = SINCLAIR TOTAL
     * @param lifter
     * @return sinclair total
     */
    @Override
    public double calcScore(Lifter lifter){
        double sinclairTotal = SinclairCoefficient.calcLifterCoefficient(lifter) * lifter.getTotalScore();

        return sinclairTotal;
    }

}
