/**
 * Copyright (c) 2007 Regents of the University of California (Regents). Created
 * by TELS, Graduate School of Education, University of California at Berkeley.
 *
 * This software is distributed under the GNU Lesser General Public License, v2.
 *
 * Permission is hereby granted, without written agreement and without license
 * or royalty fees, to use, copy, modify, and distribute this software and its
 * documentation for any purpose, provided that the above copyright notice and
 * the following two paragraphs appear in all copies of this software.
 *
 * REGENTS SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE. THE SOFTWAREAND ACCOMPANYING DOCUMENTATION, IF ANY, PROVIDED
 * HEREUNDER IS PROVIDED "AS IS". REGENTS HAS NO OBLIGATION TO PROVIDE
 * MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 * IN NO EVENT SHALL REGENTS BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
 * SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
 * ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
 * REGENTS HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.wise.portal.presentation.web.controllers;

import javax.servlet.http.HttpSession;

import org.easymock.EasyMock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.AbstractModelAndViewTests;
import org.springframework.web.servlet.ModelAndView;
import org.wise.portal.dao.ObjectNotFoundException;
import org.wise.portal.domain.newsitem.NewsItem;
import org.wise.portal.domain.newsitem.impl.NewsItemImpl;
import org.wise.portal.service.newsitem.NewsItemService;

public class IndexControllerTest extends AbstractModelAndViewTests{

	private MockHttpServletRequest request;

	private MockHttpServletResponse response;
	
	private IndexController controller;
	
	private NewsItemService newsService;
	
	private NewsItem newsItem;
	
	@Override
	public void setUp(){
		this.request = new MockHttpServletRequest();
		this.response = new MockHttpServletResponse();
		HttpSession mockSession = new MockHttpSession();
		this.request.setSession(mockSession);
		this.newsService = EasyMock.createMock(NewsItemService.class);
		this.controller = new IndexController();
		this.controller.setNewsItemService(this.newsService);
		this.newsItem = new NewsItemImpl();
	}
	
	@Override
	public void tearDown(){
		this.newsItem = null;
		this.newsService = null;
		this.controller = null;
		this.request = null;
		this.response = null;
	}
	
	public void testHandleRequestInternalHasNewsItem() throws Exception{
		this.newsItem.setTitle("title");
		this.newsItem.setNews("news");
		
		EasyMock.expect(this.newsService.retrieveAllNewsItem().get(0)).andReturn(this.newsItem);
		EasyMock.replay(this.newsService);
		
		ModelAndView modelAndView = this.controller.handleRequestInternal(request, response);
		
		assertEquals(modelAndView.getModel().get("newsItem"), this.newsItem);
		EasyMock.verify(this.newsService);
	}
	
}
