package stemmer;

import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.commons.lang.StringUtils;

public class WordNormalizer {

	String sentence;
	String dottedPattern = "";
	String camelCasePattern = "([A-Z][a-z0-9]+)+";
	String classPattern = "([A-Z][a-z]+)+";
	String methodPattern = "([a-z]+[A-Z][a-z]+)+";
	String numberPattern="[0-9]+";

	public WordNormalizer(String sentence) {
		// initialization
		this.sentence = sentence;
	}

	protected ArrayList<String> decomposeDotToken(String token) {
		// decomposing dotted tokens
		ArrayList<String> decomposed = new ArrayList<>();
		if (token.contains(".")) {
			String[] singles = token.split("\\.");
			for (String single : singles) {
				decomposed.add(single);
			}
		} else
			decomposed.add(token);
		return decomposed;
	}

	protected ArrayList<String> decomposeCamelCase(String token) {
		// decomposing camel case tokens
		ArrayList<String> refined = new ArrayList<>();
		if (token.matches(this.camelCasePattern)) {
			String[] tokenparts = StringUtils
					.splitByCharacterTypeCamelCase(token);
			if (tokenparts.length > 1) {
				for (String part : tokenparts) {
					refined.add(part);
				}
			} else
				refined.add(token);
		} else
			refined.add(token);
		return refined;
	}

	protected ArrayList<String> removeSpecialChars(String word) {
		// removing special characters
		String regex = "\\p{Punct}+";
		String[] parts = word.split(regex);
		ArrayList<String> refined = new ArrayList<>();
		for (String part : parts) {
			if (!part.trim().isEmpty()) {
				if (part.length() > 2) {
					refined.add(part);
				}
			}
		}
		// if(modifiedWord.isEmpty())modifiedWord=word;
		return refined;
	}

	public String normalizeSentence() {
		// normalize the sentences
		String refinedSentence = new String();
		Stemmer stemmer = new Stemmer();
		StringTokenizer tokenizer = new StringTokenizer(this.sentence);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			ArrayList<String> dotRemoved = removeSpecialChars(token);// decomposeDotToken(token);
			for (String token2 : dotRemoved) {
				// String stemmedToken=stemmer.stripAffixes(token2);
				if (token2.matches(classPattern))
					refinedSentence += token2 + " ";
				else if (token2.matches(methodPattern))
					refinedSentence += token2 + " ";
				else if(token2.matches(numberPattern)){
					//do not add the token
				}
				else {
					if(token2.length()>2){
						String stoken=stemmer.stripSuffixes(token2, true);
						refinedSentence+=stoken+" ";
						//refinedSentence+=token2+" ";
					}
					/*String stemmedToken = stemmer.stripAffixes(token2);
					if (stemmedToken.length() > 2) {
						refinedSentence += stemmedToken + " ";
					}*/
				}
			}
		}
		return refinedSentence;
	}

	public static void main(String[] args) {
		String sentence = "Both Checkstyle and Jalopy require entries in build.properties() org.eclipse.ui.DisplayManager disco[index] to locate their implementations see build.properties.sample for examples.";
		WordNormalizer norm = new WordNormalizer(sentence);
		System.out.println(norm.normalizeSentence());
	}
}
