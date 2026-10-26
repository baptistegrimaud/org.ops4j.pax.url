package org.ops4j.pax.url.mvn.internal.config;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class MavenRepositoryURLTest
{
    @Test
    public void testMavenRepositoryURL() throws MalformedURLException
    {
        File localrepo = new File( "my dir/repository" ).getAbsoluteFile();
        String uri = localrepo.toURI().toASCIIString();
        MavenRepositoryURL mavenRepo = new MavenRepositoryURL( uri + "@id=repository1" );
        assertEquals( localrepo, mavenRepo.getFile() );

        localrepo = new File( "myédir/repository" ).getAbsoluteFile();
        uri = localrepo.toURI().toASCIIString();
        mavenRepo = new MavenRepositoryURL( uri + "@id=repository1" );
        assertEquals( localrepo, mavenRepo.getFile() );

        String spec = "file:repository1/@id=repository1";
        mavenRepo = new MavenRepositoryURL( spec );
        assertEquals( new File( "repository1/" ), mavenRepo.getFile() );

        spec = "file:repositories/repository1/@id=repository1";
        mavenRepo = new MavenRepositoryURL( spec );
        assertEquals( new File( "repositories/repository1/" ), mavenRepo.getFile() );

        spec = "file:somewhere/localrepository\\";
        mavenRepo = new MavenRepositoryURL( spec + "@id=repository1" );
        assertEquals( new File( "somewhere/localrepository" ), mavenRepo.getFile() );
        assertEquals( URI.create( "file:somewhere/localrepository/" ), mavenRepo.getURI() );

        spec = "file:repository1%5C";
        mavenRepo = new MavenRepositoryURL( spec + "@id=repository1" );
        assertEquals( new File( "repository1/" ), mavenRepo.getFile() );
        assertEquals( URI.create( "file:repository1/" ), mavenRepo.getURI() );

        spec = "file:somewhere/localrepository%5C";
        mavenRepo = new MavenRepositoryURL( spec + "@id=repository1" );
        assertEquals( URI.create( "file:somewhere/localrepository/" ), mavenRepo.getURI() );

        spec = "file:repository1%5C";
        mavenRepo = new MavenRepositoryURL( spec + "@id=repository1" );
        assertEquals( URI.create( "file:repository1/" ), mavenRepo.getURI() );

        spec = "file:r%C3%A9positories%20/r%C3%A9pository1";
        mavenRepo = new MavenRepositoryURL( spec + "@id=repository1" );
        File expected = new File( "répositories /répository1/" );
        assertEquals( expected, mavenRepo.getFile() );

        spec = "file:r%C3%A9positories /r%C3%A9pository1";
        mavenRepo = new MavenRepositoryURL( spec + "@id=repository1" );
        expected = new File( "répositories /répository1/" );
        assertEquals( expected, mavenRepo.getFile() );
    }

    @Test
    public void testCredentialsAreRedacted() throws MalformedURLException
    {
        MavenRepositoryURL mavenRepo = new MavenRepositoryURL( "https://user:secret@repo.example.com/maven2@id=repository1" );
        assertEquals( "https://***@repo.example.com/maven2/ {id=repository1}", mavenRepo.toString() );
        assertEquals( URI.create( "https://user:secret@repo.example.com/maven2/" ), mavenRepo.getURI() );

        mavenRepo = new MavenRepositoryURL( "https://user%40example.com:secret@repo.example.com/maven2@id=repository1" );
        assertFalse( mavenRepo.toString().contains( "secret" ) );
        assertFalse( mavenRepo.toString().contains( "user" ) );
    }

    @Test
    public void testRedact()
    {
        assertEquals( "https://***@repo.example.com/maven2/", MavenRepositoryURL.redact( "https://user:secret@repo.example.com/maven2/" ) );
        assertEquals( "https://***@repo.example.com:8443/maven2/", MavenRepositoryURL.redact( "https://user@repo.example.com:8443/maven2/" ) );
        assertEquals( "https://***@repo.example.com/maven2/@id=repository1", MavenRepositoryURL.redact( "https://user:p@ss@repo.example.com/maven2/@id=repository1" ) );
        assertEquals( "https://repo.example.com/maven2/@id=repository1", MavenRepositoryURL.redact( "https://repo.example.com/maven2/@id=repository1" ) );
        assertEquals( "file:repository1/@id=repository1", MavenRepositoryURL.redact( "file:repository1/@id=repository1" ) );
    }

}
