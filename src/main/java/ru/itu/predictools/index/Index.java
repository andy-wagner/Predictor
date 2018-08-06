package ru.itu.predictools.index;

import ru.itu.predictools.metric.Metric;
import ru.itu.predictools.registry.Entry;
import ru.itu.predictools.registry.SearchDictionaryEntry;

import java.io.Serializable;
import java.util.Set;

/**
 * Индекс поискового алгоритма. Может быть сериализован.
 */
public interface Index extends Serializable {

    public void insertEntry(Entry entry);
//TODO need to settle usage of metric parameter of interface constructors inheritance
    public Set<SearchDictionaryEntry> search(String string);
    public Set<SearchDictionaryEntry> search(String string, int distance, Metric metric);
    public Set<SearchDictionaryEntry> search(String string, int distance, Metric metric, boolean prefixSearch);
    //public Set<SearchDictionaryEntry> search(String string, int distance, Metric metric, boolean substringSearch);//TODO substring prefix search (the search substring could be in the middle of strings)

    public long getEntriesCount();
    public long getNodesCount();
}
