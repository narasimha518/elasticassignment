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

import com.elasticsearch.poc.config.BlankDetailsException;
import com.elasticsearch.poc.constants.ServiceConstants;
import com.elasticsearch.poc.model.Book;
import com.elasticsearch.poc.model.Library;
import com.elasticsearch.poc.service.BookService;
import com.elasticsearch.poc.service.LibraryService;

/**
 * @author narasimhulu.chakali
 *
 */

@RestController
@RequestMapping(value = ServiceConstants.API_PREFIX + ServiceConstants.LIBRARY_SERVICE_PATH)
public class LibraryController {

	@Autowired
	LibraryService libraryService;

/* Request structure to create library
 {
  "libraryId":33,
  "libraryName":"TestLibrary03",
  "address":
   {
   	"addressId" :333 ,
   	"country": "India",
  	"state":"Telangana",
  	"city":"Hyderabad",
  	"pincode": 502321
    }
   } 
*/	
	//create library
	@PostMapping(value = ServiceConstants.CREATE_PATH)
	public String createLibrary(@RequestBody Library library) throws Exception {
     Optional<String>	result	= libraryService.createLibrary(library);
     return  result.isPresent() ? ServiceConstants.CREATE_LIBRARY_SUCCESS.toString() + library.getLibraryId()
     : ServiceConstants.CREATE_LIBRARY_FAILURE.toString();
	}

	/* Request structure to add books into library
	{
	  "libraryId":11,
	  "books": {
	   {
	   	"bookId" :1111 ,
	   	"bookName": "Test1111",
	  	"author":"Narasimha"
	    },
	    {
	   	"bookId" :2222 ,
	   	"bookName": "Test2222",
	  	"author":"Teja"
	    }	    
	  }
	} 
	*/
	
	@PostMapping(value = ServiceConstants.ADD_BOOK_PATH)
	public String addBooksToLibrary(@RequestBody Library library) throws Exception {
     String result = null;
	try {
    	result = libraryService.addBooks(library);
       }
       catch(BlankDetailsException be){
    	   be.printStackTrace();
    	   System.out.println(be.getMessage());
       }
	return result;	
	}
		
	
	/* Sample request to fetch the library
	 * 
	 * http://localhost:8081/poc/v1/library/get/22
	 * 
	 * This loads library, address details and all the books it has
	 * */
	@GetMapping(value = "/get/{libraryId}")
	public Library fetchLibrary(@PathVariable("libraryId") Integer libraryId) {
		return  libraryService.fetchLibrary(libraryId);		 
	}
/* 
 * sample request to delete the library
 * http://localhost:8081/poc/v1/library/delete/11
 * */
	@DeleteMapping("/delete/{id}")
	public String deleteLibraryById(@PathVariable Integer id) {
		Optional<String> deletedId = libraryService.deleteLibrary(id);
		return deletedId.get();
	}	

/*  Request formats:
 *  http://localhost:8081/poc/v1/library/search/libraryId/22
 *  http://localhost:8081/poc/v1/library/search/libraryName/TestLibrary02
 *  http://localhost:8081/poc/v1/library/search/address/222
 *  
 * */
	@GetMapping(value = "/search/{field}/{searchValue}")
	public List<Library> searchLibraryBySearchFieldAndValue(@PathVariable String field,
			@PathVariable("searchValue") String searchValue) {
		return libraryService.searchLibrary(field, searchValue);
	} 

	/*
	 * sample requests:
	 * http://localhost:8081/poc/v1/library/sort/libraryId/asc
	 * http://localhost:8081/poc/v1/library/sort/libraryId/desc
	 * http://localhost:8081/poc/v1/library/sort/libraryName/asc
	 * http://localhost:8081/poc/v1/library/sort/libraryName/desc
	 * http://localhost:8081/poc/v1/library/sort/address/desc
	 * http://localhost:8081/poc/v1/library/sort/address/asc
	 */
	@GetMapping(value = "/sort/{field}/{order}")
	public List<Library> sortLibrariesByOrder(@PathVariable String field, @PathVariable String order) {
		return libraryService.sortLibariesByOrder(field, order);
	}
	

}