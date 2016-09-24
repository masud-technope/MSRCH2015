package misc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileCopier {
	public static void main(String[] args){
		String tempfile="C:/My MSc/MyCourses/CMPT 352/Ass1/marking_template.docx";
		String folder="C:/My MSc/MyCourses/CMPT 352/Ass1";
		File[] files=new File(folder).listFiles();
		for(File f:files){
			String secondFolder=f.getName();
			if(!secondFolder.startsWith("A")){
				String destFile=f.getAbsolutePath()+"/marking_template.docx";
				try {
					if(new File(destFile).delete()){
					Files.copy(new File(tempfile).toPath(), new File(destFile).toPath());
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
