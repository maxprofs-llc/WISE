/**
 * Copyright (c) 2007-2017 Regents of the University of California (Regents).
 * Created by WISE, Graduate School of Education, University of California, Berkeley.
 * 
 * This software is distributed under the GNU General Public License, v3,
 * or (at your option) any later version.
 * 
 * Permission is hereby granted, without written agreement and without license
 * or royalty fees, to use, copy, modify, and distribute this software and its
 * documentation for any purpose, provided that the above copyright notice and
 * the following two paragraphs appear in all copies of this software.
 * 
 * REGENTS SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE. THE SOFTWARE AND ACCOMPANYING DOCUMENTATION, IF ANY, PROVIDED
 * HEREUNDER IS PROVIDED "AS IS". REGENTS HAS NO OBLIGATION TO PROVIDE
 * MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 * 
 * IN NO EVENT SHALL REGENTS BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
 * SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
 * ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
 * REGENTS HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.wise.portal.presentation.web.controllers.teacher.management;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.wise.portal.domain.group.Group;
import org.wise.portal.domain.run.Run;
import org.wise.portal.domain.teacher.management.ViewMyStudentsPeriod;
import org.wise.portal.domain.user.User;
import org.wise.portal.domain.workgroup.Workgroup;
import org.wise.portal.presentation.web.controllers.ControllerUtil;
import org.wise.portal.service.acl.AclService;
import org.wise.portal.service.authentication.UserDetailsService;
import org.wise.portal.service.run.RunService;

/**
 * Controller for displaying students in the run
 * 
 * @author Patrick Lawler
 * @author Hiroki Terashima
 */
@Controller
public class ViewMyStudentsController {

	@Autowired
	private RunService runService;

	@Autowired
	private AclService<Run> aclService;

	/**
	 * Handles request for viewing students in the specified run
	 *
	 * @param runId id of the Run
	 * @param servletRequest HttpServletRequest
	 * @return modelAndView containing information needed to view students
	 * @throws Exception
	 */
	@RequestMapping("/teacher/management/viewmystudents")
	protected ModelAndView viewMyStudents(
			@RequestParam("runId") Long runId,
			HttpServletRequest servletRequest) throws Exception {
		
		User user = ControllerUtil.getSignedInUser();
		Run run = runService.retrieveById(runId);

		// check that the logged-in user has permission for this run
		if (user.isAdmin() ||
				user.getUserDetails().hasGrantedAuthority(UserDetailsService.RESEARCHER_ROLE) ||
				this.aclService.hasPermission(run, BasePermission.ADMINISTRATION, user) ||
				this.aclService.hasPermission(run, BasePermission.READ, user)) {

			Set<Workgroup> allworkgroups = this.runService.getWorkgroups(runId);
			String workgroupsWithoutPeriod = "";
			Set<ViewMyStudentsPeriod> viewmystudentsallperiods = new TreeSet<ViewMyStudentsPeriod>();

			// retrieves the get parameter periodName to determine which
			// period the link is requesting
			String periodName = servletRequest.getParameter("periodName");
			
			int tabIndex = 0;
			int periodCounter = 0;
			
			for (Group period : run.getPeriods()) {
				ViewMyStudentsPeriod viewmystudentsperiod = new ViewMyStudentsPeriod();
				viewmystudentsperiod.setRun(run);
				viewmystudentsperiod.setPeriod(period);
				Set<Workgroup> periodworkgroups = new TreeSet<Workgroup>();
				Set<User> grouplessStudents = new HashSet<User>();
				grouplessStudents.addAll(period.getMembers());
				for (Workgroup workgroup : allworkgroups) {
					grouplessStudents.removeAll(workgroup.getMembers());
					try {
						if (workgroup.getMembers().size() > 0    // don't include workgroups with no members.
								&& !workgroup.isTeacherWorkgroup()
								&& workgroup.getPeriod().getId().equals(period.getId())) {
							// set url where this workgroup's work can be retrieved as PDF
							periodworkgroups.add(workgroup);				
						}
					} catch (NullPointerException npe) {
						// if a workgroup is not in a period, make a list of them and let teacher put them in a period...
						// this should not be the case if the code works correctly and associates workgroups with periods when workgroups are created.
						workgroupsWithoutPeriod += workgroup.getId().toString() + ",";
					}
				}
				viewmystudentsperiod.setGrouplessStudents(grouplessStudents);
				viewmystudentsperiod.setWorkgroups(periodworkgroups);
				viewmystudentsallperiods.add(viewmystudentsperiod);
				
				// determines which period tab to show in the AJAX tab object
				if (periodName != null && periodName.equals(period.getName())) {
					tabIndex = periodCounter;
				}
				periodCounter++;
			}
	
			if (servletRequest.getParameter("tabIndex") != null) {
				tabIndex = Integer.valueOf(servletRequest.getParameter("tabIndex"));
			}

			ModelAndView modelAndView = new ModelAndView();
			modelAndView.addObject("user", user);
			modelAndView.addObject("viewmystudentsallperiods", viewmystudentsallperiods);
			modelAndView.addObject("run", run);
			modelAndView.addObject("tabIndex", tabIndex);
			modelAndView.addObject("workgroupsWithoutPeriod", workgroupsWithoutPeriod);
			return modelAndView;
		} else {
			return new ModelAndView("errors/accessdenied");
		}
	}

	/**
	 * Get the students in the specified run and returns them in the model
	 * @param runId id of the Run
	 * @return modelAndView containing information needed to get student list
	 * @throws Exception
	 */
	@RequestMapping("/teacher/management/studentlist")
	protected ModelAndView getStudentList(
			@RequestParam("runId") Long runId) throws Exception {

		User user = ControllerUtil.getSignedInUser();
		Run run = runService.retrieveById(runId);

		// check that the logged-in user has permission for this run
		if (user.isAdmin() ||
				user.getUserDetails().hasGrantedAuthority(UserDetailsService.RESEARCHER_ROLE) ||
				this.aclService.hasPermission(run, BasePermission.ADMINISTRATION, user) ||
				this.aclService.hasPermission(run, BasePermission.READ, user)) {
			Set<Group> periods = run.getPeriods();
			Set<Group> requestedPeriods = new TreeSet<Group>();

			for (Group period : periods) {
				// TODO in future: filter by period...for now, include all periods
				requestedPeriods.add(period);
			}

			ModelAndView modelAndView = new ModelAndView();
			modelAndView.addObject("run", run);
			modelAndView.addObject("periods", requestedPeriods);
			return modelAndView;
		} else {
			return new ModelAndView("errors/accessdenied");
		}
	}
}