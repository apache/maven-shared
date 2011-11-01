assert (new File( basedir, "verify.groovy" ).exists())
System.out.println("foo="+context.get("foo"));
return true
