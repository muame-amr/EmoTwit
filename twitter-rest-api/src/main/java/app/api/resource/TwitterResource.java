package app.api.resource;

import app.api.entity.TweetEntity;
import app.model.TweetInference;
import app.twitter.GetTweet;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
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
@Tag(name = "EmoTwit Resources", description = "EmoTwit REST APIs")
public class TwitterResource {

    MultiLayerNetwork net = MultiLayerNetwork.load(new ClassPathResource("RNNSentimentModel.net").getFile(), false);
    WordVectors vec = WordVectorSerializer.loadStaticModel(new ClassPathResource("mswiki-uptrain.zip").getFile());
    TweetInference inference = new TweetInference(net, vec);
    Set<TweetEntity> tweetEntityList = Collections.newSetFromMap(Collections.synchronizedMap(new LinkedHashMap<>()));

    public TwitterResource() throws IOException {;
    }

    @GET
    @Path("/view")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            operationId = "getTweets",
            summary = "Get tweet list",
            description = "Get all tweet information inside the list"
    )
    @APIResponse(
            responseCode = "200",
            description = "Operation complete",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    public Response getTweets() {
        return Response.ok(tweetEntityList).build();
    }

    @POST
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            operationId = "searchTweets",
            summary = "Search tweets",
            description = "Search tweets by entering a keyword and add them to the list"
    )
    @APIResponse(
            responseCode = "201",
            description = "Tweets added",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    public Response searchTweets(
            @Parameter(
                    description = "Search query",
                    required = true
            )
            @QueryParam("keyword")
                    String keyword
    ) throws TwitterException {
        if(!tweetEntityList.isEmpty())
            tweetEntityList.clear();
        QueryResult tweet = new GetTweet().getTweets(keyword);
        for(Status status : tweet.getTweets()) {
            TweetEntity user = new TweetEntity();
            String tweetContent = status.getText();
            user.setId(status.getId());
            user.setIdstring(String.valueOf(status.getId()));
            user.setUsername(status.getUser().getScreenName());
            user.setDisplayname(status.getUser().getName());
            user.setContent(tweetContent);
            user.setSentiment(inference.getSentiment(tweetContent).getLeft());
            user.setScore(inference.getSentiment(tweetContent).getRight());
            user.setTwitcon(status.getUser().get400x400ProfileImageURL());
            tweetEntityList.add(user);
        }
        return Response.status(Response.Status.CREATED).entity(tweetEntityList).build();
    }

    @DELETE
    @Path("/clear")
    @Operation(
            operationId = "clearTweets",
            summary = "Clear list of tweets",
            description = "Delete all existing tweets inside the list"
    )
    @APIResponse(
            responseCode = "204",
            description = "Tweet list cleared",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @APIResponse(
            responseCode = "400",
            description = "Request not valid",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    public Response clearTweets() {
        if(!tweetEntityList.isEmpty()) tweetEntityList.clear();
        return tweetEntityList.isEmpty() ? Response.noContent().build() : Response.status(Response.Status.BAD_REQUEST).build();
    }
}
