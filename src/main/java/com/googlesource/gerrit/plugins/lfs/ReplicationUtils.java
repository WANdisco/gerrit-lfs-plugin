package com.googlesource.gerrit.plugins.lfs;


import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lfs.errors.GitMSException;
import org.eclipse.jgit.lfs.lib.AnyLongObjectId;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.util.FS;
import org.eclipse.jgit.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Utility class containing static methods which allows for reading GitMS properties and making
 * request for LFS file replication to GitMS.
 * @author ronanconway
 */

public class ReplicationUtils {


  /**
   * Reusable method whereby you can search for a specific property in gitmsconfig file.
   * @return
   * @throws IOException
   */
  public static String parseForProperty(String propertyName) throws IOException {
    String configProps = "";
    FileBasedConfig config = getConfigFile();
    String appProperties = config.getString("core", null, "gitmsconfig");

    if (!StringUtils.isEmptyOrNull(appProperties)) {
      File appPropertiesFile = new File(appProperties);
      if (appPropertiesFile.canRead()) {
        configProps = getProperty(appPropertiesFile, propertyName);
      }
    }
    return configProps;
  }

  /**
   * Parses the properties file passed in and searches for the specified property.
   * @param appProps
   * @param propertyName
   * @return
   * @throws IOException
   */
  public static String getProperty(File appProps, String propertyName) throws IOException {
    Properties props = new Properties();
    InputStream input = null;
    try {
      input = new FileInputStream(appProps);
      props.load(input);
      return props.getProperty(propertyName);
    } catch (IOException e) {
      throw new IOException("Could not read " + appProps.getAbsolutePath());
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (IOException ex) {
          ex.printStackTrace();
        }
      }
    }
  }

  /**
   * Gets the path of ./gitconfig from the enviornment variable GIT_CONFIG
   * @return
   * @throws IOException
   */
  public static FileBasedConfig getConfigFile() throws IOException {
    String gitConfigLoc = System.getenv("GIT_CONFIG");

    if (System.getenv("GIT_CONFIG") == null) {
      gitConfigLoc = System.getProperty("user.home") + "/.gitconfig";
    }

    FileBasedConfig config = new FileBasedConfig(new File(gitConfigLoc), FS.DETECTED);
    try {
      config.load();
    } catch (ConfigInvalidException e) {
      throw new IOException(e);
    }
    return config;
  }

  /**
   * Parses the gitmsconfig for two properties and returns a String array of those properties.
   * @return
   * @throws IOException
   */
  public static String[] parseGitMSConfig() throws IOException {
    String[] configProps = null;
    FileBasedConfig config = getConfigFile();
    String appProperties = config.getString("core", null, "gitmsconfig");

    if (!StringUtils.isEmptyOrNull(appProperties)) {
      File appPropertiesFile = new File(appProperties);
      if (appPropertiesFile.canRead()) {
        configProps = new String[3];
        configProps[0] = getProperty(appPropertiesFile, "gitms.local.jetty.port");
        configProps[1] = getProperty(appPropertiesFile, "gerrit.repo.home");
        configProps[2] = getProperty(appPropertiesFile, "gerrit.root");
      }
    }
    return configProps;
  }

  /**
   * Makes a HTTP request to GitMS, specifically to servlet which handles requests for the endpoint /lfsPush
   * If the response back from GitMS is not a success response then a GitMSException is thrown.
   * @param projectName
   * @param objectId
   * @throws IOException
   * @throws GitMSException
   */
  public static void replicateLfsData(final String dataNamespace, File contentDeliveryPath, String projectName, AnyLongObjectId objectId)
      throws IOException, GitMSException {
    String [] gitmsConfig = parseGitMSConfig();
    String localJettyPort = null;
    final String localJettyHost = "127.0.0.1";

    if(gitmsConfig != null || gitmsConfig.length > 0) {
      localJettyPort = gitmsConfig[0];
    }

    if (localJettyPort != null && !localJettyPort.isEmpty()) {
      LfsReplicatedRequestBuilder lfsRequestBuilder = null;
      try {
        lfsRequestBuilder = new LfsReplicatedRequestBuilder(localJettyHost, localJettyPort)
            .setProjectName(projectName)
            .setLfsObjectOID(objectId.getName())
            .setLfsDataDir(dataNamespace)
            .setLfsObjectSize(contentDeliveryPath.length())
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

  /**
   * Determine where the repository namespace is for the given repository in content delivery.
   * Pattern matching is performed to find the namespace based on the repository name as the
   * repo namespace is of the format <repositoryName>_<repositoryIdentity>
   * @param repositoryName
   * @return
   * @throws IOException
   */
  public static Path getCDRepoNameSpace(String repositoryName) throws IOException {
    String dir = parseForProperty("content.location") + "/";
    String namespaceRepo = repositoryName.substring(0, repositoryName.lastIndexOf(".git"));
    Path path = Paths.get(dir);
    Path namespace = null;
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, namespaceRepo+"*")) {
      for (Path entry: stream) {
        namespace = entry.getFileName();
      }
    } catch (IOException x) {
      x.printStackTrace();
    }
    return namespace;
  }

}
