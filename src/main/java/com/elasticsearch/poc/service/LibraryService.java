/**
 * 
 */
package com.elasticsearch.poc.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.elasticsearch.poc.config.BlankDetailsException;
import com.elasticsearch.poc.model.Book;
import com.elasticsearch.poc.model.Library;

/**
 * @author narasimhulu.chakali
 *
 */
public interface LibraryService {

	public Optional<String> createLibrary(Library library);
	
	public String addBooks(Library library) throws BlankDetailsException;

	public Library fetchLibrary(Integer libraryId);

	public Optional<String> deleteLibrary(Integer libraryId);

	public List<Library> searchLibrary(String field, String searhKey);

	public List<Library> sortLibariesByOrder(String field, String order);

}
