package dk.aau.ida8.model;

public class SinclairCoefficient {

    // The coefficient for this Olympic cycle
    private static double MALE_COEFFICIENT = 0.704358141;
    private static double FEMALE_COEFFICIENT = 0.897260740;

    //male and female world record holder's bodyweight (in the heaviest category)
    private static double MALE_WRH_BODYWEIGHT = 174.393;
    private static double FEMALE_WRH_BODYWEIGHT = 148.026;

    public static double getMaleCoefficient() {
        return MALE_COEFFICIENT;
    }

    public static double getFemaleCoefficient() {
        return FEMALE_COEFFICIENT;
    }

    public static double getMaleWrhBodyweight() {
        return MALE_WRH_BODYWEIGHT;
    }

    public static double getFemaleWrhBodyweight() {
        return FEMALE_WRH_BODYWEIGHT;
    }
}
