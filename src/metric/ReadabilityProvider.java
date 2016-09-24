package metric;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import utility.ContentLoader;
import ca.usask.cs.text.readability.FleschKincaidReadingEase;
import ca.usask.cs.text.readability.ReadabilityGradeProvider;
import config.StaticData;

public class ReadabilityProvider {

	String meticFile;
	ArrayList<Integer> answers;
	ArrayList<Integer> questions;
	String readabilityFile;
	String qaFile;
	boolean solved=false;
	

	public ReadabilityProvider(String outmet) {
		this.meticFile = outmet;
		this.answers = new ArrayList<>();
		this.loadAnswerIDs();
		this.readabilityFile = StaticData.CHALLENGE_DATA + "/readability.txt";
	}
	
	public ReadabilityProvider(String qaFile, boolean solved) {
		this.answers = new ArrayList<>();
		this.questions=new ArrayList<>();
		this.solved=solved;
		this.qaFile=StaticData.CHALLENGE_DATA+"/"+qaFile;
		this.readabilityFile = StaticData.CHALLENGE_DATA + "/readability-qst-"+qaFile;
		this.loadQAs();
	}
	
	protected void loadQAs()
	{
		String content=ContentLoader.loadFileContent(this.qaFile);
		String[] lines=content.split("\\n");
		for(String line:lines){
			String[] parts=line.split("\\s+");
			int qid=Integer.parseInt(parts[0]);
			int aid=Integer.parseInt(parts[1]);
			questions.add(qid);
			answers.add(aid);
		}
	}
	
	@Deprecated
	protected void loadAnswerIDs() {
		// loading answer IDs
		String content = ContentLoader.loadFileContent(this.meticFile);
		String[] lines = content.split("\n");
		for (String line : lines) {
			int ansID = Integer.parseInt(line.split("\\s+")[0]);
			answers.add(ansID);
		}
	}

	protected void collectReadabilityScore() {
		try {
			FileWriter fwriter = new FileWriter(new File(this.readabilityFile));
			for (Integer id : this.questions) {
				String postFile = StaticData.CHALLENGE_DATA;
				if(solved){
					postFile +="/solposts/" + id
							+ ".txt";
				}else{
					postFile +="/posts/" + id
							+ ".txt";
				}
				String content = ContentLoader.loadFileContent(postFile);
				Document document = Jsoup.parse(content);
				
				Elements codes=document.select("code");
				String codeText=codes.text();
				double codereadability=0;
				if(!codeText.isEmpty())
				codereadability=raykernel.apps.readability.eval.Main.getReadability(codeText);
				
				document.select("code").remove();
				String prose = document.text();
				// pre-processing the texts
				prose = prose.replace(':', '.');
				prose = prose.replace('?', '.');
				
				// collecting the readability scores
				FleschKincaidReadingEase easeCalc = new FleschKincaidReadingEase(prose);
				double ease=0;
				if(!prose.isEmpty())
				ease= easeCalc.getReadingEase();
				
				ReadabilityGradeProvider provider = new ReadabilityGradeProvider(prose);
				double grade = 0;
				if(!prose.isEmpty())
				grade=provider.getAvgReadabilityGrade();
				
				String line = id + "\t" + ease + "\t" + grade+"\t"+codereadability;
				fwriter.write(line + "\n");
				System.out.println("Completed:"+id);
			}
			fwriter.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public static void main(String[] args) {
		String fileName = "qa-solved.txt";
		ReadabilityProvider rProvider = new ReadabilityProvider(fileName,true);
		rProvider.collectReadabilityScore();
	}
}
