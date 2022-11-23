package hotelapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

/** Class InvertedIndex */
public class InvertedIndex {
    private HashMap<String, ArrayList<ReviewWordCount>> invIndex;

    private static final String[] stopwords = {"i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "you're",
            "you've", "you'll", "you'd", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she",
            "she's", "her", "hers", "herself", "it", "it's", "its", "itself", "they", "them", "their", "theirs",
            "themselves", "what", "which", "who", "whom", "this", "that", "that'll", "these", "those", "am", "is", "are",
            "was", "were", "be", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing",
            "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while", "of", "at", "by", "for",
            "with", "about", "against", "between", "into", "through", "during", "before", "after", "above", "below", "to",
            "from", "up", "down", "in", "out", "on", "off", "over", "under", "again", "further", "then", "once", "here",
            "there", "when", "where", "why", "how", "all", "any", "both", "each", "few", "more", "most", "other", "some",
            "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "can", "will", "just", "don't",
            "should", "should've", "now", "aren't", "couldn't", "didn't", "doesn't",  "hasn't", "haven't", "isn't", "shouldn't",
            "wasn't", "weren't", "won't", "wouldn't"};


    /**
     * Class InvertedIndex
     * HashMap
     * key = word
     * values = ArrayList of ReviewWordCounts
     *
     */
    public InvertedIndex() {
        this.invIndex = new HashMap<>();
    }

    /**
     * addWordRWC
     * For each word in the review, ReviewWordCount object is added to each key
     * @param rwc ReviewWordCount
     */
    public void addWordRWC(ReviewWordCount rwc){
        for (String word: rwc.getFreqDist().keySet()){
            String cleanWord = word.replaceAll("\\p{Punct}", "");
            if (cleanWord.matches("[a-zA-Z]+") && !Arrays.asList(stopwords).contains(cleanWord.strip().toLowerCase())){
                if (!invIndex.containsKey(cleanWord)){
                    invIndex.put(cleanWord.toLowerCase(), new ArrayList<>());
                }
                invIndex.get(cleanWord.toLowerCase()).add(rwc);
            }
        }
    }

    /**
     * getReviews
     * @param word word
     * @return ArrayList of HotelReviews whose text contains the word
     */
    public ArrayList<ReviewWordCount> getReviewWordCounts(String word){
        return this.invIndex.get(word);
    }

    public HashMap<String, ArrayList<ReviewWordCount>> getFreqDist(){
        return this.invIndex;
    }

    /**
     * getKeys
     * @return Set of the words
     */
    public Set<String> getKeys(){
        return this.invIndex.keySet();
    }
}
