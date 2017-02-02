# redsys-java-api

Refactory for [API JAVA Version 1.3](http://www.redsys.es/#descargas) ([zip](http://www.redsys.es/comercio-electronico/modulos/API_JAVA.zip)).

Inside the zip file you have:

- `apiSha256.jar`. Main Redsýs library. It also contains the Java source code.
- `bcprov-jdk15on-1.4.7.jar`. The Bouncy Castle Crypto package.
- `commons-codec-1.3.jar`. Commons Codec.
- `org.json.jar`. JSON Library.

Based on the Java source code, I decided to generate a Maven project with a new API version with the same signatures but simplifying the Java source code:

- Using Java 8. We are able to take out the `commons-codec` library.
- A new `Utils` class joining the same methods between `ApiMacSha256` and `ApiWsMacSha256` classes.

