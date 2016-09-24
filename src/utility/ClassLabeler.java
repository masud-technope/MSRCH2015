package utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import config.StaticData;

public class ClassLabeler {

	public static void main(String args[]){
		//adding class label
		String postFile=StaticData.CHALLENGE_DATA+"/postdata2.txt";
		String metric=StaticData.CHALLENGE_DATA+"/metrics.txt";
		try {
			FileWriter fwriter=new FileWriter(new File(metric));
			Scanner scanner=new Scanner(new File(postFile));
			int count=0;
			while(scanner.hasNext())
			{
				String line=scanner.nextLine();
				count++;
				if(count<=50000){
					line+="\t"+1;
				}else line+="\t"+0;
				fwriter.write(line+"\n");
			}
			scanner.close();
			fwriter.close();
			System.out.println("Class labeling completed.");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
