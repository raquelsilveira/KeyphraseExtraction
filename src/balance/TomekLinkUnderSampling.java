package balance;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;

import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.TechnicalInformation;
import weka.core.Capabilities.Capability;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;

public class TomekLinkUnderSampling {

	protected HashMap<Double, HashSet<Instance>> instancesOfEachClass;
	Instances instances = null;
	
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
	public Instances doTomekLink()
	{	
		HashSet<Instance> instancesTomek = new HashSet<Instance>();
		instancesOfEachClass = new HashMap<Double, HashSet<Instance>>();
		
		System.out.println("Qtde de instancias: " + getInstances().size());
		Enumeration instanceEnum = getInstances().enumerateInstances();
		//loop over all the instances and add each one to the matching set according to its class
		
		while(instanceEnum.hasMoreElements()) {
			
			Instance instance = (Instance) instanceEnum.nextElement();
			double classValue = instance.classValue();
			
			if (!instancesOfEachClass.containsKey(classValue))
				instancesOfEachClass.put(classValue, new HashSet<Instance>());
			
			instancesOfEachClass.get(classValue).add(instance);
			instancesTomek.add(instance);
		}
		
		//Get the minority class value
		double minorityClassValue = findMinorityClass();
		
		System.out.println("Iniciando tomek link: " + instances.size());
		//perform tomek links and remove only the majority class instances
		HashMap<Instance, Instance> nearestNeighborMap = getNearestNeighborsMap(instancesTomek);
		System.out.println("Obteve o mapa de vizinhos: " + nearestNeighborMap.size());
		
		HashSet<TomekLink> tomekLinks = getTomekLinks(nearestNeighborMap);
		System.out.println("Obteve os tomekLinks: " + tomekLinks.size());
		
		//loop over the tomek links and remove the majority class instances
		for (TomekLink currTomekLink : tomekLinks)
		{
			for (int i = 0; i < 2; i++)
			{
				if (currTomekLink.getInstance(i).classValue() != minorityClassValue)
				{
					instancesTomek.remove(currTomekLink.getInstance(i));
				}
			}
		}
		
		instances.delete();// clear();
		//push all the remaining instances out to the output queue
		for (Instance instance : instancesTomek)
		{
			instances.add(instance);
		}
		
		System.out.println("Qtde de instancias ao final do Tomek Link: " + instances.size());
		return instances;
		//push all the remaining instances out to the output queue
		/*for (Instance instance : instances)
		{
			push(instance);
		}*/
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
	
	protected HashMap<Instance, Instance> getNearestNeighborsMap(HashSet<Instance> instances)
	{
		int x = 0;
		//for each instance, we calculate its nearest neighbor
		HashMap<Instance, Instance> nearestNeighborMap = new HashMap<Instance, Instance>();
		
		for(Instance instanceI : instances)
		{
			System.out.println("X: " + ++x);
			
			Instance nearestNeighbor = findNearestNeighbor(instanceI, instances); 
			nearestNeighborMap.put(instanceI, nearestNeighbor);
		}
		return nearestNeighborMap;
	}
	
	protected Instance findNearestNeighbor(Instance instance, HashSet<Instance> neighbors)
	{
		double minDistance = Double.MAX_VALUE;
		Instance nearestNeighbor = null;
		
		for (Instance instanceJ : neighbors)
		{
			if (instance != instanceJ)
			{
				double currDistance = calculateDistance(instance, instanceJ);
				if (currDistance < minDistance)
				{
					minDistance = currDistance;
					nearestNeighbor = instanceJ;
				}
			}				
		}
		return nearestNeighbor;
	}
	
	protected double calculateDistance(Instance instanceI, Instance instanceJ)
	{
		Enumeration attrEnum;
		double distance = 0;
		attrEnum = getInstances().enumerateAttributes();
		while(attrEnum.hasMoreElements()) {
			Attribute attr = (Attribute) attrEnum.nextElement();
			if (!attr.equals(getInstances().classAttribute()) &&
				!instanceI.isMissing(attr) && !instanceJ.isMissing(attr)) {
				double iVal = instanceI.value(attr);
				double jVal = instanceJ.value(attr);
				
				distance += Math.pow(iVal - jVal, 2);
			}
		}
		distance = Math.pow(distance, .5);
		return distance;
	}
	
	protected HashSet<TomekLink> getTomekLinks(HashMap<Instance, Instance> nearestNeighborMap)
	{
		HashSet<TomekLink> tomekLinks = new HashSet<TomekLink>();
		
		for(Instance instance : nearestNeighborMap.keySet())
		{
			Instance neighbor = nearestNeighborMap.get(instance);
						
			if (neighbor != null)
			{
				if (instance.classValue() != neighbor.classValue()
						&& nearestNeighborMap.get(neighbor) == instance)
				{
					tomekLinks.add(new TomekLink(instance, neighbor));
				}
			}

		}
		return tomekLinks;
	}
}
