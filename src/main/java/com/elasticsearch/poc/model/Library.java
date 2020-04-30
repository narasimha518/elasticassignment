package com.elasticsearch.poc.model;

import java.util.List;

public class Library {

	private Integer libraryId;
	private String libraryName;
	private Address address;
	private List<Book> books;
	
	public Library() {
		super();
	}
	
	/**
	 * @param libraryId
	 * @param libraryName
	 * @param address
	 */
	public Library(Integer libraryId, String libraryName, Address address) {
		super();
		this.libraryId = libraryId;
		this.libraryName = libraryName;
		this.address = address;
	}

	public Integer getLibraryId() {
		return libraryId;
	}
	public void setLibraryId(Integer libraryId) {
		this.libraryId = libraryId;
	}
	public String getLibraryName() {
		return libraryName;
	}
	public void setLibraryName(String libraryName) {
		this.libraryName = libraryName;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	
	public List<Book> getBooks() {
		return books;
	}

	public void setBooks(List<Book> books) {
		this.books = books;
	}

	@Override
	public String toString() {
		return "Library [libraryId=" + libraryId + ", libraryName=" + libraryName + ", addressId=" + address + "]";
	}

}
