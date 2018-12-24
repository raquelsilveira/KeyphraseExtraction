package util;

import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import config.Config;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.CoreMap;

public class StanfordLemmatizer {

	protected StanfordCoreNLP pipeline;

	private MaxentTagger maxentTagger;

	public StanfordLemmatizer() {
		// Create StanfordCoreNLP object properties, with POS tagging
		// (required for lemmatization), and lemmatization
		Properties props;
		props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		String model = Config.PATH_MODEL_TAGGER;
		try {
			props.load(new FileReader(model + ".props"));
			maxentTagger = new MaxentTagger(model);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// StanfordCoreNLP loads a lot of models, so you probably
		// only want to do this once per execution
		this.pipeline = new StanfordCoreNLP(props);
	}

	public List<String> lemmatize(String documentText) {
		List<String> lemmas = new LinkedList<String>();

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(documentText);

		// run all Annotators on this text
		this.pipeline.annotate(document);

		// Iterate over all of the sentences found
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			// Iterate over all tokens in a sentence
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// Retrieve and add the lemma for each word into the list of lemmas
				lemmas.add(token.get(LemmaAnnotation.class));
			}
		}

		return lemmas;
	}
/*
	public List<HasWord> tokenizer(String sentence) {
		return MaxentTagger.tokenizeText(new StringReader(sentence)).get(0);
	}

	public List<CoreLabel> extractTokens(String sentece1) {

		PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<CoreLabel>(new StringReader(sentece1), new CoreLabelTokenFactory(), "");
		List<CoreLabel> listCoreLabel = new ArrayList<CoreLabel>();

		while (ptbt.hasNext()) {
			listCoreLabel.add(ptbt.next());
		}

		maxentTagger.tagCoreLabels(listCoreLabel);
		
		return listCoreLabel;
	}
	
	public List<CoreLabel> extractTokensWithLemma(String documentText) {
		
		List<CoreLabel> listCoreLabel = new ArrayList<CoreLabel>();
				
		// create an empty Annotation just with the given text
		Annotation document = new Annotation(documentText);

		// run all Annotators on this text
		this.pipeline.annotate(document);
		
		// Iterate over all of the sentences found
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			// Iterate over all tokens in a sentence
			listCoreLabel.addAll(sentence.get(TokensAnnotation.class));
		}
		return listCoreLabel;
	}

	public static void main(String[] args) {

		System.out.println("Starting...");
		
		/*String sentence = "The boys can't walking in New York.";
		StanfordLemmatizer lemmatizer = new StanfordLemmatizer();
		List<CoreLabel> listCore = lemmatizer.extractTokensWithLemma(sentence);
		
		for(CoreLabel core : listCore)
			System.out.println(core.word() + " [" + core.lemma() + "]: " + core.tag());
		
		List<String> lemmas = lemmatizer.lemmatize(sentence);
		for (String lema : lemmas) 
			System.out.println(lema);*/
		
		//System.out.println("Finished...");
	//}

}
