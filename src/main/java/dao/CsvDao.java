//package dao;
//
//import com.library.model.*;
//import java.io.*;
//import java.time.LocalDate;
//import java.util.*;
//import java.nio.file.*;
//// Base DAO interface
//public interface CsvDao<T> {
//    void save(T entity);
//    void saveAll(List<T> entities);
//    Optional<T> findById(String id);
//    List<T> findAll();
//    void delete(String id);
//    boolean exists(String id);
//}