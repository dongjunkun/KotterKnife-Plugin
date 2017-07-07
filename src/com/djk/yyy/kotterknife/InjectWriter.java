package com.djk.yyy.kotterknife;

import com.djk.yyy.common.Definitions;
import com.djk.yyy.common.Utils;
import com.djk.yyy.model.Element;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiFile;
import org.jetbrains.kotlin.idea.caches.resolve.ResolutionUtils;
import org.jetbrains.kotlin.idea.util.ImportInsertHelper;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.psi.KtClass;
import org.jetbrains.kotlin.psi.KtFile;
import org.jetbrains.kotlin.psi.KtImportDirective;
import org.jetbrains.kotlin.psi.KtPsiFactory;
import org.jetbrains.kotlin.resolve.ImportPath;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

        HashSet<String> set = new HashSet<>();

        for (int i = mElements.size() - 1; i >= 0; i--) {
            Element element = mElements.get(i);

            if (!element.used) {
                continue;
            }
            StringBuilder builder = new StringBuilder();

            builder.append("val ");
            builder.append(element.fieldName);
            builder.append(": ");
            builder.append(element.name);
            if (element.isOptional) {
                builder.append("? by bindOptionalView(");
                set.add("kotterknife.bindOptionalView");
            } else {
                builder.append(" by bindView(");
                set.add("kotterknife.bindView");
            }

            if (element.nameFull != null && element.nameFull.length() > 0) { // custom package+class
                set.add(element.nameFull);
            } else if (Definitions.paths.containsKey(element.name)) { // listed class
                set.add(Definitions.paths.get(element.name));
            } else { // android.widget
                set.add("android.widget."+element.name);
            }
            builder.append(element.getFullID());
            builder.append(")");

            if (mKtClass.getBody() != null) {
                mKtClass.addAfter(mFactory.createProperty(String.valueOf(builder)), mKtClass.getBody().getFirstChild());

            }


        }
        insertImports(mKtClass.getContainingKtFile(), set);

    }

    private void insertImports(KtFile ktFile, HashSet<String> set) {
        // Check if already imported Parcel and Parcelable
        for (String path : set) {
            List<KtImportDirective> importList = ktFile.getImportDirectives();
            for (KtImportDirective importDirective : importList) {
                ImportPath importPath = importDirective.getImportPath();
                if (importPath != null) {
                    String pathStr = importPath.getPathStr();
                    if (!pathStr.equals(path)) {
                        ImportInsertHelper.getInstance(ktFile.getProject())
                                .importDescriptor(ktFile, ResolutionUtils.resolveImportReference(ktFile, new FqName(path)).iterator().next(), false);
                    }
                }
            }
        }

    }

}
