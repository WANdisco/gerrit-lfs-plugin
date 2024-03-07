
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
 
package com.wandisco.api.lfs;

import com.google.common.base.Strings;
import com.googlesource.gerrit.plugins.lfs.LfsReplicatedRequestBuilder;
import com.wandisco.gerrit.gitms.shared.api.ApiResponse;
import com.wandisco.gerrit.gitms.shared.properties.GitMsApplicationProperties;
import org.eclipse.jgit.lfs.errors.GitMSException;
import org.eclipse.jgit.lfs.lib.AnyLongObjectId;

import java.io.File;
import java.io.IOException;

import static com.wandisco.gerrit.gitms.shared.util.ReplicationUtils.getRepoWithoutSuffix;
import static com.wandisco.gerrit.gitms.shared.util.ReplicationUtils.parseGitMSConfig;

public class LfsReplicateContent {

  /**
   * Makes a HTTP request to GitMS, specifically to servlet which handles requests for the endpoint /lfsPush
   * If the response back from GitMS is not a success response then a GitMSException is thrown.
   *
   * @param dataNamespace
   * @param contentDeliveryPath
   * @param projectName
   * @param objectId
   * @throws IOException
   * @throws GitMSException
   */
  public static void replicateLfsData(final String dataNamespace, File contentDeliveryPath, String projectName, AnyLongObjectId objectId)
      throws Exception, GitMSException {

    GitMsApplicationProperties gitMsApplicationProperties = parseGitMSConfig();

    // local jetty port is unauthenticated, we have a subset of unauth calls available to support lfs on same machine.
    final String localJettyPort = gitMsApplicationProperties.getGitMSLocalJettyPort();
    final String localJettyHost = "127.0.0.1";

    // if requesting this method, we need to have a jetty port configurated if not, its invalid configuration.
    if ( Strings.isNullOrEmpty(localJettyPort)) {
      throw new GitMSException("Invalid GitMS application configuration - missing public Api Rest port property: jetty.http.port");
    }

    try {
      LfsReplicatedRequestBuilder lfsRequestBuilder = new LfsReplicatedRequestBuilder
          (localJettyHost, Integer.valueOf(localJettyPort), getRepoWithoutSuffix(projectName), objectId.getName(),
          contentDeliveryPath.length(), dataNamespace, contentDeliveryPath.getName());

      ApiResponse response = lfsRequestBuilder.issueRequest();

      // basically OK, CREATED, or NOT_CONTENT responses are success but allow the whole 200-299 range.
      // anything else treat as failure.
      if (response.statusCode < 200 || response.statusCode >= 300) {
        String err = String.format("GitMS LFS Request : Response code: [%d] Replicator response: [%s].", response.statusCode, response.response);
        throw new GitMSException(err);
      }

      // if we replicate the item, we could update our local cache, but instead just let the next
      // request for the content do it for us.
    } catch (Exception e) {
      throw new Exception(String.format("Error making LFS request: %s ", e.getMessage()), e);
    }

  }

}
