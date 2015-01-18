package com.xebialabs.intellij.runner.cli;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.junit.RuntimeConfigurationProducer;

abstract public class RuntimeConfigurationProducerAdapter extends RuntimeConfigurationProducer {
  public RuntimeConfigurationProducerAdapter(ConfigurationType configurationType) {
    super(configurationType);
  }

  protected RuntimeConfigurationProducerAdapter(ConfigurationFactory configurationFactory) {
    super(configurationFactory);
  }

  public int compareTo(Object o) {
    return -1;
  }
}
