include_defs('//bucklets/gerrit_plugin.bucklet')
include_defs('//bucklets/maven_jar.bucklet')
include_defs('//lib/WD_REPOSITORY')

JGIT_VERSION = '4.5.0.201609210915-r'
REPO = MAVEN_CENTRAL
JGIT_VERSION_WD = '4.5.2.201704071617-r_WDv4_TC2'

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
  sha1 = '7a7a00a9a2c6303ce5bd5550e363b12dae8b73c5',
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
  bin_sha1 = 'a3b7af42087a3341dbfd97a08a5ad97095e3d8a8',
  src_sha1 = '00d1d1361575009c8b6b797ccebb6d82c9ba6f40',
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
  bin_sha1 = '7a715e364c9a98485eef99d8f62e93345213619f',
  src_sha1 = 'b7e13bbf1b71f5637b69bb3ecef1db95fca58743',
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
