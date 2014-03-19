package org.infobip.taskscheduler.controller.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * Cross-Origin Resource Sharing (CORS) filter.
 */
public class CorsFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(CorsFilter.class);

	public CorsFilter() {}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
			throws IOException, ServletException {

		((HttpServletResponse) response).addHeader("Access-Control-Allow-Origin", "*");
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		LOGGER.info("Loaded");
	}

	@Override
	public void destroy() {
		LOGGER.info("Destroyed");
	}

	@Override
	public String toString() {
		return "CorsFilter []";
	}

}