package com.cross.sync.swing;

import com.cross.sync.transfer.Transfer;
import com.cross.sync.transfer.TransferScheduler;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("RedundantSuppression")
class TransformationRow extends JPanel {
    private JPanel transformRow;
    private JLabel Status;
    private JProgressBar transformStatus;
    private JButton statusButton;
    private JLabel transformName;
    private JButton detailButton;
    private Transfer transfer;

    TransformationRow(JFrame parent, Transfer transfer, String name) {
        transformRow.setSize(350, 30);
        this.transfer = transfer;
        transformName.setText(name);
        Status.setText(TransferScheduler.getInstance().isTransferScheduling(transfer) ? "scheduling" : "stop");
        transformStatus.setMinimum(0);
        transformStatus.setMaximum(100);
        transformStatus.setStringPainted(true);

        statusButton.addActionListener(i -> {
            switch (statusButton.getText()) {
                case "stop":
                    TransferScheduler.getInstance().deleteFromScheduling(transfer);
                    break;
                case "start":
                    TransferScheduler.getInstance().addForScheduling(transfer);
                    break;
                case "retry":
                    TransferScheduler.getInstance().deleteFromScheduling(transfer);
                    TransferScheduler.getInstance().addForScheduling(transfer);
                    break;
            }
            updateStatusButton();
        });

        detailButton.addActionListener(i -> {
            JDialog dialog = new LogDialog(parent, transfer.getLog());
            dialog.setLocationRelativeTo(parent);
            dialog.setVisible(true);
        });

        updateStatus();
        updateStatusButton();
    }

    JPanel getContent() {
        return transformRow;
    }

    void updateStatus() {
        transformStatus.setValue(transfer.getStatus());
        updateStatusButton();
    }

    private void updateStatusButton() {
        statusButton.setEnabled(true);
        Status.setText(TransferScheduler.getInstance().isTransferScheduling(transfer)
                ?
                transfer.isRun()
                        ? "synchronizing"
                        : transfer.isInterrupted() ? "interrupted" : "scheduling"
                : "stopped");
        statusButton.setText(TransferScheduler.getInstance().isTransferScheduling(transfer)
                ? transfer.isInterrupted() ? "retry" : "stop"
                : "start");
        if (transfer.getReadProvider().isClosed() || transfer.getWriteProvider().isClosed()) {
            Status.setText("stopped(provider closed)");
            statusButton.setText("start");
            statusButton.setEnabled(false);
        }
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
        transformRow = new JPanel();
        transformRow.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
        transformRow.setMaximumSize(new Dimension(1800, 70));
        transformRow.setMinimumSize(new Dimension(410, -1));
        transformRow.setPreferredSize(new Dimension(-1, 50));
        Status = new JLabel();
        Status.setText("Label");
        transformRow.add(Status, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        transformStatus = new JProgressBar();
        transformRow.add(transformStatus, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        statusButton = new JButton();
        statusButton.setText("Button");
        transformRow.add(statusButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, new Dimension(100, -1), 0, false));
        detailButton = new JButton();
        detailButton.setText("details");
        transformRow.add(detailButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, new Dimension(100, -1), 0, false));
        transformName = new JLabel();
        transformName.setText("Label");
        transformRow.add(transformName, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return transformRow;
    }

}
