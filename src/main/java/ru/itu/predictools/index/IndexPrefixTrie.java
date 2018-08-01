package ru.itu.predictools.index;

import ru.itu.predictools.metric.LevensteinMetric;
import ru.itu.predictools.metric.Metric;
import ru.itu.predictools.registry.Dictionary;
import ru.itu.predictools.registry.Entry;
import ru.itu.predictools.registry.SearchResultEntry;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class IndexPrefixTrie extends WordIndex {
  private static final long serialVersionUID = 1L;//todo>> consider serialization
  
  private PrefixTrieNode root;
  
  private int nodesCount, entriesCount;
  
  class PrefixTrieNode {
    PrefixTrieNode[] children;
    Entry entry;
    boolean leaf;
    String chain;
    
    PrefixTrieNode() {
      children = new PrefixTrieNode[alphabet.size()];
      leaf = false;
      chain = "";
      entry = null;
      nodesCount++;
    }
  }
  
  public Dictionary getDictionary() {
    return dictionary;
  }
  
  public IndexPrefixTrie(Dictionary dictionary) {
    super(dictionary, dictionary.getCharsSet());
    nodesCount = entriesCount = 0;
    root = new PrefixTrieNode();
    dictionary.getEntries().forEach(this::insertEntry);//for each entry in getDictionary
  }
  
  @Override
  public Set<SearchResultEntry> search(String string) {
    return null;
  }//TODO prefix trie search(string) shouldn't return null
  
  @Override
  public Set<SearchResultEntry> search(String string, int distance, Metric metric) {
    return search(string, distance, metric, false);
  }
  
  @Override
  public Set<SearchResultEntry> search(String string, int distance, Metric metric, boolean prefixSearch) {
//        Set<SearchResultEntry> set = new HashSet<>();
    
    //build first row vector
    int wordLength = string.length();
    int[] currentRow = new int[wordLength + 2];
    
    //recursive Levenstein distance search from root note by each branch of the trie till each leaf
    IndexPrefixTrie.PrefixTrieNode root = getNodeByString("");
    return recursiveSearch(currentRow, root, string, distance, prefixSearch);
  }
  
  private Set<SearchResultEntry> recursiveSearch(int[] previousRow, IndexPrefixTrie.PrefixTrieNode node, CharSequence searchString,
                                                 int maxDistance, boolean prefixSearch) {
    // TODO should be optimized - there is no need to calculate matrix when prefix.length=0 or node.chain.length=0 or node.chain.length < prefix.length-maxDistance -->>
    // -->> if (node.chain.length() < searchString.length() - maxDistance) return maxDistance + 1;
    // если длина строки меньше длины префикса на количество символов большее максимально допустимого расстояния (max) то ясно что слово не удовлетворяет условию Ld<max
    
    int searchStringLength = searchString.length();
    Set<SearchResultEntry> resultSet = new HashSet<>();
    if (searchStringLength == 0) return resultSet;//no need to do anything to get zero length sequence
    
    int columns = searchStringLength + 2;
    int[] currentRow = new int[columns];
    
    LevensteinMetric metric = new LevensteinMetric(dictionary.geMaxWordLength());
    
    //Build row for the char (ch), with a columns for each letter in the target word (searchString), plus one for the empty string at column 0 and one for minimum at column searchString.length+1
    int charIndexInNodeChain = node.chain.length() - 1;
    if (charIndexInNodeChain < 0) for (int i = 1; i <= searchStringLength; i++)
      currentRow[i] = i;//the first row for null-length string of root element of trie
    else {
      char lastCharInNodeChain = node.chain.charAt(charIndexInNodeChain);
      currentRow = metric.getLevensteinVector(previousRow, lastCharInNodeChain, charIndexInNodeChain, searchString, maxDistance);
    }
    
    // if the last entry in the row indicates the optimal cost is less than the maximum cost (distance), and there
    // is a word in this trie node, then add it.
    // currentRow[columns-1] - value of penultimate element of row is actual distance
    if ((currentRow[searchStringLength] <= maxDistance) && node.leaf) {
      Integer distance;
      distance = currentRow[searchStringLength]/* / searchStringLength*/;//TODO distance - absolute or relative???
      if (prefixSearch)
        getDescendants(node).stream().map(e -> new SearchResultEntry(e, distance)).forEach(resultSet::add);
      else
        resultSet.add(new SearchResultEntry(node.entry, distance));//if prefixSearch add to the resultSet subtree of node
    } //else
    // if any entry in the row are less than the maximum cost, then recursively search each branch of the trie
    if (currentRow[columns - 1] <= maxDistance) //currentRow[last] - stores minimum of Vector values
      for (int i = 0; i < alphabet.size(); i++)
        if (node.children[i] != null)
          resultSet.addAll(recursiveSearch(currentRow, node.children[i], searchString, maxDistance, /*metric, */prefixSearch));//TODO ?metric?
    return resultSet;
    /*
    private void assembleSubtree(PrefixTrieNode node, List<Entry> subtree) {
        if(node == null) { subtree.clear(); return; }

        for (char ch: alphabet.chars()){
            int childIndex=alphabet.mapChar(ch);
            PrefixTrieNode child = node.children[childIndex];
            if (child != null) assembleSubtree(child, subtree);
        }

        if (node.leaf) { subtree.add(node.entry); }
    }
    * */
  }
  
  @Override
  public long getEntriesCount() {
    return entriesCount;
  }
  
  @Override
  public long getNodesCount() {
    return nodesCount;
  }
  
  public void insertEntry(Entry entry) {
    PrefixTrieNode selected = root;
    String chain = "";
    for (char ch : entry.getWord().toCharArray()) {
      chain += ch;
      int childIndex = alphabet.mapChar(ch);
      PrefixTrieNode next = selected.children[childIndex];
      if (next == null) selected.children[childIndex] = next = new PrefixTrieNode();
      next.chain = chain;
      selected = next;
    }
    if (!selected.leaf) {//if word not in the index yet then add it, this one is new
      selected.leaf = true;
      selected.entry = entry;
      entriesCount++;//for debugging and testing compare with getDictionary.Entries.size()
    }
    //TODO it is possible to make it without entry member, but in that case we have to pass word substring by the recursive calls and will lose word parameters such as frequency etc.
    //TODO compare performance for realization with and without .word member
  }
  
  public Entry getEntry(String prefix) {
    return getNodeByString(prefix).entry;
  }
  
  public PrefixTrieNode getNodeByString(String prefix) {
    String currentString = "";
    PrefixTrieNode selected = root;
    for (char ch : prefix.toCharArray()) {
      if (alphabet.isAlphabetChar(Character.toUpperCase(ch))) {//TODO the char is not from & upper/lower case
        int childIndex = alphabet.mapChar(ch);
        if (childIndex > alphabet.size() - 1 || childIndex < 0) return null;
        PrefixTrieNode next = selected.children[childIndex];
        if (next != null) {
          currentString += ch;
          PrefixTrieNode node = selected.children[childIndex];
          if (currentString.equals(prefix)) return node;
          selected = next;
        } else return null;
      } else return null;
    }
    return root;
  }
  
  public boolean isLeaf(String string) {
    return getNodeByString(string).leaf;
  }
  
  public Set<Entry> getDescendants(PrefixTrieNode startNode) {
    Set<Entry> descendants = new HashSet<>();
    assembleSubtree(startNode, descendants);
    return descendants;
  }
  
  public Set<Entry> getDescendants(String prefix) {
    Set<Entry> descendants = new HashSet<>();
    assembleSubtree(prefix, descendants);
    return descendants;
  }
  
  private void assembleSubtree(String prefix, Set<Entry> subtree) {
    PrefixTrieNode node = getNodeByString(prefix);
    assembleSubtree(node, subtree);
  }
  
  private void assembleSubtree(PrefixTrieNode node, Set<Entry> subtree) {
    if (node == null) {
      subtree.clear();
      return;
    }
    
    for (char ch : alphabet.chars()) {
      int childIndex = alphabet.mapChar(ch);
      PrefixTrieNode child = node.children[childIndex];
      if (child != null) assembleSubtree(child, subtree);
    }
    
    if (node.leaf) {
      subtree.add(node.entry);
    }
  }
/*
    private <P> void processSubtree(PrefixTrieNode node, P param, BiConsumer<PrefixTrieNode, P> processNode){
        for (char ch: alphabet.chars()){
            int childIndex=alphabet.mapChar(ch);
            PrefixTrieNode child = node.children[childIndex];
            if (child != null) processSubtree(child, param, processNode);
        }
        processNode.accept(node, param);
    }

    private <F,P> F reduceSubtree(PrefixTrieNode node, P param, BiFunction<PrefixTrieNode, P, F> processNode){
        for (char ch: alphabet.chars()){
            int childIndex=alphabet.mapChar(ch);
            PrefixTrieNode child = node.children[childIndex];
            if (child != null) reduceSubtree(child, param, processNode);
        }
        return processNode.apply(node, param);
    }
*/

//    public Set<Character> reducedAlphabet(String prefix) {//TODO getReducedAlphabet from prefix trie
//        Set<Character> set = new HashSet<>();
//
//        for(Entry entry: getDescendants(prefix)){
//
//        }
//        return set;
//    }

}
