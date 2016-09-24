package misc;

import utility.ContentLoader;
import config.StaticData;

public class ColSplitter {
	public static void main(String[] args){
		String fileName=StaticData.CHALLENGE_DATA+"/pointer/post-user-q-solved.txt";
		String content=ContentLoader.loadFileContent(fileName);
		String[] lines=content.split("\n");
		int lostuser=0;
		for(String line:lines){
			if(!line.trim().isEmpty()){
			//System.out.println(line.split("\\s+")[0]+",");
			if(line.trim().split("\\s+")[1].equals("null")){
				lostuser++;
			}
			}
		}
		System.out.println("User deleted:"+lostuser);
	}
}
