<#if parameters.before?exists>
${parameters.before}
</#if>
<#include "/${parameters.templateDir}/simple/submit.ftl" />
<#if parameters.after?exists>
${parameters.after}
</#if>

