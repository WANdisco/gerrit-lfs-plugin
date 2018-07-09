package com.wandisco.api.lfs;

import com.googlesource.gerrit.plugins.lfs.LfsReplicatedRequestBuilder;
import org.eclipse.jgit.lfs.errors.GitMSException;
import org.eclipse.jgit.lfs.lib.AnyLongObjectId;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.wandisco.gerrit.gitms.shared.util.ReplicationUtils.getRepoWithoutSuffix;
import static org.eclipse.jgit.lfs.server.fs.FileLfsRepository.parseGitMSConfig;

public class LfsReplicateContent {


  /**
   * Makes a HTTP request to GitMS, specifically to servlet which handles requests for the endpoint /lfsPush
   * If the response back from GitMS is not a success response then a GitMSException is thrown.
   * @param projectName
   * @param objectId
   * @throws IOException
   * @throws GitMSException
   */
  public static void replicateLfsData(final String dataNamespace, File contentDeliveryPath,
      String projectName, AnyLongObjectId objectId)
      throws IOException, GitMSException {

    // TODO Use the new 4001 authenticated port.
    // Add auth information
    // GitMsConfiguration xxx = parseGitMSConfig();
    String localJettyPort = "4001";
    final String localJettyHost = "127.0.0.1";

// if requesting this method, we need to have a jetty port configurated if not, its invalid configuration.

    if (localJettyPort != null && !localJettyPort.isEmpty()) {
      LfsReplicatedRequestBuilder lfsRequestBuilder = null;
      try {
        lfsRequestBuilder = new LfsReplicatedRequestBuilder(localJettyHost, localJettyPort)
            .setProjectName(getRepoWithoutSuffix(projectName))
            .setLfsObjectOID(objectId.getName())
            .setLfsDataDir(dataNamespace)
            .setLfsObjectSize(contentDeliveryPath.length())
            .setLfsContentDeliveryPath(contentDeliveryPath.getName())
            .setRequestURI().setHttpConnection().buildRequest();

        int response = lfsRequestBuilder.getHttpResponseCode();

        StringBuilder responseString = new StringBuilder();
        if (lfsRequestBuilder.getHttpErrorStream() != null) {
          try (BufferedReader reader = new BufferedReader(
              new InputStreamReader(lfsRequestBuilder.getHttpErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
              responseString.append(line).append("\n");
            }
          }
        }
        if (response != 202 && response != 200) {
          String err = "GitMS LFS Request : Response code: [" + response + "] " +
              "Replicator response: [" + responseString.toString() + " ]";
          throw new GitMSException(err);
        }
      } catch (IOException e) {
        IOException ee = new IOException("Error making LFS request: " + e.toString());
        ee.initCause(e);
        throw ee;
      } finally {
        lfsRequestBuilder.disconnect();
      }
    }
  }

}
