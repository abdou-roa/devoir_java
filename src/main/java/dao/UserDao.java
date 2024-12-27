//package dao;
//
//import com.library.model.*;
//import java.io.*;
//import java.time.LocalDate;
//import java.util.*;
//import java.nio.file.*;
//public class UserDao extends BaseCsvDao<User> {
//    private static final String[] HEADERS = {"id", "username", "password", "role"};
//
//    public UserDao() {
//        super("users.csv");
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
//                    User user = new User(
//                            parts[0], // id
//                            parts[1], // username
//                            parts[2], // password
//                            User.UserRole.valueOf(parts[3]) // role
//                    );
//                    cache.put(user.getId(), user);
//                });
//            }
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to load users from CSV", e);
//        }
//    }
//
//    @Override
//    protected String entityToCsv(User user) {
//        return String.format("%s,%s,%s,%s",
//                user.getId(),
//                user.getUsername().replace(",", ";"),
//                user.getPassword() != null ? user.getPassword().replace(",", ";") : "",
//                user.getRole()
//        );
//    }
//
//    @Override
//    public void save(User user) {
//        cache.put(user.getId(), user);
//        writeToFile();
//    }
//
//    @Override
//    public void delete(String id) {
//        cache.remove(id);
//        writeToFile();
//    }
//}
//
