/**
 * Copyright (c) 2007-2017 Encore Research Group, University of Toronto
 *
 * This software is distributed under the GNU General Public License, v3,
 * or (at your option) any later version.
 *
 * Permission is hereby granted, without written agreement and without license
 * or royalty fees, to use, copy, modify, and distribute this software and its
 * documentation for any purpose, provided that the above copyright notice and
 * the following two paragraphs appear in all copies of this software.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Public License for more details.
 *
 * You should have received a copy of the GNU Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.wise.vle.domain.webservice.http;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.wise.vle.domain.webservice.BadHeaderException;
import org.wise.vle.domain.webservice.HttpStatusCodeException;

/**
 * Encapsulates Http request information.
 *
 * @author Cynick Young
 */
public abstract class AbstractHttpRequest {

  protected Log logger;

  protected Map<String, String> requestHeaders;

  protected Map<String, String> requestParameters;

  protected String relativeUrl;

  protected int expectedResponseStatusCode;

  /**
   * Creates an AbstractHttpRequest object with all of the data required.
   *
   * @param requestHeaders is a map of HTTP request headers
   * @param requestParameters is a map of HTTP request parameters
   * @param relativeUrl is the target relative URL for this request
   * @param expectedResponseStatusCode is the HTTP status code that is expected
   * to be returned by the server
   * @throws BadHeaderException if the request headers contain any illegal characters either
   * in the request field name or the request field value
   */
  protected AbstractHttpRequest(final Map<String, String> requestHeaders,
      final Map<String, String> requestParameters, final String relativeUrl,
      final int expectedResponseStatusCode) throws BadHeaderException {
    logger = LogFactory.getLog(getClass());
    checkForLegalHeaders(requestHeaders);
    this.requestHeaders = Collections.unmodifiableMap(requestHeaders);
    this.requestParameters = Collections.unmodifiableMap(requestParameters);
    this.relativeUrl = relativeUrl;
    this.expectedResponseStatusCode = expectedResponseStatusCode;
  }

  /**
   * DO NOT USE THIS METHOD
   */
  protected AbstractHttpRequest() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns the expected response status code for this request.
   * @return the expectedResponseStatusCode
   */
  public int getExpectedResponseStatusCode() {
    return expectedResponseStatusCode;
  }

  /**
   * Returns the request headers for this request.
   * @return the requestHeaders
   */
  public Map<String, String> getRequestHeaders() {
    return requestHeaders;
  }

  /**
   * Returns the request parameters for this request.
   * @return the requestParameters
   */
  public Map<String, String> getRequestParameters() {
    return requestParameters;
  }

  /**
   * Returns the target URL for this request.
   * @return the relativeUrl
   */
  public String getRelativeUrl() {
    return relativeUrl;
  }

  private static final Pattern ILLEGAL_HEADER_FIELD_NAME_PATTERN = Pattern
      .compile("(.*[\\p{Cntrl}\t ()<>@,;:\\\"/\u001B\u001D?={}]+.*)+");

  private static final Pattern ILLEGAL_HEADER_FIELD_VALUE_PATTERN = Pattern
      .compile("(.*[\\p{Cntrl}]+.*)+");

  /**
   * Checks that the request headers are legal
   * @param requestHeaders A map of the request headers
   * @throws BadHeaderException Thrown if the headers are illegal.
   */
  protected void checkForLegalHeaders(final Map<String, String> requestHeaders)
      throws BadHeaderException {
    for (String key : requestHeaders.keySet()) {
      if ("".equals(key) || ILLEGAL_HEADER_FIELD_NAME_PATTERN.matcher(key).matches()) {
        throw new BadHeaderException("Request header field-name contains illegal characters.");
      }
      if (ILLEGAL_HEADER_FIELD_VALUE_PATTERN.matcher(requestHeaders.get(key)).matches()) {
        throw new BadHeaderException("Request header field-value contains illegal characters.");
      }
    }
  }

  /**
   * Checks the status code returned from an HTTP request against the status
   * code expected to be returned and logs the response information if the
   * status code is not the expected one. Override this method to handle
   * specific status codes.
   *
   * @param response The response
   * @return true if the status code matches the expected status code
   * @throws IOException when the http response body cannot be obtained.
   * @throws HttpStatusCodeException when the returned status code is not as expected.
   */
  public boolean isValidResponseStatus(HttpResponse response)
      throws IOException, HttpStatusCodeException {
    int actualStatusCode = response.getStatusLine().getStatusCode();
    if (actualStatusCode == expectedResponseStatusCode) {
      return true;
    }

    String statusText = response.getStatusLine().toString();
    logMethodInfo(response, actualStatusCode);
    throw new HttpStatusCodeException(statusText);
  }

  /**
   * Logs the HttpMethod response information
   * @param response the HttpResponse response.
   * @param actualStatusCode The status code retrieved from the response.
   * @throws IOException If the response body cannot be retrieved.
   */
  protected void logMethodInfo(HttpResponse response, int actualStatusCode) throws IOException {
    if (logger.isWarnEnabled()) {
      logger.warn(actualStatusCode + ": " + response.getStatusLine().getStatusCode());
      logger.warn("body: " + response.getEntity().getContent());
    }
  }
}
