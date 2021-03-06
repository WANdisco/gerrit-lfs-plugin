
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

import com.google.common.base.Strings;
import com.google.gerrit.common.ProjectUtil;
import com.google.gerrit.common.data.Capable;
import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.project.ProjectCache;
import com.google.gerrit.server.project.ProjectControl;
import com.google.gerrit.server.project.ProjectState;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.eclipse.jgit.lfs.errors.*;
import org.eclipse.jgit.lfs.server.LargeFileRepository;
import org.eclipse.jgit.lfs.server.LfsGerritProtocolServlet;
import org.eclipse.jgit.lfs.server.LfsObject;
import org.eclipse.jgit.lfs.server.ReplicationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

import static com.google.gerrit.extensions.client.ProjectState.HIDDEN;
import static com.google.gerrit.extensions.client.ProjectState.READ_ONLY;
import static com.google.gerrit.httpd.LfsProjectParser.parseProjectFromPath;
import static com.google.gerrit.httpd.plugins.LfsPluginServlet.URL_REGEX;

@Singleton
public class LfsApiServlet extends LfsGerritProtocolServlet {
  private static final long serialVersionUID = 1L;
  private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);
  private static final String DOWNLOAD = "download";
  private static final String UPLOAD = "upload";

  private final ProjectCache projectCache;
  private final LfsConfigurationFactory lfsConfigFactory;
  private final LfsRepositoryResolver repoResolver;
  private final LfsAuthUserProvider userProvider;
  private static final Logger logger = LoggerFactory.getLogger(LfsApiServlet.class);

  @Inject
  LfsApiServlet(ProjectCache projectCache,
      LfsConfigurationFactory lfsConfigFactory,
      LfsRepositoryResolver repoResolver,
      LfsAuthUserProvider userProvider) {
    this.projectCache = projectCache;
    this.lfsConfigFactory = lfsConfigFactory;
    this.repoResolver = repoResolver;
    this.userProvider = userProvider;
  }

  @Override
  protected LargeFileRepository getLargeFileRepository(
      LfsRequest request, String path, String auth)
          throws LfsException {

    String projName = null;
    Project.NameKey project = null;
    try {
      projName = parseProjectFromPath(path);
      project = Project.NameKey.parse(ProjectUtil.stripGitSuffix(projName));

    } catch (Exception e) {
      logger.error("Repository requested wasn't found for path: " + path);
      throw new LfsRepositoryNotFound(projName == null ? path : projName);
    }

    ProjectState state = projectCache.get(project);
    if (state == null || state.getProject().getState() == HIDDEN) {
      throw new LfsRepositoryNotFound(project.get());
    }
    authorizeUser(userProvider.getUser(auth, projName, request.getOperation()), state, request.getOperation());

    if (request.getOperation().equals(UPLOAD) && state.getProject().getState() == READ_ONLY) {
      throw new LfsRepositoryReadOnly(project.get());
    }

    LfsProjectConfigSection config = lfsConfigFactory.getProjectsConfig().getForProject(project);
    // Only accept requests for projects where LFS is enabled.
    // No config means we default to "not enabled".
    if (config != null && config.isEnabled()) {
      // For uploads, check object sizes against limit if configured
      if (request.getOperation().equals(UPLOAD)) {
        if (config.isReadOnly()) {
          throw new LfsRepositoryReadOnly(project.get());
        }

        long maxObjectSize = config.getMaxObjectSize();
        if (maxObjectSize > 0) {
          for (LfsObject object : request.getObjects()) {
            if (object.getSize() > maxObjectSize) {
              throw new LfsValidationError(
                  String.format("size of object %s (%d bytes) exceeds limit (%d bytes)", object.getOid(), object.getSize(), maxObjectSize));
            }
          }
        }
      }

      LargeFileRepository repo = repoResolver.get(project, config.getBackend());
      ReplicationInfo replicationInfo = new ReplicationInfo();
      replicationInfo.isReplica = true;
      replicationInfo.repositoryName = projName;

      // TODO What about the repoId should it be here at all?, how to we get this in gerrit? We can use the name
      // and let gitMS return more information to us when we eventually request info about this object?
      // I will do that for now, and when it returns the ID we can cache it off for later re-use.
      // replicationInfo.repositoryId =

      // TODO Test project name  in subdirectories, as we need it to be unique!
      repo.setReplicationInfo( replicationInfo );
      return repo;
    }

    throw new LfsUnavailable(project.get());
  }

  private void authorizeUser(CurrentUser user, ProjectState state,
      String operation) throws LfsUnauthorized {
    ProjectControl control = state.controlFor(user);
    if ((operation.equals(DOWNLOAD) && !control.isReadable()) ||
        (operation.equals(UPLOAD) && Capable.OK != control.canPushToAtLeastOneRef())) {
      throw new LfsUnauthorized(
          String.format("User %s is not authorized to perform %s operation",
              Strings.isNullOrEmpty(user.getUserName())
                ? "anonymous" :  user.getUserName(),
              operation.toLowerCase()));
    }
  }
}
