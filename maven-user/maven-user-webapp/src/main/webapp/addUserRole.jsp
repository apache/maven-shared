<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/webwork" prefix="ww" %>
<%@ taglib uri="/tld/extremecomponents" prefix="ec" %>
<html>
  <ww:i18n name="localization.Continuum">
    <head>
      <title><ww:text name="role.page.title"/></title>
    </head>
    <body>
      <div id="h3">
        <h3><ww:text name="role.section.title"/></h3>
        <ww:set name="permissions" value="permissions" scope="session"/>
        <ww:set name="availablePermissions" value="availablePermissions" scope="request"/>
        <ec:table items="availablePermissions"
                  var="availablePermission"
                  showExports="false"
                  showPagination="false"
                  showStatusBar="false"
                  filterable="false"
                  sortable="false">
          <ec:row>
            <ec:column property="name" title="role.rolename"/>
            <ec:column property="actions" title="&nbsp;">
              <c:url var="editUserUrl" value="/editUser!doAddPermission.action">
                <c:param name="permissionName" value="${availablePermission.name}"/>
              </c:url>
              <input type="button" onclick="window.location.href='<c:out value='${editUserUrl}'/>'" value=<ww:text name="add"/>>
            </ec:column>
          </ec:row>
        </ec:table>
      </div>
    </body>
  </ww:i18n>
</html>
