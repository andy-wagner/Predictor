package ru.itu.predictools.utils;

public interface FileSystemInterface {
    public void setFilesPath(String path);
    public void setDictionaryFileName(String filename);
    public void setInputFileName(String filename);
    public String getFilesPath();
    public String getDictionaryFileName();
    public String getInputFileName();
}
