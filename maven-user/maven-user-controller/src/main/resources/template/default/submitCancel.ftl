<#include "/${parameters.templateDir}/simple/submit.ftl" />
<#if parameters.cancel?exists>
&nbsp;<input type="button" name="Cancel" value="${parameters.cancel}" onClick="history.back()"/>
</#if>

