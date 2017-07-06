package com.djk.yyy.kotterknife;

import com.djk.yyy.common.Definitions;
import com.djk.yyy.common.Utils;
import com.djk.yyy.form.EntryList;
import com.djk.yyy.iface.ICancelListener;
import com.djk.yyy.iface.IConfirmListener;
import com.djk.yyy.model.Element;
import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtilBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.idea.internal.Location;
import org.jetbrains.kotlin.psi.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/5 0005.
 */
public class InjectAction extends BaseGenerateAction implements IConfirmListener, ICancelListener {


    protected JFrame mDialog;
    protected static final Logger log = Logger.getInstance(InjectAction.class);

    public InjectAction() {
        super(null);
    }

    public InjectAction(CodeInsightActionHandler handler) {
        super(handler);
    }

    @Override
    protected boolean isValidForClass(PsiClass targetClass) {
//        return super.isValidForClass(targetClass);
        return true;
    }


    @Override
    protected boolean isValidForFile(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
//        return super.isValidForFile(project, editor, file);
        return true;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        assert project != null;
        actionPerformedImpl(project, editor);
    }

    @Override
    public void actionPerformedImpl(@NotNull Project project, Editor editor) {

        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        PsiFile layout = Utils.getLayoutFileFromCaret(editor, file);

        if (layout == null) {
            Utils.showErrorNotification(project, "layout not found");
            return;
        }

        ArrayList<Element> elements = Utils.getIDsFromLayout(layout);
        if (!elements.isEmpty()) {
            showDialog(project, editor, elements);
        } else {
            Utils.showErrorNotification(project, "No IDs found in layout");
        }
    }

    public void onCancel() {
        closeDialog();
    }

    protected void showDialog(Project project, Editor editor, ArrayList<Element> elements) {
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        if (file == null) {
            return;
        }
//        PsiClass clazz = getTargetClass(editor, file);
        KtClass ktClass = getPsiClassFromEvent(editor);

        if (ktClass == null) {
            return;
        }

        // get parent classes and check if it's an adapter
        boolean createHolder = false;
//        PsiReferenceList list = mKtClass.getExtendsList();
//        KtTypeConstraintList list = mKtClass.getTypeConstraintList();
//        if (list != null) {
//            list.getReference()
//            for (PsiJavaCodeReferenceElement element : list.getReferenceElements()) {
//                if (Definitions.adapters.contains(element.getQualifiedName())) {
//                    createHolder = true;
//                }
//            }
//        }

        // get already generated injections
        ArrayList<String> ids = new ArrayList<>();
//        PsiField[] fields = clazz.getAllFields();
        List<KtProperty> properties = ktClass.getProperties();


//        String id;

        for (KtProperty property : properties) {

            String text = property.getText();

//            for (String annotation : annotations) {
//                id = Utils.getInjectionID(butterKnife, annotation.trim());
//                if (!Utils.isEmptyString(id)) {
//                }
//            }
            if (text.contains("by bindOptionalView") || text.contains("by bindView")) {
                text = text.substring(text.indexOf("(") + 1, text.indexOf(")")).trim();
                System.out.println("text = " + text);
            }
            ids.add(text);
        }


        EntryList panel = new EntryList(project, editor, elements, ids, createHolder, this, this);

        mDialog = new JFrame();
        mDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        mDialog.getRootPane().setDefaultButton(panel.getConfirmButton());
        mDialog.getContentPane().add(panel);
        mDialog.pack();
        mDialog.setLocationRelativeTo(null);
        mDialog.setVisible(true);
    }

    private void closeDialog() {
        if (mDialog == null) {
            return;
        }

        mDialog.setVisible(false);
        mDialog.dispose();
    }

    @Override
    public void onConfirm(Project project, Editor editor, ArrayList<Element> elements, String fieldNamePrefix, boolean createHolder, boolean splitOnclickMethods) {
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        if (file == null) {
            return;
        }
        PsiFile layout = Utils.getLayoutFileFromCaret(editor, file);


        closeDialog();

        KtClass ktClass = getPsiClassFromEvent(editor);


        if (Utils.getInjectCount(elements) > 0) { // generate injections
            new InjectWriter(file, ktClass, "Generate Injections", elements, layout.getName(), fieldNamePrefix, createHolder).execute();
        } else { // just notify user about no element selected
            Utils.showInfoNotification(project, "No injection was selected");
        }
    }

    private KtClass getPsiClassFromEvent(Editor editor) {
//        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        assert editor != null;

        Project project = editor.getProject();
        if (project == null) return null;

        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile == null || !(psiFile instanceof KtFile))
            return null;

        Location location = Location.fromEditor(editor, project);
        PsiElement psiElement = psiFile.findElementAt(location.getStartOffset());
        if (psiElement == null) return null;

        return Utils.getKtClassForElement(psiElement);
    }
}
