package ru.itu.predictools.console;

public class Key {
    private String string;
    private Integer[] coordinates;

    public Key(String string) {
        this(string, new Integer[0]/*TODO spacial allocation system for keyboard keys */);
    }
    public Key(String string, Integer[] coordinates){
        this.string = string;
        this.coordinates = coordinates;
    }

    public void setString(String string){ this.string = string; }
    public String getKeyContent(){ return this.string; }
    public int getNumberOfChars(){ return this.string.length(); }
    public Integer[] getCoordinates(){ return this.coordinates; }

}
