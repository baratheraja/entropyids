package net.floodlightcontroller.fyp.idsentropy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.sun.javafx.collections.MappingChange.Map;

import sun.util.resources.cldr.mg.LocaleNames_mg;

public class Entropy {

	
	public Double calculateEntropy(HashMap<String, Integer> values, Integer count){
		
		ArrayList<Double> Pi = new ArrayList<>();
		int n = 0;
		for(Entry<String, Integer> entry : values.entrySet()){
			//String key = entry.getKey();
			Integer c = entry.getValue();
			if(c !=0){
				n++;
				Pi.add((Double)c.doubleValue()/count);
			}
			
		}
		
		//calculate entropy formula and return
		
		Double sum= 0.0;
		for(Double i : Pi){
			Double temp = (i * Math.log(i))/Math.log(n);
			sum+=temp;
		}
		sum*=-1;
		
		return sum;
	}

}
