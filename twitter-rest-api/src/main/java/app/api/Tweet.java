package app.api;

public class Tweet {

    private long id;
    private double score;
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

    public void setScore(double score) { this.score = score;}

    public double getScore() { return score; }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() { return content; }
}
