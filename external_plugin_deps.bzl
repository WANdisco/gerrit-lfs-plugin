load("//tools/bzl:maven_jar.bzl", "GERRIT", "MAVEN_CENTRAL", "MAVEN_LOCAL", "maven_jar")

# NOTE !!!!!!!!!!!!!!
# This is only here to allow standalone building, when building as part of the parent gerrit project
# it will be using the main WORKSPACE definitions instead, so be careful... updating it here isn't really
# what is used in a production release.war package!!
JGIT_VERSION = "5.1.12-WDv1-TC5-SNAPSHOT"
# WANdisco maven assets
_GERRIT_GITMS_VERSION = "1.1.0.1-TC12-SNAPSHOT"

# Default repo being used...
REPO = MAVEN_CENTRAL

# When building only as part of gerrit release was I could import this definition, but its not in maven_jar
# used by plugins, and we would need to upload a changed one to support it, As such just writing it manually here
WANDISCO_ASSETS = "WANDISCO:"


def external_plugin_deps():
    maven_jar(
        name = "jgit-http-apache",
        artifact = "org.eclipse.jgit:org.eclipse.jgit.http.apache:" + JGIT_VERSION,
#        sha1 = "92ad62799dd7eb02cc8805f2e297714cc3bb5149",
        repository = WANDISCO_ASSETS,
        unsign = True,
        exclude = [
            "about.html",
            "plugin.properties",
        ],
    )

    maven_jar(
        name = "jgit-lfs",
        artifact = "org.eclipse.jgit:org.eclipse.jgit.lfs:" + JGIT_VERSION,
#        sha1 = "bb4972c6e127c615c4b9183f95414ff2a20916cf",
        repository = WANDISCO_ASSETS,
        unsign = True,
        exclude = [
            "about.html",
            "plugin.properties",
        ],
    )

    maven_jar(
        name = "jgit-lfs-server",
        artifact = "org.eclipse.jgit:org.eclipse.jgit.lfs.server:" + JGIT_VERSION,
#        sha1 = "b2d1e48f9daddc09e072e422c151384e845c1612",
        repository = WANDISCO_ASSETS,
        unsign = True,
        exclude = [
            "about.html",
            "plugin.properties",
        ],
    )

    maven_jar(
        name = "joda_time",
        artifact = "joda-time:joda-time:2.9.9",
        sha1 = "f7b520c458572890807d143670c9b24f4de90897",
    )

    maven_jar(
        name = "gerrit-gitms-interface",
        artifact = "com.wandisco:gerrit-gitms-interface:" + _GERRIT_GITMS_VERSION,
        repository = WANDISCO_ASSETS,
        #    sha1 = 213e4234
    )
