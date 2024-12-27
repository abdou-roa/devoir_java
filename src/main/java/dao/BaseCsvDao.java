//package dao;
//
//import com.library.model.*;
//import java.io.*;
//import java.time.LocalDate;
//import java.util.*;
//import java.nio.file.*;
//
//public abstract class BaseCsvDao<T> implements CsvDao<T> {
//    protected final Path filePath;
//    protected final Map<String, T> cache;
//
//    protected BaseCsvDao(String fileName) {
//        this.filePath = Paths.get(fileName);
//        this.cache = new HashMap<>();
//        initializeFile();
//        loadData();
//    }
//
//    protected void initializeFile() {
//        try {
//            if (!Files.exists(filePath)) {
//                Files.createFile(filePath);
//                writeHeader();
//            }
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to initialize CSV file: " + filePath, e);
//        }
//    }
//
//    protected abstract void writeHeader() throws IOException;
//    protected abstract void loadData();
//    protected abstract String entityToCsv(T entity);
//
//    @Override
//    public void saveAll(List<T> entities) {
//        entities.forEach(this::save);
//        writeToFile();
//    }
//
//    protected void writeToFile() {
//        try {
//            List<String> lines = new ArrayList<>();
//            writeHeader();
//            cache.values().forEach(entity -> lines.add(entityToCsv(entity)));
//            Files.write(filePath, lines);
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to write to CSV file: " + filePath, e);
//        }
//    }
//
//    @Override
//    public Optional<T> findById(String id) {
//        return Optional.ofNullable(cache.get(id));
//    }
//
//    @Override
//    public List<T> findAll() {
//        return new ArrayList<>(cache.values());
//    }
//
//    @Override
//    public boolean exists(String id) {
//        return cache.containsKey(id);
//    }
//}