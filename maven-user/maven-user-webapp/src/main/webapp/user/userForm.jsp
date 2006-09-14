<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<%@ taglib uri="/webwork" prefix="ww" %>
<%@ taglib uri="http://www.extremecomponents.org" prefix="ec" %>
<%@ taglib uri="http://acegisecurity.org/authz" prefix="authz" %>

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
      <ww:hidden id="addMode" name="addMode"/>
      <ww:hidden id="addMode_field" name="addMode"/>
      <ww:hidden id="id_field" name="id"/>
      <ww:if test="addMode == true">
        <ww:textfield id="username_field" label="%{getText('user.username')}" name="username" required="true"/>
      </ww:if>
      <ww:else>
        <ww:hidden id="username_field" name="username"/>
        <ww:label label="%{getText('user.username')}" name="username" required="true"/>
      </ww:else>
      <ww:textfield id="fullName_field" label="%{getText('user.fullname')}" name="fullName"/>
      <ww:password id="password_field" label="%{getText('user.password')}" name="password" required="true"/>
      <ww:password id="confirm_password_field" label="%{getText('user.confirm.password')}" name="confirmPassword" required="true"/>
      <ww:textfield id="email_field" label="%{getText('user.email')}" name="email" required="true"/>
      <ww:if test="addMode == false">
        <authz:authorize ifAnyGranted="ROLE_admin,ROLE_manageUsers">
          <ww:select label="%{getText('user.group.header')}"
                         list="allGroups"
                         name="groups"
                         value="selectedGroups"
                         listKey="id"
                         listValue="name"
                         multiple="true"
                         size="6"
                         required="true"/>
        </authz:authorize>
      </ww:if>
      <authz:authorize ifAnyGranted="ROLE_admin,ROLE_manageUsers">
        <ww:checkbox id="locked_field" label="Locked" name="locked"/>
      </authz:authorize>
    </tbody>
  </table>
  <div class="functnbar3">
    <ww:submit value="%{getText('save')}"/> <!-- todo: change to submit/cancel button -->
  </div>
</ww:form>

