<%@ taglib uri="/webwork" prefix="ww" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>

<c:choose>
  <c:when test="${empty param.id}">
    <c:set var="id" value="unknown"/>
  </c:when>
  <c:otherwise>
    <c:set var="id" value="${param.id}"/>
  </c:otherwise>
</c:choose>

<html>
<ww:i18n name="localization.MavenUser">
  <head>
    <title><ww:text name="error.${id}.title"/></title>
  </head>

  <body>
  <div id="axial" class="h3">
    <h3><ww:text name="error.${id}.title"/></h3>
    <div class="errormessage">
      <ww:text name="error.${id}.detail"/>
    </div>
  </div>
  </body>
</ww:i18n>
</html>
