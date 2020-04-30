package com.elasticsearch.poc.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.reindex.BulkByScrollResponse;

import com.elasticsearch.poc.model.Address;
import com.elasticsearch.poc.model.Book;
import com.elasticsearch.poc.model.Library;

public interface LibraryRepository {
	Optional<IndexResponse> createLibrary(Library library, String addressId);
	Optional<IndexResponse> saveAddress(Address address);
	Optional<String> getAddressId(Integer libraryId);
	Optional<IndexResponse> addBook(Integer libraryId, String addressId, String b);
	Map<String, Object> fetchLibrary(Integer libraryId);
	Optional<SearchResponse> loadLibraryBooks(Integer libraryId);
	Optional<Address> loadTheAddressDetails(String addressId);
	Optional<BulkByScrollResponse> deleteLibrary(Integer libraryId);
    List<String> getLibraryId(String field, String value);
    Optional<SearchResponse> sortLibariesByOrder(String field, String order);
}
