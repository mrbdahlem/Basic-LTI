# Basic-LTI

## What is it?
An authentication system for Spring Web MVC that can authenticate LTI Launch requests without using the
deprecated Spring-Security-Oauth

## How to use
Include this module as a dependency in your ```pom.xml``` file:

```
    <repositories>
        <repository>
            <id>Basic-LTI-mvn-repo</id>
            <url>https://github.com/mrbdahlem/Basic-LTI/raw/mvn-repo/</url>
            <snapshots>
                <enabled>true</enabled>
                <updatePolicy>always</updatePolicy>
            </snapshots>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>run.mycode</groupId>
            <artifactId>basiclti</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
    </dependencies>
```

You'll need to download and ```mvn install``` this repo for now to add it to your local maven repository.

You can see a demo of how this authentication system is used in the [Basic LTI Demo](https://github.com/mrbdahlem/Basic-Lti-Demo).

You will need to provide a ```KeyService``` as shown in the Basic LTI Demo that can retrieve consumer key/shared secret pairs.
