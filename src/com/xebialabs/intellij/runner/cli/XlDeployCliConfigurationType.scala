package com.xebialabs.intellij.runner.cli

import javax.swing.Icon

import com.intellij.execution.configurations.{ConfigurationFactory, ConfigurationType}
import com.intellij.openapi.util.IconLoader
import com.xebialabs.intellij.icons.Icons

class XlDeployCliConfigurationType extends ConfigurationType {
  val confFactory = new XlDeployCliRunConfigurationFactory(this)
  
  def getIcon: Icon = Icons.XL_DEPLOY

  def getDisplayName: String = "XL Deploy CLI script"

  def getConfigurationTypeDescription: String = "XLD CLI script run configurations"

  def getConfigurationFactories: Array[ConfigurationFactory] = Array[ConfigurationFactory](confFactory)

  def getId: String = "XlDeployCliConfigurationType"
}