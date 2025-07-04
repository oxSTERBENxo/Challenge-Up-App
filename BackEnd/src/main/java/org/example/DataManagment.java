package org.example;

import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

public class DataManagment {
    private static final Gson gson = new Gson();

    public static <T> void saveList(List<T> list, String filePath) throws IOException {
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(list, writer);
        }
    }

    public static <T> List<T> loadList(String filePath, Type typeOfT) throws IOException {
        try (Reader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, typeOfT);
        }
    }
    public static <T> void saveObject(T object, String filePath) throws IOException {
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(object, writer);
        }
    }
    public static <T> T loadObject(String filePath, Class<T> classOfT) throws IOException {
        try (Reader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, classOfT);
        }
    }
}
