package app.twitter;

import org.graalvm.collections.Pair;
import twitter4j.*;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class GetTweet {
    public QueryResult getTweets(String keyword) throws TwitterException {

        /*

            // Uncomment this and insert your own API keys

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("*********************")
                .setOAuthConsumerSecret("******************************************")
                .setOAuthAccessToken("**************************************************")
                .setOAuthAccessTokenSecret("******************************************");
        TwitterFactory twitter = new TwitterFactory(cb.build());

         */

        Twitter twitter = new TwitterFactory().getInstance();
        Query query = new Query(keyword + " -is:reply -is:retweet -is:quote -has:media -has:images -has:videos");
        QueryResult result = twitter.search(query);
        return result;
    }
}
