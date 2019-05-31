
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
  sha1 = 'e5deb3a6323d45adacd04f518aa96e574556818a',
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
  bin_sha1 = '9bcb718ee8b7375e4981431120bb5a9455974b3f',
  src_sha1 = '45133427eb522ffc52bc12a5589a55a0684f9b94',
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
  bin_sha1 = '9b3baa4ed9f51ff98bc2691632ce5c147b0bcf72',
  src_sha1 = 'c95e16eb781cca1520758c8fc2965f1ffcc7c6a6',
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
