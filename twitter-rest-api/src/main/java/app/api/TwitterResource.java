package app.api;

import app.api.Tweet;
import app.model.TweetInference;
import app.twitter.GetTweet;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.common.io.ClassPathResource;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TwitterException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;

@Path("/api")
public class TwitterResource {

    MultiLayerNetwork net = MultiLayerNetwork.load(new ClassPathResource("RNNSentimentModel.net").getFile(), true);
    WordVectors vec = WordVectorSerializer.loadStaticModel(new ClassPathResource("mswiki-uptrain.zip").getFile());
    TweetInference inference = new TweetInference(net, vec);
    Set<Tweet> tweetList = Collections.newSetFromMap(Collections.synchronizedMap(new LinkedHashMap<>()));

    public TwitterResource() throws IOException {
    }

    @GET
    @Path("/view")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTweets() {
        return Response.ok(tweetList).build();
    }

    @POST
    @Path("/search")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchTweets(
            @QueryParam("keyword")
                    String keyword
    ) throws TwitterException {
        if(!tweetList.isEmpty())
            tweetList.clear();
        QueryResult tweet = new GetTweet().getTweets(keyword);
        for(Status status : tweet.getTweets()) {
            Tweet user = new Tweet();
            String tweetContent = status.getText();
            user.setId(status.getId());
            user.setUsername(status.getUser().getScreenName());
            user.setContent(tweetContent);
            /* placeholder score for sentiment model */
            user.setSentiment(inference.getSentiment(tweetContent));
            tweetList.add(user);
        }
        return Response.status(Response.Status.CREATED).entity(tweetList).build();
    }

    @DELETE
    @Path("/clear")
    public Response clearTweets() {
        if(!tweetList.isEmpty()) tweetList.clear();
        return tweetList.isEmpty() ? Response.noContent().build() : Response.status(Response.Status.BAD_REQUEST).build();
    }
}
