package org.apache.maven.shared.repository.internal;

import org.apache.maven.shared.repository.RepositoryManager;
import org.codehaus.plexus.component.annotations.Component;
import org.sonatype.aether.repository.LocalRepository;

/**
 * 
 */
@Component( role = RepositoryManager.class, hint = "maven302" )
public class Maven302RepositoryManager
    extends Maven30RepositoryManager
{

    /**
     * Aether-1.9+ (i.e. M3.0.2+) expects "default", not "enhanced" as repositoryType
     */
    @Override
    protected String resolveRepositoryType( LocalRepository localRepository )
    {
        String repositoryType;
        if ( "enhanced".equals( localRepository.getContentType() ) )
        {
            repositoryType = "default";
        }
        else
        {
            // this should be "simple"
            repositoryType = localRepository.getContentType();
        }
        return repositoryType;
    }
}
