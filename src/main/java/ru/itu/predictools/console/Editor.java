package ru.itu.predictools.console;

import ru.itu.predictools.index.IndexPrefixTrie;
import ru.itu.predictools.registry.SearchResultEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Editor {//TODO for the whole project - consider & regulate scopes of class properties
    IndexPrefixTrie trie;
    List<String> elements;//entered words in text field
    LinkedList<Key> inputKeysBuffer;//entered keys of next incomplete word
    LinkedList<SearchResultEntry> variants;//predictive variants of entering word //TODO variants should be sorted by frequency & edit distance

    List<String> delimiters;//list of possible delimiters

    public Editor(IndexPrefixTrie trie){
        delimiters  = new ArrayList<>(Arrays.asList(new String[] {" ", ",", ".", ",", ";"}));
        variants = new LinkedList<>(); variants.add(new SearchResultEntry(""));
        elements = new ArrayList<>();
        inputKeysBuffer = new LinkedList<>();
        this.trie = trie;
    }

    public void push(Key key) {
        String keyContent=key.getKeyContent();
        if(delimiters.contains(keyContent)) {
            inputKeysBuffer.clear();
            if(variants.size()>0) {
                elements.add(variants.getLast().getWord());
                variants.clear();
                variants.add(new SearchResultEntry(""));
            }
            //TODO if there is mistake in some word then variants becomes empty, so we need work with errata
        }
        else if(variants.contains(new SearchResultEntry(keyContent))) {
                elements.add(keyContent);
                inputKeysBuffer.clear();
                variants.clear();
                variants.add(new SearchResultEntry(""));
        }
        else {
            inputKeysBuffer.add(key);
            reduceVariantsWPrefixTrie();//TODO use generics & functional interface to generalize reduce method (exact trie, trie subtree, fuzzy search, prefix fuzzy search) so we could pass method as a parameter
        }
    }

    private void reduceVariantsWPrefixTrie(){//TODO use generics to generalize reduce method (exact trie, trie subtree, fuzzy search, prefix fuzzy search)
        while (!inputKeysBuffer.isEmpty()){
            String keyString = inputKeysBuffer.poll().getKeyContent();
            int variantsSize = variants.size();
            for (int v = 0; v < variantsSize; v++) {
                SearchResultEntry variant = variants.poll();
                for (int i = 0; i < keyString.length(); i++) {

                    //add every chain as variant frequency is not defined
                    if (trie.getNodeByString(variant.getWord() + keyString.charAt(i)) != null)//exact typing without errata (distance=0)
                        variants.add(new SearchResultEntry(variant.getWord() + keyString.charAt(i)));

                    //add every exact word (leaf==true)
/*
                    if (trie.getNodeByString(variant.getWord() + keyString.charAt(i)) != null)//exact typing without errata (distance=0)
                        if (trie.isLeaf(variant.getWord() + keyString.charAt(i)))
                            variants.add(new SearchResultEntry(trie.getEntry(variant.getWord() + keyString.charAt(i))));
*/
                }
            }//TODO if there is mistake in some word then variants becomes empty end entrance stops
        }
    }

    public void fillPredictiveKeys(Keyboard keyboard) {
        if(variants.peek().getWord().length() > 1) {
            Integer[] freeKeys = keyboard.getFreeKeysIndexes();
            for (Integer freeKeyIndex : freeKeys) {
                keyboard.getKeys()[freeKeyIndex] = new Key(variants.poll().getWord());
            }
        }
    }

    public LinkedList<SearchResultEntry> getVariants(){ return variants; }

//    /**
//     * returns whole set of string variants depending on content of inputKeysBuffer
//     * @return
//     */
//    public List<String> getVariants(){
//        return variants;
//    }
//
//
}
