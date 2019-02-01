package predictor.index;

import predictor.metric.Metric;
import predictor.registry.SearchDictionary;
import predictor.registry.SearchDictionaryEntry;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"unused", "WeakerAccess"})
public class IndexPrefixTrie extends WordIndex {
  private static final Logger LOGGER = LogManager.getLogger();
  private static final long serialVersionUID = 1L;//todo>> consider serialization
  private PrefixTrieNode root;
  //  private int nodesCount, entriesCount;
  
  class PrefixTrieNode {
    PrefixTrieNode[] children;
    SearchDictionaryEntry entry;
    boolean leaf;
    String chain;
    
    PrefixTrieNode() {
      children = new PrefixTrieNode[alphabet.size()];
      leaf = false;
      chain = "";
      entry = null;
//      nodesCount++;
    }
  }
  
  public IndexPrefixTrie(SearchDictionary searchDictionary) {
    super(searchDictionary, searchDictionary.getAlphabet());
    LOGGER.debug("Building of trie index has started...");
//    nodesCount = entriesCount = 0;
    root = new PrefixTrieNode();
    searchDictionary.getSearchDictionaryEntries().forEach(this::insertEntry);//for each entry in getDictionary
    LOGGER.debug("Building of trie index has finished...");
  }
  
  @Override
  public Set<SearchDictionaryEntry> search(String searchPattern) {
    return search(searchPattern, 0, null);
  }
  
  public Set<SearchDictionaryEntry> search(String searchPattern, int distance, Metric metric) {
    return search(searchPattern, distance, metric, false);
  }
  
  public Set<SearchDictionaryEntry> search(String searchPattern, int distance, Metric metric, boolean prefixSearch) {
    LOGGER.debug("Searching of {} has started ...", searchPattern);
    
    //build first row vector
    int wordLength = searchPattern.length();
    int[] currentRow = new int[wordLength + 2];
    
    //recursive Levenstein distance search from root note by each branch of the trie till each leaf
    IndexPrefixTrie.PrefixTrieNode root = getNodeByString("");
    return recursiveSearch(currentRow, root, searchPattern, metric, distance, prefixSearch);
  }
  
  private Set<SearchDictionaryEntry> recursiveSearch(int[] previousRow, IndexPrefixTrie.PrefixTrieNode node, CharSequence searchPattern,
                                                     Metric metric, int maxDistance, boolean prefixSearch) {
    // todo>> should be optimized - there is no need to calculate matrix when prefix.length=0 or node.chain.length=0 or node.chain.length < prefix.length-maxDistance -->>
    // -->> if (node.chain.length() < searchString.length() - maxDistance) return maxDistance + 1;
    // если длина строки меньше длины префикса на количество символов большее максимально допустимого расстояния (max) то ясно что слово не удовлетворяет условию Ld<max
    
    LOGGER.debug("Next iteration of recursive searching of {} has started ...", searchPattern);
    int searchStringLength = searchPattern.length();
    Set<SearchDictionaryEntry> resultSet = new HashSet<>();
    if (searchStringLength == 0) return resultSet;//no need to do anything to get zero length sequence
    
    int columns = searchStringLength + 2;
    int[] currentRow = new int[columns];
    
    //Build row for the char (ch), with a columns for each letter in the target word (searchString),
    // plus one for the empty string at column 0 and one for minimum at column searchString.length+1
    int charIndexInNodeChain = node.chain.length() - 1;
    if (charIndexInNodeChain < 0) {
      for (int i = 1; i <= searchStringLength; i++) {
        currentRow[i] = i;//the first row for null-length string of root element of trie
      }
    } else {
      char lastCharInNodeChain = node.chain.charAt(charIndexInNodeChain);
      currentRow = metric.getVector(previousRow, lastCharInNodeChain, charIndexInNodeChain, searchPattern, maxDistance);
    }
    
    // if the last entry in the row indicates the optimal cost is less than the maximum cost (distance), and there
    // is a word in this trie node, then add it.
    // currentRow[columns-1] - value of penultimate element of row is actual distance
    if ((currentRow[searchStringLength] <= maxDistance) && node.leaf) {
      Integer distance;
      distance = currentRow[searchStringLength]/* / searchStringLength*/;//TODO distance - absolute or relative???
      if (prefixSearch)
        getDescendants(node).stream().map(e -> new SearchDictionaryEntry(e, distance)).forEach(resultSet::add);
      else
        resultSet.add(new SearchDictionaryEntry(node.entry, distance));//if prefixSearch add to the resultSet subtree of node
    } //else
    // if any entry in the row are less than the maximum cost, then recursively search each branch of the trie
    if (currentRow[columns - 1] <= maxDistance) //currentRow[last] - stores minimum of Vector values
      for (int i = 0; i < alphabet.size(); i++)
        if (node.children[i] != null)
          resultSet.addAll(recursiveSearch(currentRow, node.children[i], searchPattern, metric, maxDistance, prefixSearch));
    LOGGER.debug("Next iteration of recursive searching of {} has finished...", searchPattern);
    return resultSet;
    /*
    private void assembleSubtree(PrefixTrieNode node, List<Entry> subtree) {
        if(node == null) { subtree.clear(); return; }

        for (char ch: alphabet.chars){
            int childIndex=alphabet.mapChar(ch);
            PrefixTrieNode child = node.children[childIndex];
            if (child != null) assembleSubtree(child, subtree);
        }

        if (node.leaf) { subtree.add(node.entry); }
    }
    * */
  }
  
  public SearchDictionary getDictionary() {
    return this.searchDictionary;
  }
  
  public void insertEntry(SearchDictionaryEntry entry) {
    PrefixTrieNode selected = root;
    String chain = "";
    for (char ch : entry.getString().toCharArray()) {
      chain += ch;
      int childIndex = alphabet.mapChar(ch);
      PrefixTrieNode next = selected.children[childIndex];
      if (next == null) {
        selected.children[childIndex] = next = new PrefixTrieNode();
      }
      next.chain = chain;
      selected = next;
    }
    if (!selected.leaf) {//if word not in the index yet then add it, this one is new
      selected.leaf = true;
      selected.entry = entry;
//      entriesCount++;//for debugging and testing compare with getDictionary.Entries.size()
    }
  }
  
  public SearchDictionaryEntry getEntry(String prefix) {
    return getNodeByString(prefix).entry;
  }
  
  public PrefixTrieNode getNodeByString(String prefix) {
    String currentString = "";
    PrefixTrieNode selected = root;
    for (char ch : prefix.toCharArray()) {
      if (alphabet.hasChar(Character.toUpperCase(ch))) {//todo>> eliminate toUpperCase, the symbols case should be determined in upper levels where the alphabet is generating
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
  
  public Set<SearchDictionaryEntry> getDescendants(PrefixTrieNode startNode) {
    Set<SearchDictionaryEntry> descendants = new HashSet<>();
    assembleSubtree(startNode, descendants);
    return descendants;
  }
  
  public Set<SearchDictionaryEntry> getDescendants(String prefix) {
    Set<SearchDictionaryEntry> descendants = new HashSet<>();
    assembleSubtree(prefix, descendants);
    return descendants;
  }
  
  private void assembleSubtree(String prefix, Set<SearchDictionaryEntry> subtree) {
    PrefixTrieNode node = getNodeByString(prefix);
    assembleSubtree(node, subtree);
  }
  
  private void assembleSubtree(PrefixTrieNode node, Set<SearchDictionaryEntry> subtree) {
    if (node == null) {
      subtree.clear();
      return;
    }
    
    for (char ch : alphabet.chars) {
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
        for (char ch: alphabet.chars){
            int childIndex=alphabet.mapChar(ch);
            PrefixTrieNode child = node.children[childIndex];
            if (child != null) processSubtree(child, param, processNode);
        }
        processNode.accept(node, param);
    }

    private <F,P> F reduceSubtree(PrefixTrieNode node, P param, BiFunction<PrefixTrieNode, P, F> processNode){
        for (char ch: alphabet.chars){
            int childIndex=alphabet.mapChar(ch);
            PrefixTrieNode child = node.children[childIndex];
            if (child != null) reduceSubtree(child, param, processNode);
        }
        return processNode.apply(node, param);
    }
*/

//    public Set<Character> reducedAlphabet(String prefix) {
//        Set<Character> set = new HashSet<>();
//
//        for(SearchDictionaryEntry entry: getDescendants(prefix)){
//
//        }
//        return set;
//    }

}
