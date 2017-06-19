package edu.illinois.cs.cogcomp.lbjava.learn;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.nlp.utility.TokenizerTextAnnotationBuilder;
import edu.illinois.cs.cogcomp.annotation.TextAnnotationBuilder;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.ner.NERAnnotator;
import edu.illinois.cs.cogcomp.nlp.tokenizer.StatefulTokenizer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by haowu4 on 6/14/17.
 */
public class CleanUpLexicon {
    public static void main(String[] args) throws IOException, AnnotatorException {

        System.setProperty("LBJava.OptimizeVector", args.length > 0 ? args[0] : "true");
        System.setProperty("lbjava.inferenceMode", args.length > 1 ? args[1] : "true");

        boolean autoMode = args.length > 2 && Boolean.parseBoolean(args[2]);

        String outoutPath = args.length > 3 ? args[3] : "outputs.txt";

        Scanner scan = new Scanner(System.in);
        List<String> datas = new ArrayList<>();

        final String FILENAME = "/home/haowu4/data/1blm/text/train/news.en-00008-of-00100";

        Random random = new Random(10);

        try (BufferedReader br = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                if (random.nextBoolean()) {
                    continue;
                }
                datas.add(line.trim());
                i++;
                if (i == 10000) {
                    break;
                }
            }
        }


        System.out.println("Program starting....." + datas.size());
        System.out.println("Size: " + datas.size());
        System.out.println("Auto: " + autoMode);
        if (!autoMode) scan.nextLine();


        String text1 = "Good afternoon, gentlemen. I am a HAL-9000 "
                + "computer. I was born in Urbana, Il. in 1992";

        String corpus = "2001_ODYSSEY";
        String textId = "001";

        TextAnnotationBuilder tab;
        // don't split on hyphens, as NER models are trained this way
        boolean splitOnHyphens = false;
        tab = new TokenizerTextAnnotationBuilder(new StatefulTokenizer(splitOnHyphens));

        System.out.println("Finished setting up, Start to load model.....");

        if (!autoMode) scan.nextLine();

        NERAnnotator co = new NERAnnotator(ViewNames.NER_ONTONOTES);

        // Create a TextAnnotation using the LBJ sentence splitter
        // and tokenizers.
        System.out.println("Finished loading model... Let's test one sentence now.....");
        if (!autoMode) scan.nextLine();

        TextAnnotation ta = tab.createTextAnnotation(corpus, textId, text1);
        co.getView(ta);

        System.out.println("Finished Testing... Let's start annotating.....");
        if (!autoMode) scan.nextLine();

        List<String> outs = new ArrayList<>();

        long start = System.currentTimeMillis();

        for (String line : datas) {
            ta = tab.createTextAnnotation(corpus, textId, line);
            String result = co.getView(ta).toString();
            outs.add(result);
        }

        long end = System.currentTimeMillis();

        System.out.println("Finished Annotating... ready to exit.....");

        long duration = end - start;

        System.out.println(" Time spent " + duration + " ms");
        if (!autoMode) scan.nextLine();

        FileUtils.writeLines(new File(outoutPath), outs);

    }
}
