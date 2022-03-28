package model.RNN;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

public class SentimentIterator implements DataSetIterator {

    private final WordVectors wordVectors;
    private final int batchSize;
    private final int vectorSize;
    private final int truncateLength;

    private int cursor = 0;
//    private int posCursor = 0;
//    private int negCursor = 0;
    private final File[] positiveFiles;
    private final File[] negativeFiles;
//    private double negPosRatio;
    private final TokenizerFactory tokenizerFactory;


    public SentimentIterator(String dataDirectory, WordVectors wordVectors, int batchSize, int truncateLength, boolean train) {
        this.batchSize = batchSize;
        this.vectorSize = wordVectors.getWordVector(wordVectors.vocab().wordAtIndex(0)).length;

        File p = new File(FilenameUtils.concat(dataDirectory, (train ? "train" : "test") + "/pos/") + "/");
        File n = new File(FilenameUtils.concat(dataDirectory, (train ? "train" : "test") + "/neg/") + "/");
        System.out.println(p.getAbsolutePath());
        System.out.println(n.getAbsolutePath());
        positiveFiles = p.listFiles();
        negativeFiles = n.listFiles();

//        this.negPosRatio = negativeFiles.length / (double) positiveFiles.length;

        this.wordVectors = wordVectors;
        this.truncateLength = truncateLength;

        tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());
    }

    @Override
    public DataSet next(int num) {
        if (cursor >= positiveFiles.length + negativeFiles.length) throw new NoSuchElementException();
        try{
            return nextDataSet(num);
        }catch(IOException e){
            throw new RuntimeException(e);
        }
//        if((negCursor == negativeFiles.length) && posCursor == positiveFiles.length)
//        {
//            throw new NoSuchElementException();
//        }
//        try{
//            return nextDataSet(i);
//        }catch(IOException e){
//            throw new RuntimeException(e);
//        }
    }

    private DataSet nextDataSet(int num) throws IOException {
        //Load mails to String. Unbalanced dataset
        //Include data points of both labels in a ratio proportion
        List<String> tweets = new ArrayList<>(num);
        boolean[] positive = new boolean[num];

        for( int i=0; i<num && cursor<totalExamples(); i++ ){
            if(cursor % 2 == 0){
                //Load positive review
                int posTweetNumber = cursor / 2;
                String tweet = FileUtils.readFileToString(positiveFiles[posTweetNumber], (Charset)null);
                tweets.add(tweet);
                positive[i] = true;
            } else {
                //Load negative review
                int negTweetNumber = cursor / 2;
                String tweet = FileUtils.readFileToString(negativeFiles[negTweetNumber], (Charset)null);
                tweets.add(tweet);
                positive[i] = false;
            }
            cursor++;
        }

        //Tokenize mails and filter out unknown words
        List<List<String>> allTokens = new ArrayList<>(tweets.size());
        int maxLength = 0;
        for(String s : tweets){
            List<String> tokens = tokenizerFactory.create(s).getTokens();
            List<String> tokensFiltered = new ArrayList<>();
            for(String t : tokens ){
                if(wordVectors.hasWord(t)) tokensFiltered.add(t);
            }
            allTokens.add(tokensFiltered);
            maxLength = Math.max(maxLength,tokensFiltered.size());
        }

        //If longest mail exceeds 'truncateLength': only take the first 'truncateLength' words
        if(maxLength > truncateLength) maxLength = truncateLength;

        //Create data for training
        //Here: we have mailArray.size() examples of varying lengths
        INDArray features = Nd4j.create(new int[]{tweets.size(), vectorSize, maxLength}, 'f');
        INDArray labels = Nd4j.create(new int[]{tweets.size(), 2, maxLength}, 'f');    //Two labels: positive or negative

        //Padding arrays because of mails of different lengths and only one output at the final time step
        //Mask arrays contain 1 if data is present at that time step for that example, or 0 if data is just padding
        INDArray featuresMask = Nd4j.zeros(tweets.size(), maxLength);
        INDArray labelsMask = Nd4j.zeros(tweets.size(), maxLength);

        for( int i=0; i<tweets.size(); i++ ){
            List<String> tokens = allTokens.get(i);

            // Get the truncated sequence length of document (i)
            int seqLength = Math.min(tokens.size(), maxLength);

            //Get word vectors for each word in review, and put them in the training data
            for( int j=0; j<tokens.size() && j<maxLength; ++j){
                String token = tokens.get(j);
                INDArray vector = wordVectors.getWordVectorMatrix(token);
                features.put(new INDArrayIndex[]{NDArrayIndex.point(i), NDArrayIndex.all(), NDArrayIndex.point(j)}, vector);

                // Assign "1" to each position where a feature is present, that is, in the interval of [0, seqLength)
                featuresMask.get(new INDArrayIndex[] {NDArrayIndex.point(i), NDArrayIndex.interval(0, seqLength)}).assign(1);
            }

            int idx = (positive[i] ? 0 : 1);
            int lastIdx = Math.min(tokens.size(),maxLength);
            labels.putScalar(new int[]{i,idx,lastIdx-1},1.0);   //Set label: [0,1] for negative, [1,0] for positive
            labelsMask.putScalar(new int[]{i,lastIdx-1},1.0);   //Specify that an output exists at the final time step for this example
        }

        return new DataSet(features,labels,featuresMask,labelsMask);
    }

    public int totalExamples() {
        return negativeFiles.length + positiveFiles.length;
    }

    @Override
    public int inputColumns() {
        return vectorSize;
    }

    @Override
    public int totalOutcomes() {
        return 2;
    }

    @Override
    public boolean resetSupported() {
        return true;
    }

    @Override
    public boolean asyncSupported() {
        return true;
    }

    @Override
    public void reset() {
        cursor = 0;
    }

    @Override
    public int batch() {
        return batchSize;
    }

    @Override
    public void setPreProcessor(DataSetPreProcessor dataSetPreProcessor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataSetPreProcessor getPreProcessor() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<String> getLabels() {
        return Arrays.asList("positive", "negative");
    }

    @Override
    public boolean hasNext() {
        return cursor < totalExamples();
    }

    @Override
    public DataSet next() {
        return next(batchSize);
    }

    public INDArray loadFeaturesFromFile(File file, int maxLength) throws IOException {
        String tweet = FileUtils.readFileToString(file, "UTF-8");
        return loadFeaturesFromString(tweet, maxLength);
    }

    public INDArray loadFeaturesFromString(String tweetContents, int maxLength)
    {
        List<String> tokens = tokenizerFactory.create(tweetContents).getTokens();
        List<String> tokensFiltered = new ArrayList<>();

        for(String t : tokens){
            if(wordVectors.hasWord(t)) tokensFiltered.add(t);
        }
        int outputLength = Math.min(maxLength,tokensFiltered.size());

        INDArray features = Nd4j.create(1, vectorSize, outputLength);

        int count = 0;
        for( int j=0; j < tokensFiltered.size() && count < maxLength; ++j)
        {
            String token = tokensFiltered.get(j);
            INDArray vector = wordVectors.getWordVectorMatrix(token);
            if(vector == null) continue;   //Word not in word vectors

            features.put(new INDArrayIndex[]{NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(j)}, vector);
            ++count;
        }

        return features;
    }
}
