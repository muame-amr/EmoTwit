package utils;

import app.twitter.GetTweet;
import twitter4j.TwitterException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CleanTweet {
    public static String removeURL(String tweets) {
        // Removing URLs
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(tweets);
        String cleanTweet = tweets;
        int i = 0;
        while (m.find()) {
            cleanTweet = cleanTweet.replaceAll(m.group(i),"").trim();  // remove URLs
            i++;
        }
        return cleanTweet;
    }

    public static void main(String[] args) throws TwitterException {
        String tweets = "asdsad http://asdajn.asdasd";
        System.out.println(removeURL(tweets));
    }
}
