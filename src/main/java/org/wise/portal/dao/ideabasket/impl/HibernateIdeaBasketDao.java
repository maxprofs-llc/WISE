/**
 * Copyright (c) 2008-2015 Regents of the University of California (Regents).
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
package org.wise.portal.dao.ideabasket.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.wise.portal.dao.ObjectNotFoundException;
import org.wise.portal.dao.ideabasket.IdeaBasketDao;
import org.wise.portal.dao.impl.AbstractHibernateDao;
import org.wise.vle.domain.ideabasket.IdeaBasket;

@Repository
public class HibernateIdeaBasketDao extends AbstractHibernateDao<IdeaBasket>
    implements IdeaBasketDao<IdeaBasket> {

  @Override
  protected String getFindAllQuery() {
    return null;
  }

  @Override
  protected Class<? extends IdeaBasket> getDataObjectClass() {
    return null;
  }

  public IdeaBasket getIdeaBasketById(Long id) {
    IdeaBasket ideaBasket = null;

    try {
      ideaBasket = getById(id);
    } catch (ObjectNotFoundException e) {
      e.printStackTrace();
    }

    return ideaBasket;
  }

  @Transactional
  public void saveIdeaBasket(IdeaBasket ideaBasket) {
    save(ideaBasket);
  }

  /**
   * Get the latest IdeaBasket with the given run id and workgroup id
   * @param runId the id of the run
   * @param workgroupId the id of the workgroup
   * @return the IdeaBasket with the matching runId and workgroupId
   */
  @Transactional(readOnly=true)
  public IdeaBasket getIdeaBasketByRunIdWorkgroupId(long runId, long workgroupId) {
    Session session = this.getHibernateTemplate().getSessionFactory().getCurrentSession();

    //find the latest IdeaBasket object that matches
    List<IdeaBasket> result = session.createCriteria(IdeaBasket.class).add(
      Restrictions.eq("runId", runId)).add(
      Restrictions.eq("workgroupId", workgroupId)).addOrder(Order.desc("postTime")).setMaxResults(1).list();

    IdeaBasket ideaBasket = null;
    if(result.size() > 0) {
      /*
       * get the first IdeaBasket from the result list since
       * there will only be one element in the list
       */
      ideaBasket = result.get(0);
    }
    return ideaBasket;
  }

  /**
   * Get all the latest IdeaBaskets with the given run id
   *
   * we will basically be performing this query
   * select * from vle_database.ideaBasket i where id in(SELECT max(id) FROM vle_database.ideaBasket i where runid=<insert runId> group by workgroupid)
   *
   * @param runId the id of the run
   * @return all the latest IdeaBaskets for a run id
   */
  @SuppressWarnings("unchecked")
  @Transactional(readOnly=true)
  public List<IdeaBasket> getLatestIdeaBasketsForRunId(long runId) {
    Session session = this.getHibernateTemplate().getSessionFactory().getCurrentSession();

        /*
         * create a projection that will give us the latest idea basket id
         * for each workgroup id in the run. the projection will return
         * an array of array objects that will look like [id, workgroupId].
         * each workgroup id will only appear once.
         */
    ProjectionList projectionList = Projections.projectionList();
    projectionList.add(Projections.max("id"));
    projectionList.add(Projections.groupProperty("workgroupId"));

    //this first query will filter on the runId and the projection
    List latestIdeaBasketIdsProjection =  session.createCriteria(IdeaBasket.class).add(
      Restrictions.eq("runId", runId)).setProjection(projectionList).list();

    //the list that will contain all the idea basket ids we want
    List<Long> ideaBasketIds = new ArrayList<Long>();

    //loop through all the results from our first query
    for(int x=0; x<latestIdeaBasketIdsProjection.size(); x++) {
      //get the idea basket id
      Object[] projection = (Object[]) latestIdeaBasketIdsProjection.get(x);
      Long ideaBasketId = (Long) projection[0];
      ideaBasketIds.add(ideaBasketId);
    }

    List<IdeaBasket> result = new ArrayList<IdeaBasket>();

    if(ideaBasketIds.size() > 0) {
      //this second query will retrieve all the idea basket ids we retrieved from the first query
      result = session.createCriteria(IdeaBasket.class).add(createIdOrCriterion(ideaBasketIds, 0)).list();
    }

    return result;
  }

  /**
   * Get all the latest IdeaBaskets with the given run id
   *
   * we will basically be performing this query
   * select * from vle_database.ideaBasket i where id in(SELECT max(id) FROM vle_database.ideaBasket i where runid=<insert runId> and workgroupid in(<insert workgroup ids>) group by workgroupid)
   *
   * @param runId the id of the run
   * @param workgroupIds a list of workgroup ids
   * @return all the latest IdeaBaskets for a run id
   */
  @SuppressWarnings("unchecked")
  @Transactional(readOnly=true)
  public List<IdeaBasket> getLatestIdeaBasketsForRunIdWorkgroupIds(long runId, List<Long> workgroupIds) {
    Session session = this.getHibernateTemplate().getSessionFactory().getCurrentSession();

    /*
     * create a projection that will give us the latest idea basket id
     * for each workgroup id in the run. the projection will return
     * an array of array objects that will look like [id, workgroupId].
     * each workgroup id will only appear once.
     */
    ProjectionList projectionList = Projections.projectionList();
    projectionList.add(Projections.max("id"));
    projectionList.add(Projections.groupProperty("workgroupId"));

    //this first query will filter on the runId and workgroupids and the projection
    List latestIdeaBasketIdsProjection =  session.createCriteria(IdeaBasket.class).add(
      Restrictions.eq("runId", runId)).add(Restrictions.in("workgroupId", workgroupIds)).setProjection(projectionList).list();

    //the list that will contain all the idea basket ids we want
    List<Long> ideaBasketIds = new ArrayList<Long>();

    //loop through all the results from our first query
    for(int x=0; x<latestIdeaBasketIdsProjection.size(); x++) {
      //get the idea basket id
      Object[] projection = (Object[]) latestIdeaBasketIdsProjection.get(x);
      Long ideaBasketId = (Long) projection[0];
      ideaBasketIds.add(ideaBasketId);
    }

    List<IdeaBasket> result = new ArrayList<IdeaBasket>();

    if(ideaBasketIds.size() > 0) {
      //this second query will retrieve all the idea basket ids we retrieved from the first query
      result = session.createCriteria(IdeaBasket.class).add(createIdOrCriterion(ideaBasketIds, 0)).list();
    }

    return result;
  }

  /**
   * Get all the idea basket revisions for a run. The results will be
   * ordered by workgroup id and within the ordered workgroup ids it
   * will be ordered by post time
   * @param runId the run id
   * @return a list of idea baskets ordered by workgroup id and then
   * by post time
   */
  @Transactional(readOnly=true)
  public List<IdeaBasket> getIdeaBasketsForRunId(long runId) {
    Session session = this.getHibernateTemplate().getSessionFactory().getCurrentSession();

    //find all the IdeaBasket objects that match
    List<IdeaBasket> result = session.createCriteria(IdeaBasket.class).add(
      Restrictions.eq("runId", runId)).addOrder(Order.asc("workgroupId")).addOrder(Order.asc("periodId")).addOrder(Order.asc("postTime")).list();
    return result;
  }

  /**
   * Get the latest public idea basket for the given run id, period id
   * @param runId the run id
   * @param periodId the period id
   * @return the latest public idea basket for this run id, period id or null if there is none
   */
  @Transactional(readOnly=true)
  public IdeaBasket getPublicIdeaBasketForRunIdPeriodId(long runId, long periodId) {
    Session session = this.getHibernateTemplate().getSessionFactory().getCurrentSession();

    //get the latest idea basket revision that matches
    List<IdeaBasket> result = session.createCriteria(IdeaBasket.class).add(
      Restrictions.eq("runId", runId)).add(Restrictions.eq("periodId", periodId)).add(Restrictions.eq("isPublic", true)).addOrder(Order.desc("id")).setMaxResults(1).list();

    IdeaBasket ideaBasket = null;
    if(result.size() > 0) {
      /*
       * get the first IdeaBasket from the result list since
       * there will only be one element in the list
       */
      ideaBasket = result.get(0);
    }
    return ideaBasket;
  }

  /**
   * A recursive function that chains "or" restrictions of ids together
   * @param ids a list of ids, the list must not be empty
   * @param index the current index in the list to utilize
   * @return a Criterion of id "or"ed together
   */
  private Criterion createIdOrCriterion(List<Long> ids, int index) {
    if(index == (ids.size() - 1)) {
      /*
       * base case if the list has only one element just return a
       * restriction with the id
       */
      return Restrictions.eq("id", ids.get(index));
    } else {
      /*
       * "or" together this first element with the recursive call
       * on the rest of the list
       */
      return Restrictions.or(Restrictions.eq("id", ids.get(index)), createIdOrCriterion(ids, index + 1));
    }
  }
}
