package ru.itu.predictools.console;

import ru.itu.predictools.index.IndexPrefixTrie;
import ru.itu.predictools.alphabet.Alphabet;
//import ru.itu.predictools.registry.AlphabetRussian;
import ru.itu.predictools.registry.Dictionary;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/** Actor emulates user actions - gets some text and send it by keyboard to predictive text editor system
 *  to generate text file
 */
public class Console {
    private static final int NUMBER_OF_KEYS = 16; //Для произволного распределения алфавита
    private static final int CHARS_PER_KEY = 4; //Для произволного распределения алфавита
    private static final int NUMBER_OF_DIMENSIONS = 2; //Для произволного распределения алфавита

    Alphabet alphabet;
    Dictionary dictionary;
    IndexPrefixTrie trie;

    String inputWords;
    Keyboard keyboardS; //Single char per key keyboard
    Keyboard keyboardM; //Multiple chars per key keyboard
    Editor phraseS, phraseM;

    //public Console() throws IOException { this(PATH+DICTIONARY_FILE, PATH+INPUT_FILE);}
    public Console(String dictFile, String inputFileName) throws IOException {
        inputWords = "";
        Scanner file = new Scanner(new File(inputFileName) , "UTF8");
        while (file.hasNext())
            inputWords += file.next() + " ";
        file.close();
//        alphabet = new AlphabetRussian();
//        dictionary = new Dictionary(alphabet, dictFile); //, trie, new LevensteinMetric()
        trie = new IndexPrefixTrie(dictionary);

        keyboardS = new Keyboard(alphabet);
        keyboardM = new Keyboard(alphabet, NUMBER_OF_DIMENSIONS, NUMBER_OF_KEYS, CHARS_PER_KEY);
        phraseM = new Editor(trie);
        phraseS = new Editor(trie);
    }

    public String getInputWords() { return inputWords; }
//    private List<String> getSubStrings(){
//        List<String> list;
//        return null;
//    }

    /**
     * returns predictive set of strings depending on content of inputBuffer of editor
     * @return -
     */
//    public List<Entry> getPredictiveSet(){
//        Set<String> set = new HashSet<>();
////            inputBuffer.getFirst().getKeyContent().chars().parallel().forEach();
////        for(Key key: inputKeysBuffer){
//
//        return set;
//    }

//    public static void predictor(String... args) throws IOException, InterruptedException {
//        Console console = new Console(PATH + INPUT_FILE);
//        "hello home slices".chars().parallel().forEach(c->System.out.print((char)c));
//        System.out.println();
//    }
}
