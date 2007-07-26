package org.apache.maven.shared.io.location;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public interface Location
{

    File getFile() throws IOException;

    void open() throws IOException;

    void close();

    int read( ByteBuffer buffer ) throws IOException;

    int read( byte[] buffer ) throws IOException;

    InputStream getInputStream() throws IOException;

    String getSpecification();

}
