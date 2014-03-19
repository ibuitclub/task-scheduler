package org.infobip.taskscheduler.controller;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Wrapper for {@link HttpMethod} options.
 */
@Controller
@RequestMapping(produces = "application/json", value = "api/task/requestType")
public final class RequestTypeController {

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody HttpMethod[] select() {
		return HttpMethod.values();
	}

}