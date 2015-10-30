# Contains Resources for releasing to Public Maven Centreal Repository
 
### Release mode
 The `release.sh` script, executed in travis build, checks whether the branch that was commited to conforms to the branch `${BRANCH_TRIGGERING_RELEASE}` that is supposed to trigger releases.
 If true it starts to prepare and perform the actual release from `${BRANCH_TO_RELEASE}` which usually corresponds to `master`.
 When the build succeeds the artifacts to release have been uploaded to oss.sonatype.org. In order to finally release and make the artifacts public, it needs to be "closed" and "released". 
 To achieve that login to https://oss.sonatype.org/, navigate to "Staging Repositories", find the artifact(s) and perform the close and release tasks. For login credentials ask
 your Product Owner of trust.
 
### Standard Snapshot mode
 If the commit that caused travis execution does not match `${BRANCH_TRIGGERING_RELEASE}` a snapshot release from the current commit is deployed to the central repositry by `mvn deploy -Prelease ..`
 
## Strings that are encrypted by and for use of travis-ci
 * `NEXUS_OSSRH_USER` - The (sonatype.org - JIRA) User that is permitted to release artifacts by groupId de.asideas 
 * `NEXUS_OSSRH_PWD` - The user's password
 * `TRAVIS_GITHUB_TK` - a developer's github token to enable maven release plugin to push tags to the git repo
 * `GPG_ENCR_KEY` - The encryption key that is used to encrypt GPG pubring and secring files, required for signing artifacts to be released.
 * `GPG_PASSPHRASE` - for usage of gpg keys by release plugin

## Info on how to handle GPG signing keys

..can be found here: http://central.sonatype.org/pages/working-with-pgp-signatures.html