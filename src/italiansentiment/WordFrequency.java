/**
 * Computes the word frequency for tripadvisorITA dataset
 *
 * LICENSE: To choose
 *
 * package: italianSentiment
 * author: Dante Degl'Innocenti
 * mail: deglinnocenti.dante@spes.uniud.it
 * copyright: Dante Degl'Innocenti
 *
 * 2015/02/17 - DDI - v001: Initial structure of the class
 * 2015/02/19 - DDI - v002: Switched on opencsv library
 */
package italiansentiment;

import au.com.bytecode.opencsv.CSV;
import it.uniud.ailab.linguisticutilities.ItalianLinguisticUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Prints the stemmed word frequency for tripadvisorITA dataset
 *
 * @author DDI
 */
public class WordFrequency {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // mappa parola -> lista di voti
        HashMap<String, ArrayList<Float>> voteDictionary = new HashMap<>();

        String file = "/home/dante/Dropbox/universita/dataset/tripadvisorITA/tripadvisorBALANCED.csv";
        HashMap<String, Integer> wordFrequency = new HashMap<>();
        ArrayList<Float> allvotes=new ArrayList<>();
        CSV csv = CSV.separator(',').quote('"').create();
        csv.read(file, (int rowIndex, String[] values) -> {
            //here csv row logic
            assert values.length == 5;
            String title = values[1];
            String body = values[2];
            float vote = getVote(values[4]);
            String allReview = title + " " + body;
            allReview = allReview.toLowerCase();
            allvotes.add(vote);
            //stemming and tokenization
            ArrayList<String> stemmedReview = ItalianLinguisticUtils.italianStem(allReview);

            //inserting into the counting map
            for (String word : stemmedReview) {
                if (wordFrequency.get(word) == null) {
                    //word not in hashmap
                    wordFrequency.put(word, 1);
                    ArrayList<Float> voteList = new ArrayList<>();
                    voteList.add(vote);
                    voteDictionary.put(word, voteList);
                } else {
                    //word in hashmap. We need to increment the count value
                    wordFrequency.put(word, 1 + wordFrequency.get(word));
                    ArrayList<Float> voteList = voteDictionary.get(word);
                    voteList.add(vote);
                    voteDictionary.put(word, voteList);
                }
            }

        });
        Float datasetAverage = getAverage(allvotes);
//        Tagger myTagger = new Tagger("/home/dante/MEGA/NetBeansProjects/POSTagger/src/resources/post.db");
        //print
        for (Map.Entry<String, Integer> entry : wordFrequency.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();
            Float wordAverage = getAverage(voteDictionary.get(key));
            System.out.println(key + "," + value + "," + Math.abs(datasetAverage-wordAverage)  + "," + value*Math.abs(datasetAverage-wordAverage) );
        }

    }
    
    public static float getAverage(List<Float> votes){
        float acc = 0;
        for(Float i:votes){
            acc+=i;
        }
        return acc/(float)votes.size();
    }

    public static float getVote(String vote) {
        switch (vote) {
            case "positive":
                return 1;
            case "neutral":
                return 0;
            case "negative":
                return -1;
            default:
                return 0;
        }
    }
}
