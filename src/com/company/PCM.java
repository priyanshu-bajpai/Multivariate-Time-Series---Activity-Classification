package com.company;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
public class PCM {
    public static int TIME = 50;
    public double [] a;
    public int S;


    public double Variance(int lowerindex, int upperindex)
    {
        double var=0;
        double mean = Mean(lowerindex,upperindex);

        for(int l=lowerindex;l<upperindex;l++)
            var = var + ((a[l] - mean)*(a[l] - mean));

        return var/(upperindex - lowerindex);

    }
    public double Score(int lower, int upper, int l)
    {
        return   (upper-lower)*Variance(lower,upper) - (upper-l)*Variance(l,upper) -(l-lower)*Variance(lower,l);
    }

    public double Mean(int lowerindex, int upperindex)
    {
        double answer=0;
        for(int l=lowerindex;l <upperindex;l++)
            answer = answer + a[l];

        return answer/(upperindex-lowerindex);
    }

    public ArrayList<Segment> Build_PCM(double []a, int S)
    {
        this.S = S;
        this.a = a;

        int i = 0, j = a.length, l=0;
        double val = Mean(i,j);
        double score=0;

        for(int k=0;k<j;k++)
        {
            double temp = Score(i, j, k);
            if (score <temp)
            {
                l = k;
                score = temp;
            }
        }

        Triplet t = new Triplet(val,i,j,score,l);
        ArrayList <Triplet> list = new ArrayList<Triplet>();
        list.add(t);

        while(list.size()<S)
        {
            double maxscore = -1;
            int maxindex = -1;

            for(i=0;i<list.size();i++)
            {
                if (list.get(i).score > maxscore)
                {
                    maxscore = list.get(i).score;
                    maxindex = i;
                }
            }

            int l1 = list.get(maxindex).l;
            i = list.get(maxindex).i;
            j = list.get(maxindex).j;
            list.remove(maxindex);

            score =0;

            for(int k=i+1;k<l1;k++)
            {
                double temp = Score(i,l1,k);
                if (score<temp)
                {
                    l=k;
                    score=temp;
                }
            }
            val = Mean(i,l1);
            Triplet t1 = new Triplet(val,i,l1,score,l);
            list.add(t1);

            score =0;
            for(int k=l1+1;k<j;k++)
            {
                double temp = Score(l1,j,k);
                if (score<temp)
                {
                    l=k;
                    score=temp;
                }
            }
            val = Mean(l1,j);
            Triplet t2 = new Triplet(val,l1,j,score,l);
            list.add(t2);



        }
        //sort the list here according to the intial time
        Collections.sort(list, new Comparator<Triplet>() {
            @Override
            public int compare(Triplet z1, Triplet z2) {
                if (z1.i > z2.i)
                    return 1;
                if (z1.i < z2.i)
                    return -1;
                return 0;
            }
        });

        ArrayList <Segment> list1 = new ArrayList<Segment>();

        for(i=0;i<S;i++)
        {
            Segment s = new Segment(list.get(i).j - list.get(i).i, list.get(i).a);
            list1.add(s);
        }
        //pcModel pc = new pcModel(list1);


    return list1;
    }

    public ArrayList<Segment> pcmWithPruning(double[] atr )
    {
        double[] atrEv = new double[atr.length/2];
        double[] atrOd = new double[atr.length/2];
        int j=0;
        for(int i=0;i<atr.length;i+=2)
        {
            atrEv[j] =atr[i];
            atrOd[j]=atr[i+1];
            j++;
        }
        ArrayList<ArrayList<Segment>> models = new  ArrayList<ArrayList<Segment>>();
        for (int m=0;m<((atr.length/2)+1);m++)
            models.add(null);

        for(int i=1;i<=atr.length/2;i++)
        {
                models.set(i, Build_PCM(atrEv,i));
        }

        double error=Double.MAX_VALUE;
        int s=1;
        for(int i=1;i<=atr.length/2;i++)
        {
            double temp= findError(models.get(i),atr);
            if(error>temp){ error=temp; s=i;}
        }

        return Build_PCM(atr,s);
    }
    public double[] ConvertDouble(ArrayList<Segment> a)
    {
        double [] d = new double[TIME/2];
        int i=0;

        for(Segment s: a)
        {
            for(int j=0;j<s.d;j++)
                d[i] = s.a;
            i++;
        }

        return d;
    }

    private double findError(ArrayList<Segment> mod, double[] atr)
    {
        double []array = ConvertDouble(mod);
        double error =0;
        for(int k=0;k<array.length;k++)
                error+= Math.pow(array[k] - atr[k],2);

        return error;
    }

}