/**
 * 
 */
package com.elasticsearch.poc.repository;

import java.util.Map;
import java.util.Optional;

import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder.Item;
import org.springframework.stereotype.Repository;

import com.elasticsearch.poc.model.Book;

/**
 * @author narasimhulu.chakali
 *
 */
@Repository
public interface BookRepository {

	public Optional<IndexResponse> saveBook(Book book);

	public Optional<Map<String, Object>> getBookById(String id);

	public Optional<SearchResponse> fetchBooks();

	public Optional<Result> updateBook(String id, Book book);

	public Optional<Result> deleteBook(String bookId);

	public Optional<SearchResponse> searchBooksByKey(String field, String key);

	public Optional<SearchResponse> sortBooksByOrder(String field, String order);
	
	public Optional<SearchResponse> searchBooksByLikeValue(String[] value);
}
