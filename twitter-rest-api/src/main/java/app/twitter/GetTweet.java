package app.twitter;

import org.graalvm.collections.Pair;
import twitter4j.*;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class GetTweet {
    public QueryResult getTweets(String keyword) throws TwitterException {
        Twitter twitter = new TwitterFactory().getInstance();
        Query query = new Query(keyword + " exclude:replies exclude:retweets -filter:media");
        QueryResult result = twitter.search(query);
        return result;
//        for (Status status : result.getTweets()) {
//            System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
//        }
    }
}
