<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<%@ taglib uri="/webwork" prefix="ww" %>
<%@ taglib uri="http://www.extremecomponents.org" prefix="ec" %>
<%@ taglib uri="http://acegisecurity.org/authz" prefix="authz" %>
<script>
  function getData() {
    var addMode_field = document.getElementById('addMode_field');
    var accountId_field = document.getElementById('accountId_field');
    var username_field = document.getElementById('username_field');
    var password_field = document.getElementById('password_field');
    var confirm_password_field = document.getElementById('confirm_password_field');
    var email_field = document.getElementById('email_field');

    var addMode = document.getElementById('addMode');
    var accountId = document.getElementById('accountId');
    var username = document.getElementById('username');
    var password = document.getElementById('password');
    var confirmPassword = document.getElementById('confirmPassword');
    var email = document.getElementById('email');

    addMode.value = addMode_field.value;
    accountId.value = accountId_field.value;
    username.value = username_field.value;
    password.value = password_field.value;
    confirmPassword.value = confirm_password_field.value;
    email.value = email_field.value;
  }
</script>
<html>
  <ww:i18n name="localization.MavenUser">
    <head>
      <ww:if test="addMode == true">
        <title><ww:text name="user.add.page.title"/></title>
      </ww:if>
      <ww:else>
        <title><ww:text name="user.edit.page.title"/></title>
      </ww:else>
    </head>
    <body>
      <div id="axial" class="h3">
        <ww:if test="addMode == true">
          <h3><ww:text name="user.add.section.title"/></h3>
        </ww:if>
        <ww:else>
          <h3><ww:text name="user.edit.section.title"/></h3>
        </ww:else>
        <div class="axial">
          <ww:include value="userForm.jsp">
            <ww:param name="addMode" value="${addMode}"/>
          </ww:include>
        <authz:authorize ifAnyGranted="ROLE_admin,ROLE_manageUsers">
        <ww:if test="addMode == false">
          <div id="h3">
            <h3><ww:text name="role.section.title"/></h3>
            <ww:set name="permissions" value="permissions" scope="session"/>
              <table>
                <tr>
                  <td><ww:text name="role.rolename"/></td>
                  <td>&nbsp;</td>
                </tr>
                <ww:iterator value="permissions">
                <tr>
                  <td><ww:property value="name"/></td>
                  <td>
                    <ww:form action="edit!doDeletePermission.action" method="post">
                      <ww:hidden id="addMode" name="addMode"/>
                      <ww:hidden id="accountId" name="accountId"/>
                      <ww:hidden id="username" name="username"/>
                      <ww:hidden id="password" name="password"/>
                      <ww:hidden id="email" name="email"/>
                      <input type="hidden" name="permissionName" value="<ww:property value="name"/>">
                      <ww:submit onclick="getData()" value="%{getText('delete')}"/>
                    </ww:form>
                  </td>
                </tr>
                </ww:iterator>
              </table>
            </div>
          <div id="h3">
            <ww:form action="edit!doGetAvailablePermissions.action" method="post">
              <ww:hidden id="addMode" name="addMode"/>
              <ww:hidden id="accountId" name="accountId"/>
              <ww:hidden id="username" name="username"/>
              <ww:hidden id="password" name="password"/>
              <ww:hidden id="email" name="email"/>
              <ww:submit onclick="getData()" value="%{getText('add')}"/>
            </ww:form>
          </div>
        </ww:if>
        </authz:authorize>
        </div>
      </div>
    </body>
  </ww:i18n>
</html>
