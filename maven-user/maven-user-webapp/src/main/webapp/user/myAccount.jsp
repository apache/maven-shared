<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<%@ taglib uri="/webwork" prefix="ww" %>
<%@ taglib uri="http://www.extremecomponents.org" prefix="ec" %>
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
        <ww:include value="userForm.jsp">
          <ww:param name="addMode" value="${addMode}"/>
        </ww:include>
      </div>
    </body>
  </ww:i18n>
</html>
