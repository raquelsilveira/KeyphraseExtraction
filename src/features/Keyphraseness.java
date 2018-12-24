package features;

import java.util.ArrayList;
import java.util.Hashtable;

import util.FileData;
import util.Token;

public class Keyphraseness {

	int tagOtherDocs = 0;
	int tagWithOutKeyphras = 0;
	
	/**
	 * Quantifies how often a candidate phrase appears as tag in the training corpus
	 * @author raquelsilveira
	 * @date 22/02/2015
	 * @param token
	 * @param listFiles
	 * @return frequency of the term as tag
	 */
	public double calculateKeyphraseness(Token token, ArrayList<FileData> listFiles) {
				
		int frequencyTag = 0;
		for (FileData file : listFiles) {
			for (String tagOriginal : file.tagsOriginalFile.keySet()) {
				if (tagOriginal.equals(token.getLemma())) {
					frequencyTag++;
					break;
				}
			}
		}
		
		return (double) frequencyTag;///listFiles.size();
	}
	
	public double calculateKeyphrasenessTokens(Token token, Hashtable<String, Integer> listTokens, int qttyFiles) {
		
		int frequencyTag = 0;
		
		if (listTokens.containsKey(token.getLemma())) {
			frequencyTag = listTokens.get(token.getLemma());
		}
		
		return (double) frequencyTag;///qttyFiles;
	}
}
