package controller;


import com.library.model.Book;
import view.BookView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class BookController {
    private BookView view;
    private List<Book> books;

    public BookController(BookView view) {
        this.view = view;
        this.books = new ArrayList<>();
    }

    public void addBook(String title, String author, int year, String genre) {
        Book book = new Book(generateId(), title, author, year, genre);
        books.add(book);
        view.refreshBookList(books);
    }

    public void updateBook(int id, String title, String author, int year, String genre) {
        books.stream()
                .filter(b -> b.getId() == id)
                .findFirst()
                .ifPresent(book -> {
                    book.setTitle(title);
                    book.setAuthor(author);
                    book.setPublicationYear(year);
                    book.setGenre(genre);
                });
        view.refreshBookList(books);
    }

    public void deleteBook(int id) {
        books.removeIf(b -> b.getId() == id);
        view.refreshBookList(books);
    }

    public void searchBooks(String query) {
        List<Book> results = books.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        b.getAuthor().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        view.displaySearchResults(results);
    }

    private int generateId() {
        return books.size() + 1;
    }
}
