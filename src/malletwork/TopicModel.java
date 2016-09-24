package malletwork;

import cc.mallet.types.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;


import java.util.*;
import java.util.regex.*;
import java.io.*;

import config.StaticData;

public class TopicModel {

	public static void main(String[] args) throws Exception {

		// Begin by importing documents from text to feature sequences
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

		// Pipes: lowercase, tokenize, remove stopwords, map to features
		pipeList.add( new CharSequenceLowercase() );
		pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
		pipeList.add( new TokenSequenceRemoveStopwords(new File("stoplists/en.txt"), "UTF-8", false, false, false));
		pipeList.add( new TokenSequence2FeatureSequence());

		InstanceList instances = new InstanceList (new SerialPipes(pipeList));
		//String fileName=StaticData.REPOSITORY+"/normcorpus.txt";
		String fileName="C:/My MSc/ThesisWorks/Mining_Software_Repository/CodeReview-ICSE2017/experiment/corpus/corpus-800.txt";
		Reader fileReader = new InputStreamReader(new FileInputStream(new File(fileName)), "UTF-8");
		instances.addThruPipe(new CsvIterator (fileReader, Pattern.compile("^(\\S*)[\\s,]*(.*)$"), 2, 1, 1)); // data, label, name fields
		//instances.addThruPipe(new CsvIterator (fileReader, Pattern.compile("^(\\w+)[,](.*)$"),2, 1, 1)); // data, label, name fields

		// Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
		//  Note that the first parameter is passed as the sum over topics, while
		//  the second is 
		int numTopics = 100;
		double beta=(double)1/numTopics;
		ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, beta);
		model.addInstances(instances);

		// Use two parallel samplers, which each look at one half the corpus and combine
		//  statistics after every iteration.
		model.setNumThreads(8);

		// Run the model for 50 iterations and stop (this is for testing only, 
		//  for real applications, use 1000 to 2000 iterations)
		model.setNumIterations(2000);
		model.estimate();

		// Show the words and topics in the first instance

		// The data alphabet maps word IDs to strings
		Alphabet dataAlphabet = instances.getDataAlphabet();
		
		FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
		LabelSequence topics = model.getData().get(0).topicSequence;
		
		Formatter out = new Formatter(new StringBuilder(), Locale.US);
		for (int position = 0; position < tokens.getLength(); position++) {
			out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
		}
		System.out.println(out);
		
		// Estimate the topic distribution of the first instance, 
		// given the current Gibbs state.
		try{
			//saving topic distribution
			String topicdist="C:/My MSc/ThesisWorks/Mining_Software_Repository/CodeReview-ICSE2017/experiment/corpus/topicdist-800.txt";
			FileWriter fwriter=new FileWriter(new File(topicdist));
			//FileWriter fwriter=new FileWriter(new File(StaticData.REPOSITORY+"/topicdist.txt"));
			int instanceSize=instances.size();
			System.out.println("Instance size:"+instanceSize);
			//System.out.println(instances.get(0).getTarget());
			
			for(int ins=0;ins<instanceSize;ins++){
				double[] topicDistribution=model.getTopicProbabilities(ins);
				String indexFile=instances.get(ins).getTarget().toString().trim();
				//System.out.println(instances.get(ins).getData().toString().trim());
				String myline=indexFile.substring(0,indexFile.length())+"\t";
				for(int topic=0;topic<numTopics;topic++){
					myline+=String.format("%.3f", topicDistribution[topic])+"\t";
				}
				fwriter.write(myline.trim()+"\n");
			}
			fwriter.close();
		}catch(Exception exc){
			//handle the exception
			exc.printStackTrace();
		}
		
		try{
			LabelAlphabet labelAlphabet=model.getTopicAlphabet();	
		}catch(Exception exc){
			exc.printStackTrace();
		}
		/*double[] topicDistribution = model.getTopicProbabilities(0);

		// Get an array of sorted sets of word ID/count pairs
		ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
		
		// Show top 5 words in topics with proportions for the first document
		for (int topic = 0; topic < numTopics; topic++) {
			Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
			
			out = new Formatter(new StringBuilder(), Locale.US);
			out.format("%d\t%.3f\t", topic, topicDistribution[topic]);
			int rank = 0;
			while (iterator.hasNext() && rank < 5) {
				IDSorter idCountPair = iterator.next();
				out.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
				rank++;
			}
			System.out.println(out);
		}
		 */
		// Create a new instance with high probability of topic 0
		/*StringBuilder topicZeroText = new StringBuilder();
		Iterator<IDSorter> iterator = topicSortedWords.get(0).iterator();

		int rank = 0;
		while (iterator.hasNext() && rank < 5) {
			IDSorter idCountPair = iterator.next();
			topicZeroText.append(dataAlphabet.lookupObject(idCountPair.getID()) + " ");
			rank++;
		}*/

		// Create a new instance named "test instance" with empty target and source fields.
		//InstanceList testing = new InstanceList(instances.getPipe());
		//testing.addThruPipe(new Instance(topicZeroText.toString(), null, "test instance", null));

		//TopicInferencer inferencer = model.getInferencer();
		//double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
		//System.out.println("0\t" + testProbabilities[0]);
	}

}