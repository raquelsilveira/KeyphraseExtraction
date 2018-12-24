package preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.StringTokenizer;

import maui.filters.MauiPhraseFilter;
import maui.stopwords.Stopwords;
import maui.stopwords.StopwordsEnglish;

import org.apache.commons.io.FileUtils;

import util.FileData;
import util.StanfordLemmatizer;
import util.Token;
import config.Config;

public class ReadData {
	
	static StanfordLemmatizer lemmatizer = new StanfordLemmatizer();
	
	/** List of stop words to be used */
	static Stopwords stopwords = new StopwordsEnglish();
	
	private ArrayList<FileData> listFiles = new ArrayList<FileData>();
	
	/**
	 * Reads the files of the corpus and set the listFiles variable
	 * @author raquelsilveira
	 * @date 18/02/2015
	 */
	public ArrayList<FileData> readFiles() {
		try {
			
			System.out.println("Reading tags");
			//Read the tags of the files
			File directoryTags = new File(Config.PATH_CORPUS +"/taggers");
			//tags are mapped with the file name (key) and tag name and frequency occurrence in taggers (value)
			Hashtable<String, Hashtable<String, Integer>> tags = new Hashtable<String, Hashtable<String, Integer>>();
			readTags(directoryTags, tags);
			//Filter the tags indicated by at least two taggers
			tags = filterTags(tags);
			
			System.out.println("Reading files");
			//Reads the text of the files
			File directoryCorpus = new File(Config.PATH_CORPUS + "/documents_aux");
			//files are mapped with the name (key) and the text (value) file
			readFile(directoryCorpus, tags);
		}
		catch(Exception e) { e.printStackTrace(); }
		return listFiles;
	}

	/**
	 * Gets the title and the text of each file
	 * @author raquelsilveira
	 * @date 18/02/2015
	 * @param directoryCorpus
	 * @return
	 */
	private void readFile(File directoryCorpus, Hashtable<String, Hashtable<String, Integer>> tags) {

		try {
			for (File f : directoryCorpus.listFiles()) {
				if (f.getName().contains("txt")) {
					String fileName = f.getName().replace(".txt", "");
					String text = FileUtils.readFileToString(f, "UTF-8").toLowerCase();
					
					FileData fileData = new FileData();
					fileData.setName(fileName);
					//fileData.setText(text.toLowerCase());
					
					MauiPhraseFilter filter = new MauiPhraseFilter();
					/*fileData.setTextLemmatized(lemmatizer.lemmatize(filter.tokenize(fileData.getText())).toString().replace("[", "").replace("]", "").
							replace(",", "").replace("-", " ").replace(".", " ").toLowerCase());*/
					
					/*fileData.setTextLemmatized(lemmatizer.lemmatize(fileData.getText()).toString().replace("[", "").replace("]", "").
							replace(",", " ").replace("-", " ").replace(".", " ").replace(",", " ").replace(":", " ").replace(";", " ").
							replace("(", " ").replace("//", " ").replace("=", " ").replace("\"", " ").replace("&", " ").replace("#"," ").
							replace(")", " ").replace(">"," ").replace("<", " ").replace("{", " ").replace("}", " ").replace("?", " ").
							replace("!", " ").replace("\\", " ").replace("%", " ").replace("_", " ")
							.toLowerCase());*/
											
							/*text.charAt(i) == '\'' || text.charAt(i) == '%' || text.charAt(i) == '=' || text.charAt(i) == ' ' ||
							text.charAt(i) == '\"' || text.charAt(i) == '$' || text.charAt(i) == '@' || text.charAt(i) == '_' ||
							text.charAt(i) == '&' || text.charAt(i) == '#' || text.charAt(i) == '*' || text.charAt(i) == '^' ||
							text.charAt(i) == '`' || text.charAt(i) == '|' || text.charAt(i) == '\n' || text.charAt(i) == '+' ||
							text.charAt(i) == '~' || text.charAt(i) == '\\')*/
							
							
					//fileData.setTitle(readTitleFile(f));
					//fileData.setTitleLemmatized(lemmatizer.lemmatize(fileData.getTitle()).toString().replace("[", "").replace("]", "").replace(",", "").replace("-", " "));
					fileData.tagsOriginalFile = tags.get(fileName);
					
					//Get the tags of the file
					Hashtable<String, Integer> tagsFile = tags.get(fileName);
					//Get the tokens of the file	
					ArrayList<Token> tokens = new ArrayList<Token>();
					//tokens = generateNGram(fileData.getTextLemmatized(), tagsFile, fileData);
					fileData.setTokens(tokens);
					listFiles.add(fileData);
				}
			}
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	public ArrayList<Token> generateNGram(String text, Hashtable<String, Integer> tagsFile, FileData file) {	
		ArrayList<Token> candidates = new ArrayList<Token>();
		String[] buffer = new String[Config.MAX_LENGTH_NGRAM];

		// Extracting strings of a predefined length from "text":
		StringTokenizer tok = new StringTokenizer(text, "\n");
		int pos = 0;
		while (tok.hasMoreTokens()) {
			String tokenText = tok.nextToken();
			int numSeen = 0;
			StringTokenizer wordTok = new StringTokenizer(tokenText, " ");
			while (wordTok.hasMoreTokens()) {
				pos++;
				String word = wordTok.nextToken();
				
				// Store word in buffer
				for (int i = 0; i < Config.MAX_LENGTH_NGRAM - 1; i++)
					buffer[i] = buffer[i + 1];
				buffer[Config.MAX_LENGTH_NGRAM - 1] = word;

				// How many are buffered?
				numSeen++;
				if (numSeen > Config.MAX_LENGTH_NGRAM)
					numSeen = Config.MAX_LENGTH_NGRAM;

				// Don't consider phrases that end with a stop word
				if (stopwords.isStopword(buffer[Config.MAX_LENGTH_NGRAM - 1]))
					continue;

				// Loop through buffer and add phrases to hashtable
				StringBuffer phraseBuffer = new StringBuffer();
				for (int i = 1; i <= numSeen; i++) {
					if (i > 1)
						phraseBuffer.insert(0, ' ');
						
					phraseBuffer.insert(0, buffer[Config.MAX_LENGTH_NGRAM - i]);
					
					// Don't consider phrases that begin with a stop word
					// In free indexing only
					if ((i > 1) && (stopwords.isStopword(buffer[Config.MAX_LENGTH_NGRAM - i])))
						continue;

					// Only consider phrases with minimum length
					if (i >= Config.MIN_LENGTH_NGRAM) {
						
						// each detected candidate phase in its original spelling form
						String name = phraseBuffer.toString();
						String lemma = pseudoPhrase(name);
						
						if (lemma != null) {
							Token candidate = getTokenCandidate(lemma, candidates);
							if (candidate == null) {
								// this is the first occurrence of this candidate
								Token token = new Token();
								token.setBeginIndex(pos - i);
								token.setEndIndex(pos - i);
								token.setName(name);
								token.setLemma(lemma);
								token.setFrequencyDoc(1);
								if (tagsFile.containsKey(token.getLemma()))
									token.setClassification("Yes");
								else
									token.setClassification("No");
								candidates.add(token);
								
							} else {
								// candidate has been observed before and update its values
								candidate.setEndIndex(pos - i);
								candidate.setFrequencyDoc(candidate.getFrequencyDoc() + 1);
							}
						}
					}
				}
			}
		}
		file.setQttyTerms(pos);
		
		ArrayList<Token> tokens = new ArrayList<Token>();
		for (Token token : candidates) {
			if (token.getFrequencyDoc() >= Config.MIN_FREQ_TOKEN || token.getClassification().equals("Yes"))
				tokens.add(token);
		}
		return tokens;
	}
	
	/**
	 * Gets the token of the candidate tokens list
	 * @author raquelsilveira
	 * @date 22/02/2015
	 * @param lemma
	 * @param candidateTokens
	 * @return found token
	 */
	public Token getTokenCandidate(String lemma, ArrayList<Token> candidateTokens) {
		
		for (Token token : candidateTokens) {
			if (token.getLemma().equals(lemma))
				return token;
		}
		return null;
	}
	
	/**
	 * Generates a normalized preudo phrase from a string. A pseudo phrase is a
	 * version of a phrase that only contains non-stopwords, which are stemmed
	 * and sorted into alphabetical order.
	 */
	public static String pseudoPhrase(String str) {

		String result = "";
		str = str.toLowerCase();

		//Sort words alphabetically
		String[] words = str.split(" ");
		//TODO: Retirada ordenação das palavras
		//Arrays.sort(words);

		for (String word : words) {

			// remove all stopwords
			//if (!stopwords.isStopword(word)) {
		
				// remove all apostrophes
				word = word.replace("\'", "");
				
				//java.util.List<CoreLabel> termList = lemmatizer.extractTokensWithLemma(word);
				
				//CoreLabel term = null;
				//if (termList != null && termList.size() > 0)
				//	term = termList.get(0);
				
				//Checks if term is a noun or adjective (NN is noun and JJ is adjective)
				//if (term != null && term.lemma().length() > 1)
				//	word = term.lemma();

				result += word + " ";
			//}
		}
		result = result.trim();
		if (!result.equals("")){// && result.length() > 1) {
			return result;
		}
		return null;
	}
	
	/**
	 * Checks the tags with more of one word and update the token
	 * @author raquelsilveira
	 * @date 18/02/2015
	 * @param tagsFile
	 * @param tokens
	 * @return list tokens
	 */
	private ArrayList<Token> checksTokensTagsNGrams(Hashtable<String, Integer> tagsFile, ArrayList<Token>tokens) {
		ArrayList<Token> tokensAux = tokens;
		for (String tag : tagsFile.keySet()) {
			int qttyWords = tag.length() - tag.replace(" ", "").length() + 1;
			if (qttyWords > 1) {
				int index = -1;
				Token tokenAux = null;
				for (Token token : tokensAux) {
					if (tag.startsWith(token.getLemma())) {
						String testTag = "", tokenName = "";
						//Checks if the tokens form the tag
						for (int i = tokensAux.indexOf(token); i < tokensAux.indexOf(token)+qttyWords; i++) {
							tokenName += tokensAux.get(i).getName() + (tokensAux.get(i).getLemma() != "-" ? " " : "");
							testTag += tokensAux.get(i).getLemma() + (tokensAux.get(i).getLemma() != "-" ? " " : "");
						}
						
						if (tag.equals(testTag.trim())) {
							index = tokensAux.indexOf(token);
							tokenAux = token.clone();
							tokenAux.setName(tokenName);
							tokenAux.setLemma(testTag.trim());
							tokenAux.setClassification("Yes");
							break;
						}
					}
				}
				if (tokenAux != null) {
					for (int i = index; i < index+qttyWords; i++)
						tokensAux.remove(i);
					tokensAux.add(index, tokenAux);
				}
			}
		}
		return tokensAux;
	}
	
	/**
	 * Gets the file title
	 * @author raquelsilveira
	 * @date 18/02/2015
	 * @param file
	 * @return file title
	 */
	private String readTitleFile(File file) {
		String title = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			title = reader.readLine().toLowerCase();
			/*while(reader.ready()) {
				String line = reader.readLine().toLowerCase();
				if (line.isEmpty()) break;
				title += line;
			}*/
			reader.close();
		}
		catch (Exception e) { e.printStackTrace(); }
		return title;
	}
	
	/**
	 * Reads the tags
	 * @author raquelsilveira
	 * @date 17/02/2015
	 * @param directory
	 * @param tagList: is mapped with the name file and the tag list (obtained by all taggers)
	 */
	private void readTags(File directory, Hashtable<String, Hashtable<String, Integer>> tagList) {
		try {
			
			//Run all the folders (each folder represents one tagger) for to obtain the tags
			for (File f : directory.listFiles()) {
				if (f.isDirectory()) readTags(f, tagList);
				else {
					String fileName = f.getName().replace(".tags", "");
					BufferedReader reader = new BufferedReader(new FileReader(f));
					Hashtable<String, Integer> tag = new Hashtable<String, Integer>();
					
					//Reads each tag
					while (reader.ready()) {
						//Lemmatize the tag
						String tagReal = reader.readLine().toLowerCase();
						String tagRead = lemmatizer.lemmatize(tagReal).toString().replace("[", "").replace("]", "").replace(",", "").replace("-", " ");
						
						//The same form of text file
						tagRead = pseudoPhrase(tagRead);
						
						if (tagRead != null) {
							if (tag.containsKey(tagRead))
								tag.put(tagRead, tag.get(tagRead)+1);
							else
								tag.put(tagRead, 1);
						}
					}
					//Checks if the file already inserted in list tags
					if (tagList.containsKey(fileName)) {
						Hashtable<String, Integer> tagsAdded = tagList.get(fileName);
						for (String t : tagsAdded.keySet()) {
							int qtty = tagsAdded.get(t);
							if (tag.containsKey(t)) {
								int qttyTag = tag.get(t);
								tag.put(t, qtty + qttyTag);
							}
							else {
								tag.put(t, qtty);
							}	
						}
					}
					tagList.put(fileName, tag);
					reader.close();
				}
			}
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	
	/**
	 * Remove the tags indicated by only one tagger, 
	 * i.e., will be analised only tags indicated by at least two taggers (according Maui)
	 * @author raquelsilveira
	 * @date 18/02/2015
	 */
	private Hashtable<String, Hashtable<String, Integer>> filterTags(Hashtable<String, Hashtable<String, Integer>> tags) {
		Hashtable<String, Hashtable<String, Integer>> filter = new Hashtable<String, Hashtable<String,Integer>>();
		for (String file : tags.keySet()) {
			Hashtable<String, Integer> filterTag = new Hashtable<String, Integer>();
			for (String tag : tags.get(file).keySet()) {
				if (tags.get(file).get(tag) >= 2)
					filterTag.put(tag, tags.get(file).get(tag));
			}
			filter.put(file, filterTag);
		}
		return filter;
	}
	
	public static void main (String [] args) {
		System.out.println("Starting...");
		ReadData readData = new ReadData();
		
		Hashtable<String, Integer> tagsFile = new Hashtable<String, Integer>();
		tagsFile.put("city", 2);
		tagsFile.put("new york", 2);
		
		ArrayList<Token> candidatas = readData.generateNGram("New York is a beautiful city very beautiful", tagsFile, new FileData());
		for (Token token : candidatas) {
			System.out.println(token.getName() + " - " + token.getLemma() + " - " + token.getBeginIndex() + " - " + token.getEndIndex() + " " + token.getClassification());
		}
		
		/*try {
			File directoryCorpus = new File(Config.PATH_CORPUS + "/documents_aux");
			for (File f : directoryCorpus.listFiles()) {
				if (f.getName().contains("txt")) {
					String fileName = f.getName().replace(".txt", "");
					String text = FileUtils.readFileToString(f, "UTF-8").toLowerCase();
					readData.generateNGram(text);
				}
			}
		}
		catch(Exception e) { e.printStackTrace(); }*/
		
		
		//new ReadData().readFiles();
		System.out.println("Finished...");
	}
}
