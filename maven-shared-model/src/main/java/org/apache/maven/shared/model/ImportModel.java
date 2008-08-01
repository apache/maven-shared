package org.apache.maven.shared.model;

import java.util.List;

public interface ImportModel {

    String getId();

    List<ModelProperty> getModelProperties();
}
