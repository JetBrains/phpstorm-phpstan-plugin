package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.SmartList;
import com.jetbrains.php.tools.quality.QualityToolAnnotatorInfo;
import com.jetbrains.php.tools.quality.QualityToolConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.util.containers.ContainerUtil.map;

public class PhpStanQualityToolAnnotatorInfo extends QualityToolAnnotatorInfo<PhpStanValidationInspection> {
  private List<String> dependsOn = new ArrayList<>();

  public PhpStanQualityToolAnnotatorInfo(@Nullable PsiFile psiFile,
                                         @NotNull PhpStanValidationInspection inspection,
                                         @NotNull Project project,
                                         @NotNull QualityToolConfiguration configuration, boolean isOnTheFly) {
    super(psiFile, inspection, project, configuration, isOnTheFly);
  }


  public List<String> getDependsOn() {
    return getInspection().FULL_PROJECT
           ? new SmartList<>(getProject().getBasePath())
           : new SmartList<>(map(ProjectRootManager.getInstance(getProject()).getContentSourceRoots(), VirtualFile::getPath));
    // TODO: replace with dependsOn after dump-deps batch mode fix
    //return dependsOn;
  }

  public void setDependsOn(List<String> dependsOn) {
    this.dependsOn = dependsOn;
  }
}
