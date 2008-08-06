package org.apache.maven.shared.model;

import java.util.List;

public interface ModelPropertyTransformer {

    List<ModelProperty> transform(List<ModelProperty> modelProperties);

    String getBaseUri();
}
