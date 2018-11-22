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
import icons.SpellcheckerIcons;
import org.apache.commons.lang.StringUtils;

import java.net.ConnectException;

/**
 * @author hongyuzhou
 * @version V1.0
 * @date 2017/7/14
 * @since JDK1.8
 */
public class doTranslate extends AnAction {

    private static String from = "en";

    private static String to = "zh_CHS";

    private static String method = "POST";

    private static volatile boolean connectException = false;

    private static volatile boolean requestException = false;

    private static volatile String ans = "...";

    static class StartTrans implements Runnable {

        private String selectedContent;

        StartTrans(String selectedContent) {
            this.selectedContent = selectedContent;
        }

        @Override
        public void run() {
            try {
                ans = Util.parseAnswer(Util.httpRequest(YouDaoBasic.getUrlWithQueryString(
                        YouDaoBasic.getPreUrl(), YouDaoBasic.queryInfo(selectedContent, from, to)
                ), method));
            } catch (ConnectException e) {
                connectException = true;
                e.printStackTrace();
            } catch (Exception e) {
                requestException = true;
                e.printStackTrace();
            }
        }
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        final Editor editor = anActionEvent.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);
        final SelectionModel selectionModel = editor.getSelectionModel();
        final String selectedContent = selectionModel.getSelectedText();
        if (StringUtils.isBlank(selectedContent)) {
            return;
        }

        Thread translating = new Thread(new StartTrans(selectedContent), "translating");
        translating.start();
        try {
            translating.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (connectException) {
            connectException = false;
            Messages.showMessageDialog("Youdao server connection timed out", "连接异常", Messages.getErrorIcon());
            return;
        }
        if (requestException) {
            requestException = false;
            Messages.showMessageDialog("http request error:{}", "请求异常", Messages.getErrorIcon());
            return;
        }
        Messages.showMessageDialog(ans, "翻译结果", SpellcheckerIcons.Spellcheck);

    }


    @Override
    public void update(AnActionEvent e) {
        super.update(e);
    }
}
