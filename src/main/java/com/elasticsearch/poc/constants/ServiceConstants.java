package com.elasticsearch.poc.constants;

public interface ServiceConstants {

	String API_VERSION = "v1";
	String API_PREFIX = "/poc/" + API_VERSION;
	String BOOK_SERVICE_PATH = "/book";
	String LIBRARY_SERVICE_PATH = "/library";
	String SAVE_PATH = "/save";
	String UPDATE_PATH = "/update";
	String GET_ALL_BOOKS = "/getAll";
	String BOOK_INDEX = "book-index";
	String LIBRARY_INDEX = "library-index";
	String ADDRESS_INDEX = "address-index";
	String LIBRARY_BOOK_JOIN_INDEX = "library-book-index";
	String BOOK_TYPE = "books";
	String CREATE_PATH = "/create";
	String ADD_BOOK_PATH = "/add";

	String SAVE_BOOK_SUCCESS = "Book saved successfully with id ";
	String SAVE_BOOK_FAILURE = "Oops something went wrong! Error while saving the Book";
	String UPDATE_BOOK_SUCCESS = "Requested book has been updated successfully";
	String UPDATE_BOOK_NOCHANGE = "No changes detected to update the book";
	String UPDATE_BOOK_FAILURE = "Oops something went wrong! Either book not found or database is down.";
	String DELETE_BOOK_SUCCESS = "Requested book has been deleted successfully";
	String DELETE_BOOK_FAILURE = "Oops something went wrong! Either book not found or database is down.";
	
	String CREATE_LIBRARY_SUCCESS = "Library saved successfully with id ";
	String CREATE_LIBRARY_FAILURE = "Oops something went wrong! Error while saving the Library";
	
	String ADD_BOOKS_SUCCESS = "Book(s) have been added successfully to the Library";
	String ADD_BOOKS_FAILURE = "Oops something went wrong! while adding books to the library";
	
	String LIBRARY_DELETE_SUCCESS = "Library deleted successfully";
	String LIBRARY_DELETE_FAILURE = "Oops something went wrong! Either Library not found or database is down.";
	

	public enum Status {
		CREATED, UPDATED, DELETED, NOT_FOUND, NOOP;
	}
}
