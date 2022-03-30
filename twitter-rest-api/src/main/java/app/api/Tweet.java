package app.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.net.URL;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Tweet {
    private long id;
    private double score;
    private String sentiment;
    private String username;
    private String idstring;
    private String displayname;
    private String content;
    private String twitcon;
}
