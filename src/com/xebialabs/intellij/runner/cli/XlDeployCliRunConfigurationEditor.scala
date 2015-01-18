package com.xebialabs.intellij.runner.cli

import javax.swing.JComponent

import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project

class XlDeployCliRunConfigurationEditor(project: Project, configuration: XlDeployCliRunConfiguration)
extends SettingsEditor[XlDeployCliRunConfiguration] {
  val form = new XlDeployCliRunConfigurationForm(project, configuration)

  def resetEditorFrom(s: XlDeployCliRunConfiguration): Unit = form(s)

  def applyEditorTo(s: XlDeployCliRunConfiguration): Unit = s(form)

  def createEditor: JComponent = form.getPanel
}