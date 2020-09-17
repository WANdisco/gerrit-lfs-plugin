// Copyright (C) 2017 The Android Open Source Project
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

import static com.google.common.truth.Truth.assertThat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

public class LfsDateTimeTest {
  private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

  @Test
  public void format() throws Exception {
    Instant now = Instant.now();
    ZonedDateTime zdt = ZonedDateTime.ofInstant(now, ZoneOffset.UTC);
    String fixedFormatTime = formatter.format(zdt);

    // we used tom compare JodaTime now comparing ISO Offset zulu javatime to Lfs formatted time.
    String javaFormat = LfsDateTime.format(now);
    assertThat(javaFormat).isEqualTo(fixedFormatTime);
  }
}
