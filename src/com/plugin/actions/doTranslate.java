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
    private String from = "en";
    private String to = "zh_CHS";
    private String method = "POST";
    private volatile boolean connectException = false;
    private volatile boolean exception = false;
    private volatile int before = 1;
    private volatile int after = 1;
    private volatile String ans = "...";

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        final Editor editor = anActionEvent.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);
        final SelectionModel selectionModel = editor.getSelectionModel();
        String selectedContent = selectionModel.getSelectedText();
        if (StringUtils.isBlank(selectedContent)) {
            return;
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // System.out.println("=======开始翻译了=======");
                try {
                    ans = Util.parseAnswer(Util.httpRequest(YouDaoBasic.getUrlWithQueryString(
                            YouDaoBasic.getPreUrl(), YouDaoBasic.queryInfo(selectedContent, from, to)
                    ), method));
                    after = -after;
                } catch (ConnectException e) {
                    connectException = true;
                    e.printStackTrace();
                }catch (Exception e){
                    exception = true;
                    e.printStackTrace();
                }
            }
        });
        thread.start();


        while (true) {
            if (connectException){
                connectException = false;
                Messages.showMessageDialog("Youdao server connection timed out", "连接异常", Messages.getErrorIcon());
                break;
            }
            if (exception) {
                exception = false;
                Messages.showMessageDialog("http request error:{}", "请求异常", Messages.getErrorIcon());
                break;
            }
            if (before != after) {
                before = after;
                Messages.showMessageDialog(ans, "翻译结果", SpellcheckerIcons.Spellcheck);
                break;
            }
        }
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
    }
}
