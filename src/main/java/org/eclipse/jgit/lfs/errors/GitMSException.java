
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
 
package org.eclipse.jgit.lfs.errors;

public class GitMSException extends Exception {
  private static final long serialVersionUID = 1L;

  public GitMSException(String message) {
    super(message);
  }
}
