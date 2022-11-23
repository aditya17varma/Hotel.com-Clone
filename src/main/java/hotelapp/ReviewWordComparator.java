package hotelapp;

import java.time.LocalDate;
import java.util.Comparator;

/** Class ReviewWordComparator */
public class ReviewWordComparator implements Comparator<ReviewWordCount> {
    private String word;

    public ReviewWordComparator(String word){
        this.word = word;

    }

    /**
     * compare
     * @param rwc1 First ReviewWordCount object
     * @param rwc2 Second ReviewWordCount object
     * @return -1 if less, 1 if greater, compares LocalDate if equal
     */
    @Override
    public int compare(ReviewWordCount rwc1, ReviewWordCount rwc2){
        int count1 = rwc1.getWordCount(word);
        int count2 = rwc2.getWordCount(word);

        if (count1 == count2){
            LocalDate d1 = rwc1.getDate();
            LocalDate d2 = rwc2.getDate();
            return d1.compareTo(d2);
        }
        else {
            if (count1 < count2){
                return -1;
            }
            return 1;
        }
    }
}
