package playground;

import java.io.File;

public class Testing {
    public static void main(String[] args) {
        String resourcePath = new File(System.getProperty("user.dir"), "src/main/resources/").getAbsolutePath();
        String filePath = new File(resourcePath, "twitter-ms/raw_text.txt").getAbsolutePath();
        System.out.println(filePath);
    }
}
