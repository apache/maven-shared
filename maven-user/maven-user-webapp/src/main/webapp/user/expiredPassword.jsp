<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<%@ taglib uri="/webwork" prefix="ww" %>
<%@ taglib uri="http://www.extremecomponents.org" prefix="ec" %>
<%@ taglib uri="http://acegisecurity.org/authz" prefix="authz" %>
<html>
  <ww:i18n name="localization.MavenUser">
    <head>
        <title><ww:text name="user.expired.password.page.title"/></title>
    </head>
    <body>
      <div id="axial" class="h3">
        <h3><ww:text name="user.expired.password.section.title"/></h3>
        <div class="axial">
          <ww:form action="expiredPassword!doChangePassword" method="post" namespace="/user">
            <c:if test="${!empty actionErrors}">
              <div class="errormessage">
              <c:forEach items="${actionErrors}" var="actionError">
                <p><ww:text name="${actionError}"/></p>
              </c:forEach>
              </div>
            </c:if>
            <table>
              <tbody>
                <!-- 
                  <ww:hidden id="username" name="username" value="${username}"/>
                  <ww:label  id="username" label="%{getText('user.username')}" name="username" required="true"/>
                -->
                <ww:textfield id="username" label="%{getText('user.username')}" name="username" required="true"/>
                <ww:password id="oldPassword" label="%{getText('user.current.password')}" name="oldPassword" required="true"/>
                <ww:password id="password" label="%{getText('user.password')}" name="password" required="true"/>
                <ww:password id="confirmPassword" label="%{getText('user.confirm.password')}" name="confirmPassword" required="true"/>
              </tbody>
            </table>
            <div class="functnbar3">
              <ww:submit value="%{getText('save')}"/> <!-- todo: change to submit/cancel button -->
            </div>
          </ww:form>
        </div>
      </div>
    </body>
  </ww:i18n>
</html>
