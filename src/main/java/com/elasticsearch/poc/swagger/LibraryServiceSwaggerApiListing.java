package com.elasticsearch.poc.swagger;

import static com.elasticsearch.poc.constants.ServiceConstants.API_PREFIX;

import javax.inject.Named;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.jaxrs.listing.BaseApiListingResource;

/**
 * This exposes the generated Swagger documentation in either json or yaml format.
 * The swagger-ui will use this path to generate the docs in a pretty ui format
 */

@Named
@Path(API_PREFIX + "/swagger.{type:json|yaml}")
public class LibraryServiceSwaggerApiListing extends BaseApiListingResource {
	
	@GET
	  @Produces({MediaType.APPLICATION_JSON, "application/yaml"})
	  @ApiOperation(value = "The swagger definition in either JSON or YAML", hidden = true)
	  public Response getListing(@Context Application app, @Context ServletConfig sc, @Context HttpHeaders headers,
	      @Context UriInfo uriInfo, @PathParam("type") String type, @Context HttpServletRequest request) {
	    if (StringUtils.isNotBlank(type) && type.trim().equalsIgnoreCase("yaml")) {
	      return getListingYamlResponse(app, request.getServletContext(), sc, headers, uriInfo);
	    } else {
	      return getListingJsonResponse(app, request.getServletContext(), sc, headers, uriInfo);
	    }
	  }

}
