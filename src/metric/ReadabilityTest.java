package metric;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import utility.ContentLoader;
import ca.usask.cs.text.readability.FleschKincaidReadingEase;
import ca.usask.cs.text.readability.ReadabilityGradeProvider;
import config.StaticData;

public class ReadabilityTest {

	public static void main(String[] args){
	
		int postID=25579365;
		String fileName=StaticData.CHALLENGE_DATA+"/posts/"+postID+".txt";
		String content = ContentLoader.loadFileContent(fileName);
		Document document = Jsoup.parse(content);
		
		Elements codes=document.select("code,blockquote");
		String codeText=codes.text();
		double codereadability=0;
		if(!codeText.isEmpty())
		codereadability=raykernel.apps.readability.eval.Main.getReadability(codeText);
		System.out.println("Code:"+codeText);
		System.out.println("Code readability:"+codereadability);
		
		document.select("code,a,blockquote").remove();
		String prose = document.text();
		// preprocessing the texts
		prose = prose.replace(':', '.');
		prose = prose.replace('?', '.');
		
		System.out.println("NL text:"+prose);
		// collecting the readability scores
		FleschKincaidReadingEase easeCalc = new FleschKincaidReadingEase(prose);
		double ease=0;
		if(!prose.isEmpty())
		ease= easeCalc.getReadingEase();
		
		ReadabilityGradeProvider provider = new ReadabilityGradeProvider(prose);
		double grade = 0;
		if(!prose.isEmpty())
		grade=provider.getAvgReadabilityGrade();
		
		System.out.println("Readability Ease:"+ease);
		System.out.println("Readability Grade:"+grade);
	}
}
