package com.jetbrains.php.tools.quality.phpstan;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.php.PhpBundle;
import com.jetbrains.php.config.interpreters.PhpInterpreter;
import com.jetbrains.php.tools.quality.CustomRulesSetupDialog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.util.Arrays;

import static com.intellij.openapi.util.text.StringUtil.equalsIgnoreCase;

public class PhpStanCustomRulesSetupDialog extends CustomRulesSetupDialog {
  private final static String PHP_CS = ".php_cs";
  private static final String PHP_CS_DIST = ".php_cs.dist";

  protected PhpStanCustomRulesSetupDialog(@Nullable Project project,
                                          @Nullable PhpInterpreter interpreter,
                                          @NotNull Component parent, @NotNull String rulesetPath) {
    super(project, interpreter, parent, rulesetPath, true);
  }

  @Override
  protected boolean containsRulesetXml(@Nullable VirtualFile file) {
    if (file == null) return false;

    if (!file.isDirectory()) {
      return isCodingStandardFile(file.getPath());
    }
    else {
      return Arrays.stream(file.getChildren())
        .anyMatch(folderChild -> PHP_CS.equals(folderChild.getName()) || PHP_CS_DIST.equals(folderChild.getName()));
    }
  }

  @Nullable
  @Override
  protected ValidationInfo validatePath(String path) {
    if (path == null || path.isEmpty()) return new ValidationInfo(PhpBundle.message("quality.tool.path.must.not.be.empty"), myTopPanel);

    //TODO: validation for remote file?
    if (!myIsRemote) {
      File physicalFile = new File(path);
      if (!physicalFile.exists()) {
        return new ValidationInfo(PhpBundle.message("quality.tool.file.not.found"), myTopPanel);
      }

      if (physicalFile.isDirectory()) {
        if (!containsRulesetXml(VfsUtil.findFileByIoFile(physicalFile, true))) {
          return new ValidationInfo(PhpBundle.message("quality.tool.php_cs.dir.does.not.contain.custom", PHP_CS, PHP_CS_DIST));
        }
      }
      else if (!isCodingStandardFile(path)) {
        return new ValidationInfo(PhpBundle.message("quality.tool.php_cs.custom.rules.validation.not.php_cs"));
      }
    }
    return null;
  }

  public static boolean isCodingStandardFile(@NotNull String path) {
    String extension = FileUtilRt.getExtension(path);
    return equalsIgnoreCase(extension, "php_cs") || equalsIgnoreCase(extension, "dist");
  }
}
