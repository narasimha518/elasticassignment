/**
 * 
 */
package com.elasticsearch.poc.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.elasticsearch.poc.constants.ServiceConstants;
import com.elasticsearch.poc.model.Book;
import com.elasticsearch.poc.repository.BookRepository;
import com.elasticsearch.poc.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.elasticsearch.search.SearchHit;

/**
 * @author narasimhulu.chakali
 *
 */
@Service
public class BookServiceImpl implements BookService {

	@Autowired
	BookRepository bookRepository;
	
	private ObjectMapper objectMapper;
	
	@Inject
	public BookServiceImpl(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public Optional<String> saveBook(Book book) {
		Optional<IndexResponse> response = bookRepository.saveBook(book);

		// After save load the book from database and display
		Function<Optional<IndexResponse>, Optional<String>> f = input -> {
			return (input.isPresent() && (ServiceConstants.Status.CREATED.toString().equals(input.get().getResult().toString())
					|| input.isPresent() && ServiceConstants.Status.UPDATED.toString().equals(input.get().getResult().toString())))
							? Optional.of(input.get().getId())
							: Optional.empty();
		};

		return f.apply(response);
	}

	@Override
	public Optional<Book> getBookById(String id) {
		// TODO Auto-generated method stub
		Optional<Map<String, Object>> outputBook = Optional.empty();
		outputBook = bookRepository.getBookById(id);
		return outputBook.isPresent() ? Optional.of(objectMapper.convertValue(outputBook.get(), Book.class)) 
				               : Optional.empty();
	}

	@Override
	public List<Map<String, Object>> fetchBooks() {
		// TODO Auto-generated method stub
		List<Map<String, Object>> resultList = new ArrayList<>();
		Optional<SearchResponse> searchResults = bookRepository.fetchBooks();
		Consumer<SearchHit> c = input -> resultList.add(input.getSourceAsMap());
		searchResults.get().getHits().iterator().forEachRemaining(c);
		return resultList;
	}

	@Override
	public Optional<String> updateBook(String id, Book book) {
		// TODO Auto-generated method stub
		Optional<Result> updatedId = bookRepository.updateBook(id, book);
		Function<Optional<Result>, Optional<String>> f = input -> {
			return input.isPresent()
					&& ServiceConstants.Status.UPDATED.toString().equalsIgnoreCase(input.get().toString())
							? Optional.of(ServiceConstants.UPDATE_BOOK_SUCCESS)
							: input.isPresent()
									&& ServiceConstants.Status.NOOP.toString().equalsIgnoreCase(input.get().toString())
											? Optional.of(ServiceConstants.UPDATE_BOOK_NOCHANGE)
											: Optional.of(ServiceConstants.UPDATE_BOOK_FAILURE);
		};

		return f.apply(updatedId);

	}

	@Override
	public Optional<String> deleteBook(String bookId) {
		// TODO Auto-generated method stub
		Optional<Result> deletedId = bookRepository.deleteBook(bookId);
		// Process the result and respond with status of deletion
		Function<Optional<Result>, Optional<String>> f = input -> {
			return input.isPresent() && ServiceConstants.Status.DELETED.toString().equals(input.get().toString())
					? Optional.of(ServiceConstants.DELETE_BOOK_SUCCESS)
					: Optional.of(ServiceConstants.DELETE_BOOK_FAILURE);
		};
		return f.apply(deletedId);
	}

	@Override
	public List<Map<String, Object>> searchBooksByKey(String field, String key) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> searchResults = new ArrayList<>();
		Optional<SearchResponse> response = bookRepository.searchBooksByKey(field, key);
		Consumer<SearchHit> c = input -> searchResults.add(input.getSourceAsMap());
		response.get().getHits().iterator().forEachRemaining(c);
		return searchResults.stream().filter(input -> input.containsValue(key)).collect(Collectors.toList());
	}

	@Override
	public List<Map<String, Object>> sortBooksByOrder(String field, String order) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> sortResults = new ArrayList<>();
		Optional<SearchResponse> response = bookRepository.sortBooksByOrder(field, order);
		Consumer<SearchHit> c = input -> sortResults.add(input.getSourceAsMap());
		response.get().getHits().iterator().forEachRemaining(c);
		return sortResults;

	}
	
	@Override
	public List<Map<String, Object>> searchBooksByLikeValue(String[] value) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> searchResults = new ArrayList<>();
		Optional<SearchResponse> response = bookRepository.searchBooksByLikeValue(value);
		Consumer<SearchHit> c = input -> searchResults.add(input.getSourceAsMap());
		response.get().getHits().iterator().forEachRemaining(c);
		return searchResults;
	}

}
