package misc;

import utility.ContentLoader;
import config.StaticData;

public class QTitleAnalyzer {

	String titleFile;

	public QTitleAnalyzer(String titleFile) {
		this.titleFile = StaticData.CHALLENGE_DATA + "/" + titleFile;
	}

	protected void analyzeTitles() {
		// analyze question titles
		String content = ContentLoader.loadFileContent(this.titleFile);
		String[] lines = content.split("\n");
		int whatcount = 0;
		int howcount = 0;
		int whycount = 0;
		int whencount = 0;

		for (String line : lines) {
			if (line.toLowerCase().contains("what")) {
				whatcount++;
			} else if (line.toLowerCase().contains("how")) {
				howcount++;
			} else if (line.toLowerCase().contains("why")) {
				whycount++;
			} else if (line.toLowerCase().contains("when")) {
				whencount++;
			}
		}
		
		System.out.println("What questions:" + whatcount);
		System.out.println("How questions:" + howcount);
		System.out.println("Why questions:" + whycount);
		System.out.println("When count:" + whencount);
	}

	public static void main(String[] args) {
		String titleFile = "python-134.txt";
		QTitleAnalyzer analyzer = new QTitleAnalyzer(titleFile);
		analyzer.analyzeTitles();
	}
}
