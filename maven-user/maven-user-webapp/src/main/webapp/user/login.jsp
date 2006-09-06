<%@ taglib uri="/webwork" prefix="ww" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
  <ww:i18n name="localization.MavenUser">
    <head>
        <title><ww:text name="login.page.title"/></title>
    </head>
    <body>
      <div id="axial" class="h3">
        <h3><ww:text name="login.section.title"/></h3>

        <%-- if login_error parameter is present authentication didn't succeed --%>
        <c:if test="${not empty param.login_error}">
          <div class="errormessage">
            <ww:text name="login.bad_login_password"/>
          </div>
        </c:if>

        <div class="axial">
        <ww:form action="<c:url value='/j_acegi_security_check'/>" method="post">
          <table>
            <tbody>
              <ww:textfield label="%{getText('login.username')}" name="j_username" required="true"/>
              <ww:password label="%{getText('login.password')}" name="j_password" required="true"/>
              <%-- TODO integrate remember me with Acegi 
              <ww:checkbox label="%{getText('login.rememberMe')}" name="rememberMe" value="rememberMe" fieldValue="true"/>
              --%>
            </tbody>
          </table>
          <div class="functnbar3">
            <input name="submit" type="submit">
          </div>
        </ww:form>
      </div>
      </div>
    </body>
  </ww:i18n>
</html>
