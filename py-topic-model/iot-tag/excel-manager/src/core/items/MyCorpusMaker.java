package core.items;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

public class MyCorpusMaker {

	private static CellProcessor[] getProcessors() {

		final CellProcessor[] processors = new CellProcessor[] { new NotNull(new ParseInt()), // Id
				new Optional(new ParseInt()), // ParentId
				new NotNull(new ParseInt()), // PostType 1=question, 2=answer, 3=comment
				new NotNull(new ParseInt()), // score
				new ParseDate("yyyy-MM-dd HH:mm"), // creationDate
				new Optional(new ParseInt()), // owner user id
				new NotNull() // body

		};

		return processors;
	}

	protected static String removeNewLine(String textBody) {
		return textBody.replaceAll("\n", "\t");
	}

	protected static ArrayList preprocessRow(List myRow, int threadID) {
		// preprocess the row
		ArrayList myList = new ArrayList(myRow);

		// cleaning the post body
		String textBody = (String) myList.get(myList.size() - 1);
		String cleanedBody = removeNewLine(textBody);
		myList.set(myList.size() - 1, "\"" + cleanedBody + "\"");

		// adding a thread ID
		myList.add(0, threadID);

		return myList;
	}

	public static boolean writeContent(String outFile, ArrayList<String> items) {
		// writing content to output
		boolean written = false;
		try {
			FileWriter fwriter = new FileWriter(new File(outFile));
			for (String item : items) {
				fwriter.write(item + "\n");
			}
			fwriter.close();
			written = true;

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return written;
	}

	protected static void saveRow(ArrayList<List> masterList, int threadID) {
		String threadFile = "./so-posts/" + threadID + ".csv";
		ArrayList<String> outputItems = new ArrayList<String>();
		String header="thread_ID, Id, ParentId, PostType, Score, CreationDate, OwnerID, Body";
		outputItems.add(header);
		for (List myRow : masterList) {
			ArrayList<String> items = new ArrayList<String>(myRow);
			String singleLine = new String();
			for (Object item : items) {
				if (item instanceof Integer) {
					singleLine += "," + String.valueOf(item);
				} else {
					singleLine += "," + item;
				}
			}
			outputItems.add(singleLine.substring(1));
		}
		writeContent(threadFile, outputItems);
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String CSV_FILENAME = "./qa-iot.csv";
		String CSV_COMM_FILENAME = "./qa-comments-iot.csv";

		HashMap<Integer, List> itemMap = new HashMap<Integer, List>();
		HashMap<Integer, ArrayList<Integer>> questionAnswerMap = new HashMap<Integer, ArrayList<Integer>>();
		HashMap<Integer, List> commentMap = new HashMap<Integer, List>();
		HashMap<Integer, ArrayList<Integer>> postCommentMap = new HashMap<Integer, ArrayList<Integer>>();

		ArrayList<Integer> threadIDs = new ArrayList<Integer>();

		// reading the question and answers
		ICsvListReader listReader = null;
		try {
			listReader = new CsvListReader(new FileReader(CSV_FILENAME), CsvPreference.STANDARD_PREFERENCE);
			listReader.getHeader(true); // skip the header (can't be used with CsvListReader)
			final CellProcessor[] processors = getProcessors();

			List<Object> customerList;
			while ((customerList = listReader.read(processors)) != null) {
				int Id = (int) customerList.get(0);
				try {
					int parentId = (int) customerList.get(1);
					if (parentId > 0) {
						if (questionAnswerMap.containsKey(parentId)) {
							ArrayList<Integer> temp = questionAnswerMap.get(parentId);
							temp.add(Id);
							questionAnswerMap.put(parentId, temp);
						} else {
							ArrayList<Integer> temp = new ArrayList<Integer>();
							temp.add(Id);
							questionAnswerMap.put(parentId, temp);

						}
					}
				} catch (Exception exc) {
					// handle the exception
					// exc.printStackTrace();
					// adding the question
					threadIDs.add(Id);
				}
				itemMap.put(Id, customerList);
			}

			System.out.println("Customer list:" + itemMap.size());
			System.out.println("QA map:" + questionAnswerMap.size());
			System.out.println("Total threads:" + threadIDs.size());

		} finally {
			if (listReader != null) {
				listReader.close();
			}
		}

		// reading the comments and adding them
		try {
			listReader = new CsvListReader(new FileReader(CSV_COMM_FILENAME), CsvPreference.STANDARD_PREFERENCE);
			listReader.getHeader(true); // skip the header (can't be used with CsvListReader)
			final CellProcessor[] processors = getProcessors();

			List<Object> customerList;
			while ((customerList = listReader.read(processors)) != null) {
				int Id = (int) customerList.get(0);
				try {
					int parentId = (int) customerList.get(1);
					if (postCommentMap.containsKey(parentId)) {
						ArrayList<Integer> temp = postCommentMap.get(parentId);
						temp.add(Id);
						postCommentMap.put(parentId, temp);
					} else {
						ArrayList<Integer> temp = new ArrayList<Integer>();
						temp.add(Id);
						postCommentMap.put(parentId, temp);
					}
				} catch (Exception exc) {
					// handle the exception
				}
				commentMap.put(Id, customerList);
			}

			System.out.println("Total comments:" + commentMap.size());
			System.out.println("Comment-Post map:" + postCommentMap.size());

		} finally {
			if (listReader != null) {
				listReader.close();
			}
		}

		// now build a master map
		for (int questionID : threadIDs) {
			ArrayList<List> masterList = new ArrayList<List>();
			List questionRow = itemMap.get(questionID);
			questionRow = preprocessRow(questionRow, questionID);
			masterList.add(questionRow);
			
			if (postCommentMap.containsKey(questionID)) {
				ArrayList<Integer> commentIDs = postCommentMap.get(questionID);
				for (int commentID : commentIDs) {
					List commentRow = commentMap.get(commentID);
					commentRow = preprocessRow(commentRow, questionID);
					masterList.add(commentRow);
				}
			}

			if (questionAnswerMap.containsKey(questionID)) {
				ArrayList<Integer> answers = questionAnswerMap.get(questionID);
				for (int answerID : answers) {
					if (itemMap.containsKey(answerID)) {
						List answerRow = itemMap.get(answerID);
						answerRow = preprocessRow(answerRow, questionID);
						masterList.add(answerRow);
					}

					if (postCommentMap.containsKey(answerID)) {
						ArrayList<Integer> commentIDs = postCommentMap.get(answerID);
						for (int commentID : commentIDs) {
							List commentRow = commentMap.get(commentID);
							commentRow = preprocessRow(commentRow, questionID);
							masterList.add(commentRow);
						}
					}
				}
			}

			saveRow(masterList, questionID);
			System.out.println("Done:" + questionID);

		}
	}
}
