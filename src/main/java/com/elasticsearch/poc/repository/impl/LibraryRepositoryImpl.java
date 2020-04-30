package com.elasticsearch.poc.repository.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.lucene.search.join.ScoreMode;
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
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Repository;

import com.elasticsearch.poc.constants.ServiceConstants;
import com.elasticsearch.poc.model.Library;
import com.elasticsearch.poc.repository.BookRepository;
import com.elasticsearch.poc.repository.LibraryRepository;
import com.elasticsearch.poc.service.BookService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.elasticsearch.poc.model.Address;
import com.elasticsearch.poc.model.Book;

@Repository
public class LibraryRepositoryImpl implements LibraryRepository {

	private RestHighLevelClient client;
	private ObjectMapper objectMapper;

	@Inject
	private BookService bookService;

	@Inject
	public LibraryRepositoryImpl(ObjectMapper objectMapper, RestHighLevelClient client) {
		this.objectMapper = objectMapper;
		this.client = client;
	}

	@Override
	public Optional<IndexResponse> saveAddress(Address address) {
		// TODO Auto-generated method stub
		Optional<IndexResponse> response = Optional.empty();
		Map<String, Object> dataMap = objectMapper.convertValue(address, Map.class);
		IndexRequest indexRequest = new IndexRequest(ServiceConstants.ADDRESS_INDEX)
				.id(address.getAddressId().toString()).source(dataMap);

		try {
			response = Optional.of(client.index(indexRequest, RequestOptions.DEFAULT));
			System.out.println("Address saved successfully with id: " + response.get().getId());
			// resultId = Optional.of(indexResponse.getResult());

		} catch (ElasticsearchException e) {
			e.getDetailedMessage();
			return response;
		} catch (java.io.IOException ex) {
			ex.getLocalizedMessage();
			return response;
		} catch (Exception ex) {
			ex.getLocalizedMessage();
			return response;
		}

		return response;
	}

	@Override
	public Optional<IndexResponse> createLibrary(Library library, String addressId) {
		// TODO Auto-generated method stub

		Optional<IndexResponse> indexResponse = Optional.empty();
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("libraryId", library.getLibraryId());
		dataMap.put("libraryName", library.getLibraryName());
		dataMap.put("address", addressId);
		IndexRequest request = new IndexRequest(ServiceConstants.LIBRARY_INDEX).id(library.getLibraryId().toString())
				.source(dataMap);

		try {
			if (addressId.isEmpty() || addressId.isBlank()) {
				throw new ElasticsearchException("Address details should not be empty");
			}
			indexResponse = Optional.of(client.index(request, RequestOptions.DEFAULT));
			System.out.println("Library saved successfully with id: " + indexResponse.get().getId());
			// resultId = Optional.of(indexResponse.getResult());
		} catch (ElasticsearchException e) {
			e.printStackTrace();
			System.out.println(e.getDetailedMessage());
			return indexResponse;
		} catch (java.io.IOException ex) {
			ex.printStackTrace();
			ex.getLocalizedMessage();
			return indexResponse;
		} catch (Exception ex) {
			ex.printStackTrace();
			ex.getLocalizedMessage();
			return indexResponse;
		}

		return indexResponse;

	}

	@Override
	public Optional<String> getAddressId(Integer libraryId) {
		Optional<String> addressId = Optional.empty();
		GetRequest getRequest = new GetRequest(ServiceConstants.LIBRARY_INDEX, libraryId.toString());
		// getRequest.storedFields("address");
		String[] includes = new String[] { "address" };
		String[] excludes = Strings.EMPTY_ARRAY;
		FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includes, excludes);
		getRequest.fetchSourceContext(fetchSourceContext);
		try {
			GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
			System.out.println(getResponse.getSourceAsMap().values());
			Collection<Object> address = getResponse.getSourceAsMap().values();
			addressId = Optional.of(address.toString());
		} catch (ElasticsearchException e) {
			e.getDetailedMessage();
			return addressId;
		} catch (java.io.IOException ex) {
			ex.getLocalizedMessage();
			return addressId;
		} catch (Exception ex) {
			ex.printStackTrace();
			ex.getLocalizedMessage();
			return addressId;
		}
		return addressId;
	}

	@Override
	public Optional<IndexResponse> addBook(Integer libraryId, String addressId, String bookId) {
		Optional<IndexResponse> response = Optional.empty();
		try {
			Map<String, Object> dataMap = new HashMap<>();
			dataMap.put("libraryId", libraryId);
			dataMap.put("addressId", addressId);
			dataMap.put("bookId", bookId);
			if (bookId == null) {
				throw new ElasticsearchException("Book details should not be empty");
			}
			IndexRequest request = new IndexRequest(ServiceConstants.LIBRARY_BOOK_JOIN_INDEX).id(bookId)
					.source(dataMap);
			response = Optional.of(client.index(request, RequestOptions.DEFAULT));
			System.out.println("Book saved successfully with id: " + response.get().getId() + "into the Library");
		} catch (ElasticsearchException e) {
			e.getDetailedMessage();
			return response;
		} catch (java.io.IOException ex) {
			ex.getLocalizedMessage();
			return response;
		} catch (Exception ex) {
			ex.getLocalizedMessage();
			return response;
		}

		return response;

	}

	@Override
	public Optional<Address> loadTheAddressDetails(String addressId) {
		Optional<Address> address = Optional.empty();
		GetRequest getRequest = new GetRequest(ServiceConstants.ADDRESS_INDEX, addressId);
		try {
			GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
			address = Optional.of(objectMapper.convertValue(getResponse.getSourceAsMap(), Address.class));
		} catch (ElasticsearchException e) {
			e.getDetailedMessage();
			return address;
		} catch (java.io.IOException ex) {
			ex.printStackTrace();
			ex.getLocalizedMessage();
			return address;
		} catch (Exception ex) {
			ex.printStackTrace();
			ex.getLocalizedMessage();
			return address;
		}
		return address;
	}

	@Override
	public Map<String, Object> fetchLibrary(Integer libraryId) {
		// TODO Auto-generated method stub
		Map<String, Object> libraryMap = new HashMap<>();
		try {
			GetRequest getRequest = new GetRequest(ServiceConstants.LIBRARY_INDEX, libraryId.toString());
			GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
			libraryMap = getResponse.getSourceAsMap();
		} catch (ElasticsearchException e) {
			e.getDetailedMessage();
		} catch (java.io.IOException ex) {
			ex.printStackTrace();
			ex.getLocalizedMessage();
		} catch (Exception ex) {
			ex.printStackTrace();
			ex.getLocalizedMessage();
		}
		return libraryMap;
	}

	public Optional<GetResponse> loadLibraryAddress(String addressId) {

		return null;
	}

	@Override
	public Optional<SearchResponse> loadLibraryBooks(Integer libraryId) {

		SearchRequest searchRequest = new SearchRequest(ServiceConstants.LIBRARY_BOOK_JOIN_INDEX);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchQuery("libraryId", libraryId));
		searchRequest.source(searchSourceBuilder);
		Optional<SearchResponse> searchResponse = Optional.empty();
		try {
			searchResponse = Optional.of(client.search(searchRequest, RequestOptions.DEFAULT));
		} catch (ElasticsearchException e) {
			e.getDetailedMessage();
			// return addressId;
		} catch (java.io.IOException ex) {
			ex.printStackTrace();
			ex.getLocalizedMessage();
			// return addressId;
		} catch (Exception ex) {
			ex.printStackTrace();
			ex.getLocalizedMessage();
			// return addressId;
		}

		System.out.println(searchResponse);
		return searchResponse;
	}

	public Optional<BulkByScrollResponse> deleteLibrary(Integer libraryId) {
		Optional<BulkByScrollResponse> bulkResponse = Optional.empty();
		DeleteByQueryRequest request = new DeleteByQueryRequest(ServiceConstants.LIBRARY_INDEX,
				ServiceConstants.LIBRARY_BOOK_JOIN_INDEX);
		request.setQuery(new TermQueryBuilder("libraryId", libraryId.toString()));
		try {
			bulkResponse = Optional.of(client.deleteByQuery(request, RequestOptions.DEFAULT));
			System.out.println(bulkResponse.get().getTotal());
			System.out.println(bulkResponse.get().getDeleted());
		} catch (ElasticsearchException e) {
			e.getDetailedMessage();
			return bulkResponse;
		} catch (java.io.IOException ex) {
			ex.getLocalizedMessage();
			return bulkResponse;
		} catch (Exception ex) {
			ex.getLocalizedMessage();
			return bulkResponse;
		}
		return bulkResponse;
	}

	public List<String> getLibraryIdUsingFieldAndVlue(String field, String value) {

		List<String> idsList = new ArrayList<>();
		SearchRequest searchRequest = new SearchRequest(ServiceConstants.LIBRARY_INDEX);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		Function<String, SearchSourceBuilder> f = input -> {
			return input.equalsIgnoreCase("libraryName")
					? searchSourceBuilder.query(QueryBuilders.matchQuery("libraryName", value))
					: input.equalsIgnoreCase("address")
							? searchSourceBuilder.query(QueryBuilders.matchQuery("address", value))
							: searchSourceBuilder;
		};
		searchRequest.source(f.apply(field));
		try {
			SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
			System.out.println(searchResponse);
			Consumer<SearchHit> c = input -> idsList.add(input.getId());
			searchResponse.getHits().iterator().forEachRemaining(c);
			return idsList;
		} catch (ElasticsearchException e) {
			e.getDetailedMessage();

		} catch (java.io.IOException ex) {
			ex.getLocalizedMessage();

		} catch (Exception ex) {
			ex.getLocalizedMessage();

		}
		return null;
	}

	@Override
	public List<String> getLibraryId(String field, String value) {
		BiFunction<String, String, List<String>> biFunction = (f, v) -> {
			List<String> idsList = new ArrayList<>();
			if (f.equalsIgnoreCase("libraryId")) {
				idsList.add(v);
				return idsList;
			} else
				return getLibraryIdUsingFieldAndVlue(f, v);
		};

		return biFunction.apply(field, value);
	}
	
	@Override
	public Optional<SearchResponse> sortLibariesByOrder(String field, String order){
		Optional<SearchResponse> searchResult = Optional.empty();
		try {
			SearchRequest searchRequest = new SearchRequest(ServiceConstants.LIBRARY_INDEX);
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder.query(QueryBuilders.matchAllQuery());
			Function<String, SearchSourceBuilder> f = input -> input.equalsIgnoreCase("ASC")
					? searchSourceBuilder.sort(new FieldSortBuilder(field).order(SortOrder.ASC))
					: input.equalsIgnoreCase("DESC")
							? searchSourceBuilder.sort(new FieldSortBuilder(field).order(SortOrder.DESC))
							: searchSourceBuilder;
			searchRequest.source(f.apply(order));
			
			SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
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
}
