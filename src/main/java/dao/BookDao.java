//package dao;
//
//import com.library.model.*;
//import java.io.*;
//import java.time.LocalDate;
//import java.util.*;
//import java.nio.file.*;
//public class BookDao extends BaseCsvDao<Book> {
//    private static final String[] HEADERS = {"id", "title", "author", "publicationYear", "genre", "quantity"};
//
//    public BookDao() {
//        super("books.csv");
//    }
//
//    @Override
//    protected void writeHeader() throws IOException {
//        Files.write(filePath, Collections.singletonList(String.join(",", HEADERS)));
//    }
//
//    @Override
//    protected void loadData() {
//        try {
//            List<String> lines = Files.readAllLines(filePath);
//            if (lines.size() > 1) {
//                lines.subList(1, lines.size()).forEach(line -> {
//                    String[] parts = line.split(",");
//                    Book book = new Book(
//                            parts[0], // id
//                            parts[1], // title
//                            parts[2], // author
//                            Integer.parseInt(parts[3]), // publicationYear
//                            parts[4], // genre
//                            Integer.parseInt(parts[5]) // quantity
//                    );
//                    cache.put(book.getId(), book);
//                });
//            }
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to load books from CSV", e);
//        }
//    }
//
//    @Override
//    protected String entityToCsv(Book book) {
//        return String.format("%s,%s,%s,%d,%s,%d",
//                book.getId(),
//                book.getTitle().replace(",", ";"),
//                book.getAuthor().replace(",", ";"),
//                book.getPublicationYear(),
//                book.getGenre().replace(",", ";"),
//                book.getQuantity()
//        );
//    }
//
//    @Override
//    public void save(Book book) {
//        cache.put(book.getId(), book);
//        writeToFile();
//    }
//
//    @Override
//    public void delete(String id) {
//        cache.remove(id);
//        writeToFile();
//    }
//}