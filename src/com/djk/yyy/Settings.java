package com.djk.yyy;

import com.djk.yyy.common.Utils;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Settings UI for the plugin.
 *
 * @author David Vávra (vavra@avast.com)
 */
public class Settings implements Configurable {

    public static final String PREFIX = "butterknifezelezny_prefix";

    private JPanel mPanel;
    private JTextField mPrefix;

    @Nls
    @Override
    public String getDisplayName() {
        return "KotterKnifePlugin";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        reset();
        return mPanel;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        PropertiesComponent.getInstance().setValue(PREFIX, mPrefix.getText());
    }

    @Override
    public void reset() {
        mPrefix.setText(Utils.getPrefix());
    }

    @Override
    public void disposeUIResources() {

    }
}
