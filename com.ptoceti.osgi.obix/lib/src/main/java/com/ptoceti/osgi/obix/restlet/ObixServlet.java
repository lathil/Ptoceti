package com.ptoceti.osgi.obix.restlet;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : ObixServlet.java
 * 
 * This file is part of the Ptoceti project. More information about
 * this project can be found here: http://www.ptoceti.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2013 - 2015 ptoceti
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.util.Calendar;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.log.LogService;
import org.restlet.Application;
import org.restlet.ext.servlet.ServletAdapter;

import com.ptoceti.osgi.obix.impl.service.Activator;

/**
 * Execute the restlet application as a servlet.
 * 
 * @author lor
 *
 */
public class ObixServlet extends HttpServlet {


	static final long serialVersionUID = 0;
	
	private ServletAdapter adapter;
	
	private Application obixApplication;

	public ObixServlet(Application obixApplication) {
		super();
		this.obixApplication = obixApplication;
	}
	
	public void init() throws ServletException {
		
		adapter = new ServletAdapter(getServletContext());
		this.adapter.setNext(obixApplication);
	
	}

	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	    
		Long start = Calendar.getInstance().getTimeInMillis();
		
		this.adapter.service(req, res);
		
		Long end = Calendar.getInstance().getTimeInMillis();
		
		Activator.log(LogService.LOG_DEBUG, "ObixServlet service: " + req.getRequestURI() + " time: " +  Long.valueOf(end - start) + " ms ");
	}
	
	public Application getApplication(){
		return obixApplication;
	}
}
