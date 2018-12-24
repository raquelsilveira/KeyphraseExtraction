package features;

import java.util.ArrayList;
import java.util.Hashtable;

import preprocessing.FormatFile;
import config.Config;
import util.FileData;
import util.Token;
import weka.core.Instance;

public class Features {

	public enum features {
		firstOccurrence("FirstOccurrence", "numeric"), 
		titleOccurrence("TitleOccurrence", "numeric"),
		semanticSimilarity("SemanticSimilarity", "numeric"),  
		tf_idf("TF-IDF", "numeric"), 
		tf_idf_inf("TF-IDFInf", "numeric"),
		wikipediaKeyphraseness("WikipediaKeyphraseness", "numeric"),
		inferenceNetOccurrence("InferenceNetOccurrence", "numeric"),
		termPredictiability("TermPredictiability", "numeric"),
		keyphraseness("Keyphraseness", "numeric"),
		phraseLenght("PhraseLenght", "numeric"),
		spread("Spread", "numeric"),
		nodeDegree("NodeDegree", "numeric"),
		inverseWikipediaLinkade("InverseWikipediaLinkade", "numeric"),
	   	ofText("OfText", "numeric"),
	   	roleIsARelatedTo("roleIsARelatedTo", "numeric"),
	   	weightRole("WeightRole", "numeric"),
		importanceSemantic("ImportanceSemantic", "numeric"),
		
		if_iif("IFxIIF", "numeric"),
		iif("IIF", "numeric"),
		IF("IF", "numeric"),
		wif("wIF", "numeric"),
		wift("wIFt", "numeric"),
		ts("TS", "numeric"),
		wts("wTS", "numeric"),
		spi("SPI", "numeric"),
		spiT("SPIT", "numeric");
		
		//Represents the feature name
		private final String feature;
		
		//Represents the data type of the feature
		private final String type;
		
		features(String feature, String type) {
			this.feature = feature;
			this.type = type;
		}

		public String getType() {
			return this.type;
		}
		
		public String getFeature() {
			return this.feature;
		}
	}
	
	/**
	 * Calculates the features and set in tokens
	 * @author raquelsilveira
	 * @date 18/02/2015
	 * @param files
	 */
	public void calculateSemanticFeatures(ArrayList<FileData> files) {
		
		ArrayList<FileData> fileRead = new FormatFile().readFormatFiles("data/newTest/old/semantic/");
		
		ArrayList<String> filesReads = new ArrayList<String>();
		for (FileData fr : fileRead) {
			filesReads.add(fr.getName());
		}
		
		SintaticFeatures sintaticFeatures = new SintaticFeatures();
		WikipediaKeyphraseness wikipediaKeyphraseness = new WikipediaKeyphraseness();
		Keyphraseness keyphraseness = new Keyphraseness();
		SemanticSimilarity semanticSimilarity = new SemanticSimilarity();
		
		//System.out.println("Calculing termPredictiability...");
		//new TermPredictiability(files).calculatePredictiability();
		
		int i = 0;
		for (FileData file : files) {			

			if (filesReads.contains(file.getName()))
				continue;
			
			System.out.println(++i + " (File: " + file.getName() + " - " + file.getTokens().size() + ")");
			semanticSimilarity.getRelationsWikipedia(file);
			semanticSimilarity.calculateSimilaritySemantic(file);
			semanticSimilarity.calculateNodeDegree(file);
			semanticSimilarity.calculatesInverseWikipediaLinkage(file);
			for (Token token : file.getTokens()) {
				//if (token.features.get(features.ofText.getFeature()) == 0.0D) {
					token.features.put(features.wikipediaKeyphraseness.feature, wikipediaKeyphraseness.calculatesWikipediaKeyphraseness(token));
					token.features.put(features.keyphraseness.feature, keyphraseness.calculateKeyphraseness(token, files));
					token.features.put(features.phraseLenght.feature, sintaticFeatures.calculatePhraseLenght(token));
				//}
			}
			
			new FormatFile().writeFormatFileByFile(file, "data/newTest/old/semantic/");
		}
		
		System.out.println("Tokens como tags em outros docs: " + keyphraseness.tagOtherDocs);
		System.out.println("Tags com frequência errada: " + keyphraseness.tagWithOutKeyphras);
	}
	
	/**
	 * Calculates the sintatic features
	 * @author raquelsilveira
	 * @date 19/02/2015
	 * @param files
	 */
	public void calculateSintaticFeatures(ArrayList<FileData> files) {
		SintaticFeatures sintaticFeatures = new SintaticFeatures();
		Keyphraseness keyphraseness = new Keyphraseness();
		for (FileData file : files) {
			for (Token token : file.getTokens()) {
				token.features.put(features.ofText.feature, 1.0D);
				token.features.put(features.firstOccurrence.feature, sintaticFeatures.calculateFirstOccurrence(token, file));
				//token.features.put(features.titleOccurrence.feature, sintaticFeatures.calculateTitleOccurrence(token, file));
				token.features.put(features.tf_idf.feature, sintaticFeatures.calculateTFIDF(token, files, file, null));
				token.features.put(features.spread.feature, sintaticFeatures.calculateSpread(token));
				token.features.put(features.keyphraseness.feature, keyphraseness.calculateKeyphraseness(token, files));
			}
		}

		System.out.println("Tokens como tags em outros docs: " + keyphraseness.tagOtherDocs);
		System.out.println("Tags com frequência errada: " + keyphraseness.tagWithOutKeyphras);
	}
	
	/**
	 * Normalize the data features
	 * @author raquelsilveira
	 * @date 15/10/2014
	 */
	public void normalizeDataFeatures(ArrayList<FileData> listFiles) {
				
		//The maximum value is a set feature and value
		Hashtable<String, Double> maxValue = new Hashtable<String, Double>();
		//The minimum value is a set feature and value
		Hashtable<String, Double> minValue = new Hashtable<String, Double>();
		//Add as maximum and minimum value the value first token 
		for (features f : Config.getFeaturesNormalizeOverall()) {
			maxValue.put(f.feature, -10000.0);
			minValue.put(f.feature, 10000.0);
			
			//maxValue.put(f.feature, listFiles.get(0).getTokens().get(0).features.get(f.feature));
			//minValue.put(f.feature, listFiles.get(0).getTokens().get(0).features.get(f.feature));
		}
			
		//Run the list file to get the maximum values of the features
		for(FileData file : listFiles) {
			for (Token token : file.getTokens()) {
				for (features f : Config.getFeaturesNormalizeOverall()) {
					//System.out.println(f.feature);
					//System.out.println(token.features.get(f.feature));
					
					if (!token.features.containsKey(f.feature)) continue;
					
					if (token.features.get(f.feature) == Double.NaN) continue;
					
					if (token.features.get(f.feature) > maxValue.get(f.feature))
						maxValue.put(f.feature, token.features.get(f.feature));
					
					if (token.features.get(f.feature) < minValue.get(f.feature))
						minValue.put(f.feature, token.features.get(f.feature));
				}
			}
		}
		
		System.out.println("Menor valor importance semantic: " + minValue.get(features.importanceSemantic.getFeature()));
		System.out.println("Maior valor importance semantic: " + maxValue.get(features.importanceSemantic.getFeature()));
		
		//for (features f : Config.getFeaturesNormalizeOverall()) 
		//	System.out.println(f.getFeature() + " -> Min: " + minValue.get(f.feature) + " Max: " + maxValue.get(f.feature));
		
		//Normalize the value of the list file to the range 0 to 1
		for (FileData file : listFiles) {
			for (Token token : file.getTokens()) {
				for (features f : Config.getFeaturesNormalizeOverall()) {
					if (token.features.containsKey(f.feature))
						token.features.put(f.feature, (token.features.get(f.feature)-minValue.get(f.feature)) / (maxValue.get(f.feature)-minValue.get(f.feature)));
					//token.features.put(f.feature, token.features.get(f.feature) / maxValue.get(f.feature));
				}
			}
		}
		
		for (FileData file : listFiles) {
			
			for (features f : Config.getFeaturesNormalizeByFile()) {
				maxValue.put(f.feature, listFiles.get(0).getTokens().get(0).features.get(f.feature));
				minValue.put(f.feature, listFiles.get(0).getTokens().get(0).features.get(f.feature));
			}
			
			//Run the list file to get the maximum values of the features
			for(FileData fileAux : listFiles) {
				if (fileAux.getName().equals(file.getName())) {
					for (Token token : fileAux.getTokens()) {
						for (features f : Config.getFeaturesNormalizeByFile()) {
							
							if (!token.features.containsKey(f.feature)) continue;
							if (token.features.get(f.feature) == Double.NaN) continue;
							
							if (token.features.get(f.feature) > maxValue.get(f.feature))
								maxValue.put(f.feature, token.features.get(f.feature));
							
							if (token.features.get(f.feature) < minValue.get(f.feature))
								minValue.put(f.feature, token.features.get(f.feature));
						}
					}
				}
			}
			
			for (Token token : file.getTokens()) {
				for (features f : Config.getFeaturesNormalizeByFile()) {
					if (token.features.containsKey(f.feature))
						token.features.put(f.feature, (token.features.get(f.feature)-minValue.get(f.feature)) / (maxValue.get(f.feature)-minValue.get(f.feature)));
				}
			}
		}
		
	}
}
