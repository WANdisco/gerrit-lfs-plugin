
/********************************************************************************
 * Copyright (c) 2014-2018 WANdisco
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Apache License, Version 2.0
 *
 ********************************************************************************/
 
// Copyright (C) 2016 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.lfs;

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.server.config.AllProjectsName;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.gerrit.server.project.ProjectCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LfsConfigurationFactory {
  private final String pluginName;
  private final ProjectCache projectCache;
  private final AllProjectsName allProjects;
  private final PluginConfigFactory configFactory;

  @Inject
  LfsConfigurationFactory(
      @PluginName String pluginName,
      ProjectCache projectCache,
      AllProjectsName allProjects,
      PluginConfigFactory configFactory) {
    this.pluginName = pluginName;
    this.projectCache = projectCache;
    this.allProjects = allProjects;
    this.configFactory = configFactory;
  }

  /**
   * Return the injector Lfs Plugin name.
   * @return
   */
  public String getPluginName() {
    return pluginName;
  }

  /**
   * @return the project-specific LFS configuration.
   */
  public LfsProjectsConfig getProjectsConfig() {
    return new LfsProjectsConfig(pluginName, projectCache, allProjects);
  }

  /** @return the global LFS configuration. */
  public LfsGlobalConfig getGlobalConfig() {
    return new LfsGlobalConfig(configFactory.getGlobalPluginConfig(pluginName));
  }

  /**
   * Force a plugin from the configFactory cache, to have its global configuration reloaded.
   * This will remove the entry from the plugin config cache, and force it to be retrived fresh.
   *
   * Only required for online - refresh of plugin state.
   * @param pluginName
   */
  public void forceReloadOfGlobalConfig(final String pluginName){
    // TODO: get reload from gerrit config factory....
    //    configFactory.forceReloadGlobalPluginConfig(pluginName);
  }
}
