package com.elasticsearch.poc.controller;

import java.util.List;
import java.util.Map;

import org.elasticsearch.index.query.MoreLikeThisQueryBuilder.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elasticsearch.poc.constants.ServiceConstants;
import com.elasticsearch.poc.service.BookService;

@RestController
@RequestMapping(value = ServiceConstants.API_PREFIX + ServiceConstants.SPECIAL_SERVICE_PATH)
public class SpecialController {

	@Autowired
	BookService bookService;
	
	
	// this fetches on all the the fields mentioned in the repository file, across the indexes.
	@GetMapping(value = "/likesearch/{searchValue}")
	public List<Map<String, Object>> searchBooksByLikeValue(@PathVariable("searchValue") String[] searchValue) {
		return bookService.searchBooksByLikeValue(searchValue);
	}
	
}
