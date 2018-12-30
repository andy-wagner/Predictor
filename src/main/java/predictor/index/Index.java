package predictor.index;

import predictor.registry.SearchDictionaryEntry;
import predictor.metric.Metric;

import java.io.Serializable;
import java.util.Set;

/**
 * Индекс поискового алгоритма. Может быть сериализован.
 */
@SuppressWarnings("unused")
public interface Index extends Serializable {
  
  
  //TODO need to settle usage of metric parameter of interface constructors inheritance
  Set<SearchDictionaryEntry> search(String string);
  
  Set<SearchDictionaryEntry> search(String string, int distance, Metric metric);
  
  Set<SearchDictionaryEntry> search(String string, int distance, Metric metric, boolean prefixSearch);
  
  //Set<SearchDictionaryEntry> search(String string, int distance, Metric metric, boolean substringSearch);//TODO substring prefix search (the search substring could be in the middle of strings)

//  void insertEntry(SearchDictionaryEntry entry);
//  long getEntriesCount();
//  long getNodesCount();
}
