package ru.itu.predictools.console;

import ru.itu.predictools.alphabet.Alphabet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.log;

public class Keyboard {

    private Alphabet alphabet;
    private List<String> delimiters;//TODO continue to develop keyboard functionality & classes - add delimiter, numeric keys etc.
    private Key[] keys;
    private Key[] charKeyMap;
    private int numberOfDimensions;
    private int dimensionSize;
    private int charsPerKey;

    public Keyboard(Alphabet alphabet) { //линейная клавиатура - количество клавиш равно количеству букв алфавита
        this(alphabet, 1, alphabet.size()+1, 1);
    } //
    public Keyboard(Alphabet alphabet, int numberOfKeys) { //automatic keyboard - alphabet and number of keys assigned
        this(alphabet, (int)log(alphabet.size()), numberOfKeys, alphabet.size() / numberOfKeys+1);
    }
    public Keyboard(Alphabet alphabet, int numberOfDimensions, int numberOfKeys, int charsPerKey) { //soft keyboard,
                                                                                    // manual set of all parameters
        //numberOfKeys++;//temp operation +1 key for space/delimiter
        //TODO continue to develop keyboard functionality & classes - add delimiter, numeric keys etc.
        this.alphabet = alphabet;
        this.delimiters  = new ArrayList<String>(Arrays.asList(new String[] {" "/*, ",", ".", ",", ";"*/}));//TODO continue to develop keyboard functionality & classes - add delimiter, numeric keys etc.

        this.numberOfDimensions = numberOfDimensions;
        this.charsPerKey = charsPerKey;
        Integer[] keysCoordinates = new Integer[this.numberOfDimensions];
        if( alphabet.size() / charsPerKey >= numberOfKeys /*+ special keys count*/) {
            System.out.println("Keyboard hasn't enough keys");
            return;
        } //TODO define custom Keyboard exceptions
        keys = new Key[numberOfKeys];
        charKeyMap = new Key[alphabet.size()+1];//TODO +1 is temp element for temporary delimiter, needs to develop adding delimiters etc. (see TODO continue to develop keyboard functionality & classes - add delimiter, numeric keys etc.)
        int keyIndex=0;

        for(int i=0; i < numberOfKeys*charsPerKey; i+=charsPerKey){
            if (i > (alphabet.size() - 1)) break;
            String keyString="";
            keyIndex=i/charsPerKey;
            keys[keyIndex] = new Key(keyString, keysCoordinates);
//            char[] nextKeyChars = Arrays.copyOfRange(alphabet.getChars(), i, i + charsPerKey);
            for(int j=0; j < charsPerKey; j++){
                if(i+j+1 <= alphabet.size()) {
//                    keyString +=nextKeyChars[j];
//                    charKeyMap[alphabet.mapChar(nextKeyChars[j])]=keys[keyIndex];
                }
            }
            keys[keyIndex].setString(keyString);
        }
        keys[keyIndex+1] = new Key(" ", keysCoordinates);//temp delimiter //TODO adding delimiter, numeric keys etc. (see TODO continue to develop keyboard functionality & classes - add delimiter, numeric keys etc.)
        charKeyMap[keys[keyIndex+1].getKeyContent().charAt(0)]=keys[keyIndex+1];


        this.dimensionSize = (int) (Math.pow((double)numberOfKeys,1/(double)numberOfDimensions));
    }

    public Alphabet getAlphabet(){ return  alphabet; }

    public Key[] getKeys(){ return keys; }
    public Key[] getCharKeyMap(){ return charKeyMap; }

    public Integer[] getFreeKeysIndexes() {//to allocate predictive for example
        List<Integer> result = new ArrayList<>();
        for (int i=0; i<keys.length; i++)
            if (keys[i] == null) result.add(i);

        return result.toArray(new Integer[result.size()]);
    }

//    public void addKey()
    public void setKey(int index, String string, Integer[] coordinates){
        keys[index].setString(string);
        //keys[index].setCoordinates(coordinates);
    }

    public Key getKey(int index){ return keys[index]; }
    public Key getKey(char ch){ return charKeyMap[alphabet.mapChar(ch)]; }//TODO The char is not from the alphabet & upper/lower case error

    public int optimalDimensionsCount(){ return (int) log(alphabet.size()); }
    public int getNumberOfDimensions(){ return numberOfDimensions; }
    public int getDimensionSize(){ return dimensionSize; }
    public int getCharsPerKey() {return charsPerKey; }

}
