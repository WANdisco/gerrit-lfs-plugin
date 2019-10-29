
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
 
package com.wandisco.utils;

import com.google.common.base.Strings;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class StringUtils {
  /**
   * Create a unique string based on a prefix, and optional suffix property.  You can also have the
   * the unique part of the string added before or after the prefix you give.
   * e.g. <Prefix>_<UUID>[<SUFFIX>]  OR  <UUID>_<Prefix>[<SUFFIX>]
   * Example of use LfsFile_12345-5654-234234.lfsdata
   *
   * @throws UnsupportedEncodingException
   */
  public static String getUniqueString(String prefix, String suffix, boolean addUuidToStart) throws UnsupportedEncodingException {

    // Add unique id to start of the string.
    final String start = addUuidToStart ? UUID.randomUUID().toString() : prefix;
    final String end = !addUuidToStart ? UUID.randomUUID().toString() : prefix;

    final String tmpString = String.format("%s_%s", start, end);

    return Strings.isNullOrEmpty(suffix) ? tmpString : tmpString + suffix;
  }

  // override of getUniqueString which has no suffix only supplied string.
  public static String getUniqueString(String prefix, boolean addUuidToStart) throws UnsupportedEncodingException {
    return getUniqueString(prefix, null, addUuidToStart);
  }

}
