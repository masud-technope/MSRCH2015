package utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import config.StaticData;

public class CommitHistory {
	
	String pointerFile;
	String saveDir;
	public CommitHistory()
	{
		//initialization
		this.pointerFile=StaticData.SOTraceQData+"/commits/pointer.log";
		this.saveDir="C:/MyWorks/Mallet/mallet-2.0.7/mallet-2.0.7/data";
	}
	
	protected void saveFile(int fileID, String content)
	{
		//saving the commit message
		String saveFileName=saveDir+"/log4j/"+fileID+".txt";
		try {
			FileWriter fwriter=new FileWriter(new File(saveFileName));
			fwriter.write(content);
			fwriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void collectCommitHistory()
	{
		//collecting commit history
		try {
			Scanner scanner=new Scanner(new File(pointerFile));
			int count=0;
			while(scanner.hasNextLine())
			{
				String line=scanner.nextLine();
				String[] parts=line.split("\\s+");
				String content=new String();
				for(int i=1;i<parts.length;i++){
					content+=parts[i]+" ";
				}
				count++;
				saveFile(count, content);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	
	public static void main(String[] args){
		
		CommitHistory commHistory=new CommitHistory();
		commHistory.collectCommitHistory();
	}
}
