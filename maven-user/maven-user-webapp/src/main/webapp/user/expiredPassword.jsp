<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<%@ taglib uri="/webwork" prefix="ww" %>
<%@ taglib uri="http://www.extremecomponents.org" prefix="ec" %>
<html>
  <ww:i18n name="localization.MavenUser">
    <head>
      <title><ww:text name="user.expired.password.page.title"/></title>
    </head>
    <body>
      <div id="axial" class="h3">
        <h3><ww:text name="user.expired.password.section.title"/></h3>
<%--
        <ww:include value="userForm.jsp">
          <ww:param name="addMode" value="${addMode}"/>
        </ww:include>
--%>
      </div>
    </body>
  </ww:i18n>
</html>
