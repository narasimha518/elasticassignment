/**
 * 
 */
package com.elasticsearch.poc.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elasticsearch.poc.config.BlankDetailsException;
import com.elasticsearch.poc.constants.ServiceConstants;
import com.elasticsearch.poc.model.Address;
import com.elasticsearch.poc.model.Book;
import com.elasticsearch.poc.model.Library;
import com.elasticsearch.poc.repository.LibraryRepository;
import com.elasticsearch.poc.service.BookService;
import com.elasticsearch.poc.service.LibraryService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

/**
 * @author narasimhulu.chakali
 *
 */
@Service
public class LibraryServiceImpl implements LibraryService{

	@Autowired
	LibraryRepository libraryRepository;
	
	@Autowired
	private BookService bookService;
	
	private ObjectMapper objectMapper;
	
	@Inject
	public LibraryServiceImpl(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	
	@Override
	public Optional<String>  createLibrary(Library library) {
		// TODO Auto-generated method stub
		Address address = new Address();
		address.setAddressId(library.getAddress().getAddressId());
		address.setCountry(library.getAddress().getCountry());
		address.setState(library.getAddress().getState());
		address.setCity(library.getAddress().getCity());
		address.setPincode(library.getAddress().getPincode());
		
		Optional<IndexResponse> addressResponse = libraryRepository.saveAddress(address);
		Function<Optional<IndexResponse>, Optional<String>> f = input -> {
			return (input.isPresent() && (ServiceConstants.Status.CREATED.toString().equals(input.get().getResult().toString())
					|| input.isPresent() && ServiceConstants.Status.UPDATED.toString().equals(input.get().getResult().toString())))
							? Optional.of(input.get().getId())
							: Optional.empty();
		};

		String addressId = f.apply(addressResponse).get();
		
        Optional<IndexResponse> libraryresponse = libraryRepository.createLibrary( library, addressId);
    	System.out.println(libraryresponse);
        
        Function<Optional<IndexResponse>, Optional<String>> f1 = input -> {
			return (input.isPresent() && (ServiceConstants.Status.CREATED.toString().equals(input.get().getResult().toString())
					|| input.isPresent() && ServiceConstants.Status.UPDATED.toString().equals(input.get().getResult().toString())))
							? Optional.of(input.get().getId())
							: Optional.empty();
		};
		return f1.apply(libraryresponse);
				
	}
	
	@Override
	public String addBooks(Library library) throws BlankDetailsException {
		// TODO Auto-generated method stub
		Optional<IndexResponse> response = Optional.empty();
		List<Book> books = library.getBooks();
		if(library.getLibraryId() == null || books.isEmpty())
	      {
	    	  throw new BlankDetailsException("Library or Book details should not be empty");
	      }
		 Optional<String> addressId = libraryRepository.getAddressId(library.getLibraryId());
		 if(addressId.isEmpty())
	      {
	    	  throw new BlankDetailsException("Library with the given id does not exists");
	      }
		 for(Book book : books) {
	      Optional<String> bookId = bookService.saveBook(book);
		  response = libraryRepository.addBook(library.getLibraryId(), addressId.get(), bookId.get());	 
		  System.out.println(response);
		 }
		return response.isPresent() ? ServiceConstants.ADD_BOOKS_SUCCESS : ServiceConstants.ADD_BOOKS_FAILURE;
	}

	@Override
	public Library fetchLibrary(Integer libraryId) {
		// TODO Auto-generated method stub
		//load the library basic details
		Library library = new Library();
		Map<String, Object> libraryMap = libraryRepository.fetchLibrary(libraryId);
		
		//load the library address 
	if(null != libraryMap) {
	    Optional<Address> address = libraryRepository.loadTheAddressDetails(libraryMap.get("address").toString());
	    libraryMap.remove("address");
	 try 
	 { 
		 library = objectMapper.convertValue(libraryMap, Library.class); 
	  } 
	  catch(Exception ex) {
	  ex.printStackTrace();
	  }     
       library.setAddress(address.get());     
		// load the books details of library
					Optional<SearchResponse> searchResponse = Optional.empty();
					searchResponse = libraryRepository.loadLibraryBooks(libraryId);
					List<Map<String, Object>> searchResults = new ArrayList<>();
					Consumer<SearchHit> c = input -> searchResults.add(input.getSourceAsMap());
					searchResponse.get().getHits().iterator().forEachRemaining(c);	
					Set<Object> bookIds = new HashSet<>();
					searchResults.forEach(result -> bookIds.add(result.get("bookId")));
					List<Book> libraryBooks = new ArrayList<>();
					bookIds.forEach(book -> libraryBooks.add(bookService.getBookById(book.toString()).get()));
				    library.setBooks(libraryBooks);	
		}
		return library;
	}

	@Override
	public Optional<String> deleteLibrary(Integer libraryId) {
		// TODO Auto-generated method stub
		
		Optional<BulkByScrollResponse> bulkResponse = libraryRepository.deleteLibrary(libraryId);
		return bulkResponse.isPresent() ? Optional.of(ServiceConstants.LIBRARY_DELETE_SUCCESS):
			Optional.of(ServiceConstants.LIBRARY_DELETE_FAILURE);
	}

	@Override
	public List<Library> searchLibrary(String field, String searhKey) {
		// TODO Auto-generated method stub	
		List<Library> searchResults = new ArrayList<>();		
		List<String> libraryIds = libraryRepository.getLibraryId(field, searhKey.toString());	
		//load the libraries
		for(String id : libraryIds) {
			searchResults.add(fetchLibrary(Integer.valueOf(id)));
		}
		return searchResults;
	}

	@Override
	public List<Library> sortLibariesByOrder(String field, String order) {
		// TODO Auto-generated method stub
		List<Library> sortResults = new ArrayList<>();
		Optional<SearchResponse> response = libraryRepository.sortLibariesByOrder(field, order);
		List<String> libraryIds = new ArrayList<>();
		response.get().getHits().forEach(input -> libraryIds.add(input.getId()));
		for(String id : libraryIds) {
			sortResults.add(fetchLibrary(Integer.valueOf(id)));
		}	
		return sortResults;
	}
	
}
