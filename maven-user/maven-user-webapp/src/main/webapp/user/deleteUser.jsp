<%@ taglib uri="/webwork" prefix="ww" %>
<html>
  <ww:i18n name="localization.Continuum">
    <head>
      <title><ww:text name="deleteUser.page.title"/></title>
    </head>
    <body>
      <div id="axial" class="h3">
        <h3><ww:text name="deleteUser.section.title"/></h3>
        <div class="warningmessage">
          <p>
            <strong>
              <ww:text name="deleteUser.confirmation.message">
                <ww:param><ww:property value="username"/></ww:param>
              </ww:text>
            </strong>
          </p>
        </div>
        <div class="functnbar3">
          <ww:form action="deleteUser.action" method="post">
            <ww:hidden name="accountId"/>
            <ww:submit value="%{getText('delete')}"/> <!-- todo: change to submit/cancel button -->
          </ww:form>
        </div>
      </div>
    </body>
  </ww:i18n>
</html>
