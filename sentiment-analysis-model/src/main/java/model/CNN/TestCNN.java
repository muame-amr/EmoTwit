package model.CNN;

import org.apache.commons.io.FilenameUtils;
import org.deeplearning4j.iterator.CnnSentenceDataSetIterator;
import org.deeplearning4j.iterator.LabeledSentenceProvider;
import org.deeplearning4j.iterator.provider.FileLabeledSentenceProvider;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TestCNN {

    private static Logger log = LoggerFactory.getLogger(TestCNN.class);

    public static final String resourcePath = new File(System.getProperty("user.dir"), "src/main/resources/").getAbsolutePath();
    public static final File modelFile = new File(resourcePath, "CNNSentimentModel.net");
    public static final String filePath = new File(resourcePath, "ciapan-malaya/").getAbsolutePath();
    public static File vectorFile = new File(resourcePath, "mswiki-uptrain.zip");

    public static void main(String[] args) throws IOException {

        int batchSize = 128;
        int vectorSize = 300;               //Size of the word vectors. 300 in the Google News model
        int nEpochs = 2;                    //Number of epochs (full passes of training data) to train on
        int truncateReviewsToLength = 256;  //Truncate reviews with length (# words) greater than this
        Random rng = new Random(12345); //For shuffling repeatability

        WordVectors wordVectors = WordVectorSerializer.loadStaticModel(vectorFile);
        DataSetIterator testIter = getDataSetIterator(false, wordVectors, batchSize, truncateReviewsToLength, rng);
        String contentsFirstNegative = "Kau memang sial la jai !";
        INDArray featuresFirstNegative = ((CnnSentenceDataSetIterator) testIter).loadSingleSentence(contentsFirstNegative);

        ComputationGraph net = ComputationGraph.load(modelFile, true);

        INDArray predictionsFirstNegative = net.outputSingle(featuresFirstNegative);
        List<String> labels = testIter.getLabels();

        System.out.println("\n\nPredictions for first negative review:");
        for (int i = 0; i < labels.size(); i++) {
            System.out.println("P(" + labels.get(i) + ") = " + predictionsFirstNegative.getDouble(i));
        }
    }

    private static DataSetIterator getDataSetIterator(boolean isTraining, WordVectors wordVectors, int minibatchSize,
                                                      int maxSentenceLength, Random rng) {
        File filePositive = new File(FilenameUtils.concat(filePath, (isTraining ? "train" : "test") + "/pos/") + "/");
        File fileNegative = new File(FilenameUtils.concat(filePath, (isTraining ? "train" : "test") + "/neg/") + "/");

        Map<String, List<File>> reviewFilesMap = new HashMap<>();
        reviewFilesMap.put("Positive", Arrays.asList(Objects.requireNonNull(filePositive.listFiles())));
        reviewFilesMap.put("Negative", Arrays.asList(Objects.requireNonNull(fileNegative.listFiles())));

        LabeledSentenceProvider sentenceProvider = new FileLabeledSentenceProvider(reviewFilesMap, rng);

        return new CnnSentenceDataSetIterator.Builder(CnnSentenceDataSetIterator.Format.CNN2D)
                .sentenceProvider(sentenceProvider)
                .wordVectors(wordVectors)
                .minibatchSize(minibatchSize)
                .maxSentenceLength(maxSentenceLength)
                .useNormalizedWordVectors(false)
                .build();
    }
}
