package preprocess.utility;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.lucene.analysis.core.StopAnalyzer;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.CoreMap;
import utility.RegularExpression;
import utility.TextUtil;

public class StanfordNLP {
	public static final int SPECIAL_CHAR1 = 239;
	private static StanfordNLP instance = null;

    protected StanfordCoreNLP pipeline;
    protected MaxentTagger posTagger;

    StanfordNLP() {
        // Create StanfordCoreNLP object properties, with POS tagging
        // (required for lemmatization), and lemmatization
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");
        /*
         * This is a pipeline that takes in a string and returns various analyzed linguistic forms. 
         * The String is tokenized via a tokenizer (such as PTBTokenizerAnnotator), 
         * and then other sequence model style annotation can be used to add things like lemmas, 
         * POS tags, and named entities. These are returned as a list of CoreLabels. 
         * Other analysis components build and store parse trees, dependency graphs, etc. 
         * 
         * This class is designed to apply multiple Annotators to an Annotation. 
         * The idea is that you first build up the pipeline by adding Annotators, 
         * and then you take the objects you wish to annotate and pass them in and 
         * get in return a fully annotated object.
         * 
         *  StanfordCoreNLP loads a lot of models, so you probably
         *  only want to do this once per execution
         */
        this.pipeline = new StanfordCoreNLP(props);
        
        //Create posTagger
        this.posTagger = new MaxentTagger(
                "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger");
    }
    
    /**
     * Get an instance of StanfordNLP via the Singleton design pattern
     * @return
     */
    public static synchronized StanfordNLP getInstance(){
    	if(StanfordNLP.instance == null)
    		StanfordNLP.instance = new StanfordNLP();
    	
    	return StanfordNLP.instance;
    }

    public List<String> lemmatize(String documentText)
    {
        List<String> lemmas = new LinkedList<String>();
        // Create an empty Annotation just with the given text
        Annotation document = new Annotation(documentText);
        // run all Annotators on this text
        this.pipeline.annotate(document);
        // Iterate over all of the sentences found
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            // Iterate over all tokens in a sentence
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                // Retrieve and add the preprocess for each word into the
                // list of lemmas
                lemmas.add(token.get(LemmaAnnotation.class));
            }
        }
        return lemmas;
    }
    
    /**
     * Lemmatize the specified text into sentences
     * 
     * @param documentText
     * @return
     */
    public List<List<String>> lemmatizeIntoSentences(String documentText){
    	List<List<String>> result = new LinkedList<List<String>>();
    	
    	 // Create an empty Annotation just with the given text
        Annotation document = new Annotation(documentText);
        // run all Annotators on this text
        this.pipeline.annotate(document);
        // Iterate over all of the sentences found
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
        	List<String> lemmas = new LinkedList<String>();
            // Iterate over all tokens in a sentence
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                // Retrieve and add the preprocess for each word into the
            	
                // list of lemmas
                lemmas.add(token.get(LemmaAnnotation.class));
            }
            result.add(lemmas);
        }
        
        return result;
    }
    
    
    public List<String> lemmatizeAndFilterStopWord(String documentText, boolean removePunctuations, boolean removeNumbers){
    	char specialChar1 = (char)SPECIAL_CHAR1;
    	
    	List<String> result = new LinkedList<String>();
    	
        // Create an empty Annotation just with the given text
        Annotation document = new Annotation(documentText);
        // run all Annotators on this text
        this.pipeline.annotate(document);
    	

        //get the custom stopword set
        Set<?> stopWords = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
    	
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            // Iterate over all tokens in a sentence
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                // Retrieve and add the preprocess for each word into the
                // list of lemmas
            	String word = token.get(LemmaAnnotation.class);
            	if (!stopWords.contains(word)) {
            		
            		//Skip the word which only have one or no character
            		if(word.length() <= 1)
            			continue;
            		
            		//Skip the word which are too long (possible to be a combined word)
            		if(word.length() > 15)
            			continue;
            		
            		//Skip the word if it has no less than 2 numerical digits
            		if(TextUtil.hasNumDigits(word, 2))
            			continue;
            		
            		//If need to remove numbers, skip this word if it is a number
            		if(removeNumbers && TextUtil.isNumeric(word))
            			continue;
            		
            		//If need to remove punctuation, skip this word if it is a punctuation
            		if(removePunctuations && TextUtil.isPunctuation(word))
            			continue;
            		
            		//Skip the URL string retrieved from HTML pages
            		if(word.indexOf("http") >= 0 || word.indexOf("www") >= 0 
            				|| word.indexOf("@") >= 0 || word.indexOf("#") >= 0 
            				|| word.indexOf("?") >=0 || word.indexOf(specialChar1) >= 0
            				|| word.indexOf("'") >= 0)
            			continue;
            		
            		//If the string contains invalid character, remove them
            		//The valid characters are letters (both upper and lower), numbers, and some special punctuation
            	    // ("-", "_", ";", ".", ":")
            		if(TextUtil.hasInvalidCharacter(word))
            			continue;
            		
            		//Remove the duplicated letters (at least 3 successive repeated letters) from the word
            		if(RegularExpression.hasTripleRepeatedLetters(word))
            			word = TextUtil.removeDuplicateLetters(word);
            		
            		result.add(word);
            	}
            }
        }
    	return result;
    }

    public List<List<TaggedWord>> POSTag(String text){
    	List<List<TaggedWord>> result = null;
    	
        // Create an empty Annotation just with the given text
        Annotation document = new Annotation(text);
        // run all Annotators on this text
        this.pipeline.annotate(document);
        
        // Split the text into sentences
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        
        // Put words of sentences in a list needed by the posTagger
        List<List<CoreLabel>> sentenceList = new ArrayList<List<CoreLabel>>();
        for(CoreMap sentence: sentences){
        	List<CoreLabel> wordList = new ArrayList<CoreLabel>();
        	for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
        		wordList.add(token);
        	}
        	sentenceList.add(wordList);
        }
        
        // Tag the sentences
        result = posTagger.process(sentenceList);
        
        return result;
    }
    
    /**
     * Do Part-of-Speech tagging to the specified text first.
     * Then, replace the original words in the TaggedWord with lemmatized word
     * 
     * @param text
     * @return
     */
    public List<List<TaggedWord>> LemmatizedPOSTag(String text){
    	List<List<TaggedWord>> result = null;
    	
        // Create an empty Annotation just with the given text
        Annotation document = new Annotation(text);
        // run all Annotators on this text
        this.pipeline.annotate(document);
        
        // Split the text into sentences
        List<CoreMap> annotatedSentences = document.get(SentencesAnnotation.class);
        
        // Put words of sentences in a list needed by the posTagger
        List<List<CoreLabel>> sentenceList = new ArrayList<List<CoreLabel>>();
        for(CoreMap sentence: annotatedSentences){
        	List<CoreLabel> wordList = new ArrayList<CoreLabel>();
        	for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
        		wordList.add(token);
        	}
        	sentenceList.add(wordList);
        }
        
        // Tag the sentences with POS Tagger
        result = posTagger.process(sentenceList);
        
        // For each token in the tagged sentence, replace the raw value with lemmatized value  
        int sentID = 0;
		for(List<TaggedWord> sen : result){
			int tokenID = 0;
			for(TaggedWord tw : sen){
				try {
					String lemmatized = annotatedSentences.get(sentID).get(TokensAnnotation.class).get(tokenID).get(LemmaAnnotation.class);
					tw.setValue(lemmatized);
				} catch (Exception e) {
					e.printStackTrace();
					tokenID++;
					continue;
				}
				tokenID++;
			}
			sentID++;
		}
        
        return result;
    }

    public static void main(String[] args) {
        System.out.println("Starting Stanford Lemmatizer");
        String text = "100 How could you be seeing into my eyes like open doors? \n"+
                "You led me down into my core where I've became so numb \n"+
                "Without a soul my spirit's sleeping somewhere cold \n"+
                "Until you find it there and led it back home \n"+
                "You woke me up inside \n"+
                "Called my name and saved me from the dark \n"+
                "You have bidden my blood and it ran \n"+
                "Before I would become undone \n"+
                "You saved me from the nothing I've almost become \n"+
                "You were bringing me to life \n"+
                "Now that I knew what I'm without \n"+
                "You can've just left me \n"+
                "You breathed into me and made me real \n"+
                "Frozen inside without your touch \n"+
                "Without your love, darling \n"+
                "Only you are the life among the dead \n"+
                "I've been living a lie, there's nothing inside \n"+
                "You were bringing me to life.";
        StanfordNLP slem = new StanfordNLP();
        System.out.println(slem.lemmatize(text));
        System.out.println(slem.lemmatizeAndFilterStopWord(text,true,true));

        // The sample string
        String sample = "This is a sample text";
        slem.POSTag(sample);
    }

}