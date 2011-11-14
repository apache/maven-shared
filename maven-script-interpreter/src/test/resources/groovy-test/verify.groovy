if ( !( basedir instanceof File ) )
{
    println "Global script variable not defined: basedir or not a File"
    throw new RuntimeException("Global script variable not defined: basedir or not a File");
}
assert (new File( basedir, "verify.groovy" ).exists())

if ( !( context instanceof Map ) )
{
    println "Global script variable not defined: context or not a Map"
    throw new RuntimeException("Global script variable not defined: context or not a Map");
}

System.out.println("foo="+context.get("foo"));

if (binding.variables.containsKey("globalVar")) System.out.println("globalVar="+globalVar);

return true
