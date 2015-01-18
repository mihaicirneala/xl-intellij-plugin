package com.xebialabs.intellij.runner.cli;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.xebialabs.intellij.runner.cli.XlDeployCliRunConfiguration;

import javax.swing.*;
import java.awt.*;

public class XlDeployCliRunConfigurationForm {
    private XlDeployCliRunConfiguration myConfiguration;
    private Project myProject;
    private JPanel myPanel;
    private TextFieldWithBrowseButton cliInstallationDirectory;
    private JTextField xlDeployUsername;
    private JPasswordField xlDeployPassword;
    private TextFieldWithBrowseButton scriptPath;

    public XlDeployCliRunConfigurationForm(final Project project, final XlDeployCliRunConfiguration configuration) {
        myProject = project;
        myConfiguration = configuration;
        addFileChooser("Select CLI script file", scriptPath, project, false);
        addFileChooser("Choose XL Deploy CLI installation directory:", cliInstallationDirectory, project, true);
        VirtualFile baseDir = project.getBaseDir();
        String path = baseDir != null ? baseDir.getPath() : "";
        cliInstallationDirectory.setText(path);
    }

    public String getScriptPath() {
        return scriptPath.getText();
    }

    public void setScriptPath(String s) {
        scriptPath.setText(s);
    }

    public JPanel getPanel() {
        return myPanel;
    }

    public String getXlDeployUsername() {
        return xlDeployUsername.getText();
    }

    public void setXlDeployUsername(String username) {
        xlDeployUsername.setText(username);
    }

    public String getXlDeployPassword() {
        return xlDeployPassword.getText();
    }

    public void setXlDeployPassword(String username) {
        xlDeployPassword.setText(username);
    }

    public void apply(XlDeployCliRunConfiguration configuration) {
        setScriptPath(configuration.getScriptPath());
        setXlDeployUsername(configuration.getXlDeployUsername());
        setXlDeployPassword(configuration.getXlDeployPassword());
        setCliInstallationDirectory(configuration.getCliInstallationDirectory());
    }

    public String getCliInstallationDirectory() {
        return cliInstallationDirectory.getText();
    }

    public void setCliInstallationDirectory(String s) {
        cliInstallationDirectory.setText(s);
    }

    private FileChooserDescriptor addFileChooser(final String title,
                                                 final TextFieldWithBrowseButton textField,
                                                 final Project project, final Boolean directories) {
        final FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(!directories, directories, false, false, false, false) {
            @Override
            public boolean isFileVisible(VirtualFile file, boolean showHiddenFiles) {
                return super.isFileVisible(file, showHiddenFiles) && (file.isDirectory() || (!directories && "scala".equals(file.getExtension())));
            }
        };
        fileChooserDescriptor.setTitle(title);
        textField.addBrowseFolderListener(title, null, project, fileChooserDescriptor);
        return fileChooserDescriptor;
    }
}
