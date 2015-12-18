package com.company;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Decision_Tree {

	ArrayList<Tuple> tuples  ;
	public static int TIME=50;
	public Decision_Tree()
	{
		tuples = new ArrayList<Tuple>();
		for(int i=1;i<=9;i++)
		{
			boolean[] done =new boolean[11];

			String name = "mHealth_subject"+Integer.toString(i)+".log";

			try {
				Scanner in = new Scanner(new File(new String(name)));
				while(in.hasNextLine())
				{
					String line = in.nextLine();
					StringTokenizer st = new StringTokenizer(line,"\t");
					String[] temp =new String[24];

					for(int k=0;k<24;k++)
					{
						temp[k]= st.nextToken();
					}
					int classlabel =  Integer.parseInt(temp[23]);

					//classlabel--;
					if(classlabel<11 &&!done[classlabel])
					{
						System.out.println(classlabel);
						done[classlabel]=true;
						Tuple tup = new Tuple();
						tup.classLabel = classlabel;
						for(int j=0;j<TIME;j++)
						{
							String line1 = in.nextLine();
							StringTokenizer st1 = new StringTokenizer(line1,"\t");
							for(int k=0;k<23;k++)
							{
								Double temp1 = Double.parseDouble(st1.nextToken());
								tup.attributes[k][j]=temp1;
							}
						}
						tuples.add(tup);
					}

				}

				for(int j=0;j<11;j++)
				{
					if(!done[j]) System.err.println(j+" not present in file "+ new String(name) );
				}
			} catch (FileNotFoundException e)
			{
				System.out.println(new String(name));
				e.printStackTrace();
			}
		}
	}



	public TreeNode BuildTreeNode(ArrayList<Tuple> ls)
	{

			ArrayList<Tuple> l1 = new ArrayList<Tuple>();
			ArrayList<Tuple> l2 = new ArrayList<Tuple>();


			TrainingSet t = new TrainingSet(ls);
			TreeNode root = new TreeNode();
			root.node = t.Search_test();
			root.left = null;
			root.right = null;

			//Use this in case the pointer method doesnt work:
			for (int i = 0; i < ls.size(); i++)
			{
				if (t.test(root.node.pattern, ls.get(i).attributes[root.node.attr], root.node.threshold))
					l1.add(ls.get(i));
				else
					l2.add(ls.get(i));
			}


			if(l1.isEmpty()==false&&l2.isEmpty()==false)
			{
				root.left = BuildTreeNode(l1);
				root.right = BuildTreeNode(l2);
				root.classLabel =-1;
			}

			return root;

	}



	public int TestData(Tuple newTuple)
	{

		TreeNode root = BuildTreeNode(this.tuples);
		TreeNode traveller = root;
		TrainingSet tester = new TrainingSet(null);

		while(traveller.right!=null || traveller.left!=null)
		{
			if((tester.test(root.node.pattern,newTuple.attributes[root.node.attr],root.node.threshold))&&traveller.left!=null)
				traveller = traveller.left;
			else
				traveller = traveller.right;
		}
		return traveller.classLabel;
	}


	public static void main(String args[])
	{
		Decision_Tree Tree = new Decision_Tree();
		Tuple testData= new Tuple();
		String activity=null;

		char[] name = "mHealth_subject10.log".toCharArray();
		try {
			Scanner in1 = new Scanner(new File(new String(name)));

					for(int j=0;j<TIME;j++)
					{
						String line1 = in1.nextLine();
						StringTokenizer st1 = new StringTokenizer(line1,"\t");
						String[] temp =new String[24];
						for(int k=0;k<24;k++)
							temp[k]= st1.nextToken();
						int classlabel =  Integer.parseInt(temp[23]);
						if(classlabel<11)
						{
							for(int k=0;k<23;k++)
							{
								Double temp1 = Double.parseDouble(temp[k]);
								testData.attributes[k][j]=temp1;
							}
						}

					}

		} catch(FileNotFoundException e)
		{
			System.out.println(new String(name));
			e.printStackTrace();
		}
		int ClassLabel = Tree.TestData(testData);

		switch(ClassLabel)
		{
			case 0: activity = "Standing stil";
					break;
			case 1: activity = "Sitting and relaxing";
				break;
			case 2: activity = "Lying down";
				break;
			case 3: activity = "Walking";
				break;
			case 4: activity = "Climbing stairs";
				break;
			case 5: activity = "Waist bends forward";
				break;
			case 6: activity = "Frontal elevation of arms";
				break;
			case 7: activity = "Knees bending (crouching)";
				break;
			case 8: activity = "Cycling";
			break;
			case 9: activity = "Jogging";
				break;
			case 10: activity = "Running";
				break;
			default: activity = "tickling";
				break;
		}
			System.out.println(activity);

			if(testData.classLabel==ClassLabel)
			{
				System.out.println("Correct Prediction");
			}
		return ;
	}
}