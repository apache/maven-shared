<%@ taglib uri="/webwork" prefix="ww" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<html>
  <ww:i18n name="localization.MavenUser">
    <head>
      <title><ww:text name="user.change.password.page.title"/></title>
    </head>
    <body>
      <div id="axial" class="h3">
        <h3><ww:text name="user.change.password.section.title"/></h3>
        <div class="axial">
          <ww:form action="changeUserPassword.action" method="post">
            <c:if test="${!empty actionErrors}">
              <div class="errormessage">
                <c:forEach items="${actionErrors}" var="actionError">
                  <p><ww:text name="${actionError}"/></p>
                </c:forEach>
              </div>
            </c:if>
            <table>
              <tbody>
                <ww:hidden id="accountId_field" name="accountId"/>
                <ww:password id="current_password_field" label="%{getText('user.current.password')}" name="currentPassword" required="true"/>
                <ww:password id="new_password_field" label="%{getText('user.new.password')}" name="newPassword" required="true"/>
                <ww:password id="confirm_password_field" label="%{getText('user.confirm.password')}" name="confirmPassword" required="true"/>
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
