package com.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.plugin.youdao.Util;
import com.plugin.youdao.YouDaoBasic;
import org.apache.commons.lang.StringUtils;

/**
 * @author hongyuzhou
 * @version V1.0
 * @date 2017/7/14
 * @since JDK1.8
 */
public class doTranslate extends AnAction {
    private String from = "en";
    private String to = "zh_CHS";
    private String method = "POST";

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        final Editor editor = anActionEvent.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);
        // final Document document = editor.getDocument();
        final SelectionModel selectionModel = editor.getSelectionModel();
        String selectedContent = selectionModel.getSelectedText();
        if (StringUtils.isBlank(selectedContent)) {
            return;
        }

        // 获得翻译结果
        String translateAns = Util.parseAnswer(
                Util.httpRequest(YouDaoBasic.getUrlWithQueryString(
                        YouDaoBasic.getPreUrl(), YouDaoBasic.queryInfo(selectedContent, from, to)
                ), method)
        );
        Messages.showMessageDialog(translateAns, "Translate Answer", Messages.getInformationIcon());
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
    }
}
