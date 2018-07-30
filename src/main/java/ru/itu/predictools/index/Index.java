package ru.itu.predictools.index;

import ru.itu.predictools.metric.Metric;
import ru.itu.predictools.registry.DictionaryEntry;
import ru.itu.predictools.registry.SearchResultEntry;

import java.io.Serializable;
import java.util.Set;

/**
 * Индекс поискового алгоритма. Может быть сериализован.
 */
public interface Index extends Serializable {

    public void insertEntry(DictionaryEntry entry);
//TODO need to settle usage of metric parameter of interface constructors inheritance
    public Set<SearchResultEntry> search(String string);
    public Set<SearchResultEntry> search(String string, int distance, Metric metric);
    public Set<SearchResultEntry> search(String string, int distance, Metric metric, boolean prefixSearch);
    //public Set<SearchResultEntry> search(String string, int distance, Metric metric, boolean substringSearch);//TODO substring prefix search (the search substring could be in the middle of strings)

    public long getEntriesCount();
    public long getNodesCount();
}
