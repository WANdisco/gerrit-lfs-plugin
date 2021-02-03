load("//tools/bzl:maven_jar.bzl", "GERRIT", "MAVEN_CENTRAL", "MAVEN_LOCAL", "maven_jar")

# NOTE !!!!!!!!!!!!!!
# This is only here to allow standalone building, when building as part of the parent gerrit project
# it will be using the main WORKSPACE definitions instead, so be careful... updating it here isn't really
# what is used in a production release.war package!!
JGIT_VERSION = "5.1.13-WDv1"

# WANdisco maven assets
_GERRIT_GITMS_VERSION = "1.1.0.1"

# Default repo being used...
REPO = MAVEN_CENTRAL

# When building only as part of gerrit release was I could import this definition, but its not in maven_jar
# used by plugins, and we would need to upload a changed one to support it, As such just writing it manually here
WANDISCO_ASSETS = "WANDISCO:"

def external_plugin_deps():
    maven_jar(
        name = "jgit-http-apache",
        artifact = "org.eclipse.jgit:org.eclipse.jgit.http.apache:" + JGIT_VERSION,
        sha1 = "9849f26c5e4a69590f06be869599e851fd199a4d",
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
        sha1 = "12d5616764151c1a27b0df33594be19eaddd0025",
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
        sha1 = "af9a45cb87a2a62e6216d2b1c6ced8f6d3895dd5",
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
        sha1 = "a0eb0feb042c06fa0974b45039a728c4c28cb3fd",
    )
