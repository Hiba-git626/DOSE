package com.example.dose.entity;

public enum AgeBand {
    AGE_10_15,
    AGE_16_17,
    AGE_18_19;

    public static AgeBand fromAge(int age){
        if(age>=10 && age<=15){
            return AGE_10_15;
        }

        if(age == 16 || age == 17){
            return AGE_16_17;
        }

        if(age == 18 || age == 19){
            return AGE_18_19;
        }

        throw new IllegalArgumentException("Age " + age + " is outside 10-19");
    }
}
