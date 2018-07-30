//----------------YEAH! HERE IS THE ENTRY POINT DUDE! $)= ---------------------------

package ru.itu.predictools;

import ru.itu.predictools.index.Index;
import ru.itu.predictools.search.*;

import java.io.File;
import java.util.Scanner;

public class Main {
    private static final String DICTIONARY_PATH = System.getProperty("user.dir") + File.separator + "dictionary.txt";
//    private static final String DICTIONARY_PATH = System.getProperty("user.dir") + "\\dictionary-utf8.txt";

    public static void main(String[] args) throws Exception {
        if (!needHelp(args)) {
            runLoop(args);
        }
    }

    private static boolean needHelp(String[] args) {
        String firstParam = args.length > 0 ? args[0] : "";

        if (firstParam.equals("") || !firstParam.matches("\\d+")) {
            System.out.println("\r\n\r\n  --- Fuzzy search through dictionary utility ---" +
                    "\r\n\r\n  Usage:" +
                    "\r\n\r\n  predict {max misprinted symbols count} {result list length} {ngram N  ||  trie} [prefix]" +
                    "\r\n\r\n  --Help or -h or /? - shows this help" +
                    "\r\n  {max misprint symbols count} - required. Integer from 0 to 3" +
                    "\r\n  {result list length} - required. Length of resulting list" +
                    "\r\n  {ngram N | trie} - required. Name of index type = 'ngram of length N' or 'trie'" +
                    "\r\n  N required only if index type is ngram" +
                    "\r\n  [prefix] - optional. If specified then prefix searching mode is enabled" +
                    "\r\n\r\n  when launched, REPL prompt '>' will appear. Enter the search template and press [Enter]" +
                    "\r\n    the program will output the resulting prediction list with result length specified" +
                    "\r\n    also some commands available in REPL mode:" +
                    "\r\n    > .trie - switch into trie index mode from current" +
                    "\r\n    > .ngram - switch into ngram index mode from current" +
                    "\r\n    > .whole - switch into whole words search mode" +
                    "\r\n    > .prefix - switch into prefix search mode" +
                    "\r\n    > .lXX - (little L) result list length, where XX - integer value of new result list length" +
                    "\r\n    > .nXX - ngram length, where XX - integer value of ngram length" +
                    "\r\n    > .dXX - max mistyping distance, where XX - integer value of new editor's distance" +
                    "\r\n    > .exit - exit from REPL loop to command line. To exit you can also press CTRL-C twice" +
                    "\r\n\r\n  Examples:" +
                    "\r\n\r\n  " +
                    "\r\n  predict 1 10 ngram 2 - runs prediction with 2-gram index, max 1 misspelling allowed, 10 items length resulting list" +
                    "\r\n  predict 2 15 trie - runs prediction with trie index, max 2 misspellings allowed and 15 items list as a result." +
                    "\r\n\r\n  " +
                    "\r\n  > .l30 - set result list length into 30" +
                    "\r\n  > .d0 - set misspelling distance in" +
                    "\r\n  > .n5 - set ngram length into 5" +
                    "\r\n  > .trie - switch into trie index mode" +
                    "\r\n  > .ngram - switch into ngram i" +
                    "\r\n  > .whole - switch into whole words search mode" +
                    "\r\n  > .prefix - switch into words with prefix search mode" +
                    "\r\n  > .exit - exit into shell" +
                    "\r\n\r\n  " +
                    "\r\n "
            );
            return true;
        }
        return false;
    }

    private static void runLoop(String[] args) throws Exception {
        try {
            //run search with parameters specified by args values
            String template;
            Index index;
            Integer distance = Integer.valueOf(args[0]);
            Integer resultListLength = Integer.valueOf(args[1]);
            String typeOfIndex = args[2];
            boolean prefixMode = false;
            Integer ngramLength = 0;
            if (typeOfIndex.matches("ngram")) {
                ngramLength = Integer.valueOf(args[3]);
                if (args.length > 4 && args[4].matches("prefix")) {
                    prefixMode = true;
                }
            }
            else if(typeOfIndex.matches("trie")){
                if (args.length > 3 && args[3].matches("prefix")) {
                    prefixMode = true;
                }
            }
            else{
                throw new ArrayIndexOutOfBoundsException();
            }

            System.out.println("\r\n\r\n --- Hi there! This is the \"Fuzzy search through dictionary\" utility ---\r\n\r\n");
            System.out.println("Search:");
            System.out.println(prefixMode?" - prefix mode enabled":" - prefix mode disabled");
            System.out.println(" - index type: " + typeOfIndex + (typeOfIndex.matches("ngram") ? "-" + ngramLength : ""));
            System.out.println(" - distance: " + distance);
            System.out.println(" - number of displayed result lines: " + resultListLength + "\r\n");
//            Arrays.stream(args).forEach(System.out::println); //debug - show parameters
            if (typeOfIndex.matches("ngram")) {
                index = (Index) new NGramSearch(DICTIONARY_PATH, distance, ngramLength, resultListLength);
            } else {
                index = (Index) new TrieSearch(DICTIONARY_PATH, distance, resultListLength);
            }

            System.out.println("Enter template to search:\r\n\r\n");

            Scanner scanner = new Scanner(System.in);
            Boolean exit = false;

            while (true) {
                try {
                    System.out.print("> ");
                    template = scanner.next();
                    //check if there is a command
                    if (template.substring(0, 1).matches("\\.")) {//this is a command
                        String number = template.substring(2);
                        if (number.matches("\\d+")) {
                            switch (template.substring(1, 2)) {
                                case "n":
                                    if (typeOfIndex.matches("ngram")) {
                                        //<editor-fold desc="Description">
                                        System.out.println("new size for ngram is " + number);
                                        ngramLength = Integer.valueOf(number);
                                        //noinspection ConstantConditions
                                        ((NGramSearch) index).setN(ngramLength);
                                        //</editor-fold>
                                    } else {
                                        System.out.println("This command available only for ngram index mode, but current is " + typeOfIndex);
                                    }
                                    break;
                                case "l":
                                    System.out.println("new length for resulting list is " + number);
                                    resultListLength = Integer.valueOf(number);
//                                    index.setResultLength(resultListLength);
                                    break;
                                case "d":
                                    System.out.println("new search distance is " + number);
                                    distance = Integer.valueOf(number);
//                                    index.setMaxDistance(distance);
                                    break;
                                default:
                                    break;
                            }
                        }
                        else {
                            switch (template.substring(1)) {
                                case "trie":
                                    System.out.println("switching into trie index mode");
//                                    index = new TrieSearch(DICTIONARY_PATH, distance, resultListLength);
                                    break;
                                case "ngram":
                                    System.out.println("switch into ngram index mode");
//                                    index = new NGramSearch(DICTIONARY_PATH, distance, ngramLength, resultListLength);
                                    break;
                                case "prefix":
                                    System.out.println("switch into prefix search mode");
                                    prefixMode = true;
                                    break;
                                case "whole":
                                    System.out.println("switch into whole words search mode");
                                    prefixMode = false;
                                    break;
                                case "exit":
                                    System.out.println("bye bye");
                                    exit = true;
                                    break;
                                default:
                                    break;
                            }
                        }
                        if (exit) return;
                    }
                    else {
                        //if not a command run search
                        System.out.println("result for search template: " + template);
//                        index.run(template, prefixMode);
                    }
                }
                catch (StringIndexOutOfBoundsException e) {
                    System.out.println("wrong or unknown command.");
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("\r\n\r\nError: Invalid parameters... \r\n");
            String[] params = new String[0];
            needHelp(params);
        }
    }

}
