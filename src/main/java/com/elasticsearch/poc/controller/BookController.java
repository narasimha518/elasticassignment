package com.elasticsearch.poc.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elasticsearch.poc.constants.ServiceConstants;
import com.elasticsearch.poc.model.Book;
import com.elasticsearch.poc.service.BookService;

/**
 * @author narasimhulu.chakali
 *
 */

@RestController
@RequestMapping(value = ServiceConstants.API_PREFIX + ServiceConstants.BOOK_SERVICE_PATH)
public class BookController {

	@Autowired
	BookService bookService;

	// to insert the book into data base
	@PostMapping(value = ServiceConstants.SAVE_PATH)
	public String saveBook(@RequestBody Book book) throws Exception {
		Optional<String> bookId = bookService.saveBook(book);
		return bookId.isPresent() ? ServiceConstants.SAVE_BOOK_SUCCESS.toString() + bookId.get()
		: ServiceConstants.SAVE_BOOK_FAILURE.toString();
	}

	// to get the details of the book by its id, id is unique, so you can get only a
	// single book
	@GetMapping(value = "/get/{bookId}")
	public Optional<Book> getBookById(@PathVariable("bookId") String bookId) {
		return bookService.getBookById(bookId);
	}

	@GetMapping(ServiceConstants.GET_ALL_BOOKS)
	public List<Map<String, Object>> fetchAllBooks() {
		return bookService.fetchBooks();
	}

	@PutMapping("/update/{id}")
	public Optional<String> updateBookById(@RequestBody Book book, @PathVariable String id) {
		return bookService.updateBook(id, book);
	}

	@DeleteMapping("/delete/{id}")
	public String deleteBookById(@PathVariable String id) {
		Optional<String> deletedId = bookService.deleteBook(id);
		return deletedId.get();

	}

	/*
	 * input for parameters field - books will be searched and displayed based on
	 * the value being passed. values: 'name' and 'author' searchKey - when filed is
	 * name, pass searchKey as any book name when field is author, pass any author
	 * name as searchKey
	 */
	@GetMapping(value = "/search/{field}/{searchKey}")
	public List<Map<String, Object>> searchBooksBySearchKey(@PathVariable String field,
			@PathVariable("searchKey") String searchKey) {
		return bookService.searchBooksByKey(field, searchKey);
	}

	/*
	 * input for parameters field - books will be sorted and displayed based on the
	 * value being passed values: 'bookId', 'bookName' and 'author' order - pass
	 * order to get the data displayed in order. values: asc, desc
	 */
	@GetMapping(value = "/sort/{field}/{order}")
	public List<Map<String, Object>> sortBooksByOrder(@PathVariable String field, @PathVariable String order) {
		return bookService.sortBooksByOrder(field, order);
	}

}