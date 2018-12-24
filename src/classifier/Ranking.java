package classifier;

import java.util.ArrayList;
import java.util.Collections;

import features.Features.features;
import util.FileData;
import util.Token;

public class Ranking {

	ArrayList<FileData> listFile = new ArrayList<FileData>();
	
	public int correctText = 0, correctNonText = 0, incorrectText = 0, incorrectNonText = 0;
	
	public Ranking(ArrayList<FileData> listFile) {
		this.listFile = listFile;
	}
	
	/**
	 * This method realized the ranking of four first tokens
	 * @author raquelsilveira
	 * @date 21/05/2015
	 */
	public int realizeRanking() {
		
		System.out.println("Ranking...");
		int countCorrect = 0, tags = 0;
		for (FileData file : listFile) {
			tags += file.getTags().size();
			//System.out.println("File: " + file.getName() + " Qtde tags: " + file.getTags().size());
			Collections.sort(file.getTokens());
			int stop = 0;
			for (Token t : file.getTokens()) {
				if (stop < 5) { 
					//System.out.print(t.getLemma() + ": " + t.getRankFunction());
					if (file.getTags().contains(t)) {
						//System.out.println(" -> Correto!");
						countCorrect++;
						if (t.features.get(features.ofText.getFeature()) == 1)
							correctText++;
						else
							correctNonText++;
					} else {
						//System.out.println("");
						if (t.features.get(features.ofText.getFeature()) == 1)
							incorrectText++;
						else
							incorrectNonText++;
					}
					
				}
				else
					break;
						
				stop++;
			}
		}
		
		System.out.println("Total files: " + listFile.size());
		System.out.println("Total corrects: " + countCorrect);
		System.out.println("Tags: " + tags);
		return countCorrect;
	}
}
