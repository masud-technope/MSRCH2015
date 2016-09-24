package misc;

import utility.ContentLoader;
import config.StaticData;

public class PercentMaker {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileName=StaticData.CHALLENGE_DATA+"/user-ac-unsolved.txt";
		String content=ContentLoader.loadFileContent(fileName);
		String[] lines=content.split("\n");
		for(String line:lines){
			if(!line.trim().isEmpty()){
				String[] parts=line.split("\\s+");
				int total=Integer.parseInt(parts[1]);
				int accepted=Integer.parseInt(parts[2]);
				int notAccepted=total-accepted;
				double nacperc=(double)notAccepted/total;
				double acperc=(double)accepted/total;
				System.out.println(parts[0]+"\t"+parts[1]+"\t"+nacperc+"\t"+acperc);
			}
		}
	}
}
