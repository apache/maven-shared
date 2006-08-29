<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://acegisecurity.org/authz" prefix="authz" %>

<%@ page import="org.acegisecurity.context.SecurityContextHolder" %>
<%@ page import="org.acegisecurity.Authentication" %>
<%@ page import="org.acegisecurity.GrantedAuthority" %>
<%@ page import="org.acegisecurity.adapters.AuthByAdapter" %>

<% 
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) { %>
			Authentication object is of type: <%= auth.getClass().getName() %><BR><BR>
			Authentication object as a String: <%= auth.toString() %><BR><BR>
			
			Authentication object holds the following granted authorities:<BR><BR>
<%			GrantedAuthority[] granted = auth.getAuthorities();
			for (int i = 0; i < granted.length; i++) { %>
				<%= granted[i].toString() %> (getAuthority(): <%= granted[i].getAuthority() %>)<BR>
<%			}

			if (auth instanceof AuthByAdapter) { %>
				<BR><B>SUCCESS! Your container adapter appears to be properly configured!</B><BR><BR>
<%			} else { %>
				<BR><B>SUCCESS! Your web filters appear to be properly configured!</B><BR>
<%			}
			
		} else { %>
			Authentication object is null.<BR>
			This is an error and your Acegi Security application will not operate properly until corrected.<BR><BR>
<%		}
%>

<hr/>

<%-- other way to access the info using taglibs --%>

  <c:set var="authentication" value="${sessionScope['ACEGI_SECURITY_CONTEXT'].authentication}"/>
  <c:set var="user" value="${authentication.principal}" scope="session"/>

  user: <c:out value="${user.username}"/><br/>
  Roles: 
    <ul>
      <c:forEach var="item" items="${user.authorities}">
        <li><c:out value="${item.authority}"/></li>
      </c:forEach>
    </ul>

<hr/>

<%-- Yet another way to access the info using Acegi taglib --%>

  User: <authz:authentication operation="username"/>
