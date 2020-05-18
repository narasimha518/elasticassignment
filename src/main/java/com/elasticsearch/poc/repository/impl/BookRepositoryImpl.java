/**
 * 
 */
package com.elasticsearch.poc.repository.impl;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder.Item;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Repository;
import com.elasticsearch.poc.constants.ServiceConstants;
import com.elasticsearch.poc.model.Book;
import com.elasticsearch.poc.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author narasimhulu.chakali
 *
 */
@Repository
public class BookRepositoryImpl implements BookRepository {

	private RestHighLevelClient restHighLevelClient;
	private ObjectMapper objectMapper;

	@Inject
	public BookRepositoryImpl(ObjectMapper objectMapper, RestHighLevelClient restHighLevelClient) {
		this.objectMapper = objectMapper;
		this.restHighLevelClient = restHighLevelClient;
	}

	@Override
	public Optional<IndexResponse> saveBook(Book book) {
		Map<String, Object> dataMap = objectMapper.convertValue(book, Map.class);
		IndexRequest indexRequest = new IndexRequest(ServiceConstants.BOOK_INDEX)
				.id(book.getBookId().toString())
				.source(dataMap);
		Optional<IndexResponse> indexResponse = Optional.empty();

		try {
			indexResponse = Optional.of(restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT));
			//System.out.println("Book saved successfully with id: " + indexResponse.getId());
			//resultId = Optional.of(indexResponse.getResult());
		} catch (ElasticsearchException e) {
			e.getDetailedMessage();
			return indexResponse;
		} catch (java.io.IOException ex) {
			ex.getLocalizedMessage();
			return indexResponse;
		} catch (Exception ex) {
			ex.getLocalizedMessage();
			return indexResponse;
		}

		return indexResponse;
	}

	@Override
	public Optional<Map<String, Object>> getBookById(String id) {
		// TODO Auto-generated method stub
		Optional<Map<String, Object>> returnBook = Optional.empty();
		GetRequest getRequest = new GetRequest(ServiceConstants.BOOK_INDEX, id);
		try {
			GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
			returnBook = Optional.of(getResponse.getSource());
		} catch (ElasticsearchException e) {
			e.getDetailedMessage();
			return returnBook;
		} catch (java.io.IOException ex) {
			ex.getLocalizedMessage();
			return returnBook;
		} catch (Exception ex) {
			ex.printStackTrace();
			ex.getLocalizedMessage();
			return returnBook;
		}
		return returnBook;
	}

	@Override
	public Optional<SearchResponse> fetchBooks() {
		// TODO Auto-generated method stub
		Optional<SearchResponse> searchResult = Optional.empty();
		try {
			SearchRequest searchRequest = new SearchRequest(ServiceConstants.BOOK_INDEX);
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder.query(QueryBuilders.matchAllQuery());
			searchRequest.source(searchSourceBuilder);
			SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
			searchResult = Optional.of(searchResponse);
		} catch (ElasticsearchException e) {
			e.getDetailedMessage();
			return searchResult;
		} catch (java.io.IOException ex) {
			ex.getLocalizedMessage();
			return searchResult;
		} catch (Exception ex) {
			ex.getLocalizedMessage();
			return searchResult;
		}
		return searchResult;
	}

	@Override
	public Optional<Result> updateBook(String id, Book book) {
		// TODO Auto-generated method stub
		Optional<Result> resultId = Optional.empty();
		try {
			Map<String, Object> jsonMap = objectMapper.convertValue(book, Map.class);
			UpdateRequest request = new UpdateRequest(ServiceConstants.BOOK_INDEX, id).doc(jsonMap);
			UpdateResponse updateResponse = restHighLevelClient.update(request, RequestOptions.DEFAULT);
			System.out.println("Document with id " + id + " updated successfully");
			resultId = Optional.of(updateResponse.getResult());
		} catch (ElasticsearchException e) {
			e.printStackTrace();
			e.getDetailedMessage();
			return resultId;
		} catch (java.io.IOException ex) {
			ex.getLocalizedMessage();
			return resultId;
		} catch (Exception ex) {
			ex.getLocalizedMessage();
			return resultId;
		}
		return resultId;
	}

	@Override
	public Optional<Result> deleteBook(String bookId) {
		// TODO Auto-generated method stub
		Optional<Result> deletedId = Optional.empty();
		DeleteRequest request = new DeleteRequest(ServiceConstants.BOOK_INDEX, bookId);
		try {
			DeleteResponse deleteResponse = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
			System.out.println(deleteResponse.getResult());
			deletedId = Optional.of(deleteResponse.getResult());
		} catch (ElasticsearchException e) {
			e.getDetailedMessage();
			return deletedId;
		} catch (java.io.IOException ex) {
			ex.getLocalizedMessage();
			return deletedId;
		} catch (Exception ex) {
			ex.getLocalizedMessage();
			return deletedId;
		}
		return deletedId;

	}

	@Override
	public Optional<SearchResponse> searchBooksByKey(String field, String key) {
		// TODO Auto-generated method stub
		Optional<SearchResponse> searchResults = Optional.empty();
		SearchRequest searchRequest = new SearchRequest(ServiceConstants.BOOK_INDEX);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		Function<String, SearchSourceBuilder> f = input -> {
			return input.equalsIgnoreCase("name") ? searchSourceBuilder.query(QueryBuilders.matchQuery("bookName", key))
					: input.equalsIgnoreCase("author")
							? searchSourceBuilder.query(QueryBuilders.matchQuery("author", key))
							: searchSourceBuilder;
		};
		searchRequest.source(f.apply(field));

		try {
			SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
			searchResults = Optional.of(searchResponse);

		} catch (ElasticsearchException e) {
			e.getDetailedMessage();
			return searchResults;
		} catch (java.io.IOException ex) {
			ex.getLocalizedMessage();
			return searchResults;
		} catch (Exception ex) {
			ex.getLocalizedMessage();
			return searchResults;
		}
		return searchResults;
	}

	@Override
	public Optional<SearchResponse> sortBooksByOrder(String field, String order) {
		// TODO Auto-generated method stub
		Optional<SearchResponse> searchResult = Optional.empty();
		try {
			SearchRequest searchRequest = new SearchRequest(ServiceConstants.BOOK_INDEX);
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder.query(QueryBuilders.matchAllQuery());
			Function<String, SearchSourceBuilder> f = input -> input.equalsIgnoreCase("ASC")
					? searchSourceBuilder.sort(new FieldSortBuilder(field).order(SortOrder.ASC))
					: input.equalsIgnoreCase("DESC")
							? searchSourceBuilder.sort(new FieldSortBuilder(field).order(SortOrder.DESC))
							: searchSourceBuilder;
			searchRequest.source(f.apply(order));
			SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
			searchResult = Optional.of(searchResponse);
		} catch (ElasticsearchException e) {
			e.printStackTrace();
			e.getDetailedMessage();
			return searchResult;
		} catch (java.io.IOException ex) {
			ex.getLocalizedMessage();
			return searchResult;
		} catch (Exception ex) {
			ex.getLocalizedMessage();
			return searchResult;
		}
		return searchResult;

	}
	
	
	@Override
	public Optional<SearchResponse> searchBooksByLikeValue(String[] texts) {
		// TODO Auto-generated method stub
		Optional<SearchResponse> searchResults = Optional.empty();
		SearchRequest searchRequest = new SearchRequest();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		
		String[] fields = {"bookName", "author"};               
		//String[] texts = {"text like this one"};
		
		searchRequest.source(searchSourceBuilder.query(QueryBuilders.moreLikeThisQuery(fields, texts, null)
			    .minTermFreq(1) 
			    .minDocFreq(1)
			    .maxQueryTerms(12)));
		try {
			SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
			searchResults = Optional.of(searchResponse);

		} catch (ElasticsearchException e) {
			e.getDetailedMessage();
			e.printStackTrace();
			return searchResults;
		} catch (java.io.IOException ex) {
			ex.getLocalizedMessage();
			ex.printStackTrace();
			return searchResults;
		} catch (Exception ex) {
			ex.getLocalizedMessage();
			return searchResults;
		}
		return searchResults;
	}

}
