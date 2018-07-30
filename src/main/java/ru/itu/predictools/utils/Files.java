package ru.itu.predictools.utils;

import ru.itu.predictools.index.Index;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для работы с файлами системы предиктивного ввода с исправлением ошибок
 */
public class Files implements FileSystemInterface {
    private static final String DEFAULT_PATH = System.getProperty("user.dir") + "\\";
    private static final String DEFAULT_DICTIONARY_FILE = "getDictionary.txt";
    private static final String DEFAULT_INDEX_FILE = "index.txt";
    private static final String DEFAULT_INPUT_FILE = "input.txt";



    private static final String PATH = System.getProperty("user.dir") + "\\";
    private static final String INPUT_FILE = "input.txt";

    private String path;
    private String dictionaryFileName;
    private String indexFileName;
    private String inputFileName;
    private String[] dictionary;
    private Index[] index;
    private String[] input;

    public Files() throws IOException {
        this(DEFAULT_PATH, DEFAULT_DICTIONARY_FILE, DEFAULT_INDEX_FILE, DEFAULT_INPUT_FILE);
    }
    /**
     * Подготовка к работе - инициализация переменных, загрузка файла словаря
     * @param path - рабочая папка - место хранения файлов словаря, индекса, текса для ввода (источник текста для эмулятора ввода)
     * @param dictionaryFileName - имя файла словаря
     * @param indexFileName - имя файла индекса
     * @param inputFileName - имя файла вводимого текста
     */
    public Files(String path, String dictionaryFileName, String indexFileName, String inputFileName) throws IOException{
        this.path=path;
        this.dictionaryFileName = path + dictionaryFileName;
        this.inputFileName = path + inputFileName;
        this.indexFileName = path + indexFileName;

        List<String> lines = new ArrayList<>();
        String line;

        BufferedReader reader = new BufferedReader(new FileReader(dictionaryFileName));
        while ((line = reader.readLine()) != null)
            lines.add(line);

        reader.close();
        this.dictionary = lines.toArray(new String[lines.size()]);
    }
    public String[] getDictionary(){ return this.dictionary; }

    public void saveObject(Object object, String filename) throws Exception {
        FileOutputStream fileOutputStream = new FileOutputStream(filename);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

        objectOutputStream.writeObject(object);

        objectOutputStream.close();
        fileOutputStream.close();
    }

    public Object loadObject(String filename) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(filename);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Object object = objectInputStream.readObject();

        objectInputStream.close();
        fileInputStream.close();

        return object;
    }

    public void getCharByChar() throws InterruptedException, IOException {
        FileInputStream fin = new FileInputStream(inputFileName);
        InputStreamReader file_reader = new InputStreamReader(fin,"UTF8");

        System.out.println("Размер файла: " + fin.available() + " байт(а)");

        int i;
        while ((i = file_reader.read()) != -1) {
            Thread.sleep(100);
            System.out.print((char) i);
        }
    }

    @Override
    public void setFilesPath(String path){this.path = path; }
    @Override
    public void setDictionaryFileName(String filename){ this.dictionaryFileName=filename; }
    @Override
    public void setInputFileName(String filename){ this.inputFileName=filename; }
    @Override
    public String getFilesPath(){ return path; }
    @Override
    public String getDictionaryFileName(){ return dictionaryFileName; }
    @Override
    public String getInputFileName() { return inputFileName; }

}
