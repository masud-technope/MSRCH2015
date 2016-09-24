package utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import config.StaticData;

public class TopicResultTest {
	
	public static void main(String[] args){
		String keys=StaticData.MALLET_HOME+"/tkeys.txt";
		double prob=0;
		try {
			Scanner scanner=new Scanner(new File(keys));
			while(scanner.hasNextLine())
			{
				String line=scanner.nextLine();
				String[] parts=line.split("\\s+");
				double prob1=Double.parseDouble(parts[1]);
				prob+=prob1;
			}
			scanner.close();
			System.out.println("Total probability:"+prob);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
