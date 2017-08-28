package org.classfoo.onyx.impl.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.security.Login;
import org.classfoo.onyx.api.web.OnyxApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Onyx API
 */
@RestController
@RequestMapping("/onyxapi/**")
public class ActionOnyxApi {

	@Autowired
	private OnyxService onyxService;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private HttpServletResponse response;

	@Autowired
	private Login login;

	/**
	 * Handle Get Request
	 * url:/ctx/onyxapi/v1/xxxx
	 * @param args
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public Object get(@RequestParam Map<String, Object> args) {
		return onyxService.service(RequestMethod.GET, this.formatArgs(request, args));
	}

	/**
	 * Handle Post Request
	 * @param args
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public Object post(@RequestParam Map<String, Object> args,
			@RequestParam(name = "file", required = false) MultipartFile[] file) {
		if (file != null) {
			args.put("file", file);
		}
		return onyxService.service(RequestMethod.POST, this.formatArgs(request, args));
	}

	/**
	 * Handle Put Request
	 * @param args
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public Object put(@RequestParam Map<String, Object> args) {
		return onyxService.service(RequestMethod.PUT, this.formatArgs(request, args));
	}

	/**
	 * Handle Delete Request
	 * @param args
	 * @return
	 */
	@RequestMapping(method = RequestMethod.DELETE)
	public Object delete(@RequestParam Map<String, Object> args) {
		return onyxService.service(RequestMethod.DELETE, this.formatArgs(request, args));
	}

	/**
	 * Format the request uri into args
	 * @param request
	 * @param args
	 * @return
	 */
	private Map<String, Object> formatArgs(HttpServletRequest request, Map<String, Object> args) {
		HashMap<String, Object> result = new HashMap<String, Object>(args.size() + 6);
		result.putAll(args);
		String uri = request.getRequestURI();
		String ctx = request.getContextPath();
		uri = uri.substring(ctx.length() + 1);
		String[] splits = uri.split("/");
		if (splits.length > 1) {
			String version = splits[1];
			result.put(OnyxApi.VERSION, version);
		}
		if (splits.length > 2) {
			String resource = splits[2];
			result.put(OnyxApi.RESOURCE, resource);
		}
		if (splits.length > 3) {
			String id = splits[3];
			result.put(OnyxApi.RESID, id);
		}
		result.put(OnyxApi.LOGIN, login);
		result.put(OnyxApi.REQUEST, request);
		result.put(OnyxApi.RESPONSE, response);
		return result;
	}
}
