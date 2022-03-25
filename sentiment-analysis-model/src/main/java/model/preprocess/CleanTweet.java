package model.preprocess;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CleanTweet {

    public static String removeURL(String tweets) {
        // Removing URLs
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
//        Pattern u = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
//        Matcher mu = u.matcher(tweets);
        String cleanTweet = tweets.replaceAll(urlPattern, "").trim();
//        int i = 0;
//        while (mu.find()) {
//            cleanTweet = cleanTweet.replaceAll(mu.group(i),"").trim();
//            i++;
//        }
        return cleanTweet;
    }

    public static String removeHandle(String tweets) {
        String cleanTweets = tweets.replaceAll("(\\s+|^)@\\w+", "").trim();
        return cleanTweets;
    }

    public static String removeHashtag(String tweets) {
        String cleanTweets = tweets.replaceAll("#[^\\\\s]*", "").trim();
        return cleanTweets;
    }

    public static String removeStopWords(String tweets, List<String> stopwords) throws IOException {
        ArrayList<String> allWords =
                Stream.of(tweets.toLowerCase().split(" "))
                        .collect(Collectors.toCollection(ArrayList<String>::new));
        allWords.removeAll(stopwords);

        String cleanTweets = allWords.stream().collect(Collectors.joining(" ")).trim();
        return cleanTweets;
    }

    public static void main(String[] args) throws IOException {
        File readFile = new File(System.getProperty("user.dir"), "src/main/resources/twitter-malaya/raw_tweets.txt");
        File writeFile = new File(System.getProperty("user.dir"), "src/main/resources/twitter-malaya/raw_tweets_clean.txt");
        Scanner myReader = new Scanner(readFile);
        FileWriter myWriter = new FileWriter(writeFile);
        Scanner s = new Scanner(new File(System.getProperty("user.dir"), "src/main/resources/stopwords-ms.txt"));
        List<String> stopwords = new ArrayList<String>();
        while (s.hasNext()){
            stopwords.add(s.next());
        }
        s.close();
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            data = removeURL(data);
            data = removeHandle(data);
            data = removeHashtag(data);
            data = removeStopWords(data, stopwords);
            myWriter.write(data + "\n");
        }
        myWriter.close();
        myReader.close();
    }
}
