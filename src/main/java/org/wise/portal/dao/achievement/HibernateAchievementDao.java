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
package org.wise.portal.dao.achievement;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.wise.portal.dao.impl.AbstractHibernateDao;
import org.wise.portal.domain.run.Run;
import org.wise.portal.domain.workgroup.Workgroup;
import org.wise.vle.domain.achievement.Achievement;

import java.util.List;

/**
 * Domain Access Object hibernate implementation for Achievements
 * @author Hiroki Terashima
 */
@Repository
public class HibernateAchievementDao
  extends AbstractHibernateDao<Achievement>
  implements AchievementDao<Achievement> {

  @Override
  public List<Achievement> getAchievementsByParams(Integer id, Run run, Workgroup workgroup,
      String achievementId, String type) {
    Session session = this.getHibernateTemplate().getSessionFactory().getCurrentSession();
    Criteria sessionCriteria = session.createCriteria(Achievement.class);
    if (id != null) {
      sessionCriteria.add(Restrictions.eq("id", id));
    }
    if (run != null) {
      sessionCriteria.add(Restrictions.eq("run", run));
    }
    if (workgroup != null) {
      sessionCriteria.add(Restrictions.eq("workgroup", workgroup));
    }
    if (achievementId != null) {
      sessionCriteria.add(Restrictions.eq("achievementId", achievementId));
    }
    if (type != null) {
      sessionCriteria.add(Restrictions.eq("type", type));
    }

    // order the achievements by achievement time from oldest to newest
    sessionCriteria.addOrder(Order.asc("achievementTime"));

    return sessionCriteria.list();
  }

  @Override
  protected String getFindAllQuery() {
    return null;
  }

  @Override
  protected Class<? extends Achievement> getDataObjectClass() {
    return Achievement.class;
  }
}
