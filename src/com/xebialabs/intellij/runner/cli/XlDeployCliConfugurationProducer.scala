package com.xebialabs.intellij.runner.cli

import java.util

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl
import com.intellij.execution.{Location, RunManager, RunnerAndConfigurationSettings}
import com.intellij.psi.{PsiElement, PsiFile}

class XlDeployCliConfugurationProducer extends {
  val configurationType = new XlDeployCliConfigurationType
  val confFactory = configurationType.confFactory
} with RuntimeConfigurationProducerAdapter(configurationType) {
  private var myPsiElement: PsiElement = null
  def getSourceElement: PsiElement = myPsiElement

  override def findExistingByElement(location: Location[_ <: PsiElement], existingConfigurations: util.List[RunnerAndConfigurationSettings],
                                     context: ConfigurationContext): RunnerAndConfigurationSettings = {
    import scala.collection.JavaConversions._
    existingConfigurations.find(c => isConfigurationByLocation(c.getConfiguration, location)).orNull
  }

  def createConfigurationByElement(location: Location[_ <: PsiElement], context: ConfigurationContext): RunnerAndConfigurationSettings = {
    myPsiElement = location.getPsiElement
    createConfigurationByLocation(location).asInstanceOf[RunnerAndConfigurationSettingsImpl]
  }

  private def createConfigurationByLocation(location: Location[_ <: PsiElement]): RunnerAndConfigurationSettings = {
    val file = location.getPsiElement.getContainingFile
//    file match {
//      case null => null
//      case scalaFile: ScalaFile if scalaFile.isScriptFile() && !scalaFile.isWorksheetFile => {
//        val settings = RunManager.getInstance(location.getProject).createRunConfiguration(scalaFile.name, confFactory)
//        val conf: XlDeployCliRunConfiguration = settings.getConfiguration.asInstanceOf[XlDeployCliRunConfiguration]
//        val module = ModuleUtilCore.findModuleForFile(scalaFile.getVirtualFile, scalaFile.getProject)
//        if (module == null || !module.hasScala) return null
//        conf.setModule(module)
//        conf.setScriptPath(scalaFile.getVirtualFile.getPath)
//        settings
//      }
//      case _ => null
//    }
    null
  }

  private def isConfigurationByLocation(configuration: RunConfiguration, location: Location[_ <: PsiElement]): Boolean = {
    configuration match {
      case conf: XlDeployCliRunConfiguration => {
//        val file: PsiFile = location.getPsiElement.getContainingFile
//        if (file == null || !file.isInstanceOf[ScalaFile]) return false
//        conf.getScriptPath.trim == file.getVirtualFile.getPath.trim
        false
      }
      case _ => false
    }
  }
}
