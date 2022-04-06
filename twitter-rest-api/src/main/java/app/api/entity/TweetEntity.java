package app.api.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
      name = "Tweet",
      description = "Tweet profile or representation"
)

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class TweetEntity {
    private long id;
    private double score;
    private String sentiment;
    private String username;
    private String idstring;
    private String displayname;
    private String content;
    private String twitcon;
}
