package com.djk.yyy.form;


import com.djk.yyy.iface.OnCheckBoxStateChangedListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class EntryHeader extends JPanel {

    private JCheckBox mAllCheck;
    private JLabel mType;
    private JLabel mID;
    private JCheckBox mIsOptional;
    private JLabel mName;
    private OnCheckBoxStateChangedListener mAllListener;

    private OnCheckBoxStateChangedListener mAllOptionalListener;

    public void setmAllOptionalListener(final OnCheckBoxStateChangedListener onStateChangedListener){
        this.mAllOptionalListener = onStateChangedListener;
    }

    public void setAllListener(final OnCheckBoxStateChangedListener onStateChangedListener) {
        this.mAllListener = onStateChangedListener;
    }

    public EntryHeader() {
        mAllCheck = new JCheckBox();
        mAllCheck.setPreferredSize(new Dimension(40, 26));
        mAllCheck.setSelected(false);
        mAllCheck.addItemListener(new AllCheckListener());

        mType = new JLabel("Element");
        mType.setPreferredSize(new Dimension(100, 26));
        mType.setFont(new Font(mType.getFont().getFontName(), Font.BOLD, mType.getFont().getSize()));

        mID = new JLabel("ID");
        mID.setPreferredSize(new Dimension(100, 26));
        mID.setFont(new Font(mID.getFont().getFontName(), Font.BOLD, mID.getFont().getSize()));

        mIsOptional = new JCheckBox("isOptional");
        mIsOptional.setPreferredSize(new Dimension(100, 26));
        mIsOptional.setFont(new Font(mIsOptional.getFont().getFontName(), Font.BOLD, mIsOptional.getFont().getSize()));
        mIsOptional.addItemListener(new AllOptionalCheckListener());

        mName = new JLabel("Variable Name");
        mName.setPreferredSize(new Dimension(100, 26));
        mName.setFont(new Font(mName.getFont().getFontName(), Font.BOLD, mName.getFont().getSize()));

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        add(Box.createRigidArea(new Dimension(1, 0)));
        add(mAllCheck);
        add(Box.createRigidArea(new Dimension(11, 0)));
        add(mType);
        add(Box.createRigidArea(new Dimension(12, 0)));
        add(mID);
        add(Box.createRigidArea(new Dimension(6, 0)));
        add(mIsOptional);
        add(Box.createRigidArea(new Dimension(12, 0)));
        add(mName);
        add(Box.createHorizontalGlue());
    }

    public JCheckBox getAllCheck() {
        return mAllCheck;
    }

    public JCheckBox getmIsOptional() {
        return mIsOptional;
    }

    // classes

    private class AllCheckListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent itemEvent) {
            if (mAllListener != null) {
                mAllListener.changeState(itemEvent.getStateChange() == ItemEvent.SELECTED);
            }
        }
    }

    private class AllOptionalCheckListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent itemEvent) {
            if (mAllOptionalListener != null) {
                mAllOptionalListener.changeState(itemEvent.getStateChange() == ItemEvent.SELECTED);
            }
        }
    }
}
