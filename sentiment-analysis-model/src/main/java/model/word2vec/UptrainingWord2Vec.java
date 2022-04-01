package model.word2vec;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;

public class UptrainingWord2Vec {

    private static Logger log = LoggerFactory.getLogger(UptrainingWord2Vec.class);

    public static void main(String[] args) throws FileNotFoundException {
        String resourcePath = new File(System.getProperty("user.dir"), "src/main/resources/").getAbsolutePath();
        String filePath = new File(resourcePath, "twitter-ms/raw_text.txt").getAbsolutePath();
        File vectorFile = new File(resourcePath, "mswiki-uptrain.zip");

        Word2Vec vec = WordVectorSerializer.readWord2VecModel(vectorFile);

        log.info("Load & Vectorize Sentences....");
        SentenceIterator iterator = new BasicLineIterator(filePath);
        TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

        log.info("Reconfiguring model....");
        vec.setTokenizerFactory(tokenizerFactory);
        vec.setSentenceIterator(iterator);
        vec.getConfiguration().setMinWordFrequency(3);
//        vec.getConfiguration().setNegative(0.1);

        log.info("Uptraining Word2Vec model....");
        vec.fit();

        log.info("Vector configurations");
        System.out.println(vec.getConfiguration().toString());

        log.info("Writing word vectors to text file....");

        // Write word vectors to file
        String destination = new File(resourcePath, "mswiki-uptrain.zip").getAbsolutePath();
        WordVectorSerializer.writeWord2VecModel(vec, destination);
    }
}
