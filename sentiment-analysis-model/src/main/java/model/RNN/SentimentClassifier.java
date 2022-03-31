package model.RNN;

// .lossFunction(new LossMCXENT(Nd4j.create(new double[]{1, 0.8591}))) pos * 1.597673115851304

import model.word2vec.CreateWord2Vec;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.deeplearning4j.core.storage.StatsStorage;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.InvocationType;
import org.deeplearning4j.optimize.listeners.EvaluativeListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.model.stats.StatsListener;
import org.deeplearning4j.ui.model.storage.InMemoryStatsStorage;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class SentimentClassifier {

    private static Logger log = LoggerFactory.getLogger(SentimentClassifier.class);

    public static final String resourcePath = new File(System.getProperty("user.dir"), "src/main/resources/").getAbsolutePath();
    public static final String filePath = new File(resourcePath, "ciapan-malaya/").getAbsolutePath();
    public static File vectorFile = new File(resourcePath, "mswiki-uptrain.zip");

    public static void main(String[] args) throws IOException {

        int batchSize = 128;     //Number of examples in each minibatch
        int vectorSize = 300;   //Size of the word vectors. 300 in the ms-wiki model
        int nEpochs = 5;        //Number of epochs (full passes of training data) to train on
        int truncateTweetsToLength = 256;  //Truncate reviews with length (# words) greater than this
        final int seed = 0;     //Seed for reproducibility

        Nd4j.getMemoryManager().setAutoGcWindow(10000);

        MultiLayerConfiguration conf =  new NeuralNetConfiguration.Builder()
                .seed(seed)
                .updater(new Adam(5e-3))
                .l2(1e-5)
                .weightInit(WeightInit.XAVIER)
                .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                .gradientNormalizationThreshold(1.0)
                .list()
                .layer(new LSTM.Builder()
                        .nIn(vectorSize)
                        .activation(Activation.TANH)
                        .nOut(256)
                        .build())
                .layer(new LSTM.Builder()
                        .nIn(256)
                        .activation(Activation.TANH)
                        .nOut(384)
                        .build())
                .layer(new LSTM.Builder()
                        .nIn(384)
                        .activation(Activation.TANH)
                        .nOut(128)
                        .build())
                .layer(new RnnOutputLayer.Builder()
                        .nIn(128)
                        .nOut(2)
                        .lossFunction(LossFunctions.LossFunction.MCXENT)
                        .activation(Activation.SOFTMAX)
                        .build())
                .build();

        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();

        /*Visualization of training UI
        UIServer uiServer = UIServer.getInstance();

        StatsStorage statsStorage = new InMemoryStatsStorage();
        uiServer.attach(statsStorage); */

        WordVectors wordVectors = WordVectorSerializer.loadStaticModel(vectorFile);
        SentimentIterator trainIter = new SentimentIterator(filePath, wordVectors, batchSize, truncateTweetsToLength, true);
        SentimentIterator testIter = new SentimentIterator(filePath, wordVectors, batchSize, truncateTweetsToLength, false);

        net.setListeners(
                new ScoreIterationListener(1),
                new EvaluativeListener(testIter, 1, InvocationType.EPOCH_END)
//                new StatsListener(statsStorage)
        );

        //Train model
        log.info("Training model...");
        net.fit(trainIter, nEpochs);

        log.info("Evaluating model...");
        log.info("Training model evaluation...");
        Evaluation evalTrain = net.evaluate(trainIter);
        System.out.println(evalTrain.stats());
        log.info("Testing model evaluation...");
        Evaluation evalTest = net.evaluate(testIter);
        System.out.println(evalTest.stats());

        net.save(new File(resourcePath,"RNNSentimentModel.net"), true);

        // After training: load a single example and generate predictions
        // File shortNegativeReviewFile = new File(FilenameUtils.concat(filePath, "test/neg/n500.txt"));
        String shortNegativeTweet = "Babi apa bangi sentral dah jem. Belum lagi ramadan kowtttt ! Kalau dah ramadan nanti tak tahu lah macam mana !";

        INDArray features = testIter.loadFeaturesFromString(shortNegativeTweet, truncateTweetsToLength);
        INDArray networkOutput = net.output(features);
        long timeSeriesLength = networkOutput.size(2);
        INDArray probabilitiesAtLastWord = networkOutput.get(NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(timeSeriesLength - 1));

        System.out.println("\n\n-------------------------------");
        System.out.println("Short negative tweet: \n" + shortNegativeTweet);
        System.out.println("\n\nProbabilities at last time step:");
        System.out.println("p(positive): " + probabilitiesAtLastWord.getDouble(0));
        System.out.println("p(negative): " + probabilitiesAtLastWord.getDouble(1));

        System.out.println("----- Classifier complete -----");
    }
}
