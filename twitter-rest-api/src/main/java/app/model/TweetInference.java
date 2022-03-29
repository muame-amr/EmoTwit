package app.model;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.common.io.ClassPathResource;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TweetInference {
    private MultiLayerNetwork net;
    private WordVectors vec;
    private TokenizerFactory tokenizerFactory;
    private final int maxLength = 280;
    private final int vectorSize = 300;

    public TweetInference(MultiLayerNetwork net, WordVectors vec) {
        this.net = net;
        this.vec = vec;

        tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());
    }

    public String getSentiment(String tweet) {
        INDArray features = getFeatures(tweet);

        INDArray networkOutput = net.output(features);
        long timeSeriesLength = networkOutput.size(2);
        INDArray probabilitiesAtLastWord = networkOutput.get(NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(timeSeriesLength - 1));

        return probabilitiesAtLastWord.getDouble(0) > probabilitiesAtLastWord.getDouble(1) ? "Positive" : "Negative";
    }

    private INDArray getFeatures(String tweet) {
        List<String> tokens = tokenizerFactory.create(tweet).getTokens();
        List<String> tokensFiltered = new ArrayList<>();

        for(String t : tokens){
            if(vec.hasWord(t)) tokensFiltered.add(t);
        }
        int outputLength = Math.min(maxLength,tokensFiltered.size());

        INDArray features = Nd4j.create(1, vectorSize, outputLength);

        int count = 0;
        for( int j=0; j < tokensFiltered.size() && count < maxLength; ++j)
        {
            String token = tokensFiltered.get(j);
            INDArray vector = vec.getWordVectorMatrix(token);
            if(vector == null) continue;   //Word not in word vectors

            features.put(new INDArrayIndex[]{NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(j)}, vector);
            ++count;
        }
        return features;
    }
}
