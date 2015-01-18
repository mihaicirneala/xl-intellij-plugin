package com.xebialabs.intellij.runner.cli

import com.intellij.execution.configurations.{ConfigurationFactory, ConfigurationType, RunConfiguration}
import com.intellij.openapi.project.Project

class XlDeployCliRunConfigurationFactory(val typez: ConfigurationType) extends ConfigurationFactory(typez) {
  def createTemplateConfiguration(project: Project): RunConfiguration = {
    val configuration = new XlDeployCliRunConfiguration(project, this, "")
    initDefault(configuration)
    return configuration
  }

  override def createConfiguration(name: String, template: RunConfiguration): RunConfiguration = {
    val configuration = (super.createConfiguration(name, template)).asInstanceOf[XlDeployCliRunConfiguration]
    //template.getProject.anyScalaModule.foreach(configuration.setModule(_))
    configuration
  }

  private def initDefault(configuration: XlDeployCliRunConfiguration): Unit = {
  }
}