package features;

import java.util.Hashtable;

import preprocessing.ReadData;
import config.Config;
import features.Features.features;
import util.FileData;
import util.StanfordLemmatizer;
import util.Token;
import br.com.informar.knowledgebase.collection.Mentions;
import br.com.informar.knowledgebase.db.MongoDB;
import br.com.informar.knowledgebase.model.Mention;

public class WikipediaKeyphraseness {

	public WikipediaKeyphraseness () {
		MongoDB.changeMongoParameters(Config.SERVIDOR_DB, Config.PORT_DB, Config.DB_NAME);
	}
	
	public double calculatesWikipediaKeyphraseness(Token token) {

		Mention mention = new Mentions().getByName(token.getLemma());
		if (mention != null)
			return mention.getKeyphraseness();
		return 0;
	}
	
	public static void main(String [] args) {
		StanfordLemmatizer lemmatizer = new StanfordLemmatizer();
		FileData file = new FileData();
		file.setName("teste");
		//file.setText("the study of networks pervades all of science, from neurobiology to statistical physics the study of networks pervades all of science, from neurobiology to statistical physics");
		//file.setTextLemmatized(lemmatizer.lemmatize(file.getText()).toString().replace("[", "").replace("]", "").replace(",", ""));
		Hashtable<String, Integer> tags = new Hashtable<String, Integer>();
		tags.put("network", 2);
		tags.put("science", 2);
		//file.setTokens(new ReadData().generateNGram(file.getTextLemmatized(), tags, file));
		
		WikipediaKeyphraseness wiki = new WikipediaKeyphraseness(); 
		for (Token t : file.getTokens())
			t.features.put(features.wikipediaKeyphraseness.getFeature(), wiki.calculatesWikipediaKeyphraseness(t));
		
		System.out.println("Tokens: ");
		for (Token t : file.getTokens()) {
			System.out.println(t.getLemma() + " [" + t.getBeginIndex() + " - " + t.getEndIndex() + "]: " + t.features.get(features.wikipediaKeyphraseness.getFeature()));
		}
		
	}
}
