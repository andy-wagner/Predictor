package ru.itu.predictools.registry;

import java.util.Objects;

@SuppressWarnings("unused")
public class SearchResultEntry extends Entry implements Comparable<SearchResultEntry>{
    private Integer distance; //distance from this entry word to search target //TODO absolute or relative distance what kind is preferable???

    public SearchResultEntry(){ this("",0L,0); }
    public SearchResultEntry(String word) { this(word,0L,0); }
    public SearchResultEntry(String word, Long frequency) { this(word,frequency,0); }
    public SearchResultEntry(DictionaryEntry entry) { this(entry.getWord(), entry.getFrequency(), 0); }
    public SearchResultEntry(DictionaryEntry entry, Integer distance) { this(entry.getWord(), entry.getFrequency(), distance); }
    public SearchResultEntry(String word, Long frequency, Integer distance) {
        super(word,frequency);
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof SearchResultEntry)) return false;

        SearchResultEntry entry = (SearchResultEntry) o;
        boolean result = Objects.equals(entry.getWord(),word)
                && Objects.equals(entry.getDistance(), distance)
                && Objects.equals(entry.getFrequency(), frequency);

        if(entry.getWord().equals(word)) entry.setDistance(distance = Math.min(entry.getDistance(), distance));

        return result;
    }

    @Override //overrides Comparable.compareTo
    public int compareTo(SearchResultEntry entry) { //@NotNull

        return this.equals(entry) ?
                equals(entry) ? 0 : frequency > entry.getFrequency() ? 1 : -1
               :
                distance > entry.getDistance() ? 1 : -1;
    }

    public void setWord(String word) { this.word=word; }
    public void setFrequency(Long frequency) { this.frequency=frequency; }
    public void setDistance(Integer distance){ this.distance=distance; }
    public String getWord(){ return word; }
    public Long getFrequency(){ return frequency; }
    public Integer getDistance(){ return distance; }
}
