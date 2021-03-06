package org.wise.portal.presentation.web.controllers.teacher;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wise.portal.domain.user.User;
import org.wise.portal.presentation.web.controllers.ControllerUtil;
import org.wise.portal.service.mail.IMailFacade;
import org.wise.portal.service.user.UserService;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

@RestController
@RequestMapping("/api/teacher/forgot")
public class TeacherForgotAccountAPIController {

  @Autowired
  private Properties wiseProperties;

  @Autowired
  private UserService userService;

  @Autowired
  protected IMailFacade mailService;

  @Autowired
  protected MessageSource messageSource;

  @RequestMapping(value = "/username", method = RequestMethod.POST)
  protected String sendForgotUsernameEmail(HttpServletRequest request,
                                           @RequestParam("email") String email) throws JSONException {
    List<User> users = userService.retrieveUserByEmailAddress(email);
    JSONObject response;
    if (users.isEmpty()) {
      response = getEmailNotFoundFailureResponse();
    } else {
      User user = users.get(0);
      String username = user.getUserDetails().getUsername();
      String from = wiseProperties.getProperty("portalemailaddress");
      String [] to = new String[] {email};
      String subject = messageSource.getMessage("forgotaccount.teacher.username.email.subject", new Object[] {}, Locale.US);
      String signInUrl = getSignInUrl(request);
      String contactUrl = getContactUrl(request);
      String body = messageSource.getMessage("forgotaccount.teacher.username.email.body", new Object[] {username, signInUrl, contactUrl}, Locale.US);
      boolean successfullySentEmail = sendEmail(to, subject, body, from);
      if (successfullySentEmail) {
        response = getEmailSentSuccessResponse();
      } else {
        response = getFailedToSendEmailFailureResponse();
      }
    }
    return response.toString();
  }

  private String getSignInUrl(HttpServletRequest request) {
    return ControllerUtil.getPortalUrlString(request) + "/login";
  }

  private boolean sendEmail(String[] to, String subject, String body, String from) {
    try {
      mailService.postMail(to, subject, body, from);
      return true;
    } catch (MessagingException e) {
      e.printStackTrace();
      return false;
    }
  }

  @RequestMapping(value = "/password/verification-code", method = RequestMethod.GET)
  protected String sendVerificationCodeEmail(HttpServletRequest request,
        @RequestParam("username") String username) throws JSONException {
    JSONObject response;
    User user = userService.retrieveUserByUsername(username);
    if (user != null && user.isTeacher()) {
      if (isTooManyVerificationCodeAttempts(user)) {
        response = getVerificationCodeTooManyAttemptsFailureResponse();
      } else {
        boolean successfullySentEmail = sendVerificationCodeEmail(request, user);
        if (successfullySentEmail) {
          response = getEmailSentSuccessResponse();
        } else {
          response = getFailedToSendEmailFailureResponse();
        }
      }
    } else {
      response = getUsernameNotFoundFailureResponse();
    }

    return response.toString();
  }

  private boolean sendVerificationCodeEmail(HttpServletRequest request, User user) {
    String from = wiseProperties.getProperty("portalemailaddress");
    String email = user.getUserDetails().getEmailAddress();
    String [] to = new String[] {email};
    String subject = messageSource.getMessage("forgotaccount.teacher.verificationcode.email.subject", new Object[] {}, Locale.US);
    String verificationCode = getVerificationCode(user);
    String contactUrl = getContactUrl(request);
    String body = messageSource.getMessage("forgotaccount.teacher.verificationcode.email.body", new Object[] {verificationCode, contactUrl}, Locale.US);
    boolean successfullySentEmail = sendEmail(to, subject, body, from);
    return successfullySentEmail;
  }

  private boolean isTooManyVerificationCodeAttempts(User user) {
    Date recentFailedVerificationCodeAttemptTime =
        user.getUserDetails().getRecentFailedVerificationCodeAttemptTime();
    Integer numberOfRecentFailedVerificationCodeAttempts =
        user.getUserDetails().getNumberOfRecentFailedVerificationCodeAttempts();
    if (recentFailedVerificationCodeAttemptTime == null || numberOfRecentFailedVerificationCodeAttempts == null) {
      return false;
    } else {
      return isWithinLast10Minutes(recentFailedVerificationCodeAttemptTime) &&
        numberOfRecentFailedVerificationCodeAttempts >= 5;
    }
  }

  private boolean isWithinLast10Minutes(Date date) {
    if (date == null) {
      return false;
    } else {
      long timeDifferenceInMilliseconds = getTimeDifferenceInMilliseconds(new Date(), date);
      long tenMinutesInMilliseconds = ControllerUtil.convertMinutesToMilliseconds(10);
      return timeDifferenceInMilliseconds < tenMinutesInMilliseconds;
    }
  }

  @RequestMapping(value = "/password/verification-code", method = RequestMethod.POST)
  protected String checkVerificationCode(@RequestParam("username") String username,
                                         @RequestParam("verificationCode") String verificationCode) throws JSONException {
    JSONObject response = new JSONObject();
    User user = userService.retrieveUserByUsername(username);
    if (user != null) {
      resetVerificationCodeAttemptsIfNecessary(user);
      if (isTooManyVerificationCodeAttempts(user)) {
        response = getVerificationCodeTooManyAttemptsFailureResponse();
      } else if (isVerificationCodeExpired(user)) {
        response = getVerificationCodeExpiredFailureResponse();
      } else if (!isVerificationCodeCorrect(user, verificationCode)) {
        incrementFailedVerificationCodeAttempt(user);
        if (isTooManyVerificationCodeAttempts(user)) {
          response = getVerificationCodeTooManyAttemptsFailureResponse();
        } else {
          response = getVerificationCodeIncorrectFailureResponse();
        }
      } else if (!isVerificationCodeExpired(user) && isVerificationCodeCorrect(user, verificationCode)) {
        response = getVerificationCodeCorrectSuccessResponse();
      }
    } else {
      response = getUsernameNotFoundFailureResponse();
    }

    return response.toString();
  }

  private void resetVerificationCodeAttemptsIfNecessary(User user) {
    Date recentFailedVerificationCodeAttemptTime = user.getUserDetails().getRecentFailedVerificationCodeAttemptTime();
    if (!isWithinLast10Minutes(recentFailedVerificationCodeAttemptTime)) {
      user.getUserDetails().clearNumberOfRecentFailedVerificationCodeAttempts();
    }
  }

  private void incrementFailedVerificationCodeAttempt(User user) {
    user.getUserDetails().setRecentFailedVerificationCodeAttemptTime(new Date());
    user.getUserDetails().incrementNumberOfRecentFailedVerificationCodeAttempts();
    userService.updateUser(user);
  }

  @RequestMapping(value = "/password/change", method = RequestMethod.POST)
  protected String changePassword(@RequestParam("username") String username,
                                  @RequestParam("verificationCode") String verificationCode,
                                  @RequestParam("password") String password,
                                  @RequestParam("confirmPassword") String confirmPassword) throws JSONException {
    JSONObject response = new JSONObject();
    User user = userService.retrieveUserByUsername(username);
    if (user != null) {
      if (isTooManyVerificationCodeAttempts(user)) {
        response = getVerificationCodeTooManyAttemptsFailureResponse();
      } else if (isVerificationCodeExpired(user)) {
        response = getVerificationCodeExpiredFailureResponse();
      } else if (!isVerificationCodeCorrect(user, verificationCode)) {
        response = getVerificationCodeIncorrectFailureResponse();
      } else if (isPasswordBlank(password, confirmPassword)) {
        response = getPasswordIsBlankFailureResponse();
      } else if (!isPasswordsMatch(password, confirmPassword)) {
        response = getPasswordsDoNotMatchFailureResponse();
      } else if (!isVerificationCodeExpired(user) &&
          isVerificationCodeCorrect(user, verificationCode) &&
          !isPasswordBlank(password, confirmPassword) &&
          isPasswordsMatch(password, confirmPassword)) {
        userService.updateUserPassword(user, password);
        response = getVerificationCodeCorrectSuccessResponse();
        clearVerificationCodeData(user);
      }
    } else {
      response = getUsernameNotFoundFailureResponse();
    }
    return response.toString();
  }

  private boolean isVerificationCodeCorrect(User user, String verificationCode) {
    String correctVerificationCode = user.getUserDetails().getResetPasswordVerificationCode();
    if (verificationCode == null || correctVerificationCode == null) {
      return false;
    } else if (verificationCode.equals(correctVerificationCode)) {
      return true;
    } else {
      return false;
    }
  }

  private boolean isVerificationCodeExpired(User user) {
    Date verificationCodeCreationTime = user.getUserDetails().getResetPasswordVerificationCodeRequestTime();
    Date now = new Date();
    long timeDifferenceInMilliseconds = getTimeDifferenceInMilliseconds(now, verificationCodeCreationTime);
    long expirationInMilliseconds = ControllerUtil.convertMinutesToMilliseconds(10);
    return timeDifferenceInMilliseconds > expirationInMilliseconds;
  }

  private long getTimeDifferenceInMilliseconds(Date laterTime, Date earlierTime) {
    return laterTime.getTime() - earlierTime.getTime();
  }

  private String getVerificationCode(User user) {
    String verificationCode = RandomStringUtils.randomNumeric(6);
    user.getUserDetails().setResetPasswordVerificationCode(verificationCode);
    Date now = new Date();
    user.getUserDetails().setResetPasswordVerificationCodeRequestTime(now);
    userService.updateUser(user);
    return verificationCode;
  }
  private boolean isPasswordBlank(String password1, String password2) {
    return password1 == null || password2 == null || password1.equals("") || password2.equals("");
  }

  private boolean isPasswordsMatch(String password, String confirmPassword) {
    return password.equals(confirmPassword);
  }

  private void clearVerificationCodeData(User user) {
    user.getUserDetails().clearResetPasswordVerificationCode();
    user.getUserDetails().clearResetPasswordVerificationCodeRequestTime();
    user.getUserDetails().clearRecentFailedVerificationCodeAttemptTime();
    user.getUserDetails().clearNumberOfRecentFailedVerificationCodeAttempts();
    userService.updateUser(user);
  }

  private JSONObject getEmailSentSuccessResponse() throws JSONException {
    JSONObject response = new JSONObject();
    response.put("status", "success");
    response.put("messageCode", "emailSent");
    return response;
  }

  private JSONObject getFailedToSendEmailFailureResponse() throws JSONException {
    JSONObject response = new JSONObject();
    response.put("status", "failure");
    response.put("messageCode", "failedToSendEmail");
    return response;
  }

  private JSONObject getEmailNotFoundFailureResponse() throws JSONException {
    JSONObject response = new JSONObject();
    response.put("status", "failure");
    response.put("messageCode", "emailNotFound");
    return response;
  }

  private JSONObject getUsernameNotFoundFailureResponse() throws JSONException {
    JSONObject response = new JSONObject();
    response.put("status", "failure");
    response.put("messageCode", "usernameNotFound");
    return response;
  }

  private JSONObject getVerificationCodeExpiredFailureResponse() throws JSONException {
    JSONObject response = new JSONObject();
    response.put("status", "failure");
    response.put("messageCode", "verificationCodeExpired");
    return response;
  }

  private JSONObject getVerificationCodeIncorrectFailureResponse() throws JSONException {
    JSONObject response = new JSONObject();
    response.put("status", "failure");
    response.put("messageCode", "verificationCodeIncorrect");
    return response;
  }

  private JSONObject getVerificationCodeTooManyAttemptsFailureResponse() throws JSONException {
    JSONObject response = new JSONObject();
    response.put("status", "failure");
    response.put("messageCode", "tooManyVerificationCodeAttempts");
    return response;
  }

  private JSONObject getVerificationCodeCorrectSuccessResponse() throws JSONException {
    JSONObject response = new JSONObject();
    response.put("status", "success");
    response.put("messageCode", "verificationCodeCorrect");
    return response;
  }

  private JSONObject getPasswordIsBlankFailureResponse() throws JSONException {
    JSONObject response = new JSONObject();
    response.put("status", "failure");
    response.put("messageCode", "passwordIsBlank");
    return response;
  }

  private JSONObject getPasswordsDoNotMatchFailureResponse() throws JSONException {
    JSONObject response = new JSONObject();
    response.put("status", "failure");
    response.put("messageCode", "passwordsDoNotMatch");
    return response;
  }

  private String getContactUrl(HttpServletRequest request) {
    return ControllerUtil.getPortalUrlString(request) + "/contact";
  }
}
