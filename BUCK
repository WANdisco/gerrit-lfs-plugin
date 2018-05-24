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
  sha1 = '2cf7c5550b3c8657a2e3f9aa986044fc8f6184f1',
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
  bin_sha1 = '4789809c69c697e9bf4163e81ca2d67bb59737e2',
  src_sha1 = '48513511d0f6997263fd35d229dd71bc8f06cdda',
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
  bin_sha1 = '35459d60efd04c1f0faa1a4816ca0690116f44e3',
  src_sha1 = '6896afe609e8a2d2f4e90847b64df62d5facddf5',
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
