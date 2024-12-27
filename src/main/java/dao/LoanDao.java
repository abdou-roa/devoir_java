//package dao;
//
//import com.library.model.*;
//import java.io.*;
//import java.time.LocalDate;
//import java.util.*;
//import java.nio.file.*;
//public class LoanDao extends BaseCsvDao<Loan> {
//        private static final String[] HEADERS = {
//                "id", "bookId", "userId", "loanDate", "dueDate", "returnDate", "penalty"
//        };
//
//        private final BookDao bookDao;
//        private final UserDao userDao;
//
//        public LoanDao(BookDao bookDao, UserDao userDao) {
//                super("loans.csv");
//                this.bookDao = bookDao;
//                this.userDao = userDao;
//        }
//
//        @Override
//        protected void writeHeader() throws IOException {
//                Files.write(filePath, Collections.singletonList(String.join(",", HEADERS)));
//        }
//
//        @Override
//        protected void loadData() {
//                try {
//                        List<String> lines = Files.readAllLines(filePath);
//                        if (lines.size() > 1) {
//                                lines.subList(1, lines.size()).forEach(line -> {
//                                        String[] parts = line.split(",");
//
//                                        Optional<Book> book = bookDao.findById(parts[1]);
//                                        Optional<User> user = userDao.findById(parts[2]);
//
//                                        if (book.isPresent() && user.isPresent()) {
//                                                Loan loan = new Loan(
//                                                        parts[0], // id
//                                                        book.get(),
//                                                        user.get()
//                                                );
//                                                // Additional loan data would be set here
//                                                cache.put(loan.getId(), loan);
//                                        }
//                                });
//                        }
//                } catch (IOException e) {
//                        throw new RuntimeException("Failed to load loans from CSV", e);
//                }
//        }
//
//        @Override
//        protected String entityToCsv(Loan loan) {
//                return String.format("%s,%s,%s,%s,%s,%s,%.2f",
//                        loan.getId(),
//                        loan.getBook().getId(),
//                        loan.getUser().getId(),
//                        loan.getLoanDate(),
//                        loan.getDueDate(),
//                        loan.getReturnDate() != null ? loan.getReturnDate() : "",
//                        loan.getPenalty()
//                );
//        }
//
//        @Override
//        public void save(Loan loan) {
//                cache.put(loan.getId(), loan);
//                writeToFile();
//        }
//
//        @Override
//        public void delete(String id) {
//                cache.remove(id);
//                writeToFile();
//        }
//}
