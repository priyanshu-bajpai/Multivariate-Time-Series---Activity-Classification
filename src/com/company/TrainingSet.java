package com.company;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class TrainingSet
{
    ArrayList<Tuple> ls  ;
    public static int TIME=50;

    public TrainingSet(ArrayList<Tuple>ls)
    {
        this.ls = ls;
    }

    public double Score(ArrayList<Tuple> s,Test test)
    {
        int n1=0,n2=0;
        ArrayList<Tuple> s1 =new ArrayList<Tuple>();
        ArrayList<Tuple> s2 =new ArrayList<Tuple>();

        for(int i=0;i<s.size();i++)
        {
            if(test (test.pattern,s.get(i).attributes[test.attr], test.threshold)){
                n1++;s1.add(s.get(i));
            }
            else
                n2++; s2.add(s.get(i));

        }
        int n=n1+n2;

        double ict= Hc(s) - ((double)n1/(double)n) * Hc(s1) -((double)n2/(double)n) *Hc(s2);
        double ht =  - ((double)n1/(double)n) *Math.log(((double)n1/(double)n))/ Math.log(2) -((double)n2/(double)n) *Math.log(((double)n2/(double)n))/ Math.log(2);
        //l1 = s1;
        //l2 = s2;
        return test.score= 2*ict/(ht+Hc(s));
    }


    public double Hc(ArrayList<Tuple> s)
    {
        int[] numClas = new int[11];
        int n= s.size();
        for(int i=0;i<s.size();i++)
        {
            numClas[s.get(i).classLabel]++;
        }
        double sum=0;
        for(int i=0;i<11; i++)
        {
            sum+= ((double) numClas[i]/(double) n) * Math.log(((double)numClas[i]/(double)n) )/Math.log(2);
        }
        return -sum;
    }

    public double[] ConvertDouble(ArrayList<Segment> a)
    {
        double [] d = new double[TIME];
        int i=0;

        for(Segment s: a)
        {
            for(int j=0;j<s.d;j++)
                d[i] = s.a;
            i++;
        }

        return d;
    }

    public double dist(double[]pattern,double []a)
    {
        double minDist=Double.MAX_VALUE;
        for(int i=0;i<=a.length-pattern.length;i++)
        {
            double temp =0;
            for(int j=0;j<pattern.length;j++)
            {
            //calculate the distance between the corresponding segments in the pattern and sample's attribute values
            temp += Math.pow(pattern[j]-a[i+j], 2);
            }
            if(minDist>temp)
                minDist = temp;
        }

        return minDist;
    }


    public boolean test(double[] p, double[] atr, double thresh)
    {
        double err= dist(p, atr);
        if(err<thresh) return true;
        return false;
    }


    public Test Pattern_Search(ArrayList<Tuple>ls, int attr, double []a)
    {
        System.out.println("Search Best_Pattern for Attribute:"+attr);
        PCM pcm = new PCM();
        double []pat =null;
        double maxscore=0, threshold =100;
        double tempscore;
        int S = TIME;
        int i,k=0;
        Test t ;

        double[] distance = new double[ls.size()];

        for(int index=0;index<S;index++)
        {
            for(int length=2;index+length<=S;length++)
            {
                double[] pattern = new double[length];
                for(k=0;k<length;k++)
                    pattern[k] = a[index+k];

                for(k=0;k< distance.length;k++)
                    distance[k] = dist(pattern,ls.get(k).attributes[attr]);

                Arrays.sort(distance);

                for(k=0;k<distance.length-1;k++)
                {
                    double thresh = (distance[k]+distance[k+1])/2;
                    t = new Test(attr,thresh,0, pattern);
                    tempscore = Score(ls,t);
                    if(maxscore<tempscore)
                    {
                        maxscore = tempscore;
                        threshold = thresh;
                        pat = pattern;

                    }
                }

            }
        }
        System.out.println("PatternSearch for Attribute:"+attr+" over");

        return new Test(attr,threshold,maxscore,pat);
        /*
            ArrayList <ArrayList<Segment>> Dataset = new ArrayList <ArrayList<Segment>>(ls.size());
            //for attribute attr within each object present
            for(i=0;i<ls.size();i++)
               Dataset.set(i,pcm.Build_PCM(ls.get(i).attributes[attr],S));
        */

    }


    public Test Search_test()
    {
        System.out.println("BEST TEST SEARCH");
        PCM pcm = new PCM();
        Test test,best_test=null;
        Tuple tup;
        int attr=0;
        double[] pat=null;
        double maxscore=0;
        double threshold=0;        //store the score and the corresponding threshold value

        ArrayList<ArrayList<Tuple>> clasList = new ArrayList<ArrayList<Tuple>> ();
        for(int i=0;i<11;i++)
        {
            clasList.add(null);
        }
        for(Tuple t: this.ls)
        {
            if(clasList.get(t.classLabel)==null) clasList.set(t.classLabel,new ArrayList<Tuple>());
            clasList.get(t.classLabel).add(t);
        }

        for(int i=0;i<23;i++)
        {
            for(int j=0;j<11;j++)
            {
                System.out.println("CLASS:"+j);
               if(clasList.get(j)!=null)
               {
                   int size = clasList.get(j).size();
                   tup = clasList.get(j).get(new Random().nextInt(size));
                   ArrayList<Segment> a = pcm.pcmWithPruning(tup.attributes[i]);
                   test = Pattern_Search(this.ls, i,ConvertDouble(a));

                   //score = Score(this.ls, test);

                   if(test.score>maxscore||best_test==null)
                       best_test =test;

               }

            }
        }
        System.out.println("BEST TEST SEARCH OVER");
        //test = new Test(attr,threshold,maxscore,pat);
        return best_test;
    }
}