package com.example.dose.score;

public enum Riskband {
    VERY_LOW("Very Low", Intensity.MAINTAIN),
    LOW("Low", Intensity.ENCOURAGE),
    MODERATE("Moderate", Intensity.SUPPORT),
    HIGH("High", Intensity.INTENSIVE),
    VERY_HIGH("Very High", Intensity.PRIORITY);

    private final String label;
    private final Intensity intensity;

    Riskband(String label, Intensity intensity){
        this.label=label;
        this.intensity=intensity;
    }

    public String label(){
        return label;
    }

    public Intensity intensity(){
        return intensity;
    }

    /** Intensité des rappels : nombre de rappels par semaine. */
    public enum Intensity {
        MAINTAIN(3),
        ENCOURAGE(5),
        SUPPORT(7),
        INTENSIVE(7),
        PRIORITY(7);


        private final int remindersPerWeek;

        Intensity(int remindersPerWeek) {
            this.remindersPerWeek = remindersPerWeek;
        }

        public int remindersPerWeek() {
            return remindersPerWeek;
        }
    }


}
