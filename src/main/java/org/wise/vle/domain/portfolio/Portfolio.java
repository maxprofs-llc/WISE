/**
 * Copyright (c) 2008-2015 Regents of the University of California (Regents). Created
 * by TELS, Graduate School of Education, University of California at Berkeley.
 *
 * This software is distributed under the GNU General Public License, v3.
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
package org.wise.vle.domain.portfolio;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONException;
import org.json.JSONObject;
import org.wise.vle.domain.PersistableDomain;

/**
 * Controller for processing requests related to portfolio.
 * @author Hiroki Terashima
 * @author Eddie Pan
 */
@Entity
@Table(name = "portfolio", indexes = {
    @Index(columnList = "runId,workgroupId", name = "portfolioRunIdAndWorkgroupIdIndex") } )
@Getter
@Setter
public class Portfolio extends PersistableDomain implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id = null;

  @Column(name = "runId")
  private Long runId = null;

  @Column(name = "workgroupId")
  private Long workgroupId = null;

  @Column(name = "metadata", columnDefinition = "mediumtext")
  private String metadata = null;

  @Column(name = "items", length = 512000, columnDefinition = "mediumtext")
  private String items = null;

  @Column(name = "deletedItems", length = 512000, columnDefinition = "mediumtext")
  private String deletedItems = null;

  @Column(name = "isPublic")
  private Boolean isPublic = false;

  @Column(name = "isSubmitted")
  private Boolean isSubmitted = false;

  @Column(name = "tags")
  private String tags;

  @Column(name = "postTime")
  private Timestamp postTime;

  public Portfolio() {
  }

  public Portfolio(JSONObject portfolioJSONObject) {
    try {
      this.runId = portfolioJSONObject.getLong("runId");
      this.workgroupId = portfolioJSONObject.getLong("workgroupId");
      this.metadata = portfolioJSONObject.getString("metadata");
      this.items = portfolioJSONObject.getString("items");
      this.deletedItems = portfolioJSONObject.getString("deletedItems");
      Calendar now = Calendar.getInstance();
      this.postTime = new Timestamp(now.getTimeInMillis());
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  /**
   * Constructor that does not populate the data field
   * @param runId
   * @param workgroupId
   */
  public Portfolio(long runId, long workgroupId) {
    this.runId = runId;
    this.workgroupId = workgroupId;
    Calendar now = Calendar.getInstance();
    this.postTime = new Timestamp(now.getTimeInMillis());
  }

  @Override
  protected Class<?> getObjectClass() {
    return Portfolio.class;
  }

  public String toJSONString() {
    String jsonString = null;
    JSONObject jsonObject = new JSONObject(this);
    try {
      if(jsonObject != null) {
        jsonString = jsonObject.toString(3);
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return jsonString;
  }
}
