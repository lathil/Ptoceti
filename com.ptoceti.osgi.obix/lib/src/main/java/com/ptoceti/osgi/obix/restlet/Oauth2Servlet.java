package com.ptoceti.osgi.obix.restlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.restlet.Application;
import org.restlet.ext.servlet.ServletAdapter;

public class Oauth2Servlet extends HttpServlet {


	static final long serialVersionUID = 0;
	
	private ServletAdapter adapter;
	
	private Application oauth2Application;

	public Oauth2Servlet(Application oauth2Application) {
		super();
		this.oauth2Application = oauth2Application;
	}
	
	public void init() throws ServletException {
		
		adapter = new ServletAdapter(getServletContext());
		this.adapter.setNext(oauth2Application);
	}

	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	     
		this.adapter.service(req, res);
	}
	
	public Application getApplication(){
		return oauth2Application;
	}
}
