package com.company;

public class Test {
    double threshold;
    double score;
    double []pattern;
    int attr;

    Test(int attr, double threshold,double score,double []pattern)
    {
        this.pattern = pattern;
        this.score = score;
        this.threshold = threshold;
        this.attr = attr;
    }
}