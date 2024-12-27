//package dao;
//
//public class CsvDatabase {
//    private final BookDao bookDao;
//    private final UserDao userDao;
//    private final LoanDao loanDao;
//
//    public CsvDatabase() {
//        this.bookDao = new BookDao();
//        this.userDao = new UserDao();
//        this.loanDao = new LoanDao(bookDao, userDao);
//    }
//
//    public BookDao getBookDao() {
//        return bookDao;
//    }
//
//    public UserDao getUserDao() {
//        return userDao;
//    }
//
//    public LoanDao getLoanDao() {
//        return loanDao;
//    }
//}