package model.paragraph2vec;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class ParagraphVectorInference {

    private static final Logger log = LoggerFactory.getLogger(ParagraphVectorInference.class);

    public static void main(String[] args) throws IOException {
        String resourcePath = new File(System.getProperty("user.dir"), "src/main/resources/").getAbsolutePath();
        ParagraphVectors vec = WordVectorSerializer.readParagraphVectors(new File(resourcePath, "paravec/twitter-ms-paravec.zip"));

        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());

        // we load externally originated model
        vec.setTokenizerFactory(t);
        vec.getConfiguration().setIterations(1);

        INDArray inferredVectorA = vec.inferVector("Saya suka awak.");
        INDArray inferredVectorA2 = vec.inferVector("Saya sayang awak.");
        INDArray inferredVectorB = vec.inferVector("Saya benci awak.");

        // high similarity expected here, since in underlying corpus words WAY and WORLD have really close context
        log.info("Cosine similarity A/B: {}", Transforms.cosineSim(inferredVectorA, inferredVectorB));

        // equality expected here, since inference is happening for the same sentences
        log.info("Cosine similarity A/A2: {}", Transforms.cosineSim(inferredVectorA, inferredVectorA2));
    }
}
