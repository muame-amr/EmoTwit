package app.api;

public class Tweet {

    private long id;
    private String sentiment;
    private String username;
    private String content;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() { return content; }
}
