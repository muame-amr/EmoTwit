package model.CNN;

import model.RNN.SentimentClassifier;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.deeplearning4j.iterator.CnnSentenceDataSetIterator;
import org.deeplearning4j.iterator.LabeledSentenceProvider;
import org.deeplearning4j.iterator.provider.FileLabeledSentenceProvider;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.graph.MergeVertex;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.GlobalPoolingLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.PoolingType;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.InvocationType;
import org.deeplearning4j.optimize.listeners.EvaluativeListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

public class SentimentCNN {

    private static Logger log = LoggerFactory.getLogger(SentimentCNN.class);

    public static final String resourcePath = new File(System.getProperty("user.dir"), "src/main/resources/").getAbsolutePath();
    public static final String filePath = new File(resourcePath, "ciapan-malaya/").getAbsolutePath();
    public static File vectorFile = new File(resourcePath, "mswiki-uptrain.zip");

    public static void main(String[] args) throws IOException {

        //Basic configuration
        int batchSize = 128;
        int vectorSize = 300;               //Size of the word vectors. 300 in the Google News model
        int nEpochs = 2;                    //Number of epochs (full passes of training data) to train on
        int truncateReviewsToLength = 256;  //Truncate reviews with length (# words) greater than this

        int cnnLayerFeatureMaps = 300;      //Number of feature maps / channels / depth for each CNN layer
        PoolingType globalPoolingType = PoolingType.MAX;
        Random rng = new Random(12345); //For shuffling repeatability

        //Set up the network configuration. Note that we have multiple convolution layers, each wih filter
        //widths of 3, 4 and 5 as per Kim (2014) paper.

        Nd4j.getMemoryManager().setAutoGcWindow(5000);

        ComputationGraphConfiguration config = new NeuralNetConfiguration.Builder()
                .weightInit(WeightInit.RELU)
                .activation(Activation.LEAKYRELU)
                .updater(new Adam(5e-3))
                .convolutionMode(ConvolutionMode.Same)      //This is important so we can 'stack' the results later
                .l2(0.0001)
                .graphBuilder()
                .addInputs("input")
                .addLayer("cnn3", new ConvolutionLayer.Builder()
                        .kernelSize(3, vectorSize)
                        .stride(1, vectorSize)
                        .nOut(cnnLayerFeatureMaps)
                        .build(), "input")
                .addLayer("cnn4", new ConvolutionLayer.Builder()
                        .kernelSize(4, vectorSize)
                        .stride(1, vectorSize)
                        .nOut(cnnLayerFeatureMaps)
                        .build(), "input")
                .addLayer("cnn5", new ConvolutionLayer.Builder()
                        .kernelSize(5, vectorSize)
                        .stride(1, vectorSize)
                        .nOut(cnnLayerFeatureMaps)
                        .build(), "input")
                //MergeVertex performs depth concatenation on activations: 3x[minibatch,100,length,300] to 1x[minibatch,300,length,300]
                .addVertex("merge", new MergeVertex(), "cnn3", "cnn4", "cnn5")
                //Global pooling: pool over x/y locations (dimensions 2 and 3): Activations [minibatch,300,length,300] to [minibatch, 300]
                .addLayer("globalPool", new GlobalPoolingLayer.Builder()
                        .poolingType(globalPoolingType)
                        .dropOut(0.5)
                        .build(), "merge")
                .addLayer("out", new OutputLayer.Builder()
                        .lossFunction(LossFunctions.LossFunction.MCXENT)
                        .activation(Activation.SOFTMAX)
                        .nOut(2)    //2 classes: positive or negative
                        .build(), "globalPool")
                .setOutputs("out")
                //Input has shape [minibatch, channels=1, length=1 to 256, 300]
                .setInputTypes(InputType.convolutional(truncateReviewsToLength, vectorSize, 1))
                .build();

        ComputationGraph net = new ComputationGraph(config);
        net.init();

        System.out.println("Number of parameters by layer:");
        for (Layer l : net.getLayers()) {
            System.out.println("\t" + l.conf().getLayer().getLayerName() + "\t" + l.numParams());
        }

        //Load word vectors and get the DataSetIterators for training and testing
        System.out.println("Loading word vectors and creating DataSetIterators");
        WordVectors wordVectors = WordVectorSerializer.loadStaticModel(vectorFile);
        DataSetIterator trainIter = getDataSetIterator(true, wordVectors, batchSize, truncateReviewsToLength, rng);
        DataSetIterator testIter = getDataSetIterator(false, wordVectors, batchSize, truncateReviewsToLength, rng);

        System.out.println("Starting training");
        net.setListeners(new ScoreIterationListener(100), new EvaluativeListener(testIter, 1, InvocationType.EPOCH_END));
        net.fit(trainIter, nEpochs);

        net.save(new File(resourcePath,"CNNSentimentModel.net"), true);

        Evaluation evalTrain = net.evaluate(trainIter);
        Evaluation evalTest = net.evaluate(testIter);
        System.out.println("=============== Train evaluation ===============");
        System.out.println(evalTrain.stats());
        System.out.println("=============== Test evaluation ===============");
        System.out.println(evalTest.stats());

        //After training: load a single sentence and generate a prediction
        String contentsFirstNegative = "Selamat pagi semua ! Sila duduk";
        INDArray featuresFirstNegative = ((CnnSentenceDataSetIterator) testIter).loadSingleSentence(contentsFirstNegative);

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
