include_defs('//bucklets/gerrit_plugin.bucklet')
include_defs('//bucklets/maven_jar.bucklet')

JGIT_VERSION = '4.5.0.201609210915-r'
REPO = MAVEN_CENTRAL
REPO_WD = 'http://artifacts.wandisco.com/artifactory/libs-release-local'
JGIT_VERSION_WD = '4.5.2.201704071617-r_WDv3_Test'

gerrit_plugin(
  name = 'gerrit-lfs-plugin',
  srcs = glob(['src/main/java/**/*.java']),
  resources = glob(['src/main/resources/**/*']),
  deps = [
    ':jgit-http-apache',
    ':jgit-lfs',
    ':jgit-lfs-server',
  ],
  provided_deps = [
    '//lib/httpcomponents:httpcore',
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
  sha1 = '5511508590400743cc649ee710f61bb9c8a9c637',
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
  bin_sha1 = '6b7a1405f689a7c67b2fdbd72ad625c84740be80',
  src_sha1 = 'c266dc25384dcc6b236bc4a4464da0d19eb8d68e',
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
  bin_sha1 = '5cc3497eb5823c2f2b37a79e698219759aacf192',
  src_sha1 = '1f4cd9eaf02a2efd91c76fff51d721f943be4539',
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
