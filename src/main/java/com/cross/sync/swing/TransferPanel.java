package com.cross.sync.swing;

import com.cross.sync.swing.controller.ResourceController;
import com.cross.sync.transfer.TransferScheduler;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;

class TransferPanel {
    private final DefaultListModel<String> jobsModel = new DefaultListModel<>();
    private JPanel panel1;
    private JList<String> jobsList;
    private JButton addButton;
    private JButton deleteButton;
    private JButton changeButton;

    TransferPanel(SettingsDialog settingsDialog) {
        addButton.addActionListener(e -> {
            JDialog dialog = new CreationJobDialog(settingsDialog, true, this::updateJobList, null);
            dialog.setLocationRelativeTo(settingsDialog);
            dialog.setVisible(true);
        });

        changeButton.addActionListener(e -> {
            if (jobsList.getSelectedValue() != null) {
                JDialog dialog = new CreationJobDialog(settingsDialog, true, this::updateJobList, ResourceController.getInstance().getTransferMap().get(jobsList.getSelectedValue()));
                dialog.setLocationRelativeTo(settingsDialog);
                dialog.setVisible(true);
            }
        });

        deleteButton.addActionListener(e -> {
            if (jobsList.getSelectedValue() != null) {
                String transferName = jobsList.getSelectedValue();
                ResourceController.getInstance().getTransferMap().remove(transferName);
                TransferScheduler.getInstance().deleteFromScheduling(ResourceController.getInstance().getTransferMap().get(transferName));
                updateJobList();
            }
        });

        jobsList.setModel(jobsModel);

        updateJobList();

    }

    private void updateJobList() {
        jobsModel.clear();
        jobsModel.addAll(ResourceController.getInstance().getTransferMap().keySet());
    }

    JPanel getPanel() {
        return panel1;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        jobsList = new JList();
        scrollPane1.setViewportView(jobsList);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        addButton = new JButton();
        addButton.setText("+");
        panel2.add(addButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(40, -1), null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        deleteButton = new JButton();
        deleteButton.setText("-");
        panel2.add(deleteButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(40, -1), null, 0, false));
        changeButton = new JButton();
        changeButton.setText("Change");
        panel2.add(changeButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}
