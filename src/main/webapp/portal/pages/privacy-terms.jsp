<%@ include file="../include.jsp"%>

<!DOCTYPE html>
<html dir="${textDirection}">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<meta http-equiv="X-UA-Compatible" content="chrome=1" />
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<%@ include file="../favicon.jsp"%>
<title><spring:message code="pages.gettingstarted.title" /></title>

<link href="${contextPath}/<spring:theme code="globalstyles"/>" media="screen" rel="stylesheet"  type="text/css" />
<link href="${contextPath}/<spring:theme code="superfishstylesheet"/>" rel="stylesheet" type="text/css" >
<c:if test="${textDirection == 'rtl' }">
    <link href="${contextPath}/<spring:theme code="rtlstylesheet"/>" rel="stylesheet" type="text/css" >
</c:if>

<script src="${contextPath}/<spring:theme code="jquerysource"/>" type="text/javascript"></script>
<script src="${contextPath}/<spring:theme code="superfishsource"/>" type="text/javascript"></script>
</head>
<body>
<spring:htmlEscape defaultHtmlEscape="false">
<spring:escapeBody htmlEscape="false">
<div id="pageWrapper">

	<%@ include file="../headermain.jsp"%>

	<div id="page">

		<div id="pageContent">

			<div class="contentPanel">
				<div class="panelHeader">WISE Terms of Use & Privacy Policy</div>
				<div class="panelContent">
                    <div class="terms">
                        <div class="sectionContent">
                            <h2>Table of Contents</h2>
                            <ol>
                                <li><a href="#user-agreement">User Agreement</a></li>
                                <li><a href="#information-we-collect">Information We Collect</a></li>
                                <li><a href="#how-we-use-the-data">How We Use the Data</a></li>
                                <li><a href="#rules-of-use">Rules of Use</a></li>
                                <li><a href="#information-security">Information Security</a></li>
                                <li><a href="#state-and-federal-laws">State & Federal Laws</a></li>
                                <li><a href="#changes-and-updates">Changes & Updates</a></li>
                            </ol>
                        </div>
                        <hr />
                        <div class="sectionContent">
                            <h2 id="user-agreement">1. User Agreement</h2>
                            <p>We understand how important privacy is to our community, especially the teachers and students all over the world who use WISE resources in their educational endeavors. This policy explains what information we collect, how we use it, and what we do to keep it safe.</p>
                            <p>These Terms of Use and Privacy Policy constitute an agreement between you and the Web-based Inquiry Science Environment team that governs your use of wise.berkeley.edu and all associated services and websites (collectively "WISE"). The WISE team is a federally-funded research group headquartered at the University of California, Berkeley and is a non-profit organization.</p>
                            <p>Please read this document carefully. By using WISE you affirm that you have read, understood, and accepted the terms and conditions outlined here. If you do not agree with any of these conditions, please do not use WISE. If you have questions about this policy, please feel free to contact us at <a href="https://wise.berkeley.edu/contact/contactwise.html">https://wise.berkeley.edu/contact/contactwise.html</a>.</p>
                            <p style="font-weight: bold;">This policy was last updated on September 8, 2017.</p>
                        </div>

                        <div class="sectionContent">

                            <h2 id="information-we-collect">2. Information We Collect</h2>

                            <h4 id="personal-information">Personal Information</h4>
                            <p>In order to use some features of WISE, you will need to register by creating a user account. Creating an account is optional, but without an account you will not be able to create or publish curriculum projects or use WISE curricula to capture, store, review, or assess student work.</p>
                            <p>When creating a WISE teacher account, we ask you to provide your first and last name, email address, password, city, state, country, school name, school level, and school subjects. To create a WISE student account, we ask you to provide your first and last name, birth day and month, gender, and password, and choose a security question and answer.</p>
                            <p>To help ensure unauthorized use of your account, please keep your password secret. You are solely responsible for any use of your account, even if your account is used by another person. If any use of your account violates the Terms of Use, your account may be suspended or deleted.</p>
                            <p>If you believe that your account is no longer secure for any reason, please change your password. If you cannot access your account to change your password, contact us at <a href="https://wise.berkeley.edu/contact/contactwise.html">https://wise.berkeley.edu/contact/contactwise.html</a>.</p>

                            <h4 id="user-generated-content">User-Generated Content</h4>
                            <p>When students submit work in a WISE project run (while logged into their accounts), we save their responses to questions and any content they create within the WISE platform. Teachers can review the responses and artifacts students create in the WISE runs they own. Teachers can also grade student work and provide feedback. Scores and comments generated by teachers through the WISE platform are stored by WISE and can be viewed by the students they are sent to.</p>
                            <p>Curriculum projects that teachers author using the WISE platform are also saved and stored on our servers and may be published to a public project library that is accessible to other WISE users or visitors (with the owner's permission). Please note: Every WISE project hosted by the WISE site (whether it is shared in a public project library or not) is viewable by anyone using the internet via the project's unique URL.</p>
                            <p>Some sections or features of WISE may allow you to share information concerning your opinions, experiences, or preferences about WISE content or other topics. For example, you might be able to voluntarily review or rate WISE resources, answer user questions, designate favorite WISE projects, respond to surveys, share personal information or social media profile links to your public WISE profile, etc. Information you provide will be stored by WISE and may be shared with other WISE users either anonymously or not (as long as you provide permission). Unless otherwise expressly provided for in this policy, WISE shall have no liability to you or to any third party with respect to information submitted voluntarily by you via the WISE site.<p>

                            <h4 id="licensing">Licensing</h4>
                            <p>All projects that you create on or share to the WISE site shall be licensed under the Creative Commons Attribution-ShareAlike 4.0 license. For more information about the terms of this license, visit <a href="https://creativecommons.org/licenses/by-sa/4.0/" target="_blank">https://creativecommons.org/licenses/by-sa/4.0/</a>. The WISE team reserves the right to remove any content that, in the WISE team's discretion, is harmful to the community or violates the WISE Terms of Use. Harmful use includes spam or repeated commercial advertisement through projects, comments, or discussion posts, as well as inappropriate or offensive material.<p>
                            <p>You may link to or include outside resources or materials in your WISE projects or in any public parts of the WISE site, but it is your responsibility to obtain any necessary permission from the original owners and include any required attribution for your use of such content. WISE will not be held responsible for the unauthorized use by a WISE account holder of any third-party content in WISE projects or on any other sections of the WISE site.</p>

                            <h4 id="auto-generated-content">Auto-Generated Data</h4>
                            <p>When students and teachers use WISE, we save event data such as mouse clicks, time spent viewing pages, and other site interactions. This data is used for research purposes and for automated analyses and tools within WISE project runs. (See ‘How We Use the Data' for more information.)</p>

                            <h4 id="cookies">Cookies</h4>
                            <p>When you log in, WISE will ask your browser to put a "cookie" on your computer. This allows WISE to remember that you are logged in when you go to a different page. You can delete this cookie at any time through your browser's settings.</p>

                            <h4 id="google-analytics">Google Analytics</h4>
                            <p>WISE uses Google Analytics to help us understand our site usage. We use data like which pages you visit and clicks you make to help us improve the site.  Information collected and processed by Google Analytics includes user IP addresses, network locations, and geographic locations. Some browsers allow you to opt-out of the Google Analytics tracking by installing add-ons for your browser.</p>

                        </div>

                        <div class="sectionContent">
                            <h2 id="how-we-use-the-data">3. How We Use the Data</h2>
                            <p>We will not sell your information to anyone under any circumstances.</p>
                            <p>The WISE team uses de-identified user data to perform research. Researchers in the Graduate School of Education at The University of California at Berkeley and partner institutions may use de-identified data to perform data analyses and explore research topics. This data includes both user-generated and auto-generated content (see 'Information We Collect' section). Results of our research may be shared with educators and other researchers through conferences, journals, and other publications.</p>
                            <p>When generating and analyzing research data, student names are removed from all student work and replaced with identification numbers. This process of replacing names with identification numbers is performed automatically by the WISE platform when exporting research data and will be performed by teachers if any other student work (e.g., from other instructional materials) is to be analyzed by researchers.</p>
                            <p>Email addresses for teacher accounts are used for communication: (a) for support, and (b) for occasional public announcements from the WISE Team. Teacher email addresses are also used to facilitate resetting of passwords.</p>
                            <p>We do not ask students for their email address and do not share any student personal information with outside parties. Only the students' teachers can access student names and usernames within WISE (for purposes of reviewing student work, grading, sending feedback, etc). Each participating teacher will have access to his or her own students' project work and will not be able to see any information from other teachers' students. Teachers are also able to reset passwords for their students.</p>
                            <p>A small set of WISE administrators are able to access user account information and data for support and technical purposes. A few select researchers may access teacher accounts to view anonymized student work or support teachers in setting up and managing projects and runs.</p>
                            <p>We will not disclose your personally identifying information to any other parties except in the following cases: 1) In cases where you (or a legal guardian) have provided your express consent; 2) As required by applicable law; or 3) With the appropriate authorities (including schools, school districts, and law enforcement, when necessary) for the purposes of protecting the safety of users, other individuals, or the security of the site.</p>
                        </div>

                        <div class="sectionContent">
                            <h2 id="rules-of-use">4. Rules of Use</h2>
                            <p>WISE promotes and supports freedom of speech and expression and strives to create a welcoming, supportive, and productive community of educators and learners.</p>
                            <p>You may not use WISE to promote bigotry, discrimination, hatred, or violence against any individual or group. You may not expose any other person's identifying or contact information without that person's permission. Threatening, harassing, or intimidating any other person is not allowed and we reserve the right to remove any user-generated content that, in our discretion, does so. Projects, discussion posts, and user-profile pages containing offensive or sexually explicit material will not be tolerated and removed from the site immediately once brought to the attention of the WISE team.</p>
                            <p>You may not deliberately perform actions that are intended to negatively affect the WISE website or its users. You may not post links to harmful sites to download viruses or malware.</p>
                            <p>All user-generated content is provided as-is. The WISE Team is not responsible for the accuracy or reliability of any user-generated content available through WISE. The WISE Team does not endorse any views, opinions, or advice expressed in user-generated content. You agree to relieve the WISE Team of any and all liability arising from your user-generated content.</p>
                            <p>If you notice any offensive material or user-generated content that are in violation with the rules above, you must report them to the WISE Team using our contact form at <a href="https://wise.berkeley.edu/contact/contactwise.html">https://wise.berkeley.edu/contact/contactwise.html</a>.</p>
                        </div>

                        <div class="sectionContent">
                            <h2 id="information-security">5. Information Security</h2>
                            <p>The security of your information is important to us and we try to maintain a high standard for security by using technical safeguards. We will try our best to keep your data safe. However, no system can ever be 100% secure so we can not guarantee your personal information will be 100% safe from unauthorized access, either while stored on our site or our databases or while being transmitted to WISE over the internet.</p>
                            <p>You can help us protect against unauthorized access to your account by choosing a strong password and keeping your password secret at all times.</p>
                        </div>

                        <div class="sectionContent">
                            <h2 id="state-and-federal-laws">6. State & Federal Laws</h2>

                            <h4 id="ca-privacy-rights-for-minors">California Privacy Rights for Minors</h4>
                            <p>If you live in California and are under the age of 18 or the parent or legal guardian of a student under the age of 18, you may request the removal of your information from our site. When you request removal of your information, we will remove all your personal information to the best of our ability. Please note that we may not be able to remove all de-identified information or pieces of content stored in our databases and we may not remove content that we are required to retain under applicable federal and state laws. If you would like to request removal of your or your child's content or information, please contact us at https://wise.berkeley.edu/contact/contactwise.html for assistance or you can delete specific information from you or your child's account.</p>

                            <h4 id="ferpa">Family Educational Rights and Privacy Act (FERPA)</h4>
                            <p>We take care to be familiar with FERPA legislation and we take all possible measures to ensure your compliance with FERPA's requirements when you use WISE. We are confident that the protection of data outlined in this document satisfies its requirements.</p>
                        </div>

                        <div class="sectionContent">
                            <h2 id="changes-and-updates">7. Changes and Updates to the Terms of Use & Privacy Policy</h4>
                            <p>We may occasionally update or modify our Terms of Use and Privacy Policy. We will always display the date this document was last updated. If we make significant changes to these terms, we will display a prominent notice on the WISE home page and may notify users via registered email.  We encourage you to review this document periodically. By continuing to use WISE after any changes to this document, you acknowledge that you agree to the current version of these terms.</p>
                            <p>If you have any questions or concerns about this document or the WISE site, please contact us at <a href="https://wise.berkeley.edu/contact/contactwise.html">https://wise.berkeley.edu/contact/contactwise.html</a>. We can also be reached by mail at:</p>

                            <p>Web-based Inquiry Science Environment (WISE)<br />
                            4523 Tolman Hall<br />
                            Berkeley, CA 94720-1670</p>
                        </div>
                    </div>
				</div>
			</div>
		</div>
		<div style="clear: both;"></div>
	</div>   <!-- End of page-->

	<%@ include file="../footer.jsp"%>
</div>
</spring:escapeBody>
</spring:htmlEscape>
</body>
</html>
