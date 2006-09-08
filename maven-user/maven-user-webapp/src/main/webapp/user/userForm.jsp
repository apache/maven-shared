<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<%@ taglib uri="/webwork" prefix="ww" %>
<%@ taglib uri="http://www.extremecomponents.org" prefix="ec" %>
<ww:form action="saveAccount.action" method="post">
  <c:if test="${!empty actionErrors}">
    <div class="errormessage">
    <c:forEach items="${actionErrors}" var="actionError">
      <p><ww:text name="${actionError}"/></p>
    </c:forEach>
    </div>
  </c:if>
  <table>
    <tbody>
      <ww:hidden id="addMode_field" name="addMode"/>
      <ww:hidden id="accountId_field" name="accountId"/>
      <ww:if test="addMode == true">
        <ww:textfield id="username_field" label="%{getText('user.username')}" name="username" required="true"/>
      </ww:if>
      <ww:else>
        <ww:hidden id="username_field" name="username"/>
        <ww:label label="%{getText('user.username')}" name="username" required="true"/>
      </ww:else>
      <ww:password id="password_field" label="%{getText('user.password')}" name="password" required="true"/>
      <ww:password id="confirm_password_field" label="%{getText('user.confirm.password')}" name="confirmPassword" required="true"/>
      <ww:textfield id="email_field" label="%{getText('user.email')}" name="email" required="true"/>
    </tbody>
  </table>
  <div class="functnbar3">
    <ww:submit value="%{getText('save')}"/> <!-- todo: change to submit/cancel button -->
  </div>
</ww:form>
