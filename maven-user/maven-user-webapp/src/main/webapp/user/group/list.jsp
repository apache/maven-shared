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
        <h3><ww:text name="usergroups.section.title"/></h3>
        <ww:set name="usergroups" value="usergroups" scope="request"/>
        <ec:table items="usergroups"
                  var="usergroup"
                  showExports="false"
                  showPagination="false"
                  showStatusBar="false"
                  filterable="false">
          <ec:row>
            <ec:column property="name" title="usergroup.name"/>
            <ec:column property="description" title="usergroup.description"/>
            <ec:column property="actions" title="&nbsp;">

              <c:url var="editUrl" value="/user/group/edit!doEdit.action">
                <c:param name="addMode" value="false"/>
                <c:param name="id" value="${usergroup.id}"/>
                <c:param name="name" value="${usergroup.name}"/>
              </c:url>

              <a href="<c:out value='${editUrl}'/>"><ww:text name="edit"/></a>

              <c:url var="deleteUrl" value="/user/group/delete!doDelete.action">
                <c:param name="id" value="${usergroup.id}"/>
                <c:param name="name" value="${usergroup.name}"/>
              </c:url>

              <a href="<c:out value='${deleteUrl}'/>"><ww:text name="delete"/></a>
            </ec:column>
          </ec:row>
        </ec:table>
      </div>
      <div class="functnbar3">
        <ww:form action="edit!doAdd.action" method="post">
          <ww:hidden name="addMode" value="true"/>
          <ww:hidden name="name" value=""/>
          <ww:submit value="%{getText('add')}"/>
        </ww:form>
    </div>
    </body>
  </ww:i18n>
</html>
