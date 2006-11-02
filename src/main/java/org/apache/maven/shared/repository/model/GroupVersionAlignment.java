package org.apache.maven.shared.repository.model;

import java.util.List;

public interface GroupVersionAlignment
{

    String getId();

    List getExcludes();

    String getVersion();

}
