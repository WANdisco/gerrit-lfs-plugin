package com.wandisco.api.lfs;

import com.google.common.base.Strings;
import com.googlesource.gerrit.plugins.lfs.LfsReplicatedRequestBuilder;
import com.wandisco.gerrit.gitms.shared.properties.GitMsApplicationProperties;
import org.eclipse.jgit.lfs.errors.GitMSException;
import org.eclipse.jgit.lfs.lib.AnyLongObjectId;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.wandisco.gerrit.gitms.shared.util.ReplicationUtils.getRepoWithoutSuffix;
import static com.wandisco.gerrit.gitms.shared.util.ReplicationUtils.parseGitMSConfig;

public class LfsReplicateContent {

  /**
   * Makes a HTTP request to GitMS, specifically to servlet which handles requests for the endpoint /lfsPush
   * If the response back from GitMS is not a success response then a GitMSException is thrown.
   *
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

    LfsReplicatedRequestBuilder lfsRequestBuilder = null;
    try {
      lfsRequestBuilder = new LfsReplicatedRequestBuilder(localJettyHost, Integer.valueOf(localJettyPort), getRepoWithoutSuffix(projectName), objectId.getName(),
          contentDeliveryPath.length(), dataNamespace, contentDeliveryPath.getName());

      int response = lfsRequestBuilder.getHttpResponseCode();

      StringBuilder responseString = new StringBuilder();
      if (lfsRequestBuilder.getHttpErrorStream() != null) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(lfsRequestBuilder.getHttpErrorStream()))) {
          String line;
          while ((line = reader.readLine()) != null) {
            responseString.append(line).append("\n");
          }
        }
      }
      if (response != 202 && response != 200) {
        String err = "GitMS LFS Request : Response code: [" + response + "] " + "Replicator response: [" + responseString.toString() + " ]";
        throw new GitMSException(err);
      }
    } catch (IOException e) {
      throw new Exception("Error making LFS request: " + e.toString(), e);
    } catch (Exception e) {
      throw new Exception("Error making LFS request: " + e.toString(), e);
    } finally {
      lfsRequestBuilder.disconnect();
    }

  }

}
