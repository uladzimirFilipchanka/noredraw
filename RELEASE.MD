1. Make sure ~/.gradle/gradle.properties has appropriate config, i.e.
   * signing.keyId
   * signing.secretKeyRingFile=~/.gnupg/secring.gpg
   * ossrhUsername and ossrhPassword in case you fix direct publishing
2. GPG key is already distributed to Ubuntu Keyserver
3. Run ` ./gradlew publishMavenPublicationToMavenRepository`
3. `cd noredraw-core/build/staging`
4. `zip ./noredraw-core.zip -r io/`
5. Upload the zip to Sonatype, approve and release