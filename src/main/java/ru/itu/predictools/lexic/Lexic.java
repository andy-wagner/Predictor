package ru.itu.predictools.lexic;

import ru.itu.predictools.metric.Metric;

/**
 * Interface for working with lexical units such as words, phrases, texts
 * @param <E>
 * @param <L>
 */
public interface Lexic<E, L> {

    public String getSymbolicLexeme();
    public String getPhoneticLexeme();
    public String getTelegramLexeme();
    public String getCloseLexeme(Metric metric, int distance);
    public Double getFrequency();
    public Integer getR();
    public Integer getD();


}
