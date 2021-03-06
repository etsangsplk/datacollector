/**
 * Copyright 2015 StreamSets Inc.
 *
 * Licensed under the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.streamsets.datacollector.config;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.streamsets.datacollector.creation.RuleDefinitionsConfigBean;
import com.streamsets.datacollector.definition.ConfigDefinitionExtractor;
import com.streamsets.datacollector.definition.ConfigGroupExtractor;
import com.streamsets.datacollector.definition.StageDefinitionExtractor;
import com.streamsets.pipeline.api.Config;
import com.streamsets.pipeline.api.impl.Utils;

import java.util.ArrayList;
import java.util.List;

public class PipelineRulesDefinition {
  /*The config definitions of the pipeline rules*/
  private final List<ConfigDefinition> configDefinitions;
  private final ConfigGroupDefinition groupDefinition;
  private final List<Config> defaultConfigs;

  public static PipelineRulesDefinition getPipelineRulesDef() {
    return new PipelineRulesDefinition().localize();
  }

  private PipelineRulesDefinition localize() {
    ClassLoader classLoader = getClass().getClassLoader();

    // stage configs
    List<ConfigDefinition> configDefs = new ArrayList<>();
    for (ConfigDefinition configDef : getConfigDefinitions()) {
      configDefs.add(configDef.localize(classLoader, RuleDefinitionsConfigBean.class.getName() + "-bundle"));
    }

    // stage groups
    ConfigGroupDefinition groupDefs = StageDefinition.localizeConfigGroupDefinition(
        classLoader,
        getConfigGroupDefinition()
    );
    return new PipelineRulesDefinition(configDefs, groupDefs);
  }

  private PipelineRulesDefinition(List<ConfigDefinition> configDefs, ConfigGroupDefinition groupDef) {
    configDefinitions = configDefs;
    groupDefinition = groupDef;
    List<Config> configs = new ArrayList<>();
    for (ConfigDefinition configDef : configDefs) {
      configs.add(new Config(configDef.getName(), configDef.getDefaultValue()));
    }
    defaultConfigs = ImmutableList.copyOf(configs);
  }

  private static List<ConfigDefinition> createPipelineRulesConfigs() {
    return ConfigDefinitionExtractor.get().extract(RuleDefinitionsConfigBean.class,
        StageDefinitionExtractor.getGroups(RuleDefinitionsConfigBean.class), "Pipeline Definition");
  }

  @VisibleForTesting
  PipelineRulesDefinition() {
    this(createPipelineRulesConfigs(), ConfigGroupExtractor.get().extract(
        RuleDefinitionsConfigBean.class,
        "Pipeline Rules Definition"
    ));
  }

  /*Need this API for Jackson to serialize*/
  public List<ConfigDefinition> getConfigDefinitions() {
    return configDefinitions;
  }

  public ConfigGroupDefinition getConfigGroupDefinition() {
    return groupDefinition;
  }

  public List<Config> getPipelineRulesDefaultConfigs() {
    return defaultConfigs;
  }

  @Override
  public String toString() {
    return Utils.format("PipelineRulesDefinition[configDefinitions='{}']", configDefinitions);
  }

}
