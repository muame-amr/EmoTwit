package model.word2vec;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;

import java.io.File;
import java.util.Collection;

public class LoadWord2Vec {
    public static void main(String[] args) {
        String resourcePath = new File(System.getProperty("user.dir"), "src/main/resources/").getAbsolutePath();
        File vectorFile = new File(resourcePath, "mswiki-uptrain.zip");

        Word2Vec vec = WordVectorSerializer.readWord2VecModel(vectorFile);

        Collection<String> lst = vec.wordsNearest("berjaya", 5);
        System.out.println("5 Words closest to 'berjaya': " + lst);
        System.out.println(vec.getVocab().words().size());
    }
}
