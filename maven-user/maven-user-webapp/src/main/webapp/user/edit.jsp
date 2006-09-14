<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<%@ taglib uri="/webwork" prefix="ww" %>
<%@ taglib uri="http://www.extremecomponents.org" prefix="ec" %>
<%@ taglib uri="http://acegisecurity.org/authz" prefix="authz" %>
<script>
  function getData() {
    var addMode_field = document.getElementById('addMode_field');
    var id_field = document.getElementById('id_field');
    var username_field = document.getElementById('username_field');
    var password_field = document.getElementById('password_field');
    var confirm_password_field = document.getElementById('confirm_password_field');
    var locked_field = document.getElementById('locked_field');
    var email_field = document.getElementById('email_field');

    var addMode = document.getElementById('addMode');
    var id = document.getElementById('id');
    var username = document.getElementById('username');
    var password = document.getElementById('password');
    var confirmPassword = document.getElementById('confirmPassword');
    var locked = document.getElementById('locked');
    var email = document.getElementById('email');

    addMode.value = addMode_field.value;
    id.value = id_field.value;
    username.value = username_field.value;
    password.value = password_field.value;
    confirmPassword.value = confirm_password_field.value;
    locked.value = locked_field.value;
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
        </div>
      </div>
    </body>
  </ww:i18n>
</html>
