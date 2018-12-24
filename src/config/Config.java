package config;

import java.util.ArrayList;

import test.MainInfNet;
import features.Features.features;


public class Config {

	public static final String SERVIDOR_DB = "localhost";
	public static final int PORT_DB = 27017;
	public static final String DB_NAME = "knowledge-base-enwiki";
	public static final String DB_NAME_CONCEPTNET = "conceptNet5";
	public static final String LANGUAGE = "en";
	public static final String PATH_CORPUS = "/Users/raquelsilveira/Documents/Unifor/Aprendizagem de Máquina/Extração de Tópicos/citeulike180";
	public static final int RF_NUM_TREES = 30;
	public static final String URL_MYSQL = "jdbc:mysql://localhost:3306/inferencenet" + 
			 								"?useCursorFetch=true&useUnicode=yes&characterEncoding=UTF-8";
	public static final String USER_MYSQL = "root";
	public static final String PASSWORD_MYSQL = "root";
	public static final selection MODE_SELECTION_INF_NET = selection.reflexivity;
	public static final String PATH_MODEL_CONFIG = "data/models/en.xml";
	public static final String PATH_MODEL_EN = "data/models/disambig_en_In.model";
	public static final String PATH_MODEL_TAGGER = "data/tagger/english-bidirectional-distsim.tagger";
	public static final int MAX_LENGTH_NGRAM = 3;
	public static final int MIN_LENGTH_NGRAM = 1;
	public static final int MIN_FREQ_TOKEN = 1;
	public static final String OUTPUT_TRAIN_TEST = "cross_validation_full";
	
	public enum selection {
		reflexivity,
		transitivy
	}
	
	public static ArrayList<features> getFeaturesFoil() {
		ArrayList<features> list = new ArrayList<features>();
		list.add(features.firstOccurrence);
		list.add(features.semanticSimilarity);
		list.add(features.tf_idf);
		list.add(features.wikipediaKeyphraseness);
		list.add(features.keyphraseness);
		list.add(features.phraseLenght);
		list.add(features.spread);
		list.add(features.inverseWikipediaLinkade);
		list.add(features.nodeDegree);
		list.add(features.roleIsARelatedTo);
		return list;
	}
	
	/**
	 * Gets the features
	 * @author raquelsilveira
	 * @date 18/02/2015
	 * @return
	 */
	public static ArrayList<features> getFeatures() {
		ArrayList<features> list = new ArrayList<features>();
		list.add(features.firstOccurrence);
		//list.add(features.titleOccurrence);
		list.add(features.semanticSimilarity);
		list.add(features.tf_idf);
		list.add(features.wikipediaKeyphraseness);
		//list.add(features.termPredictiability);
		list.add(features.ofText);
		list.add(features.keyphraseness);
		list.add(features.phraseLenght);
		list.add(features.spread);
		list.add(features.inverseWikipediaLinkade);
		list.add(features.nodeDegree);
		if (MainInfNet.modelFeatures == 2 || MainInfNet.modelFeatures == 4)
			list.add(features.roleIsARelatedTo);
		if (MainInfNet.modelFeatures == 3 || MainInfNet.modelFeatures == 4)
			list.add(features.importanceSemantic);
		if (MainInfNet.modelFeatures == 5)
			list.add(features.inferenceNetOccurrence);
		//list.add(features.weightRole);
		//list.add(features.tf_idf_inf);
		return list;
	}
	
	/**
	 * Gets the features to normalization
	 * @author raquelsilveira
	 * @date 28/03/2015
	 * @return
	 */
	public static ArrayList<features> getFeaturesNormalizeOverall(){
		ArrayList<features> list = new ArrayList<features>();
		list.add(features.firstOccurrence);
		//list.add(features.titleOccurrence);
		list.add(features.semanticSimilarity);
		list.add(features.tf_idf);
		list.add(features.wikipediaKeyphraseness);
		
		list.add(features.keyphraseness);
		list.add(features.nodeDegree);
		list.add(features.phraseLenght);
		list.add(features.spread); //por doc
		list.add(features.inverseWikipediaLinkade);
		
		if (MainInfNet.modelFeatures == 3 || MainInfNet.modelFeatures == 4)
			list.add(features.importanceSemantic);
		
		if (MainInfNet.modelFeatures == 5)
			list.add(features.inferenceNetOccurrence);
		
		//list.add(features.roleIsARelatedTo);
		//list.add(features.tf_idf_inf);
		
		//list.add(features.termPredictiability);
		return list;
	}
	
	public static  ArrayList<features> getFeaturesNormalizeByFile(){
		ArrayList<features> list = new ArrayList<features>();
		//list.add(features.firstOccurrence); //por doc
		//list.add(features.semanticSimilarity); //por doc
		//list.add(features.spread); //por doc
		return list;
	}
	
	public static ArrayList<String> getAttributesTrain() {
		ArrayList<String> list = new ArrayList<String>();
		//list.add(features.titleOccurrence.getFeature());
		
		//HUMB
		/*list.add(features.firstOccurrence.getFeature());
		list.add(features.tf_idf.getFeature());
		list.add(features.keyphraseness.getFeature());
		list.add(features.wikipediaKeyphraseness.getFeature());
		list.add(features.phraseLenght.getFeature());*/
		
		/*list.add(features.firstOccurrence.getFeature());
		list.add(features.tf_idf.getFeature());
		//list.add(features.keyphraseness.getFeature());
		list.add(features.semanticSimilarity.getFeature());
		list.add(features.spread.getFeature());
		list.add(features.wikipediaKeyphraseness.getFeature());
		
		/*
		list.add(features.phraseLenght.getFeature());
		list.add(features.spread.getFeature());
		list.add(features.nodeDegree.getFeature());
		list.add(features.inverseWikipediaLinkade.getFeature());*/
	
		
		list.add(features.firstOccurrence.getFeature());
		list.add(features.tf_idf.getFeature());
		list.add(features.keyphraseness.getFeature());
		
		if (MainInfNet.modelFeatures >= 1) {
			list.add(features.semanticSimilarity.getFeature());
			list.add(features.wikipediaKeyphraseness.getFeature());
			list.add(features.phraseLenght.getFeature());
			list.add(features.spread.getFeature());
			list.add(features.nodeDegree.getFeature());
			list.add(features.inverseWikipediaLinkade.getFeature());
		}
		
		if (MainInfNet.modelFeatures == 2 || MainInfNet.modelFeatures == 4)
			list.add(features.roleIsARelatedTo.getFeature());
		
		if (MainInfNet.modelFeatures == 3 || MainInfNet.modelFeatures == 4)
			list.add(features.importanceSemantic.getFeature());
		
		if (MainInfNet.modelFeatures == 5)
			list.add(features.inferenceNetOccurrence.getFeature());
			
		//list.add(features.weightRole.getFeature());
		//list.add(features.termPredictiability.getFeature());
		//list.add(features.ofText.getFeature());
		//list.add(features.tf_idf_inf.getFeature());
		//list.add(features.titleOccurrence.getFeature());
		return list;
	}
}
