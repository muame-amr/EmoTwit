package model.paragraph2vec;

import model.paragraph2vec.tools.LabelSeeker;
import model.paragraph2vec.tools.MeansBuilder;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.deeplearning4j.text.documentiterator.FileLabelAwareIterator;
import org.deeplearning4j.text.documentiterator.LabelAwareIterator;
import org.deeplearning4j.text.documentiterator.LabelledDocument;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.common.primitives.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class ParagraphVectorClassifier {

    private static final Logger log = LoggerFactory.getLogger(ParagraphVectorClassifier.class);

    public static void main(String[] args) {

        // initial file - small twitter-ms datasets
        String resourcePath = new File(System.getProperty("user.dir"), "src/main/resources/").getAbsolutePath();
        File labeled = new File(resourcePath, "paravec/labeled");
        File unlabeled = new File(resourcePath, "paravec/unlabeled");
        ;

        Word2Vec vec = WordVectorSerializer.readWord2VecModel(new File(resourcePath, "twitter-malaya-vector.zip"));

        LabelAwareIterator iterator = new FileLabelAwareIterator.Builder()
                .addSourceFolder(labeled)
                .build();

        TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

        AbstractCache<VocabWord> cache = new AbstractCache<>();

        ParagraphVectors paragraphVectors = new ParagraphVectors.Builder()
                .useExistingWordVectors(vec)
                .learningRate(0.025)
                .minLearningRate(0.001)
                .batchSize(1000)
                .layerSize(100)
                .epochs(20)
                .windowSize(15)
                .iterations(5)
                .minWordFrequency(5)
                .vocabCache(cache)
                .iterate(iterator)
                .trainWordVectors(true)
                .tokenizerFactory(tokenizerFactory)
                .build();

        paragraphVectors.fit();

        String destination = new File(resourcePath, "paravec/twitter-malaya-paravec.zip").getAbsolutePath();
        WordVectorSerializer.writeParagraphVectors(paragraphVectors, destination);

        FileLabelAwareIterator unlabeledIterator = new FileLabelAwareIterator.Builder()
                .addSourceFolder(unlabeled)
                .build();

        MeansBuilder meansBuilder = new MeansBuilder(
                (InMemoryLookupTable<VocabWord>) paragraphVectors.getLookupTable(),
                tokenizerFactory);

        LabelSeeker seeker = new LabelSeeker(iterator.getLabelsSource().getLabels(),
                (InMemoryLookupTable<VocabWord>) paragraphVectors.getLookupTable());

        while (unlabeledIterator.hasNextDocument()) {
            LabelledDocument document = unlabeledIterator.nextDocument();
            INDArray documentAsCentroid = meansBuilder.documentAsVector(document);
            List<Pair<String, Double>> scores = seeker.getScores(documentAsCentroid);

            log.info("Document '" + document.getContent() + "' falls into the following categories: ");
            for (Pair<String, Double> score : scores) {
                log.info("        " + score.getFirst() + ": " + score.getSecond());
            }
        }

    }
}