package app.api;

import app.api.Tweet;
import app.twitter.GetTweet;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TwitterException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/api")
public class TwitterResource {

    Set<Tweet> tweetList = Collections.newSetFromMap(Collections.synchronizedMap(new LinkedHashMap<>()));

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
            user.setId(status.getId());
            user.setUsername(status.getUser().getScreenName());
            user.setContent(status.getText());
            /* placeholder score for sentiment model */
            user.setScore((int)Math.floor(Math.random() * (10 - 1 + 1) + 1));
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