<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/webwork" prefix="ww" %>
<%@ taglib uri="http://www.extremecomponents.org" prefix="ec" %>
<html>
  <ww:i18n name="localization.MavenUser">
    <head>
      <title><ww:text name="users.page.title"/></title>
    </head>
    <body>
      <div id="h3">
        <h3><ww:text name="users.section.title"/></h3>
        <ww:set name="users" value="users" scope="request"/>
        <ec:table items="users"
                  var="user"
                  showExports="false"
                  showPagination="false"
                  showStatusBar="false"
                  filterable="false">
          <ec:row>
            <ec:column property="username" title="user.username"/>
            <ec:column property="email" title="user.email"/>
            <ec:column property="actions" title="&nbsp;">

              <c:url var="editUrl" value="/user/edit!doEdit.action">
                <c:param name="accountId" value="${user.accountId}"/>
                <c:param name="username" value="${user.username}"/>
              </c:url>

              <a href="<c:out value='${editUrl}'/>"><ww:text name="edit"/></a>

              <c:url var="deleteUrl" value="/user/delete!doDelete.action">
                <c:param name="accountId" value="${user.accountId}"/>
                <c:param name="username" value="${user.username}"/>
              </c:url>

              <a href="<c:out value='${deleteUrl}'/>"><ww:text name="delete"/></a>
            </ec:column>
          </ec:row>
        </ec:table>
      </div>
      <div class="functnbar3">
        <ww:form action="edit!doAdd.action" method="post">
          <ww:submit value="%{getText('add')}"/>
        </ww:form>
    </div>
    </body>
  </ww:i18n>
</html>
