<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>

<html>
  <head>
    <title><decorator:title/></title>
    <decorator:head/>
  </head>

  <body onload="focus()" marginwidth="0" marginheight="0" class="composite">
    <h1>Hello (none)</h1>
    <decorator:body/>
  </body>
</html>
