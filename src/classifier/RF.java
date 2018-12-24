package classifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;

import balance.Cnn;
import balance.RUSBoost;
import balance.SmoteAndEnn;
import balance.SmoteBoost;
import balance.TomekLinkUnderSampling;
import config.Config;
import preprocessing.Balance;
import features.Features;
import features.Keyphraseness;
import features.Features.features;
import test.Main;
import test.MainInfNet;
import test.ResultSchutz;
import util.FileData;
import util.Token;
import util.WriteArff;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.Logistic;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.filters.Filter;
import weka.filters.supervised.instance.SMOTE;

public class RF {

	/**
	 * Run Random Forest with 10-cross validation
	 * The folds are files
	 * @author raquelsilveira
	 * @date 24/02/2015
	 * @param listFiles
	 */
	public void runRFCrossValidationDoc(ArrayList<FileData> listFiles) {
		
		System.out.println("Files: " + listFiles.size());
		
		Hashtable<Integer, ArrayList<Token>> groupTokens = divideFiles(listFiles);
		writeFilesCrossValidation(groupTokens);
		
		trainTest(listFiles, null);
		
		System.out.println("Finished!!!!");		
	}
	
	public void getRankFunction(ArrayList<FileData> listFiles, Classifier cls, Instances test) throws Exception {
		int i = 0;
		for (FileData f : listFiles) {
			for (Token t : f.getTokens()) {
				t.rankFunction = cls.distributionForInstance(test.instance(i))[0];
				i++;
			}
		}
	}
	
	
	/**
	 * Run Random Forest with 10-cross validation
	 * The folds are files
	 * @author raquelsilveira
	 * @date 24/02/2015
	 * @param listFiles
	 * @param listFilesNoBalance
	 */
	public void runRFCrossValidationDoc(ArrayList<FileData> listFiles, ArrayList<FileData> listFilesNoBalance, boolean arquivosGerados) {
		
		Hashtable<Integer, ArrayList<FileData>> groupTest = null;
		if (!arquivosGerados) {
			System.out.println("Files: " + listFiles.size());
			
			System.out.println("Dividing files...");
			Hashtable<Integer, ArrayList<FileData>> groupTokens = divideFiles(listFiles, listFilesNoBalance);
			
			System.out.println("Checking files...");
			groupTokens = checksFiles(groupTokens);
			
			System.out.println("Writing files...");
			groupTest = writeFilesCrossValidation(groupTokens, listFilesNoBalance, arquivosGerados);
		} 
		
		System.out.println("Training and testing...");
		trainTest(listFiles, groupTest);
		
		System.out.println("Finished!!!!");		
	}
	
	public Hashtable<Integer, ArrayList<FileData>> checksFiles(Hashtable<Integer, ArrayList<FileData>> groupTokens) {
		
		for (int i = 0; i < groupTokens.size(); i++) {
			
			int tokenText = 0, tokenNoText = 0;
			int tagText = 0, tagNoText = 0;
			for (FileData file : groupTokens.get(i)) {
				for (Token token : file.getTokens()) {
					if (token.features.get(features.ofText.getFeature()) == 1) {
						if (token.getClassification().equals("Yes"))
							tagText++;
						else
							tokenText++;
					}
					else {
						if (token.getClassification().equals("Yes"))
							tagNoText++;
						else
							tokenNoText++;
					}
				}
			}
			
			System.out.println(i);
			System.out.println("Token text: " + tokenText);
			System.out.println("Tag text: " + tagText);
			System.out.println("Token no text: " + tokenNoText);
			System.out.println("Tag no text: " + tagNoText);
		}
		
		return groupTokens;
	}
	
	/**
	 * Get the files set divided in 10 groups
	 * @author raquelsilveira
	 * @date 24/02/2015
	 * @param listFiles
	 * @return files groups
	 */
	private Hashtable<Integer, ArrayList<Token>> divideFiles(ArrayList<FileData> listFiles) {
		Hashtable<Integer, ArrayList<Token>> groupTokens = new Hashtable<Integer, ArrayList<Token>>();
		
		int begin = listFiles.size() / 10;
		for (int i = 0; i < 10; i++) {
			int end = i < 9 ? (i+1) * begin - 1 : listFiles.size() - 1;
			ArrayList<Token> tokens = new ArrayList<Token>();
			for (int j = i * begin; j <= end; j++)
				tokens.addAll(listFiles.get(j).getTokens());
			groupTokens.put(i, tokens);
		}
		return groupTokens;
	}
	
	/**
	 * Get the files set divided in 10 groups
	 * @author raquelsilveira
	 * @date 24/02/2015
	 * @param listFiles
	 * @return files groups
	 */
	public Hashtable<Integer, ArrayList<FileData>> divideFiles(ArrayList<FileData> listFiles, ArrayList<FileData> listFilesNoBalance) {
		Hashtable<Integer, ArrayList<FileData>> groupTokens = new Hashtable<Integer, ArrayList<FileData>>();
		
		//Os arquivos sao adicionados de forma sequencial nos grupos
		int begin = listFiles.size() / 10;
		for (int i = 0; i < 10; i++) {
			int end = i < 9 ? (i+1) * begin - 1 : listFiles.size() - 1;
			ArrayList<FileData> files = new ArrayList<FileData>();
			for (int j = i * begin; j <= end; j++)
				files.add(listFiles.get(j));
			groupTokens.put(i, files);
		}
		
		//Sorteia os arquivos para os grupos de 0 a 8
		/*int qtdeGrupo = listFiles.size() / 10;
		System.out.println("Tamanho dos grupos: " + qtdeGrupo);
		HashSet<Integer> arquivosAdded = new HashSet<Integer>();
		Random r = new Random();
		for (int i = 0; i < 9; i++) {
			System.out.println("Grupo: " + i);
			ArrayList<FileData> files = new ArrayList<FileData>();
			for (int j = 0; j < qtdeGrupo; j++) {
				System.out.println(j);
				int indiceSorteado = r.nextInt(listFiles.size());
				while (arquivosAdded.contains(indiceSorteado)) {
					indiceSorteado = r.nextInt(listFiles.size());
					System.out.println("Sorteado: " + indiceSorteado);
				}
				arquivosAdded.add(indiceSorteado);
				files.add(listFiles.get(indiceSorteado));
			}
			groupTokens.put(i, files);
			System.out.println("Formou grupo: " + i + " (" + files.size() + " arquivos)");
		}
		
		//Os arquivos que sobrarem (nao entrarem nos grupos de 0 a 8) entrarao no grupo 9
		ArrayList<FileData> grupo9 = new ArrayList<FileData>();
		for (int i = 0; i < listFiles.size(); i++) {
			if (!arquivosAdded.contains(i))
				grupo9.add(listFiles.get(i));
		}
		groupTokens.put(9, grupo9);*/
		
		return groupTokens;
	}
	
	/**
	 * Finds the file in listFileNoBalane to add in test set
	 * @author raquelsilveira
	 * @date 05/03/2015
	 * @param selectedFile
	 * @param listFileNoBalance
	 * @return found file
	 */
	private FileData findFileNoBalance(FileData selectedFile, ArrayList<FileData> listFileNoBalance) {
		
		for (FileData file : listFileNoBalance) {
			if (file.getName().equals(selectedFile.getName()))
				return file;
		}
		return null;
	}
	
	private Hashtable<Integer, ArrayList<FileData>> writeFilesCrossValidation(Hashtable<Integer, ArrayList<FileData>> filesGroup, ArrayList<FileData> listFilesNoBalance, boolean arquivosGerados) {
		
		Hashtable<Integer, ArrayList<FileData>> groupTest = new Hashtable<Integer, ArrayList<FileData>>();
		
		Keyphraseness keyphraseness = new Keyphraseness();
		//Run the files grouped to realize the cross-validation 
		for (int i = 0; i < 10; i++) {
			ArrayList<Token> train = new ArrayList<Token>();
			ArrayList<Token> test = new ArrayList<Token>();
			ArrayList<FileData> filesTrain = new ArrayList<FileData>();
			ArrayList<FileData> filesTest = new ArrayList<FileData>();
			
			for (Integer index : filesGroup.keySet()) {
				if (i == index) { //test
					for (int k = 0; k < filesGroup.get(index).size(); k++) {
						FileData selectedFile = filesGroup.get(index).get(k);
						FileData foundFile = findFileNoBalance(selectedFile, listFilesNoBalance);
						//if (foundFile == null) { System.out.println("NULL"); }
						test.addAll(foundFile.getTokens());
						filesTest.add(foundFile);
					}
					groupTest.put(i, filesTest);
				}
				else { //train
					for (int k = 0; k < filesGroup.get(index).size(); k++) {
						filesTrain.addAll(filesGroup.get(index));
						train.addAll(filesGroup.get(index).get(k).getTokens());
					}
				}
			}
			
			countTestSet(train, true);
			countTestSet(test, false);
			
			/*Hashtable<String, Integer> tags = new Hashtable<String, Integer>();
			for (Token t : train) {
				if (t.getClassification().equals("Yes")) {
					if (tags.containsKey(t.getLemma())) 
						tags.put(t.getLemma(), tags.get(t.getLemma())+1);
					else
						tags.put(t.getLemma(), 1);
				}
			}
			
			//Calculates the keyphraseness feature to test set
			for (Token tokenTrain : train) {
				double keyphr = keyphraseness.calculateKeyphrasenessTokens(tokenTrain, tags, (qttyFiles - filesGroup.get(i).size()));
				tokenTrain.features.put(features.keyphraseness.getFeature(), keyphr);
			}
			
			//Calculates the keyphraseness feature to test set
			for (Token tokenTest : test) {
				double keyphr = keyphraseness.calculateKeyphrasenessTokens(tokenTest, tags, (qttyFiles - filesGroup.get(i).size()));
				tokenTest.features.put(features.keyphraseness.getFeature(), keyphr);
			}*/
			
			System.out.println("Train d" + i + ": " + train.size());
			System.out.println("Test d" + i + ": " + test.size());
			
			if (!arquivosGerados) {
					try {
						new WriteArff().createFolders(Config.OUTPUT_TRAIN_TEST + "/train_d" + i);
						new WriteArff().writeFilesByToken(train, Config.OUTPUT_TRAIN_TEST + "/train_d" + i);
						new WriteArff().createFolders(Config.OUTPUT_TRAIN_TEST + "/test_d" + i);
						new WriteArff().writeFilesByToken(test, Config.OUTPUT_TRAIN_TEST + "/test_d" + i);
					}
					catch (IOException e) { e.printStackTrace(); }
			}
		}
		System.out.println("----- TRAIN -----");
		System.out.println("Tag text: " + countTagTextTrain);
		System.out.println("Tag no text: " + countTagNoTextTrain);
		System.out.println("No tag text: " + countNoTagTextTrain);
		System.out.println("No tag no text: " + countNoTagNoTextTrain);
		
		System.out.println("----- TEST -----");
		System.out.println("Tag text: " + countTagText);
		System.out.println("Tag no text: " + countTagNoText);
		System.out.println("No tag text: " + countNoTagText);
		System.out.println("No tag no text: " + countNoTagNoText);
		
		return groupTest;
	}

	int countNoTagText = 0;
	int countTagText = 0;
	int countNoTagNoText = 0;
	int countTagNoText = 0;
	
	int countNoTagTextTrain = 0;
	int countTagTextTrain = 0;
	int countNoTagNoTextTrain = 0;
	int countTagNoTextTrain = 0;
	
	private void countTestSet(ArrayList<Token> tokens, boolean train) {
		
		for (Token t : tokens) {
			if (t.getClassification().equals("Yes")) {
				if (t.features.get(features.ofText.getFeature()) == 1.0D)
					if (train) countTagTextTrain++; 
					else countTagText++;
				else
					if (train) countTagNoTextTrain++;
					else countTagNoText++;
			}
			else {
				if (t.features.get(features.ofText.getFeature()) == 1.0D)
					if (train) countNoTagTextTrain++;
					else countNoTagText++;
				else
					if (train) countNoTagNoTextTrain++;
					else countNoTagNoText++;
			}
		}
	}
	
	
	private void writeFilesCrossValidation(Hashtable<Integer, ArrayList<Token>> tokensGroup) {
		
		
		//Run the files grouped to realize the cross-validation 
		for (int i = 0; i < 10; i++) {
			ArrayList<Token> train = new ArrayList<Token>();
			ArrayList<Token> test = new ArrayList<Token>();
			
			for (Integer index : tokensGroup.keySet()) {
				if (i == index)
					test.addAll(tokensGroup.get(index));
				else
					train.addAll(tokensGroup.get(index));
			}
			
			System.out.println("Train d" + i + ": " + train.size());
			System.out.println("Test d" + i + ": " + test.size());
			
			try {
				new WriteArff().createFolders(Config.OUTPUT_TRAIN_TEST + "/train_d" + i);
				new WriteArff().writeFilesByToken(train, Config.OUTPUT_TRAIN_TEST + "/train_d" + i);
				new WriteArff().createFolders(Config.OUTPUT_TRAIN_TEST + "/test_d" + i);
				new WriteArff().writeFilesByToken(test, Config.OUTPUT_TRAIN_TEST + "/test_d" + i);
			}
			catch (IOException e) { e.printStackTrace(); }
		}
	}
	
	public void runRandomForestCrossValidation(ArrayList<FileData> listFiles) {
				
		//System.out.println("Files: " + listFiles.size());
		
		ArrayList<Token> tokenNoText = new ArrayList<Token>();
		ArrayList<Token> tokenText = new ArrayList<Token>();
		ArrayList<Token> tagNoText = new ArrayList<Token>();
		ArrayList<Token> tagText = new ArrayList<Token>();
		
		int countTags = 0;
		int countTokens = 0;
		for (FileData file : listFiles) {
			countTags += file.getTags().size();
			countTokens += file.getTokens().size();
			
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 1) { 
					if (token.getClassification().equals("Yes"))
						tagText.add(token.clone()); 
					else 
						tokenText.add(token.clone());
				} else {
					if (token.getClassification().equals("Yes"))
						tagNoText.add(token.clone());
					else
						tokenNoText.add(token.clone());
				}
			}
		}
		
		divideSetTokens(tokenText, tokenNoText, tagText, tagNoText);
		
		System.out.println("Tags: " + countTags);
		System.out.println("Tokens: " + countTokens);
		
		System.out.println("Token of the text: " + tokenText.size());
		System.out.println("Token no text: " + tokenNoText.size());

		trainTest(listFiles, null);
		
		System.out.println("Finished!!!!");
	}
	
	int correctTagText = 0, correctTagNoText = 0, correctNoTagText = 0, correctNoTagNoText = 0;
	int incorrectTagText = 0, incorrectTagNoText = 0, incorrectNoTagText = 0, incorrectNoTagNoText = 0;
	
	/**
	 * Realize the train and the test with the files divided in 10 groups
	 * @author raquelsilveira
	 * @date 08/02/2015
	 */
	private void trainTest(ArrayList<FileData> listFiles, Hashtable<Integer, ArrayList<FileData>> groupTest) {
		
		try {
			double avgPrecisionYes = 0;
			double avgPrecisionNo = 0;
			double totalTruePositive = 0;
			double totalTrueNegative = 0;
			double totalFalsePositive = 0;
			double totalFalseNegative = 0;
			double avgRecallNo = 0;
			double numInstances = 0;
			int correctRank = 0;
			int correctRankText = 0, correctRankNonText = 0, incorrectRankText = 0, incorrectRankNonText = 0;
			//int totalTags = getTotalOriginalTags(listFiles);
			
			for (int i = ResultSchutz.interactionRF; i < 10; i++) {
				
				System.out.println("");
				System.out.println("Starting the train and the test: " + i);
				
				String pathTrain = "output/" + Config.OUTPUT_TRAIN_TEST + "/train_d" + i + ".arff";
				String pathTest = "output/" + Config.OUTPUT_TRAIN_TEST + "/test_d" + i + ".arff";
				BufferedReader readerTrain = new BufferedReader(new FileReader(pathTrain));
				Instances train = new Instances(readerTrain);
				train.setClassIndex(train.numAttributes() - 1);
				
				BufferedReader readerTest = new BufferedReader(new FileReader(pathTest));
				Instances test = new Instances(readerTest);
				test.setClassIndex(test.numAttributes() - 1);

				//Remove the attributes needless
				ArrayList<String> attributes = Config.getAttributesTrain();
				ArrayList<Integer> indexRemove = new ArrayList<Integer>();
				//Don't considers the attribute that represents the classification (last attribute)
				for (int k = 0; k < train.numAttributes()-1; k++) {
					if (!attributes.contains(train.attribute(k).name()))
						indexRemove.add(k);
				}
				
				//Remove the attributes of the train and test sets
				for (int k = indexRemove.size()-1; k >= 0; k--) {
					int j = indexRemove.get(k);
					train.deleteAttributeAt(j);
					test.deleteAttributeAt(j);
				}
				
				readerTrain.close();
				System.out.println("Number attributes (train): " + train.numAttributes());
				System.out.println("Number attributes (test): " + test.numAttributes());
				for (int y = 0; y < train.numAttributes(); y++)
					System.out.println("Attributes: " + train.attribute(y).name());
				
				int qttyYes = 0, qttyNo = 0;
				for (int j = 0; j < train.numInstances(); j++) {
					if (train.instance(j).classValue() == 0) 
						qttyYes++;
					else
						qttyNo++;
				}
				
				System.out.println("Qtde de Yes: " + qttyYes);
				System.out.println("Qtde de No: " + qttyNo);
				

				int percentageOver = MainInfNet.percentageOverSampling;//(double)qttyNo/qttyYes*100;
				int percentageUnder = 20;
				
				/*SmoteAndTomekLinks smoteEnn = new SmoteAndTomekLinks();
				smoteEnn.setPercentage(200);
				smoteEnn.setNearestNeighbors(5);
				smoteEnn.setInputFormat(train);
				Instances balancedTrain = Filter.useFilter(train, smoteEnn);*/
				
				/*SmoteAndEnn smoteEnn = new SmoteAndEnn();
				smoteEnn.setEnnNumNearestNeighbors(5);
				smoteEnn.setPercentage(200);
				smoteEnn.setNearestNeighbors(5);
				smoteEnn.setInputFormat(train);
				Instances balancedTrain = Filter.useFilter(train, smoteEnn);*/
				
				Instances balancedTrain = train;
				
				if (MainInfNet.modelBalance == 1) {
					SMOTE smote = new SMOTE();
					smote.setNearestNeighbors(5);
					smote.setPercentage(percentageOver);
					smote.setInputFormat(train);
					balancedTrain = Filter.useFilter(train, smote);
				}
				
				//new Balance().realizeUnderSampling(balancedTrain, percentageUnder, percentageOver);
				//System.out.println("terminou under sampling");
				
				if (MainInfNet.modelBalance == 2) {
					balancedTrain = new Balance().realizeOverSampling(train, percentageOver);
					System.out.println("terminou over sampling");
				}
				
				if (MainInfNet.modelBalance == 3) {
					System.out.println("Configura balanceamento...");
					SmoteAndEnn smote = new SmoteAndEnn();
					smote.setNearestNeighbors(5); //parameter
					smote.setEnnNumNearestNeighbors(ResultSchutz.nearestNeighbors); //parameter
					smote.setPercentage(percentageOver); //parameter
					smote.setInputFormat(train);
					balancedTrain = Filter.useFilter(train, smote);
					System.out.println("Finaliza configuração do balanceamento...");
				}
				
				/*if (MainInfNet.modelBalance == 4) {
					System.out.println("Configura o balanceamento (Smote + Tomek Links...)");
					SmoteAndTomekLinks smoteTL = new SmoteAndTomekLinks();
					smoteTL.setPercentage(percentageOver);
					smoteTL.setNearestNeighbors(1);
					smoteTL.setInputFormat(train);
					
					
					
					balancedTrain = balance.filters.Filter.useFilter(train, smoteTL);
				}
				
				if (MainInfNet.modelBalance == 5) {
					System.out.println("Configura o balanceamento (CNN + Tomek Links...");
					CnnAndTomekLinks ctl = new CnnAndTomekLinks();
					ctl.setInputFormat(train);
					balancedTrain = Filter.useFilter(train, ctl);
					System.out.println("Finaliza configuração do balanceamento...");
				}*/
				
				//Tomek Links
				if (MainInfNet.modelBalance == 6) {
					System.out.println("Configura o balanceamento (Tomek Links)...");
					TomekLinkUnderSampling tomekLink = new TomekLinkUnderSampling();
					tomekLink.setInstances(train);
					balancedTrain = tomekLink.doTomekLink();
					System.out.println("Finalizou o balanceamento (Tomek Links): " + balancedTrain.size());
				}
				
				//CNN
				if (MainInfNet.modelBalance == 7) {
					System.out.println("Configura o balanceamento (CNN)...");
					Cnn cnn = new Cnn();
					cnn.setInstances(train);
					balancedTrain = cnn.doCnn();
					System.out.println("Finalizou o balanceamento (CNN): " + balancedTrain.size());
				}
				
				/*qttyYes = 0; qttyNo = 0;
				for (int j = 0; j < train.numInstances(); j++) {
					if (train.instance(j).classValue() == 0) 
						qttyYes++;
					else
						qttyNo++;
				}
				
				System.out.println("Qtde de Yes: " + qttyYes);
				System.out.println("Qtde de No: " + qttyNo);
				*/
				
				
				//Instances balancedTrain = train;
				
				/*int qttyYesTrain = 0, qttyNoTrain = 0;
				for (int j = 0; j < balancedTrain.numInstances(); j++) {
					if (balancedTrain.instance(j).classValue() == 0) 
						qttyYesTrain++;
					else
						qttyNoTrain++;
				}
				System.out.println("Qtde de Yes: " + qttyYesTrain);
				System.out.println("Qtde de No: " + qttyNoTrain);
				*/
				
				int qttyYesTest = 0, qttyNoTest = 0;
				for (int j = 0; j < test.numInstances(); j++) {
					if (test.instance(j).classValue() == 0) 
						qttyYesTest++;
					else
						qttyNoTest++;
				}
				
		        System.out.println("Number instances balanced train: " + balancedTrain.numInstances());
		        System.out.println("Number instances train: " + train.numInstances());
		        System.out.println("Number instances test: " + test.numInstances());
		        
		        
		        //Random Forest
				Evaluation eval = new Evaluation(balancedTrain);
				//J48 rf = new J48();
				//Logistic rf = new Logistic();
				//rf.setMaxIts(-1);
				RandomForest rf = new RandomForest();
				rf.setSeed(1);
				//rf.setUnpruned(false);
				rf.setNumFeatures(0);
				rf.setMaxDepth(0);
				rf.setNumTrees(Config.RF_NUM_TREES);	
				System.out.println("Numero de árvores: " + rf.getNumTrees());
				rf.setPrintTrees(true);
				
				//Logistic Regression
				/*Evaluation eval = new Evaluation(balancedTrain);
				Logistic rf = new Logistic();
				rf.setMaxIts(-1);*/
		        
		        //SVM Rank - WEKA Antigo
		        /*Evaluation eval = new Evaluation(balancedTrain);
		        LibSVM rf = new LibSVM();
				rf.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_POLYNOMIAL, LibSVM.TAGS_KERNELTYPE));
				rf.setProbabilityEstimates(true);*/
				
				Classifier cls = null;
				if (MainInfNet.modelBalance == 8) {
					SmoteBoost sb = new SmoteBoost();
					sb.setSMOTE_NearestNeighbors(5);
					sb.setNumIterations(ResultSchutz.interactionSmoteBoost);
					sb.setSMOTE_Percentage(MainInfNet.percentageOverSampling);
					sb.setClassifier(rf);
					cls = sb;
				}
				else {
					if (MainInfNet.modelBalance == 9) {
						RUSBoost rb = new RUSBoost();
						rb.setNumIterations(ResultSchutz.interactionSmoteBoost);
						rb.setRUS_Percentage(MainInfNet.percentageOverSampling);
						rb.setClassifier(rf);
						cls = rb;
					}
					else
						cls = rf;
				}
					
				
				cls.buildClassifier(balancedTrain);
				
				FileWriter writer = new FileWriter(MainInfNet.outPutExperiments, true);
				writer.write("\n\nResult interaction " + i);
				writer.write("\nTags: " + qttyYesTest);
				writer.write("\nTokens: " + qttyNoTest);
				writer.close();
				
				eval.evaluateModel(cls, test);
				correctTagText = 0;
				correctTagNoText = 0;
				incorrectTagNoText = 0;
				incorrectTagText = 0;
				
				correctNoTagNoText = 0;
				correctNoTagText = 0;
				incorrectNoTagNoText = 0;
				incorrectNoTagText = 0;
				
				printTrees(rf, i);
				
				if (groupTest != null && groupTest.size() > 0)
					checksInstanceTest(pathTest, test, cls, groupTest.get(i));
				else
					checksInstanceTest(pathTest, test, cls, null);
				
				/*int count = 0;
				for (FileData file : groupTest.get(i)) {
					count += file.getTokens().size();
				}
				
				System.out.println("Num tokens test: "  + count);*/
				
				//System.out.println("RANKING...");
				//System.out.println("Num instances test: "  + test.numInstances());
				if (groupTest != null && groupTest.get(i) != null) {
					getRankFunction(groupTest.get(i), cls, test);
				
					for (FileData file : groupTest.get(i)) {
						if (file.getName().replace(".xml", "").equals("101") || 
								file.getName().replace(".xml", "").equals("112878") ||
								file.getName().replace(".xml", "").equals("114199") ||
								file.getName().replace(".xml", "").equals("1206611") ||
								file.getName().replace(".xml", "").equals("126997") ||
								file.getName().replace(".xml", "").equals("1272533") ||
								file.getName().replace(".xml", "").equals("1307464") ||
								file.getName().replace(".xml", "").equals("1320727") ||
								file.getName().replace(".xml", "").equals("1322799") ||
								file.getName().replace(".xml", "").equals("1336057") ||
								file.getName().replace(".xml", "").equals("136657") ||
								file.getName().replace(".xml", "").equals("1624776") ||
								file.getName().replace(".xml", "").equals("1632947") ||
								file.getName().replace(".xml", "").equals("2235507") ||
								file.getName().replace(".xml", "").equals("238188") ||
								file.getName().replace(".xml", "").equals("292") ||
								file.getName().replace(".xml", "").equals("375823") ||
								file.getName().replace(".xml", "").equals("407273") ||
								file.getName().replace(".xml", "").equals("438129") ||
								file.getName().replace(".xml", "").equals("44") ||
								file.getName().replace(".xml", "").equals("504894") ||
								file.getName().replace(".xml", "").equals("506455") ||
								file.getName().replace(".xml", "").equals("546157") ||
								file.getName().replace(".xml", "").equals("559064") ||
								file.getName().replace(".xml", "").equals("668933") ||
								file.getName().replace(".xml", "").equals("738207") ||
								file.getName().replace(".xml", "").equals("740681") ||
								file.getName().replace(".xml", "").equals("771168") ||
								file.getName().replace(".xml", "").equals("86487") ||							
								file.getName().replace(".xml", "").equals("880918")) {
							FileWriter writer1 = new FileWriter(MainInfNet.outPutExperiments, true);
							writer1.write("Termos classificados como tags do arquivo " + file.getName() + ": \n");
							for (Token t : file.getTokens()) {
								if (t.getRankFunction() >= 0.5)
									writer1.write(t.getLemma() + " " + t.features.get(features.ofText.getFeature()) + " " + t.getClassification() + "\n");
							}
							writer1.close();
							//break;
						}
					}
				}
				
				/*Ranking rank = new Ranking(groupTest.get(i));
				correctRank += rank.realizeRanking();
				correctRankText = rank.correctText;
				correctRankNonText = rank.correctNonText;
				incorrectRankText = rank.incorrectText;
				incorrectRankNonText = rank.incorrectNonText;
				System.out.println("T: " + correctRankText + " NT: " + correctRankNonText);
				
				writer = new FileWriter(MainInfNet.outPutExperiments, true);
				writer.write("\n\nRanking: ");
				writer.write("\nTags text correct: " + correctRankText);
				writer.write("\nTags text incorrect: " + incorrectRankText);
				writer.write("\nTags non-text correct: " + correctRankNonText);
				writer.write("\nTags non-text incorrect: " + incorrectRankNonText);
				writer.close();*/
				
				
				/*System.out.println("Qtty instances: " + test.numInstances());
				System.out.println("Qtty Yes: " + countYes);
				System.out.println("True positive: " + eval.numTruePositives(0));
				System.out.println("False positive: " + eval.numFalsePositives(0));
				
				System.out.println("True negative: " + eval.numTruePositives(1));
				System.out.println("False negative: " + eval.numFalsePositives(1));
				
				System.out.println("Precision (Yes): " + eval.precision(0));
				System.out.println("Precision (No): " + eval.precision(1));*/
				
				numInstances += test.numInstances();
				totalTruePositive += eval.numTruePositives(0);
				totalTrueNegative += eval.numTrueNegatives(0);
				
				totalFalsePositive += eval.numFalsePositives(0);
				totalFalseNegative += eval.numFalseNegatives(0);
				
				avgPrecisionYes += eval.precision(0);
				avgPrecisionNo += eval.precision(1);
				
				avgRecallNo += eval.recall(1);
			}
			
			FileWriter writer = new FileWriter(MainInfNet.outPutExperiments, true);
			writer.write("\n\nTOTAL");
			writer.write("\nTags text correct: " + correctTagTextTotal);
			writer.write("\nTags text incorrect: " + incorrectTagTextTotal);
			writer.write("\nNon-Tags text correct: " + correctNoTagTextTotal);
			writer.write("\nNon-Tags text incorrect: " + incorrectNoTagTextTotal);
			
			writer.write("\nTags non-text correct: " + correctTagNoTextTotal);
			writer.write("\nTags non-text incorrect: " + incorrectTagNoTextTotal);
			writer.write("\nNon-Tags non-text correct: " + correctNoTagNoTextTotal);
			writer.write("\nNon-Tags non-text incorrect: " + incorrectNoTagNoTextTotal);			
			
			writer.close();
			
			/*System.out.println("Number instances: " + numInstances);
			System.out.println("Matriz de confusão:");
			System.out.println(totalTruePositive + "\t" + totalFalseNegative);
			System.out.println(totalFalsePositive + "\t" + totalTrueNegative);
			
			
			double precision = avgPrecisionYes/10;
			double recall = totalTruePositive/935;
			double fMeasure = 2 * (precision * recall) / (precision + recall);
			
			double precisionNo = avgPrecisionNo/10;
			double recallNo = avgRecallNo/10;
			double fMeasureNo = 2 * (precisionNo * recallNo) / (precisionNo + recallNo);
			
			System.out.println("Average Precision Yes: " + precision);
			System.out.println("Average Recall Yes: " + recall);
			System.out.println("Average F-measure Yes: " + fMeasure);
			
			System.out.println("Average Precision No: " + precisionNo);
			System.out.println("Average Recall No: " + recallNo);
			System.out.println("Average F-measure No: " + fMeasureNo);
			
			System.out.println("F-measure: " + (fMeasure + fMeasureNo)/2);
			
			System.out.println("Total tags: " + totalTags);
			
			System.out.println("Correct tag text: " + correctTagText);
			System.out.println("Correct no tag text: " + correctNoTagText);
			System.out.println("Correct tag no text: " + correctTagNoText);
			System.out.println("Correct no tag no text: " + correctNoTagNoText);
			
			System.out.println("Incorrect tag text: " + incorrectTagText);
			System.out.println("Incorrect no tag text: " + incorrectNoTagText);
			System.out.println("Incorrect tag no text: " + incorrectTagNoText);
			System.out.println("Incorrect no tag no text: " + incorrectNoTagNoText);
			
			System.out.println("Total keyphraseness equal 1: " + keyph);
			
			System.out.println("Correct ranking: " + correctRank + " (Text: " + correctRankText + ", Non-text: " + correctRankNonText + ")");
			System.out.println("Incorrect ranking: " + (incorrectRankText + incorrectRankNonText) + " (Text: " + incorrectRankText + ", Non-text: " + incorrectRankNonText + ")");
			
			Integer[] values = new Integer[classifiedTagFile.size()];
			classifiedTagFile.values().toArray(values);
			System.out.print("Desvio padrão classificação (tags): ");
			calculateStDev(values);
			
			System.out.print("Desvio padrão arquivos (tags): ");
			calculateStDev(getTagsFile(listFiles));*/
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	
	private Integer[] getTagsFile(ArrayList<FileData> listFiles) {

		Integer[] values = new Integer[listFiles.size()];
		
		int i = 0;
		for (FileData file : listFiles) {
			values[i++] = file.tagsOriginalFile.size();
		}
		return values;
	}
	
	private void calculateStDev(Integer[] values) {
		
		double avg = 0.0, stdev = 0.0;
		for(int i = 0; i < values.length; i++) {
			avg += values[i];
		}
		System.out.println("Soma: " + avg + " " + values.length);
		avg /= (double) values.length;
		
		for (int i = 0; i < values.length; i++) {
			stdev += Math.pow((double) values[i] - avg, 2);
		}
		
		stdev = Math.sqrt(1.0/(double)values.length * stdev);
		
		System.out.println(avg + " +/- " + stdev);
	}
	
	private void printTrees(Classifier rf, int interaction) {
		try {
			FileWriter fileWriter = new FileWriter("output/tree/rf_tree.txt", true);
			fileWriter.write("******************** Fold " + interaction + " ********************\n");
			fileWriter.write(rf.toString());
			fileWriter.close();
		}
		catch(Exception e) { e.printStackTrace(); }
		
	}
	int keyph = 0;
	
	Hashtable<String, Integer> classifiedTagFile = new Hashtable<String, Integer>();
	
	private void checksInstanceTest(String pathTest, Instances test, Classifier cls, ArrayList<FileData> listFiles) 
			throws Exception {
			
		Instances testCheck = new Instances(new BufferedReader(new FileReader(pathTest)));
		int indexAttribOfText = getIndexAttrib(testCheck, features.ofText.getFeature());
		int indexAttribKeyph = getIndexAttrib(testCheck, features.keyphraseness.getFeature());
		int indexAttribSpread = getIndexAttrib(testCheck, features.spread.getFeature());
		System.out.println("Instances test...");
		System.out.println("Qtde de arquivos: " + listFiles.size());
		ArrayList<Token> tokens = new ArrayList<Token>();
		for (FileData f : listFiles) {
			for (Token t : f.getTokens()) {
				t.fileName = f.getName();
			}
			tokens.addAll(f.getTokens());
		}
		
		for (int j = 0; j < test.numInstances(); j++) {
			
			//Instancia classificada como tag
			//if (cls.classifyInstance(test.instance(j)) == 0) {
			//	classifiedTagFile.put(files.get(j).getName(), classifiedTagFile.get(files.get(j).getName())+1);
			//}
			
			if (tokens.get(j).fileName.contains("778023")) {
				
				if (tokens.get(j).getClassification().equals("Yes") || cls.classifyInstance(test.instance(j)) == 0) {
					System.out.println(tokens.get(j));
					System.out.println("Classificação: " + cls.classifyInstance(test.instance(j)) + " (" + cls.distributionForInstance(test.instance(j))[0] + ")");
				}
			}
				
			
			if (test.instance(j).classValue() == 0) {
				
				//System.out.println("File: " + files.get(j).getName() + " -> Token: " + tokens.get(j).getLemma());
				
				//if (tokens.get(j).fileName.contains("Stevaux  José Candido - O Rio Paraná: geomorfogênese  sedimentação e evolução qu.."))
					System.out.println(tokens.get(j));
				
				if (testCheck.instance(j).value(indexAttribOfText) == 0.0) { //Non text
					//if (tokens.get(j).fileName.contains("Stevaux  José Candido - O Rio Paraná: geomorfogênese  sedimentação e evolução qu.."))
							System.out.println("Non text: " + (test.instance(j).classValue() == cls.classifyInstance(test.instance(j)) ? 
								"Correct" : "Incorrect") + " ( " + cls.distributionForInstance(test.instance(j))[0] + ") -> " +
								test.instance(j));
					
					if (test.instance(j).classValue() == cls.classifyInstance(test.instance(j))) {
						if (testCheck.instance(j).value(indexAttribKeyph)*180 == 1)
							keyph++;
					}
				}
				else { //Text
					//if (tokens.get(j).fileName.equals("Universidad do Chile pegará São Paulo na Sul-Americana"))
					//if (tokens.get(j).fileName.contains("Stevaux  José Candido - O Rio Paraná: geomorfogênese  sedimentação e evolução qu.."))
	
						System.out.println("Text: " + (test.instance(j).classValue() == cls.classifyInstance(test.instance(j)) ? 
								"Correct" : "Incorrect") + " ( " + cls.distributionForInstance(test.instance(j))[0] + ") -> " +
								test.instance(j));
				}
			} 
			
			if (indexAttribOfText >= 0) {
				//Term of the text
				if (testCheck.instance(j).value(indexAttribOfText) == 1.0) {
					if (test.instance(j).classValue() == cls.classifyInstance(test.instance(j))) { //Correct
						if (test.instance(j).classValue() == 0) //Tag
							correctTagText++;
						else //No tag
							correctNoTagText++;
					}
					else { //Incorrect
						if (test.instance(j).classValue() == 0) //Tag
							incorrectTagText++;
						else //No tag
							incorrectNoTagText++;
					}
				}
				else { //Term non of the text
					if (test.instance(j).classValue() == cls.classifyInstance(test.instance(j))) { //Correct
						if (test.instance(j).classValue() == 0)
							correctTagNoText++;
						else
							correctNoTagNoText++;
					}
					else { //Incorrect
						if (test.instance(j).classValue() == 0) //Tag
							incorrectTagNoText++;
						else //No tag
							incorrectNoTagNoText++;
					}
				}
			}
		}
		
		correctTagTextTotal += correctTagText;
		incorrectTagTextTotal += incorrectTagText;
		correctNoTagTextTotal += correctNoTagText;
		incorrectNoTagTextTotal += incorrectNoTagText;
		
		correctTagNoTextTotal += correctTagNoText;
		incorrectTagNoTextTotal += incorrectTagNoText;
		correctNoTagNoTextTotal += correctNoTagNoText;
		incorrectNoTagNoTextTotal += incorrectNoTagNoText;
		
		System.out.println("\nClassifier: ");
		System.out.println("\nTags text correct: " + correctTagText);
		System.out.println("\nTags text incorrect: " + incorrectTagText);
		System.out.println("\nNon-Tags text correct: " + correctNoTagText);
		System.out.println("\nNon-Tags text incorrect: " + incorrectNoTagText);
		
		System.out.println("\nTags non-text correct: " + correctTagNoText);
		System.out.println("\nTags non-text incorrect: " + incorrectTagNoText);
		System.out.println("\nNon-Tags non-text correct: " + correctNoTagNoText);
		System.out.println("\nNon-Tags non-text incorrect: " + incorrectNoTagNoText);
		
		FileWriter writer = new FileWriter(MainInfNet.outPutExperiments, true);
		writer.write("\nClassifier: ");
		writer.write("\nTags text correct: " + correctTagText);
		writer.write("\nTags text incorrect: " + incorrectTagText);
		writer.write("\nNon-Tags text correct: " + correctNoTagText);
		writer.write("\nNon-Tags text incorrect: " + incorrectNoTagText);
		
		writer.write("\nTags non-text correct: " + correctTagNoText);
		writer.write("\nTags non-text incorrect: " + incorrectTagNoText);
		writer.write("\nNon-Tags non-text correct: " + correctNoTagNoText);
		writer.write("\nNon-Tags non-text incorrect: " + incorrectNoTagNoText);
		
		writer.close();
	}
	
	double correctTagTextTotal = 0,  incorrectTagTextTotal = 0, correctNoTagTextTotal = 0, incorrectNoTagTextTotal = 0;
	double correctTagNoTextTotal = 0, incorrectTagNoTextTotal = 0, correctNoTagNoTextTotal = 0, incorrectNoTagNoTextTotal = 0;
			
	
	private int getIndexAttrib(Instances testCheck, String attribute) {
		for (int i = 0; i < testCheck.numAttributes(); i++) { 
			if (testCheck.attribute(i).name().equals(attribute))
				return i;
		}
		return -1;
	}
	
	/**
	 * Gets the total original tags of the file list
	 * @author raquelsilveira
	 * @date 24/02/2015
	 * @param listFiles
	 * @return total original tags
	 */
	private int getTotalOriginalTags(ArrayList<FileData> listFiles) {
		
		int total = 0;
		for (FileData file : listFiles) {
			total += file.tagsOriginalFile.size();
		}
		
		return total;
	}
	
	/**
	 * Divide the tokens set in 10 fold
	 * @author raquelsilveira
	 * @date 07/01/2015
	 * @param tokenText
	 * @param tokenNoText
	 */
	private void divideSetTokens(ArrayList<Token> tokenText, ArrayList<Token> tokenNoText, ArrayList<Token> tagText, ArrayList<Token> tagNoText) {
		
		Hashtable<Integer, ArrayList<Token>> dataSet = new Hashtable<Integer, ArrayList<Token>>();
		
		for (int i = 0; i < 10; i++) {
			
			ArrayList<Token> tokens = new ArrayList<Token>();
			
			System.out.println(i);
			getSetTokens(tokens, tokenText, i);
			int aux = 0;
			System.out.println("Token text: " + (tokens.size() - aux));
			aux = tokens.size();
			
			getSetTokens(tokens, tokenNoText, i);
			System.out.println("Token no text: " + (tokens.size() - aux));
			aux = tokens.size();
			
			getSetTokens(tokens, tagText, i);
			System.out.println("Tag text: " + (tokens.size() - aux));
			aux = tokens.size();
			
			getSetTokens(tokens, tagNoText, i);
			System.out.println("Tag no text: " + (tokens.size() - aux));
			aux = tokens.size();
				
			System.out.println(i + ": " + tokens.size());
			dataSet.put(i, tokens);
		}
		
		//Run the files grouped to realize the cross-validation 
		for (int i = 0; i < 10; i++) {
			ArrayList<Token> train = new ArrayList<Token>();
			ArrayList<Token> test = new ArrayList<Token>();
			
			for (Integer index : dataSet.keySet()) {
				if (i == index)
					test.addAll(dataSet.get(index));
				else
					train.addAll(dataSet.get(index));
			}
			
			System.out.println("Train d" + i + ": " + train.size());
			System.out.println("Test d" + i + ": " + test.size());
			
			try {
				new WriteArff().createFolders("rf_cross_validation/train_d" + i);
				new WriteArff().writeFilesByToken(train, "rf_cross_validation/train_d" + i);
				new WriteArff().createFolders("rf_cross_validation/test_d" + i);
				new WriteArff().writeFilesByToken(test, "rf_cross_validation/test_d" + i);
			}
			catch (IOException e) { e.printStackTrace(); }
		}
	}
	
	/**
	 * Get the cross of the term list (token text, token no text, tag text and tag no text) and add in token list
	 * @author raquelsilveira
	 * @date 07/02/2015
	 * @param tokens
	 * @param terms
	 * @param i
	 */
	private void getSetTokens(ArrayList<Token> tokens, ArrayList<Token> terms, int i) {
		
		int begin = terms.size() / 10;
		int end = i < 9 ? (i+1) * begin - 1 : terms.size() - 1;
		
		for (int j = i * begin; j <= end; j++)
			tokens.add(terms.get(j).clone());
	}
}
