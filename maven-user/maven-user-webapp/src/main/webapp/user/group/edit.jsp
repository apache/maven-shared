<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<%@ taglib uri="/webwork" prefix="ww" %>
<%@ taglib uri="http://www.extremecomponents.org" prefix="ec" %>
<%@ taglib uri="http://acegisecurity.org/authz" prefix="authz" %>
<script>
  function getData() {
    var addMode_field = document.getElementById('addMode_field');
    var id_field = document.getElementById('id_field');
    var name_field = document.getElementById('name_field');
    var description_field = document.getElementById('description_field');

    var addMode = document.getElementById('addMode');
    var id = document.getElementById('id');
    var name = document.getElementById('name');
    var description = document.getElementById('description');

    addMode.value = addMode_field.value;
    id.value = id_field.value;
    name.value = name_field.value;
    description.value = description_field.value;
  }
</script>
<html>
  <ww:i18n name="localization.MavenUser">
    <head>
      <ww:if test="addMode == true">
        <title><ww:text name="usergroup.add.page.title"/></title>
      </ww:if>
      <ww:else>
        <title><ww:text name="usergroup.edit.page.title"/></title>
      </ww:else>
    </head>
    <body>
      <div id="axial" class="h3">
        <ww:if test="addMode == true">
          <h3><ww:text name="usergroup.add.section.title"/></h3>
        </ww:if>
        <ww:else>
          <h3><ww:text name="usergroup.edit.section.title"/></h3>
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
                    <ww:form action="editGroup!doDeletePermission.action" method="post">
                      <ww:hidden id="addMode" name="addMode"/>
                      <ww:hidden id="id" name="id"/>
                      <ww:hidden id="name" name="name"/>
                      <ww:hidden id="description" name="description"/>
                      <input type="hidden" name="permissionName" value="<ww:property value="name"/>">
                      <ww:submit onclick="getData()" value="%{getText('delete')}"/>
                    </ww:form>
                  </td>
                </tr>
                </ww:iterator>
              </table>
            </div>
          <div id="h3">
            <ww:form action="editGroup!doGetAvailablePermissions.action" method="post">
              <ww:hidden id="addMode" name="addMode"/>
              <ww:hidden id="id" name="id"/>
              <ww:hidden id="name" name="name"/>
              <ww:hidden id="description" name="description"/>
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
