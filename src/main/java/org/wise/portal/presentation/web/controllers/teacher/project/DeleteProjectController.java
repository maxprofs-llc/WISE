/**
 * Copyright (c) 2008-2017 Regents of the University of California (Regents).
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
package org.wise.portal.presentation.web.controllers.teacher.project;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.wise.portal.dao.ObjectNotFoundException;
import org.wise.portal.domain.project.Project;
import org.wise.portal.domain.run.Run;
import org.wise.portal.domain.user.User;
import org.wise.portal.presentation.web.controllers.ControllerUtil;
import org.wise.portal.presentation.web.exception.NotAuthorizedException;
import org.wise.portal.service.run.RunService;
import org.wise.portal.service.project.ProjectService;

@Controller
public class DeleteProjectController {

  @Autowired
  private ProjectService projectService;

  @Autowired
  private RunService runService;

  /**
   * @param projectIdStr
   * @param revive if deleting the project, the value should be null or anything besides
   * the string "true". if reviving the project, the value should be the string "true"
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @RequestMapping("/teacher/projects/deleteproject.html")
  protected ModelAndView handleRequestInternal(
      @RequestParam("projectId") String projectIdStr,
      @RequestParam("revive") String revive,
      HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String responseString = "failure";
    if (projectIdStr != null && !projectIdStr.equals("")) {
      Long projectId = Long.parseLong(projectIdStr);
      if (projectId != null) {
        try {
          Project project = projectService.getById(projectId);
          if (project != null) {
            User signedInUser = ControllerUtil.getSignedInUser();
            User owner = project.getOwner();
            Long signedInUserId = signedInUser.getId();
            Long ownerId = owner.getId();
            if (signedInUserId == ownerId) {
              if (revive != null && revive.equals("true")) {
                project.setDeleted(false);
                project.setDateDeleted(null);

                try {
                  projectService.updateProject(project, signedInUser);
                  responseString = "success";
                } catch (NotAuthorizedException e) {
                  e.printStackTrace();
                }
                try {
                  List<Run> runsUsingProject = this.runService.getProjectRuns(projectId);
                  if (!runsUsingProject.isEmpty()) {
                    Run run = runsUsingProject.get(0);
                    runService.startRun(run);
                  }
                } catch (Exception e) {
                  // ignore exceptions
                }
              } else {
                project.setDeleted(true);
                project.setDateDeleted(new Date());

                try {
                  projectService.updateProject(project, signedInUser);
                  responseString = "success";
                } catch (NotAuthorizedException e) {
                  e.printStackTrace();
                }

                try {
                  List<Run> runsUsingProject = this.runService.getProjectRuns(projectId);
                  if (!runsUsingProject.isEmpty()) {
                    Run run = runsUsingProject.get(0);
                    runService.endRun(run);
                  }
                } catch (Exception e) {
                  // ignore exceptions
                }

              }
            } else {
              responseString = "failure: not owner";
            }
          } else {
            responseString = "failure: project does not exist";
          }
        } catch (ObjectNotFoundException e) {
          e.printStackTrace();
        }
      } else {
        responseString = "failure: invalid project id";
      }
    } else {
      responseString = "failure: invalid project id";
    }
    response.getWriter().write(responseString);
    return null;
  }
}
