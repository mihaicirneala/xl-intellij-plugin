package com.xebialabs.intellij.runner.cli

import java.util

import com.intellij.execution.configurations._
import com.intellij.execution.filters._
import com.intellij.execution.process.{OSProcessHandler, ProcessHandler}
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.{ExecutionException, Executor}
import com.intellij.openapi.module.{ModuleManager, Module, ModuleUtilCore}
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.JDOMExternalizer
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.{PsiElement, PsiManager}
import com.intellij.refactoring.listeners.{RefactoringElementAdapter, RefactoringElementListener}
import com.intellij.vcsUtil.VcsUtil
import org.jdom.Element

class XlDeployCliRunConfiguration(val project: Project, val configurationFactory: ConfigurationFactory, val name: String)
        extends ModuleBasedConfiguration[RunConfigurationModule](name, new RunConfigurationModule(project), configurationFactory) with RefactoringListenerProvider {

  private var scriptPath = ""
  private var xlDeployUsername = ""
  private var xlDeployPassword = ""
  
  private var cliInstallationDirectory = {
    val base = getProject.getBaseDir
    if (base != null) base.getPath
    else ""
  }

  def getScriptPath = scriptPath
  def getXlDeployUsername = xlDeployUsername
  def getXlDeployPassword = xlDeployPassword
  def getCliInstallationDirectory: String = cliInstallationDirectory
  def setScriptPath(s: String) {
    scriptPath = s
  }
  def setXlDeployUsername(s: String) {
    xlDeployUsername = s
  }
  def setXlDeployPassword(s: String) {
    xlDeployPassword = s
  }
  def setCliInstallationDirectory(s: String) {
    cliInstallationDirectory = s
  }

  def apply(params: XlDeployCliRunConfigurationForm) {
    setScriptPath(params.getScriptPath)
    setXlDeployUsername(params.getXlDeployUsername)
    setXlDeployPassword(params.getXlDeployPassword)
    setCliInstallationDirectory(params.getCliInstallationDirectory)
  }

  def getState(executor: Executor, env: ExecutionEnvironment): RunProfileState = {
    def fileNotFoundError() {
      throw new ExecutionException("Scala script file not found.")
    }
//    try {
//      val file: VirtualFile = VcsUtil.getVirtualFile(scriptPath)
//      PsiManager.getInstance(project).findFile(file) match {
//        case f: VirtualFile if f.getExtension == ".py"  =>
//        //case f: ScalaFile if f.isScriptFile() && !f.isWorksheetFile =>
//        case _ => fileNotFoundError()
//      }
//    }
//    catch {
//      case e: Exception => fileNotFoundError()
//    }

    val module = getModule
    if (module == null) throw new ExecutionException("Module is not specified")
    
    ProjectRootManager.getInstance(project).getFileIndex()
    project.getProjectFile
    env.getProject.getProjectFile
    val script = VcsUtil.getVirtualFile(scriptPath)
    val state = new CommandLineState(env) {
      override def startProcess(): ProcessHandler = {
        val commandLine = new GeneralCommandLine();
        commandLine.setWorkDirectory(env.getProject().getBasePath());
        commandLine.setExePath(getCliInstallationDirectory + "/bin/cli.sh");
        val parametersList: ParametersList = commandLine.getParametersList();
        parametersList.add("-username");
        parametersList.add(getXlDeployUsername);
        parametersList.add("-password");
        parametersList.add(getXlDeployPassword);
        parametersList.add("-f");
        parametersList.add(getScriptPath);
        return new OSProcessHandler(commandLine.createProcess());

        
      }
    }

    val consoleBuilder = TextConsoleBuilderFactory.getInstance.createBuilder(getProject)
    consoleBuilder.addFilter(getFilter(script))
    state.setConsoleBuilder(consoleBuilder)
    state
  }

  def getModule: Module = {
    var module: Module = null
    try {
      val file: VirtualFile = VcsUtil.getVirtualFile(scriptPath)
      module = ModuleUtilCore.findModuleForFile(file, getProject)
    }
    catch {
      case e: Exception =>
    }
    if (module == null) module = getConfigurationModule.getModule
    module
  }

  def getValidModules: java.util.List[Module] = new util.ArrayList[Module]()

  def getConfigurationEditor: SettingsEditor[_ <: RunConfiguration] = new XlDeployCliRunConfigurationEditor(project, this)

  override def writeExternal(element: Element) {
    super.writeExternal(element)
    writeModule(element)
    JDOMExternalizer.write(element, "path", getScriptPath)
    JDOMExternalizer.write(element, "username", getXlDeployUsername)
    JDOMExternalizer.write(element, "password", getXlDeployPassword)
    JDOMExternalizer.write(element, "installDirectory", getCliInstallationDirectory)
  }

  override def readExternal(element: Element) {
    super.readExternal(element)
    readModule(element)
    scriptPath = JDOMExternalizer.readString(element, "path")
    xlDeployUsername = JDOMExternalizer.readString(element, "username")
    xlDeployPassword = JDOMExternalizer.readString(element, "password")
    val pp = JDOMExternalizer.readString(element, "installDirectory")
    if (pp != null) cliInstallationDirectory = pp
  }

  private def getFilter(file: VirtualFile): Filter = {
    import com.intellij.execution.filters.Filter._
    new Filter {
      def applyFilter(line: String, entireLength: Int): Result = {
        val start = entireLength - line.length
        var end = entireLength - line.length
        if (line.startsWith("(fragment of ")) {
          try {
            var cache = line.replaceFirst("[(][f][r][a][g][m][e][n][t][ ][o][f][ ]", "")
            cache = cache.replaceFirst("[^)]*[)][:]", "")
            val lineNumber = Integer.parseInt(cache.substring(0, cache.indexOf(":")))
            cache = cache.replaceFirst("[^:]", "")
            end += line.length - cache.length
            val hyperlink = new OpenFileHyperlinkInfo(getProject, file, lineNumber-1)
            new Result(start, end, hyperlink)
          }
          catch {
            case _: Exception => return null
          }
        } else null
      }
    }
  }

  def getRefactoringElementListener(element: PsiElement): RefactoringElementListener = element match {
//    case file: ScalaFile => new RefactoringElementAdapter {
//      def elementRenamedOrMoved(newElement: PsiElement) = {
//        newElement match {
//          case f: ScalaFile =>
//            val newPath = f.getVirtualFile.getPath
//            setScriptPath(newPath)
//          case _ =>
//        }
//      }
//
//      //todo this method does not called when undo of moving action executed
//      def undoElementMovedOrRenamed(newElement: PsiElement, oldQualifiedName: String) {
//        setScriptPath(oldQualifiedName)
//      }
//    }
    case _ => RefactoringElementListener.DEAF
  }
}