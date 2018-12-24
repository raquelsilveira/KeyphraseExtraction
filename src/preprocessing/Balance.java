package preprocessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

import features.Features.features;
import util.FileData;
import util.Token;
import weka.core.Instance;
import weka.core.Instances;

public class Balance {

	/**
	 * Realize balance adding as candidate term those which are in others documents as tag
	 * @author raquelsilveira
	 * @date 06/01/2014
	 * @param files
	 * @return new file list
	 */
	public ArrayList<FileData> realizeBalance(ArrayList<FileData> files) {
		ArrayList<FileData> filesBalanced = new ArrayList<FileData>();
		
		int k = 0;
		for(FileData file : files) {
			k++;
			//Create new file and add all the tags
			FileData fileBalanced = new FileData();
			fileBalanced.setName(file.getName());
			/*fileBalanced.setDomain(file.getDomain());
			fileBalanced.setTitle(file.getTitle());
			fileBalanced.setTitleLemmatized(file.getTitleLemmatized());
			fileBalanced.setText(file.getText());
			fileBalanced.setTextLemmatized(file.getTextLemmatized());*/
			fileBalanced.tagsOriginalFile = file.tagsOriginalFile;
			
			fileBalanced.getTokens().addAll(file.getTags());
			
			int qttyTags = file.getTags().size();
			//Run all tokens to obtain those which are in others documents as tag
			ArrayList<Token> t = new ArrayList<Token>();//getTokensTagsOtherFiles(file.getTokens(), file, files);
			
			System.out.println(k);
			
			//over-sampling
			/*ArrayList<Token> tokens = new ArrayList<Token>();
			for (Token tAux : file.getTokens()) {
				if (tAux.getClassification().equals("No"))
					tokens.add(tAux);
			}
			fileBalanced.getTokens().addAll(tokens);
			if (qttyTags > 0) {
				//int rate = ((int)(file.getTokens().size() - qttyTags) / qttyTags);  //2 representa o dobro (aumento em 100%)
				int qttyAdded = qttyTags;
				//while (qttyAdded <= (qttyTags * rate)) {
				while (qttyAdded < file.getTokens().size() - file.getTags().size()) {
					int indexSorted = new Random().nextInt(qttyTags);
					if (indexSorted >= 0 && indexSorted < qttyTags) {
						fileBalanced.getTokens().add(file.getTags().get(indexSorted));
						qttyAdded++;
					}
				}
			}
			
			System.out.println("Qtty tags: " + fileBalanced.getTags().size());
			System.out.println("Qtty tokens: " + (fileBalanced.getTokens().size() - fileBalanced.getTags().size()));
			*/
			//Add the tokens to file. Case the quantity tokens found in other documents as tag is less that the tags quantity of the own document,
			//other tokens are added randomly
			//under-sampling
			/*int rate = 1;
			if (t.size() >= qttyTags * rate) {
				Collections.sort(t);
				for(int i = 0; i < qttyTags * rate; i++) {
					fileBalanced.getTokens().add(t.get(i));
				}
			}
			else {
				fileBalanced.getTokens().addAll(t);
				for (Token tokenAux : file.getTokens()) {
					if (!fileBalanced.getTokens().contains(tokenAux))
						fileBalanced.getTokens().add(tokenAux);
					
					if (fileBalanced.getTokens().size() == qttyTags + (qttyTags * rate)) 
						break;
				}
			}*/
			
			//under-sampling majority class
			int percentageUnder = 200;
			int percentageOver = 200;
			int rateNonTags = (qttyTags * (1 + percentageOver/100)) * 100 / percentageUnder;
			
			System.out.println("Qtty tags: " + qttyTags + " Aumento tags: " + (qttyTags * (1 + percentageOver/100)) + " Qtde under: " + rateNonTags);
			
			ArrayList<Token> nonTags = new ArrayList<Token>(); 
			for (Token tokenAux : file.getTokens()) {
				if (tokenAux.getClassification().equals("No"))
					nonTags.add(tokenAux);
			}
			HashSet<Integer> indexInserted = new HashSet<Integer>();
			
			while (t.size() < rateNonTags) {
				int indexSorted = new Random().nextInt(nonTags.size());
				if (indexSorted >= 0 && indexSorted < nonTags.size() && !indexInserted.contains(indexInserted)) {
					t.add(nonTags.get(indexSorted));
					indexInserted.add(indexSorted);
				}
			}
			fileBalanced.getTokens().addAll(t);
			
			System.out.println("Tokens + tags: " + fileBalanced.getTokens().size());
			filesBalanced.add(fileBalanced);
		}
		
		int tokens = 0, tags = 0;
		for (FileData file : filesBalanced) {
			tags += file.getTags().size();
			tokens += file.getTokens().size() - file.getTags().size();
		}
		
		System.out.println("Qtde total de tags: " + tags);
		System.out.println("Qtde total de tokens: " + tokens);
		return filesBalanced;
	}
	
	public Instances realizeOverSampling(Instances train, int percentageOver) {
		
		ArrayList<Instance> tags = new ArrayList<Instance>();
		for (int j = 0; j < train.numInstances(); j++) {
			if (train.instance(j).classValue() == 0) {
				tags.add(train.instance(j));
			}
		}
		
		int rate = tags.size() * (1 + percentageOver/100);
		int tagsAdded = tags.size();
		
		while(tagsAdded < rate) {
			int indexSorted = new Random().nextInt(tags.size());
			if (indexSorted >= 0 && indexSorted < tags.size()) {
				train.add(tags.get(indexSorted));
				tagsAdded++;
			}
		}
		return train;
	}
	
	public Instances realizeUnderSampling(Instances train, int percentageUnder, int percentageOver) {
		
		/*int qttyTags = 0;
		HashSet<Integer> nonTags = new HashSet<Integer>();
		for (int j = 0; j < train.numInstances(); j++) {
			if (train.instance(j).classValue() == 0) 
				qttyTags++;
			else
				nonTags.add(j);
		}
		
		//under-sampling majority class
		int rateNonTags = qttyTags * 100 / percentageUnder;
			
		System.out.println("Qtty tags: " + qttyTags + " Aumento tags: " + (qttyTags * (1 + percentageOver/100)) + " Qtde under: " + rateNonTags);
			
		HashSet<Integer> indexInserted = new HashSet<Integer>();
		
		for (int i = 0; i < rateNonTags; i++) {
			indexInserted.add((Integer)nonTags.toArray()[i]);
		}
		
		ArrayList<Instance> instancesRemoved = new ArrayList<Instance>();
		for (int j = 0; j < train.numInstances(); j++) {
			if (!indexInserted.contains(j) && train.instance(j).classValue() == 1)
				instancesRemoved.add(train.instance(j));
		}
		System.out.println("Terminou... " + instancesRemoved.size());
		train.removeAll(instancesRemoved);
			
		return train;*/
		return null;
	}
	
	/**
	 * Get tokens that are tags in other files
	 * @author raquelsilveira
	 * @date 08/01/2015
	 * @param file
	 * @param files
	 * @return
	 */
	private ArrayList<Token> getTokensTagsOtherFiles(ArrayList<Token> listToken, FileData file, ArrayList<FileData> files) {
		
		ArrayList<Token> t = new ArrayList<Token>();
		for (Token token : listToken) {
			if (token.getClassification().equals("No")) {
				t.add(token);
				if (token.features.containsKey(features.keyphraseness)) {
					token.qttyOccurrence = Integer.parseInt(token.features.get(features.keyphraseness).toString());
				}
				else {
					for (FileData fileAux : files) {
						if (!fileAux.getName().equals(file.getName())) {
							for (Token tokenAux : fileAux.getTags()) {
								if (token.getLemma().equals(tokenAux.getLemma())) {
									token.qttyOccurrence++;
								}
							}
						}
					}
				}
			}
		}
		return t;
	}
}
