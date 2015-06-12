package edu.illinois.cs.cogcomp.comma.utils;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier;
import edu.illinois.cs.cogcomp.lbjava.classify.TestDiscrete;
import edu.illinois.cs.cogcomp.lbjava.parse.Parser;
import edu.illinois.cs.cogcomp.lbjava.util.TableFormat;

public class EvaluateDiscrete extends TestDiscrete{
	HashMap<String, HashMap<String, Integer>> confusionMatrix;
	public EvaluateDiscrete() {
		confusionMatrix = new HashMap<String, HashMap<String,Integer>>();
	}
	
	@Override
	public void reportPrediction(String p, String l) {
		// TODO Auto-generated method stub
		super.reportPrediction(p, l);
		HashMap<String, Integer> predictionHistogramForL = confusionMatrix.get(l);
		if(predictionHistogramForL == null){
			predictionHistogramForL = new HashMap<String, Integer>();
			confusionMatrix.put(l, predictionHistogramForL);
		}
		histogramAdd(predictionHistogramForL, p, 1);
	}
	
	public void printConfusion(PrintStream out){
		List<String> labels = new ArrayList<String>(confusionMatrix.keySet());
		Collections.sort(labels);
		int numLabels = labels.size();
		Double[][]confusion = new Double[numLabels][];
		for(int x = 0; x<numLabels; x++){
			confusion[x] = new Double[numLabels];
			HashMap<String, Integer> predictionHistogramForL = confusionMatrix.get(labels.get(x));
			for(int y = 0; y<numLabels; y++){
				Integer count = predictionHistogramForL.get(labels.get(y));
				double fraction = ((count==null) ? 0 : count.intValue())/(double)getLabeled(labels.get(x));
				confusion[x][y] = new Double(fraction);
			}
		}
		
		String[]columnLabels = new String[numLabels+1];
		columnLabels[0] = "Confusion";
		String[] rowLabels = labels.toArray(new String[numLabels]);
		System.arraycopy(rowLabels, 0, columnLabels, 1, numLabels);
		TableFormat.printTableFormat(out, columnLabels, rowLabels, confusion);
	}
	
	public void reportAll(EvaluateDiscrete ed){
		super.reportAll(ed);
		matrixAddAll(confusionMatrix, ed.confusionMatrix);
	}
	
	public void matrixAddAll(HashMap<String, HashMap<String,Integer>> addTo, HashMap<String, HashMap<String,Integer>> addFrom){
		for(String label : addFrom.keySet()){
			HashMap<String, Integer> addToLabelHist = addTo.get(label);
			HashMap<String, Integer> addFromLabelHist = addFrom.get(label);
			if(addToLabelHist == null){
				addToLabelHist = new HashMap<String, Integer>();
				addTo.put(label, addToLabelHist);
			}
			histogramAddAll(addToLabelHist, addFromLabelHist);
		}
	}
	
	public static EvaluateDiscrete evaluateDiscrete(Classifier classifier,
			Classifier oracle, Parser parser) {
		return (EvaluateDiscrete) testDiscrete(new EvaluateDiscrete(), classifier, oracle, parser,
				false, 0);
	}
}
