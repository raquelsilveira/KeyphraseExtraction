package util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import config.Config;
import features.Features.features;

public class WriteArff {

	/**
	 * Create the foulders to the output files
	 * @author raquelsilveira
	 * @date 13/09/2014
	 */
	public void createFolders(String path) {
		try {

			//Map file
			FileWriter fileWriterMap = new FileWriter("output/" + path + "_map.txt");
			fileWriterMap.close();

			//Arff file
			FileWriter fileWriter = new FileWriter("output/" + path + ".arff");

			fileWriter.write("@relation topics\n\n");
			for (features f : Config.getFeatures())
				fileWriter.write("@attribute " + f.getFeature() + " " + f.getType() + "\n");

			fileWriter.write("@attribute class {Yes,No}\n\n");
			fileWriter.write("@data\n");
			fileWriter.close();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Write the arff and mapping files
	 * @author raquelsilveira
	 * @date 18/02/2015
	 * @throws IOException
	 */
	public void writeFiles(ArrayList<FileData> list, String path) throws IOException {
		
		for (FileData file : list) {
			
			String pathFile = "output/" + path;
			FileWriter fileWriter = new FileWriter(pathFile + ".arff", true);
			FileWriter fileWriterMap = new FileWriter(pathFile + "_map.txt", true);	
			
			for (Token token : file.getTokens()) {
				fileWriterMap.write(token.getName() + "\t\t\t -----> " + file.getName() + "\n");
								
				for (features f : Config.getFeatures()) {
					fileWriter.write(token.features.get(f.getFeature()) + ",");
				}
				
				fileWriter.write(token.getClassification() + "\n");
			}
			
			fileWriter.close();
			fileWriterMap.close();
		}
	}
	
	/**
	 * Write the arff and mapping files
	 * @throws IOException
	 */
	public void writeFilesByToken(ArrayList<Token> list, String path) throws IOException {
		
		String pathFile = "output/" + path;
		FileWriter fileWriter = new FileWriter(pathFile + ".arff", true);
		FileWriter fileWriterMap = new FileWriter(pathFile + "_map.txt", true);	
		
		for (Token token : list) {
			fileWriterMap.write(token.getLemma());// getName());
							
			for (features f : Config.getFeatures()) {
				fileWriter.write(token.features.get(f.getFeature()) + ",");
			}
			
			fileWriter.write(token.getClassification() + "\n");
		}
		
		fileWriter.close();
		fileWriterMap.close();
	}
}
