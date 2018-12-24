package features;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.StringTokenizer;

import preprocessing.ReadData;
import features.Features.features;
import util.FileData;
import util.StanfordLemmatizer;
import util.Token;

public class SintaticFeatures {

	/**
	 * Calculates the first occurrence of the term in document
	 * Let dist be the relative distance of the first occurrence of the term to the beginning of the document, 
	 * normalized by the size of the document. 
	 * The value of the feature is defined as 1-dist.
	 * @author raquelsilveira
	 * @date 18/02/2015
	 * @param token
	 * @param file
	 * @return first occurrence
	 */
	public double calculateFirstOccurrence(Token token, FileData file) {
		
		return 1 - ((double) token.getBeginIndex() / file.getQttyTerms());
	}
	
	/**
	 * Calculates if the token is in the tile
	 * @author raquelsilveira
	 * @date 18/02/2015
	 * @param token
	 * @param file
	 * @return 1: occurs in title; 0: don't occur
	 */
	/*public double calculateTitleOccurrence(Token token, FileData file) {
		if (file.getTitleLemmatized().contains(token.getLemma()))
			return 1.0D;
		return 0.0;
	}*/
	
	/**
	 * Calculates the TFxIDF
	 * TF = frequency of the term in file / more frequency in the file
	 * IDF = log (number total of files / number of files that the term occurs)   
	 * @author raquelsilveira
	 * @date 18/02/2015
	 * @param token
	 * @param listFile
	 * @param file
	 * @return tfxidf
	 */
	public double calculateTFIDF(Token token, ArrayList<FileData> listFile, FileData file, Hashtable<String, HashSet<String>> tokensFile) {
		
		int df = 0;
		
		if (tokensFile == null) {
			for (FileData fileAux : listFile) {
				for (Token tokenAux : fileAux.getTokens()) {
					if (tokenAux.getLemma().equals(token.getLemma()))
					{ df++; break; }
				}
			}
		}
		else {
			for (String keyFile : tokensFile.keySet()) {
				if (tokensFile.get(keyFile).contains(token.getLemma()))
					df++;
			}
		}
		
		//double idf = Math.log((double) listFile.size() / df);
		
		//Gets the max frequency
		//int maxFrequency = file.getTokens().get(0).getFrequencyDoc();
		int totalFrequencyDoc = 0;
		for (Token t : file.getTokens()) {
			//if (t.getFrequencyDoc() > maxFrequency)
			//	maxFrequency = t.getFrequencyDoc();
			
			totalFrequencyDoc += t.getFrequencyDoc();
		}
		
		//double tf = (double)token.getFrequencyDoc() / maxFrequency;
		//return (double) tf * idf;
		
		//tesis phd maui
		double tfIdf = (double) token.getFrequencyDoc() / totalFrequencyDoc * - (Math.log((double)df/listFile.size()) / Math.log(2));
		
		//System.out.println(token.getFrequencyDoc() + " " + totalFrequencyDoc);
		
		return tfIdf;
		//Maui
		//tf = token.getFrequencyDoc();
		//idf = -Math.log((listFile.size() + 1) / ((double) df + 1));
	}
	
	/**
	 * Calculates the phrase lenght measured in words
	 * @author raquelsilveira
	 * @date 22/02/2015
	 * @param token
	 * @return size of words in lemma token
	 */
	public double calculatePhraseLenght(Token token) {
		
		return new StringTokenizer(token.getLemma(), " ").countTokens();	
	}
	
	/**
	 * Calculates the distance between its first and last occurrences in a document
	 * @author raquelsilveira
	 * @date 22/02/2015
	 * @param token
	 * @return spread
	 */
	public double calculateSpread(Token token) {
		
		return token.getEndIndex() - token.getBeginIndex();
	}
	
	public static void main(String [] args) {
		StanfordLemmatizer lemmatizer = new StanfordLemmatizer(); 
		ArrayList<FileData> listFiles = new ArrayList<FileData>();
		FileData file = new FileData();
		file.setName("teste");
		//file.setText("the study of networks pervades all of science, from neurobiology to statistical physics the study of networks pervades all of science, from neurobiology to statistical physics");
		//file.setTextLemmatized(lemmatizer.lemmatize(file.getText()).toString().replace("[", "").replace("]", "").replace(",", ""));
		Hashtable<String, Integer> tags = new Hashtable<String, Integer>();
		tags.put("network", 2);
		tags.put("science", 2);
		//file.setTokens(new ReadData().generateNGram(file.getTextLemmatized(), tags, file));
		file.getTokens().get(0).setFrequencyDoc(5);
		
		FileData file2 = new FileData();
		Token aux = new Token();
		aux.setLemma("Jaguar");
		file2.getTokens().add(aux);
		
		listFiles.add(file2);
		listFiles.add(file);
		
		SintaticFeatures sintatic = new SintaticFeatures();
		
		for (Token t : file.getTokens()) {
			t.features.put(features.firstOccurrence.getFeature(), sintatic.calculateFirstOccurrence(t, file));
			t.features.put(features.tf_idf.getFeature(), sintatic.calculateTFIDF(t, listFiles, file, null));
			t.features.put(features.spread.getFeature(), sintatic.calculateSpread(t));
			t.features.put(features.phraseLenght.getFeature(), sintatic.calculatePhraseLenght(t));
		}
		
		System.out.println("Qtty terms: " + file.getQttyTerms());
		System.out.println("Tokens: ");
		for (Token t : file.getTokens()) {
			System.out.println(t.getLemma() + " [" + t.getBeginIndex() + " - " + t.getEndIndex() + "]: " + t.features.get(features.firstOccurrence.getFeature()) + " > " + t.features.get(features.tf_idf.getFeature()) 
					+ " > " + t.features.get(features.spread.getFeature()) + " > " + t.features.get(features.phraseLenght.getFeature()));
		}
	}
}
