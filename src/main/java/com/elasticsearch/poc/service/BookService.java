/**
 * 
 */
package com.elasticsearch.poc.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.elasticsearch.poc.model.Book;

/**
 * @author narasimhulu.chakali
 *
 */
public interface BookService {

	public Optional<String> saveBook(Book book);

	public Optional<Book> getBookById(String id);

	public Optional<String> updateBook(String id, Book book);

	public Optional<String> deleteBook(String bookId);

	public List<Map<String, Object>> fetchBooks();

	public List<Map<String, Object>> searchBooksByKey(String field, String key);

	public List<Map<String, Object>> sortBooksByOrder(String field, String order);

}
