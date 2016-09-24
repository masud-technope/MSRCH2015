package metric;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;
import config.StaticData;

public class MetricNormalizer {
	
	String metricFile;
	ArrayList<Double> readabilityEase;
	ArrayList<Double> readabilityGrade;
	ArrayList<Double> reputation;
	double maxEase=0;
	double maxGrade=0;
	double maxRep=0;
	
	public MetricNormalizer(String metricFile)
	{
		this.metricFile=metricFile;
		this.readabilityEase=new ArrayList<>();
		this.readabilityGrade=new ArrayList<>();
		this.reputation=new ArrayList<>();
		this.loadMetrics();
	}
	
	protected void loadMetrics()
	{
		//loading the metrics
		try {
			Scanner scanner=new Scanner(new File(this.metricFile));
			while(scanner.hasNext()){
				String line=scanner.nextLine();
				String[] parts=line.split("\\s+");
				
				double readease=Double.parseDouble(parts[0]);
				if(readease>maxEase)maxEase=readease;
				this.readabilityEase.add(readease);
				
				double readgrade=Double.parseDouble(parts[1]);
				if(readgrade>maxGrade)maxGrade=readgrade;
				this.readabilityGrade.add(readgrade);
				
				double repu=Double.parseDouble(parts[4]);
				if(repu>maxRep)maxRep=repu;
				this.reputation.add(repu);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void normalizeMetrics()
	{
		//now normalize the metrics
		for(int i=0;i<readabilityEase.size();i++){
			double val=readabilityEase.get(i);
			val=val/maxEase;
			readabilityEase.set(i, val);
		}
		for(int i=0;i<readabilityGrade.size();i++){
			double val=readabilityGrade.get(i);
			val=val/maxGrade;
			readabilityGrade.set(i, val);
		}
		for(int i=0;i<reputation.size();i++){
			double val=reputation.get(i);
			val=val/maxRep;
			reputation.set(i, val);
		}
	}
	
	protected void saveNewMetrics()
	{
		//now save new metrics
		try {
			String outFile=StaticData.CHALLENGE_DATA+"/metrics-norm.txt";
			FileWriter fwriter=new FileWriter(new File(outFile));
			Scanner scanner=new Scanner(new File(this.metricFile));
			int count=0;
			while(scanner.hasNext()){
				String line=scanner.nextLine();
				String[] parts=line.split("\\s+");
				String sline=new String();
				sline+=this.readabilityEase.get(count)+"\t";
				sline+=this.readabilityGrade.get(count)+"\t";
				sline+=parts[2]+"\t";
				sline+=parts[3]+"\t";
				sline+=this.reputation.get(count)+"\t";
				sline+=parts[5]+"\t";
				sline+=parts[6];
				fwriter.write(sline+"\n");
				count++;
			}
			scanner.close();
			fwriter.close();
			System.out.println("Saved new metrics");
		}catch(Exception exc){
			//handle the exception
		}
	}
	
	public static void main(String[] args){
		String metricFile=StaticData.CHALLENGE_DATA+"/metrics2.txt";
		MetricNormalizer mnormalizer=new MetricNormalizer(metricFile);
		mnormalizer.normalizeMetrics();
		mnormalizer.saveNewMetrics();
	}
}
