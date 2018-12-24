package balance;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;

public class Cnn {
	
	Instances instances = null;
	protected HashMap<Double, HashSet<Instance>> instancesOfEachClass;
	//static protected HashMap<String, Double> distancesInstances;
	
	/**
	 * Set the instances to filter with the Cnn (under-sampling)
	 * @param instances
	 */
	public void setInstances(Instances instances) {
		this.instances = instances;
	}
	
	/**
	 * Get the instances
	 * @return
	 */
	public Instances getInstances() {
		return instances;
	}
	
	/**
	 * Returns a string describing this classifier.
	 * 
	 * @return 		a description of the classifier suitable for
	 * 			displaying in the explorer/experimenter gui
	 */
	public String globalInfo() {
		return "Undersamples a dataset by applying CNN" + 
		"(choosing a subset which classifys correctly all the samples in the set according to one nearest neighbors - " +
		" starting with the minority samples, and iteratively adding to the subset only samples from the majority class which" +
		" were not classifying correctly according to the current subset), and then finding Tomek Links (pairs from different classes" +
		" which are closest to each other than to any other sample) and removing the majority class instance from each Tomek Link." +
		" The original dataset must fit entirely in memory." +		
		" For more information, see \n\n" 
		+ getTechnicalInformation().toString();
	}
	
	
	/**
	 * Returns an instance of a TechnicalInformation object, containing 
	 * detailed information about the technical background of this class,
	 * e.g., paper reference or book this class is based on.
	 * 
	 * @return 		the technical information about this class
	 */
	public TechnicalInformation getTechnicalInformation() {
		TechnicalInformation result = new TechnicalInformation(Type.ARTICLE);

		result.setValue(Field.AUTHOR, "G E A P A Batista, R C Prati, M C Monard");
		result.setValue(Field.TITLE, "A study of the behavior of several methods for balancing machine learning training data");
		result.setValue(Field.JOURNAL, "Sigkdd Explorations");
		result.setValue(Field.YEAR, "2004");

		return result;
	}

	/**
	 * Main function of this class which performs the CNN and then Tomek Links
	 */
	public Instances doCnn()
	{	
		//if (Cnn.distancesInstances == null)
		//	Cnn.distancesInstances = new HashMap<String, Double>();
		
		//a set of instances per class value
		instancesOfEachClass = new HashMap<Double, HashSet<Instance>>();
		
		Enumeration instanceEnum = getInstances().enumerateInstances();

		System.out.println("Separando as instâncias conforme as classes...");
		int j = 0;
		//loop over all the instances and add each one to the matching set according to its class
		while(instanceEnum.hasMoreElements()) {
			System.out.println("R: " + ++j);
			Instance instance = (Instance) instanceEnum.nextElement();
			
			double classValue = instance.classValue();
			
			if (!instancesOfEachClass.containsKey(classValue))
				instancesOfEachClass.put(classValue, new HashSet<Instance>());
			
			instancesOfEachClass.get(classValue).add(instance);
		}
		
		System.out.println("Total de instâncias: " + instancesOfEachClass.size() + 
				   " Total classe 0.0: " + instancesOfEachClass.get(0.0).size() +
				   " Total classe 1.0: " + instancesOfEachClass.get(0.0).size());
		
		System.out.println("Obtendo a minority class...");
		//find the minority class
		double minorityClassValue = findMinorityClass();
		System.out.println("Minority class: " + minorityClassValue);
		
		System.out.println("Obtendo as instâncias da minority class...");
		//at first, the CNN output is all the minority class instances
		HashSet<Instance> cnnOutput = instancesOfEachClass.get(minorityClassValue);
		System.out.println("Qtde de instâncias da minority class: " + cnnOutput.size());
		
		//next, add only the majority instances which are not classified correctly by one nearest neighbor
		for (Double currClassValue : instancesOfEachClass.keySet())
		{
			if (currClassValue != minorityClassValue)
			{
				//Parameters: cnnOutput (instances Yes), currClassValue (majority class value)
				addMajorityInstancesToCnnOutput(cnnOutput, currClassValue);
			}
		}
		
		System.out.println("Tamanho do cnnOutput: " + cnnOutput.size());
		System.out.println("aqui...");
		
		instances.delete();// clear();
		//push all the remaining instances out to the output queue
		for (Instance instance : cnnOutput)
		{
			instances.add(instance);
		}
		
		System.out.println("Tamanho apos o under-sampling: " + instances.size());
		return instances;
	}

	/**
	 * Find the value of the minority class
	 * @return
	 */
	protected double findMinorityClass()
	{
		int minorityClassSize = Integer.MAX_VALUE;
		double minorityClassValue = 0;
		
		//loop over all the classes
		for (Double classValue : instancesOfEachClass.keySet())
		{
			//check if the current class is smaller than the minority class found so far
			int currClassSize = instancesOfEachClass.get(classValue).size();
			if (currClassSize < minorityClassSize)
			{
				minorityClassSize = currClassSize;
				minorityClassValue = classValue;
			}
		}
		return minorityClassValue;
	}

	/**
	 * This function adds the instances of the majority class to the CNN output
	 * It only adds the instances which are not classified correctly by one nearest neighbor
	 * @param cnnOutput - the subset in the CNN algorithm
	 * @param majorityClassValue - the value of the majority class
	 */
	private void addMajorityInstancesToCnnOutput(HashSet<Instance> cnnOutput, Double majorityClassValue)
	{
		//TODO: tentativa de melhorar o codigo
		//HashSet<String> cnnString = new HashSet<String>();
		//for(Instance instance : cnnOutput)
		//	cnnString.add(instance.toString());
		
		System.out.println("Tamanho da majority class: " + instancesOfEachClass.get(majorityClassValue).size());
		int x = 0;
		//loop over all the majority class instances
		for (Instance majorityClassInstance : instancesOfEachClass.get(majorityClassValue))
		{
			if (x % 1000 == 0)
				System.out.println("X: " + x);
			++x;
			
			//if (cnnString.contains(majorityClassInstance.toString())) continue;
			
			//System.out.println("cnnOutput.size(): " + cnnOutput.size());
			
			//find the nearest neighbor of the current majority class instance within the current CNN output
			Instance nearestNeighbor = findNearestNeighbor(majorityClassInstance, cnnOutput);
			
			//if the nearest neighbor is from a different class it means 
			//that the current instance is not classified correctly
			//so we need to add it the CNN output
			//System.out.println("nearestNeighbor: " + nearestNeighbor);
			//System.out.println("nearestNeighbor.classValue(): " + nearestNeighbor.classValue());
			//System.out.println("majorityClassValue: " + majorityClassValue);
			if (nearestNeighbor.classValue() != majorityClassValue)
			{
				cnnOutput.add(majorityClassInstance);
				//cnnString.add(majorityClassInstance.toString());
			}
		}
	}
	
	protected Instance findNearestNeighbor(Instance instance, HashSet<Instance> neighbors)
	{
		double minDistance = Double.MAX_VALUE;
		Instance nearestNeighbor = null;
		
		for (Instance instanceJ : neighbors)
		{
			if (instance != instanceJ)
			{
				//System.out.println("Instance: " + instance);
				//System.out.println("InstanceJ: " + instanceJ);
				double currDistance = calculateDistance(instance, instanceJ);
				//System.out.println("Distância atual: " + currDistance);
				if (currDistance < minDistance)
				{
					minDistance = currDistance;
					nearestNeighbor = instanceJ;
				}
			}				
		}
		//System.out.println("vizinho retornado: " + nearestNeighbor);
		return nearestNeighbor;
	}
	
	protected double calculateDistance(Instance instanceI, Instance instanceJ)
	{
		Enumeration attrEnum;
		double distance = 0;
		attrEnum = getInstances().enumerateAttributes();
		while(attrEnum.hasMoreElements()) {
			Attribute attr = (Attribute) attrEnum.nextElement();
			//System.out.println("I: " + instanceI.value(attr) + " J: " + instanceJ.value(attr));
			//System.out.println((instanceI.value(attr) != Double.MIN_VALUE) + " " + (instanceI.value(attr) != Double.NaN) +
			//		" " + instanceI.isMissing(attr));
			if ((!attr.equals(getInstances().classAttribute())) &&
				!instanceI.isMissing(attr) && !instanceJ.isMissing(attr)) {
				double iVal = instanceI.value(attr);
				double jVal = instanceJ.value(attr);
				if (attr.isNumeric()) {
					distance += Math.pow(iVal - jVal, 2);
				} 
				//System.out.println("distância parcial: " + distance);
			}
		}
		distance = Math.pow(distance, .5);
		return distance;
	}
}
