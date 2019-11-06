package com.cross.sync.swing;

import com.cross.sync.provider.Provider;
import com.cross.sync.swing.controller.ResourceController;
import com.cross.sync.transfer.Transfer;
import com.cross.sync.transfer.TransferScheduler;
import com.cross.sync.transfer.impl.FullTempTransfer;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.stream.Collectors;

@SuppressWarnings({"ALL", "RedundantSuppression"})
public class CreationJobDialog extends JDialog {
    private final JDialog parent;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel transferAttrPanel;
    private JTextField nameField;
    private JTextField pathFromField;
    private JTextField pathToField;
    private JComboBox<Object> providerFromBox;
    private JComboBox<Object> providerToBox;
    private com.cross.sync.util.Action updateAction;
    private Transfer originTransfer;

    CreationJobDialog(JDialog parent, Boolean modal, com.cross.sync.util.Action updateAction, Transfer transfer) {
        super(parent, modal);
        this.originTransfer = transfer;
        setTitle("Job creation");
        this.updateAction = updateAction;
        this.parent = parent;
        setContentPane(contentPane);
        setSize(250, 230);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        formTransferAttrPanel();

        if (originTransfer != null) {
            nameField.setText(originTransfer.getName());
            providerToBox.getModel().setSelectedItem(originTransfer.getWriteProvider().getName());
            providerFromBox.getModel().setSelectedItem(originTransfer.getReadProvider().getName());
            pathToField.setText(originTransfer.getWritePath());
            pathFromField.setText(originTransfer.getReadPath());
            buttonOK.setText("Recreate");
        }
    }

    private void onCancel() {
        // add your code here if necessary
        updateAction.doAction();
        dispose();
    }

    private void formTransferAttrPanel() {
        transferAttrPanel.setLayout(new GridLayout(5, 2, 0, 0));
        Object[] providers = ResourceController.getInstance().getLinuxProviderMap().values()
                .stream()
                .filter(provider -> !provider.isClosed())
                .map(provider -> provider.getName())
                .collect(Collectors.toList())
                .toArray();
        ComboBoxModel<Object> providersModelFrom = new DefaultComboBoxModel<>(providers);
        ComboBoxModel<Object> providersModelTo = new DefaultComboBoxModel<>(providers);
        providerFromBox = new JComboBox<>(providersModelFrom);
        providerFromBox.setEditable(false);
        providerToBox = new JComboBox<Object>(providersModelTo);
        providerToBox.setEditable(false);
        JLabel nameLabel = new JLabel("name");
        JLabel providerFromLabel = new JLabel("FROM provider");
        JLabel providerToLabel = new JLabel("TO provider");
        JLabel pathFromLabel = new JLabel("FROM path");
        JLabel pathToLabel = new JLabel("TO path");
        pathFromField = new JTextField();
        pathToField = new JTextField();
        nameField = new JTextField();
        transferAttrPanel.add(nameLabel);
        transferAttrPanel.add(nameField);
        transferAttrPanel.add(providerFromLabel);
        transferAttrPanel.add(providerFromBox);
        transferAttrPanel.add(pathFromLabel);
        transferAttrPanel.add(pathFromField);
        transferAttrPanel.add(providerToLabel);
        transferAttrPanel.add(providerToBox);
        transferAttrPanel.add(pathToLabel);
        transferAttrPanel.add(pathToField);

    }

    private void onOK() {
        // add your code here
        if (pathFromField.getText() == null || pathFromField.getText().isBlank()
                || pathToField.getText() == null || pathToField.getText().isBlank()
                || nameField.getText() == null || nameField.getText().isBlank()
                || providerToBox.getSelectedItem() == null || providerFromBox.getSelectedItem() == null) {
            JDialog dialogError = new ExceptionDialog(this, "Please, fill all fields");
            dialogError.setVisible(true);
            return;
        }
        Provider fromProvider = ResourceController.getInstance().getLinuxProviderMap().get(providerFromBox.getSelectedItem());
        Provider toProvider = ResourceController.getInstance().getLinuxProviderMap().get(providerToBox.getSelectedItem());
        if (!validatePathes(fromProvider, pathFromField.getText(), toProvider, pathToField.getText())) {
            return;
        }
        if (originTransfer != null) {
            TransferScheduler.getInstance().deleteFromScheduling(originTransfer);
            ResourceController.getInstance().getTransferMap().remove(originTransfer.getName());
            ResourceController.getInstance().getTransfersByProvider().get(originTransfer.getReadProvider().getName()).remove(originTransfer);
            ResourceController.getInstance().getTransfersByProvider().get(originTransfer.getWriteProvider().getName()).remove(originTransfer);
        }
        Transfer transfer = new FullTempTransfer(nameField.getText(), fromProvider, pathFromField.getText(), toProvider, pathToField.getText());
        ResourceController.getInstance().getTransferMap().put(nameField.getText(), transfer);
        ResourceController.getInstance().getTransfersByProvider().get(providerFromBox.getSelectedItem()).add(transfer);
        ResourceController.getInstance().getTransfersByProvider().get(providerToBox.getSelectedItem()).add(transfer);
        updateAction.doAction();
        dispose();
    }

    private boolean validatePathes(Provider fromProvider, String fromPath, Provider toProvider, String toPath) {
        JDialog dialogFrom = null;
        JDialog dialogTo = null;
        if (!fromProvider.validatePath(fromPath)) {
            dialogFrom = new ExceptionDialog(this, String.format("From path '%s' is not valide", fromPath));
        } else if (!fromProvider.existFile(fromPath)) {
            dialogFrom = new ExceptionDialog(this, String.format("From file '%s' doesn't exists", fromPath));
        } else if (!fromProvider.canRead(fromPath)) {
            dialogFrom = new ExceptionDialog(this, String.format("Can't read from file '%s'", fromPath));
        } else if (!toProvider.validatePath(toPath)) {
            dialogTo = new ExceptionDialog(this, String.format("To path '%s' is not valide", toPath));
        } else if (!toProvider.canWrite(toPath)) {
            dialogTo = new ExceptionDialog(this, String.format("Can't write to path '%s'", toPath));
        }
        if (dialogFrom != null) {
            dialogFrom.setVisible(true);
            return false;
        }
        if (dialogTo != null) {
            dialogTo.setVisible(true);
            return false;
        }
        return true;
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
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel2.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        transferAttrPanel = new JPanel();
        transferAttrPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(transferAttrPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
