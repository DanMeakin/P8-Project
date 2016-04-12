package dk.aau.ida8.model;

public class SinclairCoefficient {

    // The coefficient for this Olympic cycle
    private static final double MALE_COEFFICIENT = 0.704358141;
    private static final double FEMALE_COEFFICIENT = 0.897260740;

    //male and female world record holder's bodyweight (in the heaviest category)
    private static final double MALE_WRH_BODYWEIGHT = 174.393;
    private static final double FEMALE_WRH_BODYWEIGHT = 148.026;

    public SinclairCoefficient(){};

    /**
     * calculates an individual lifters sinclair coefficient with regards to gender.
     * Follows the official calculation method: 10^(A(log10(x/b))^2).
     * x = this lifters bodyweight
     * b = the bodyweight of the world record holder in the heaviest category
     * A = the official coefficient for this olympic cycle which is used as long as the lifters bodyweight is less than the world record holders body weight. If its larger the coefficient is 1.0
     * @param lifter
     * @return sinclairCoefficient for the lifter
     */
    public static double calcLifterCoefficient(Lifter lifter){
        double sinclairCoefficient = 0;

        //calculates the lifters sinclair coefficient according to gender on gender
        if(lifter.getGender().equals("M")) {

            sinclairCoefficient = Math.pow(10, MALE_COEFFICIENT * (Math.pow(Math.log10(lifter.getBodyWeight() / MALE_WRH_BODYWEIGHT), 2)));

        } else if(lifter.getGender().equals("F")){

            sinclairCoefficient = Math.pow(10, FEMALE_COEFFICIENT * (Math.pow(Math.log10(lifter.getBodyWeight() / FEMALE_WRH_BODYWEIGHT), 2)));
        }

        return sinclairCoefficient;
    }

}
