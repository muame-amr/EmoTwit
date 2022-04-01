package app.api;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.core.Application;

@OpenAPIDefinition(
        info = @Info(
                title = "EmoTwit APIs",
                description = "APIs to search tweets and classify their sentiments in real-time",
                version = "1.0.0",
                license = @License(
                        name = "MIT",
                        url = "http://localhost:8080"
                )
        ),
        tags = {
                @Tag(name = "Tweet", description = "Tweets")
        }
)
public class TwitterApplication extends Application {}