# Contains Resources for releasing to Public Maven Centreal Repository
 
 The `release.sh` script checks whether the branch that was commited to conforms to the branch that is supposed to trigger releases.
 If true it starts to prepare and perform the actual release.
 
## Strings that are encrypted by and for use of travis-ci
 * `NEXUS_OSSRH_USER` - The (sonatype.org - JIRA) User that is permitted to release artifacts by groupId de.asideas 
 * `NEXUS_OSSRH_PWD` - The user's password
 * `TRAVIS_GITHUB_TK` - a developer's github token to enable maven release plugin to push tags to the git repo
 * `GPG_ENCR_KEY` - The encryption key that is used to encrypt GPG pubring and secring files, required for signing artifacts to be released.
 * `GPG_PASSPHRASE` - for usage of gpg keys by release plugin

## Info on how to handle GPG signing keys

..can be found here: http://central.sonatype.org/pages/working-with-pgp-signatures.html