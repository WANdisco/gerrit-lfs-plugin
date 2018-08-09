include_defs('//bucklets/gerrit_plugin.bucklet')
include_defs('//bucklets/maven_jar.bucklet')
include_defs('//lib/WD_REPOSITORY')

JGIT_VERSION = '4.5.0.201609210915-r'
REPO = MAVEN_CENTRAL
JGIT_VERSION_WD = '4.5.2.201704071617-r_WDv4_TC1'

gerrit_plugin(
  name = 'lfs',
  srcs = glob(['src/main/java/**/*.java']),
  resources = glob(['src/main/resources/**/*']),
  deps = [
    ':jgit-http-apache',
    ':jgit-lfs',
    ':jgit-lfs-server',
    '//lib/wandisco:gerrit-gitms-interface',
    '//lib/jackson:jackson-mapper-asl'
  ],
  provided_deps = [
    '//lib/httpcomponents:httpcore'
  ],
  manifest_entries = [
    'Gerrit-PluginName: lfs',
    'Gerrit-Module: com.googlesource.gerrit.plugins.lfs.Module',
    'Gerrit-HttpModule: com.googlesource.gerrit.plugins.lfs.HttpModule',
    'Gerrit-SshModule: com.googlesource.gerrit.plugins.lfs.SshModule',
    'Gerrit-InitStep: com.googlesource.gerrit.plugins.lfs.InitLfs',
  ],
)

maven_jar(
  name = 'jgit-http-apache',
  id = 'org.eclipse.jgit:org.eclipse.jgit.http.apache:' + JGIT_VERSION_WD,
  sha1 = 'cb24679c30bb398a106582ce0e28c0f9a6a1ed94',
  license = 'jgit',
  repository = REPO_WD,
  unsign = True,
  exclude = [
    'about.html',
    'plugin.properties',
  ],
)

maven_jar(
  name = 'jgit-lfs',
  id = 'org.eclipse.jgit:org.eclipse.jgit.lfs:' + JGIT_VERSION_WD,
  bin_sha1 = 'e74e1f3a31f4a1602f124816ec6462ec3772ea45',
  src_sha1 = 'ff129877b10b15112c506014823142b5ed8e2736',
  license = 'jgit',
  repository = REPO_WD,
  unsign = True,
  exclude = [
    'about.html',
    'plugin.properties',
  ],
)

maven_jar(
  name = 'jgit-lfs-server',
  id = 'org.eclipse.jgit:org.eclipse.jgit.lfs.server:' + JGIT_VERSION_WD,
  bin_sha1 = '9516ebd09b0f6c6c44ebef59636a196bd3b7c2fc',
  src_sha1 = '91f3e5afc91d800d16e69ce8291910ce5f87d448',
  license = 'jgit',
  repository = REPO_WD,
  unsign = True,
  exclude = [
    'about.html',
    'plugin.properties',
  ],
)

java_test(
  name = 'lfs_tests',
  srcs = glob(['src/test/java/**/*.java']),
  labels = ['lfs'],
  source_under_test = [':lfs__plugin'],
  deps = GERRIT_PLUGIN_API + GERRIT_TESTS + [
    ':lfs__plugin',
    '//plugins/lfs:jgit-lfs',
  ],
)
