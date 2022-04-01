package model.RNN;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SentimentInference {

    private static Logger log = LoggerFactory.getLogger(SentimentClassifier.class);
    private static final String resourcePath = new File(System.getProperty("user.dir"), "src/main/resources/").getAbsolutePath();
    private static File modelFile = new File(resourcePath, "RNNSentimentModel.net");
    private static File vectorFile = new File(resourcePath, "mswiki-uptrain.zip");
    private static int maxLength = 256;
    private static int vectorSize = 300;
    private static WordVectors vec;
    private static MultiLayerNetwork net;
    private static TokenizerFactory tokenizerFactory;

    public static void main(String[] args) throws IOException {

        String tweetContents = "@CikguFaridah Selamat pagi murid-murid !";
        String negativeTweet = "Babi apa bangi sentral dah jem. Belum lagi ramadan kowtttt ! Kalau dah ramadan nanti tak tahu lah macam mana !";
        vec = WordVectorSerializer.loadStaticModel(vectorFile);
        net = MultiLayerNetwork.load(modelFile, true);

        tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

        INDArray features = getFeature(tweetContents);

        INDArray networkOutput = net.output(features);
        long timeSeriesLength = networkOutput.size(2);
        INDArray probabilitiesAtLastWord = networkOutput.get(NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(timeSeriesLength - 1));

        System.out.println("\n\n-------------------------------");
        System.out.println("Short tweet: \n" + tweetContents);
        System.out.println("\n\nProbabilities at last time step:");
        System.out.println("p(positive): " + probabilitiesAtLastWord.getDouble(0));
        System.out.println("p(negative): " + probabilitiesAtLastWord.getDouble(1));

        features = getFeature(negativeTweet);

        networkOutput = net.output(features);
        timeSeriesLength = networkOutput.size(2);
        probabilitiesAtLastWord = networkOutput.get(NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(timeSeriesLength - 1));

        System.out.println("\n\n-------------------------------");
        System.out.println("Short tweet: \n" + negativeTweet);
        System.out.println("\n\nProbabilities at last time step:");
        System.out.println("p(positive): " + probabilitiesAtLastWord.getDouble(0));
        System.out.println("p(negative): " + probabilitiesAtLastWord.getDouble(1));
    }

    public static INDArray getFeature(String tweet) {
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
