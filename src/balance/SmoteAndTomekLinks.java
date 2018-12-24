package balance;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.filters.supervised.instance.SMOTE;

/**
 * 
 * This class is a filter which resamples a dataset by applying 
 * the Synthetic Minority Oversampling TEchnique (SMOTE), 
 * and then applying Tomek Links (pairs from different classes which 
 * are closest to each other than to any other sample) and removing them
 * 
 * @author Ayelet and Roni
 *
 */
public class SmoteAndTomekLinks extends SMOTE
{


	private static final long serialVersionUID = -6214097953334039028L;

	/**
	 * Returns a string describing this classifier.
	 * 
	 * @return 		a description of the classifier suitable for
	 * 			displaying in the explorer/experimenter gui
	 */
	public String globalInfo() {
		return "Resamples a dataset by applying the Synthetic Minority Oversampling TEchnique (SMOTE), and then applying Tomek Links (pairs from different classes which are closest to each other than to any other sample) and removing them." +
		" The original dataset must fit entirely in memory." +
		" The amount of SMOTE and number of nearest neighbors may be specified." +
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
	 * Signify that this batch of input to the filter is finished. 
	 * If the filter requires all instances prior to filtering,
	 * output() may now be called to retrieve the filtered instances.
	 *
	 * @return 		true if there are instances pending output
	 * @throws IllegalStateException if no input structure has been defined
	 * @throws Exception 	if provided options cannot be executed 
	 * 			on input instances
	 */
	public boolean batchFinished() throws Exception {
		
		System.out.println("Entrou no SMOTE And TomekLinks...");
		//perform SMOTE phase
		super.batchFinished();
		System.out.println("Saiu do construtor do SMOTE");
		//get all the instances from SMOTE
		HashSet<Instance> instances = new HashSet<Instance>();
		
		int i = 0;
		Instance currInstance = output();
		while (currInstance != null)
		{
			System.out.println("R: " + ++i);
			instances.add(currInstance);
			currInstance = output();
		}
		System.out.println("Obteve as instâncias de saída do SMOTE...");
		
		//find the nearest neighbor of each instance
		HashMap<Instance, Instance> nearestNeighborMap = getNearestNeighborsMap(instances);
		
		System.out.println("Obteve os vizinhos...: " + nearestNeighborMap.size());
		//identify all the Tomek Links
		HashSet<TomekLink> tomekLinks = getTomekLinks(nearestNeighborMap);
		
		System.out.println("Obteve os tomek links...: " + tomekLinks.size());
		i = 0;
		//remove all the instances of the Tomek Links
		for (TomekLink currTomekLink : tomekLinks)
		{
			System.out.println("RQ: " + ++i);
			instances.remove(currTomekLink.getInstance(0));
			instances.remove(currTomekLink.getInstance(1));
		}
		
		System.out.println("Terminou o laço...");
		i = 0;
		//push all the remaining instances to the output
		for (Instance instance : instances)
		{
			System.out.println("RL: " + ++i);
			push(instance);
		}
		
		return (numPendingOutput() != 0);
		
	}

	protected HashMap<Instance, Instance> getNearestNeighborsMap(HashSet<Instance> instances)
	{
		//for each instance, we calculate its nearest neighbor
		HashMap<Instance, Instance> nearestNeighborMap = new HashMap<Instance, Instance>();
		
		for(Instance instanceI : instances)
		{
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
		attrEnum = getInputFormat().enumerateAttributes();
		while(attrEnum.hasMoreElements()) {
			Attribute attr = (Attribute) attrEnum.nextElement();
			if (!attr.equals(getInputFormat().classAttribute()) &&
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