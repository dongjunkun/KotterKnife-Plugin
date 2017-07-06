package com.djk.yyy.kotterknife;

import com.djk.yyy.common.Utils;
import com.djk.yyy.model.Element;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.ElementType;
import org.jetbrains.kotlin.psi.KtClass;
import org.jetbrains.kotlin.psi.KtPsiFactory;
import org.jetbrains.kotlin.resolve.jvm.KotlinJavaPsiFacade;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/5 0005.
 */
public class InjectWriter extends WriteCommandAction.Simple {

    protected ArrayList<Element> mElements;
    protected KtClass mKtClass;
    protected KtPsiFactory mFactory;

    public InjectWriter(PsiFile file, KtClass ktClass, String command, ArrayList<Element> elements, String name, String fieldNamePrefix, boolean createHolder) {
        super(ktClass.getProject(), command);
        mElements = elements;
        mKtClass = ktClass;
        mFactory = new KtPsiFactory(mKtClass.getProject());
    }

    @Override
    protected void run() throws Throwable {

        if (Utils.getInjectCount(mElements) > 0) {
            generateFields();
        }

    }

    private void generateFields() {

        for (int i = mElements.size() - 1; i >= 0; i--) {
            Element mElement = mElements.get(i);

            if (!mElement.used){
                continue;
            }
            StringBuilder builder = new StringBuilder();

            builder.append("val ");
            builder.append(mElement.fieldName);
            builder.append(": ");
            builder.append(mElement.name);
            if (mElement.isOptional) {
                builder.append("? by bindOptionalView(");
            } else {
                builder.append(" by bindView(");
            }
            builder.append(mElement.getFullID());
            builder.append(")");
//            String s = "val " +
//                    mElement.fieldName +
//                    ": " +
//                    mElement.name +
//                    " by bindView(" +
//                    mElement.getFullID() +
//                    ")\n";

//            mKtClass.add(mFactory.createProperty(s));
//            System.out.println("mKtClass = " + mKtClass.getColon());
//            System.out.println("mKtClass = " + mKtClass.getClassOrInterfaceKeyword());
//            for (PsiElement psiElement : mKtClass.getChildren()) {
//                System.out.println("psiElement = " + psiElement);
//            }
//
//            System.out.println("mKtClass = " + mKtClass.getParent());
//            System.out.println("mKtClass = " + mKtClass.getContext());
//            System.out.println("mKtClass = " + mKtClass.getPsiOrParent());
//            System.out.println("mKtClass = " + mKtClass.getNameIdentifier());
//            System.out.println("mKtClass = " + mKtClass.getNavigationElement());
//            System.out.println("mKtClass = " + mKtClass.getOriginalElement());
//            System.out.println("mKtClass = " + mKtClass.getFirstChild());
//            System.out.println("mKtClass = " + mKtClass.getLastChild());
//            System.out.println("mKtClass = " + mKtClass.getNextSibling());
//            System.out.println("mKtClass = " + mKtClass.getPrevSibling());
//            System.out.println("mKtClass = " + mKtClass.getBody().getLBrace());
//            System.out.println("mKtClass = " + mKtClass.getBody().getContext());

            if (mKtClass.getBody() != null) {
                mKtClass.addAfter(mFactory.createProperty(String.valueOf(builder)), mKtClass.getBody().getFirstChild());

//                for (PsiElement psiElement : mKtClass.getBody().getChildren()) {
//                    if (psiElement.getNode().getElementType().toString().equals("FUN")) {
//                        mKtClass.addBefore(mFactory.createProperty(s), psiElement);
//
//                        break;
//                    }
//                }
            }


        }

    }

}
