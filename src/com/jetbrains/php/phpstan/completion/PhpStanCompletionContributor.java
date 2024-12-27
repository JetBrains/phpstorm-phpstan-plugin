package com.jetbrains.php.phpstan.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.project.DumbAware;
import com.jetbrains.php.completion.PhpTraitDocTagCompletionProvider;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.jetbrains.php.lang.documentation.phpdoc.lexer.PhpDocTokenTypes.DOC_TAG_NAME;

public final class PhpStanCompletionContributor extends CompletionContributor implements DumbAware {
  private static final String[] ADDITIONAL_TRAIT_DOC_TAG_COMPLETIONS = {
    "phpstan-require-extends",
    "phpstan-require-implements"
  };

  public PhpStanCompletionContributor() {
    extend(CompletionType.BASIC, psiElement(DOC_TAG_NAME), new PhpTraitDocTagCompletionProvider(ADDITIONAL_TRAIT_DOC_TAG_COMPLETIONS));
  }

}
