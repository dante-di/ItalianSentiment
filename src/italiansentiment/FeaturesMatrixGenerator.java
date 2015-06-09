/**
 * Computes the features matrix and generete the ARFF (Weka) file
 *
 * LICENSE: To choose
 *
 * package: italianSentiment
 * author: Dante Degl'Innocenti & Dario De Nart
 * mail: deglinnocenti.dante@spes.uniud.it
 * copyright: Dante Degl'Innocenti
 *
 * 2015/02/24 - DDI - v001: Initial structure of the class
 */
package italiansentiment;

import au.com.bytecode.opencsv.CSV;
import it.uniud.ailab.linguisticutilities.ItalianLinguisticUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dante Degl'Innocenti
 */
public class FeaturesMatrixGenerator {

    public static void main(String[] args) {
        //Review SET
//        String reviewDataset = "/home/dante/Dropbox/universita/dataset/tripadvisorITA/tripadvisorBALANCED.csv";
//        String reviewDataset = "/home/dante/Dropbox/universita/dataset/tripadvisorITA/validationSet.csv";
        String reviewDataset = "/home/dante/Dropbox/universita/dataset/tripadvisorITA/tripadvisorCLEANED_VOTE.csv";
        
        
        Set<String> features = new HashSet<>();
        
//        String fileName = "/home/dante/Dropbox/universita/dottorato/corsiSeguiti/ANN/frequency/250.txt";
        String fileName = "/home/dante/Dropbox/universita/dottorato/corsiSeguiti/ANN/frequency/250.txt";
        String outputArffFile = "/home/dante/Scrivania/65K_VALIDATION.arff";
        BufferedReader br = null;

        //read the features file
        try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader(fileName));
            while ((sCurrentLine = br.readLine()) != null) {
                features.add(sCurrentLine); //insert each word in the hashset
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        ArrayList<String> featureList= new ArrayList<>();
        featureList.addAll(features);
        System.out.println(featureList.size());
        ArffExporter myArffExporter = new ArffExporter(featureList);

        //Now we have the features (words) set
        CSV csv = CSV.separator(',').quote('"').create();
        csv.read(reviewDataset, (int rowIndex, String[] values) -> {
            assert values.length == 5;
            String title = values[1];
            String body = values[2];
            String label = values[4];   //classification!
            String allReview = title + " " + body;
            allReview = allReview.toLowerCase();
            //stemming and tokenization
            ArrayList<String> stemmedReview = ItalianLinguisticUtils.italianStem(allReview);
            HashMap<String, Double> reviewFeatures = new HashMap<>();
            //for each word check its presence in the features set
            System.out.print("Review " + (rowIndex + 1));
            for (String word : stemmedReview) {
                if (features.contains(word)) {
                    reviewFeatures.put(word, 1.0);
                }
            }
            myArffExporter.addElement(label, reviewFeatures);
            System.out.println(" DONE");
        });

        System.out.println("Writing ARFF File...");
        //Write arff file
        try {
            myArffExporter.writeSparseArffFile(new File(outputArffFile), Boolean.TRUE, Boolean.TRUE);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FeaturesMatrixGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
