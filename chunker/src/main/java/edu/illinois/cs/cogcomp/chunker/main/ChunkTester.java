/**
 * This software is released under the University of Illinois/Research and
 *  Academic Use License. See the LICENSE file in the root folder for details.
 * Copyright (c) 2016
 *
 * Developed by:
 * The Cognitive Computation Group
 * University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.chunker.main;

import edu.illinois.cs.cogcomp.chunker.main.lbjava.ChunkLabel;
import edu.illinois.cs.cogcomp.chunker.main.lbjava.Chunker;
import edu.illinois.cs.cogcomp.chunker.utils.CoNLL2000Parser;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.lbjava.parse.*;
import edu.illinois.cs.cogcomp.lbjava.nlp.seg.BIOTester;
import edu.illinois.cs.cogcomp.lbjava.util.ClassUtils;
import edu.illinois.cs.cogcomp.pos.lbjava.POSTagger;

import java.net.URL;

import static org.junit.Assert.assertNotNull;


/**
 * This class may be used to produce a detailed report of the <i>phrase by
 * phrase</i> performance of {@link Chunker} on given
 * testing data.  {@link Chunker} must exist before
 * attempting to compile this code.
 *
 * <h4>Usage</h4>
 * <blockquote><code>
 *   java edu.illinois.cs.cogcomp.chunker.main.ChunkTester &lt;test data&gt;
 *                                                      [&lt;parser&gt;]
 * </code></blockquote>
 *
 * <h4>Input</h4>
 * <p> The first command line parameter should be filled in with the name of
 * a file containing labeled testing data.  The optional second parameter is
 * the name of a <code>LBJ2.parse.Parser</code> whose constructor takes the
 * name of a file as a <code>String</code> as input and that produces
 * <code>LBJ2.parse.LinkedVector</code> objects representing sentences.  When
 * omitted, the default is {@link CoNLL2000Parser}.
 *
 * <h4>Output</h4>
 * The output is generated by the <code>LBJ2.classify.TestDiscrete</code>
 * class.  See its online documentation at
 * <a href="http://l2r.cs.uiuc.edu/~cogcomp/software/LBJ2/library/LBJ2/classify/TestDiscrete.html">
 *     http://l2r.cs.uiuc.edu/~cogcomp/software/LBJ2/library/LBJ2/classify/TestDiscrete.html</a>.
 *
 * @author Nick Rizzolo
 **/
public class ChunkTester {
    /**
     * Implements the program described above.
     *
     * @param args The command line parameters.
     **/
    public static void main(String[] args) {
        ResourceManager rm = new ChunkerConfigurator().getDefaultConfig();
        String testFileName = rm.getString("testGoldPOSData");
        String testNoPOSFileName = rm.getString("testNoPOSData");

        String parserName = null;
        URL testFileURL = ChunkTester.class.getClassLoader().getResource(testFileName);
        assertNotNull("Test file missing", testFileURL);
        String testFile = testFileURL.getFile();

        URL testNoPOSFileURL = ChunkTester.class.getClassLoader().getResource(testNoPOSFileName);
        assertNotNull("Test file missing", testNoPOSFileURL);
        String testNoPOSFile = testNoPOSFileURL.getFile();
        Parser parser;

        System.out.println("\nWith Gold POS");

        if (parserName == null) parser = new CoNLL2000Parser(testFile);
        else
            parser = ClassUtils.getParser(parserName, new Class[]{ String.class }, new String[]{testFile});

        BIOTester tester = new BIOTester(new Chunker(), new ChunkLabel(), new ChildrenFromVectors(parser));
        tester.test().printPerformance(System.out);

        System.out.println("\nWith NO POS");

        if (parserName == null) parser = new CoNLL2000Parser(testNoPOSFile);
        else
            parser = ClassUtils.getParser(parserName, new Class[]{ String.class }, new String[]{testNoPOSFileName});

        tester = new BIOTester(new Chunker(), new ChunkLabel(), new ChildrenFromVectors(parser));
        tester.test().printPerformance(System.out);




    }
}

