load("//tools/bzl:maven_jar.bzl", "GERRIT", "MAVEN_CENTRAL", "MAVEN_LOCAL", "maven_jar")

# NOTE !!!!!!!!!!!!!!
# This is only here to allow standalone building, when building as part of the parent gerrit project
# it will be using the main WORKSPACE definitions instead, so be careful... updating it here isn't really
# what is used in a production release.war package!!
JGIT_VERSION = "5.1.15-WDv1"

# WANdisco maven assets
_GERRIT_GITMS_VERSION = "1.1.1.1"

# Default repo being used...
REPO = MAVEN_CENTRAL

# When building only as part of gerrit release was I could import this definition, but its not in maven_jar
# used by plugins, and we would need to upload a changed one to support it, As such just writing it manually here
WANDISCO_ASSETS = "WANDISCO:"

def external_plugin_deps():
    maven_jar(
        name = "jgit-http-apache",
        artifact = "org.eclipse.jgit:org.eclipse.jgit.http.apache:" + JGIT_VERSION,
        sha1 = "4f4608f469daf078b7fe1851b97849fb0d2be845",
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
        sha1 = "731f318bf1fa43b2968cccf21ecd545f21e508d6",
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
        sha1 = "3896fa684ae4bafda596ca2291b3089566ed452a",
        repository = WANDISCO_ASSETS,
        unsign = True,
        exclude = [
            "about.html",
            "plugin.properties",
        ],
    )

    maven_jar(
        name = "gerrit-gitms-interface",
        artifact = "com.wandisco:gerrit-gitms-interface:" + _GERRIT_GITMS_VERSION,
        repository = WANDISCO_ASSETS,
        sha1 = "aaba9cb9bd26710374ca1834a4bbbc569abd5d36",
    )
