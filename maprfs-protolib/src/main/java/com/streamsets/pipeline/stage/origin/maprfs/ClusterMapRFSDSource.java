/**
 * Copyright 2016 StreamSets Inc.
 *
 * Licensed under the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.streamsets.pipeline.stage.origin.maprfs;

import com.streamsets.datacollector.stage.HadoopConfigurationSynchronizedClusterSource;
import com.streamsets.pipeline.api.ExecutionMode;
import com.streamsets.pipeline.api.GenerateResourceBundle;
import com.streamsets.pipeline.api.HideConfigs;
import com.streamsets.pipeline.api.Source;
import com.streamsets.pipeline.api.StageDef;
import com.streamsets.pipeline.stage.origin.hdfs.cluster.ClusterHdfsDSource;

@StageDef(
    version = 1,
    label = "MapR FS",
    description = "Reads from a MapR filesystem",
    execution = ExecutionMode.CLUSTER_BATCH,
    libJarsRegex = {"avro-\\d+.*", "avro-mapred.*"},
    icon = "mapr.png",
    privateClassLoader = false,
    onlineHelpRefUrl = "index.html#Origins/MapRFS.html#task_h2p_mb4_lx"
)
@HideConfigs(value =
                 {
                     "clusterHDFSConfigBean.dataFormatConfig.compression",
                     "clusterHDFSConfigBean.dataFormatConfig.includeCustomDelimiterInTheText"
                 }
)
@GenerateResourceBundle
public class ClusterMapRFSDSource extends ClusterHdfsDSource {

  private Source clusterMapRFSSource;

  @Override
  protected Source createSource() {
    if(clusterHDFSConfigBean.hdfsUri == null || clusterHDFSConfigBean.hdfsUri.isEmpty()) {
      clusterHDFSConfigBean.hdfsUri = "maprfs:///";
    }
    clusterMapRFSSource = new HadoopConfigurationSynchronizedClusterSource(new ClusterMapRFSSource(clusterHDFSConfigBean));
    return clusterMapRFSSource;
  }

  @Override
  public Source getSource() {
    return clusterMapRFSSource;
  }

}
