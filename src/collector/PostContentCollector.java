package collector;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;
import config.StaticData;

public class PostContentCollector {

	ArrayList<Integer> postIds;

	public PostContentCollector() {
		postIds = new ArrayList<Integer>();
		this.loadPostIds();
	}

	protected void loadPostIds() {
		// loading the post IDs
		String metricFile = StaticData.CHALLENGE_DATA + "/answer.txt";
		try {
			Scanner scanner = new Scanner(new File(metricFile));
			scanner.nextLine();
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				String[] parts = line.split("\\s+");
				int postID = Integer.parseInt(parts[0].trim());
				// int answerID = Integer.parseInt(parts[1].trim());
				this.postIds.add(postID);
				// this.postIds.add(answerID);
			}
			scanner.close();
		} catch (Exception exc) {
			// handle the exception
		}
	}

	protected void savePost(int ID, String content) {
		// saving the post content
		try {
			String fileName = StaticData.CHALLENGE_DATA + "/posts/" + ID
					+ ".txt";
			FileWriter fwriter = new FileWriter(new File(fileName));
			fwriter.write(content);
			// closing the writer
			fwriter.close();
		} catch (Exception exc) {
			// handle the exception
		}
	}

	protected void collectQuestions() {
		// collecting questions
		try {
			Class.forName(StaticData.Driver_name).newInstance();
			Connection conn = DriverManager
					.getConnection(StaticData.connectionString);

			int saved = 0;
			for (int id : this.postIds) {
				String selectpost = "select Id,Body from Posts where Id="
						+ id;
				Statement stmt = conn.createStatement();
				ResultSet result = stmt.executeQuery(selectpost);
				while (result.next()) {
					int postid = result.getInt("Id");
					//String title = result.getString("Title");
					String postcontent = result.getString("Body");
					//postcontent = title + "\n" + postcontent;
					savePost(postid, postcontent);
					saved++;
					if(saved%1000==0){
						System.out.println("Saved:"+saved);
					}
				}
			}
			conn.close();
			System.out.println("Items saved:"+saved);
		} catch (Exception exc) {
			// handle the exception
			exc.printStackTrace();
		}
	}

	protected void collectPostContent() {
		// collecting post content
		try {
			Class.forName(StaticData.Driver_name).newInstance();
			Connection conn = DriverManager
					.getConnection(StaticData.connectionString);
			for (int id : this.postIds) {
				String selectpost = "select Id,Title,Body from Posts where Id="
						+ id;
				Statement stmt = conn.createStatement();
				ResultSet result = stmt.executeQuery(selectpost);
				while (result.next()) {
					int postid = result.getInt("Id");
					String title = result.getString("Title");
					String postcontent = result.getString("Body");
					postcontent = title + "\n" + postcontent;
					savePost(postid, postcontent);
				}
			}
			conn.close();
		} catch (Exception exc) {
			// handle the exception
		}
	}

	protected static void checkDBConnection()
	{
		try{
			Class.forName(StaticData.Driver_name).newInstance();
			Connection conn = DriverManager
					.getConnection(StaticData.connectionString);
			if(conn!=null){
				System.out.println("Connection successful");
			}
		}catch(Exception exc){
			exc.printStackTrace();;
		}
	}
	
	
	
	public static void main(String[] args) {
		//checkDBConnection();
		PostContentCollector collctor = new PostContentCollector();
		collctor.collectQuestions();
	}
}
