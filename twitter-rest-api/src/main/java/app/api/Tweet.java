package app.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Tweet {

    private long id;
    private String sentiment;
    private String username;
    private String content;
}
