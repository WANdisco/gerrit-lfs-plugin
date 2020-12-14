
#/********************************************************************************
# * Copyright (c) 2014-2018 WANdisco
# *
# * Licensed under the Apache License, Version 2.0 (the "License");
# * you may not use this file except in compliance with the License.
# * You may obtain a copy of the License at
# * http://www.apache.org/licenses/LICENSE-2.0
# *
# * Apache License, Version 2.0
# *
# ********************************************************************************/
 
include_defs('//bucklets/gerrit_plugin.bucklet')
include_defs('//bucklets/maven_jar.bucklet')

# WD Definitions - using gerrit main lib def files.
# If you need to build plugin on its own, then define these values here.
# VERS=Jgit Base Version
# VERS_WD=WD Replicated Jgit version
# REPO=MAVEN_CENTRAL
# REPO_WD=WD Public Repo
include_defs('//lib/WD_REPOSITORY')
include_defs('//lib/JGIT_VERSION')




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
  id = 'org.eclipse.jgit:org.eclipse.jgit.http.apache:' + VERS_WD,
  sha1 = '1afabd943ebd756d1161e2ffb6752aff4d2427bc',
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
  id = 'org.eclipse.jgit:org.eclipse.jgit.lfs:' + VERS_WD,
  bin_sha1 = '7a5074939b3fd11dec2050475a3f41d010ccca0d',
  src_sha1 = '6af7751fd1465728bdab1d7421e57b05e36de5fe',
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
  id = 'org.eclipse.jgit:org.eclipse.jgit.lfs.server:' + VERS_WD,
  bin_sha1 = '4db18fc5ea9d31cafa9f6a553ecfd9f137fb2ba9',
  src_sha1 = '7fb54e79702bd2311158c23861c45fef2ae5efdb',
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
