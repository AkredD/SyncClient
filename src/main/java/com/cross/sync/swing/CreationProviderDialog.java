package com.cross.sync.swing;

import com.cross.sync.exception.ProviderException;
import com.cross.sync.provider.Provider;
import com.cross.sync.provider.impl.LocalProvider;
import com.cross.sync.provider.impl.SSHProvider;
import com.cross.sync.swing.controller.ResourceController;
import com.cross.sync.transfer.TransferScheduler;
import com.cross.sync.util.Slf4fLogger;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashSet;

class CreationProviderDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox<String> comboBox1;
    private JPanel AttrPane;
    private ComboBoxModel<String> comboBoxModel;
    private String providerClassName = "LocalProvider";
    private JTextField nameField;
    private JTextField hostField;
    private JTextField loginField;
    private com.cross.sync.util.Action updateAction;
    private Provider originProvider;
    private String originName;

    CreationProviderDialog(JDialog parent, com.cross.sync.util.Action updateAction, Provider provider) {
        super(parent, true);
        this.originProvider = provider;
        this.originName = provider != null ? provider.getName() : null;
        this.updateAction = updateAction;
        setTitle("Provider creation");
        setContentPane(contentPane);
        setModal(true);
        setSize(250, 200);
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

        comboBox1.addActionListener(ev -> {
            providerClassName = (String) comboBoxModel.getSelectedItem();
            switch (providerClassName) {
                case "LocalProvider":
                    hostField.setEnabled(false);
                    loginField.setEnabled(false);
                    break;
                case "SSHProvider":
                    hostField.setEnabled(true);
                    loginField.setEnabled(true);
            }
        });
        formAttrPane();
        hostField.setEnabled(false);
        loginField.setEnabled(false);

        if (provider != null) {
            buttonOK.setText("Recreate");
            nameField.setText(originName);
            if (provider instanceof SSHProvider) {
                hostField.setText(((SSHProvider) provider).getHost());
                loginField.setText(((SSHProvider) provider).getPublicKey());
                comboBox1.setSelectedIndex(1);
            } else {
                comboBox1.setSelectedIndex(0);
            }
        }


    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void formAttrPane() {
        String[] providerClasses = {"LocalProvider", "SSHProvider"};
        comboBoxModel = new DefaultComboBoxModel<>(providerClasses);
        comboBox1.setModel(comboBoxModel);
        comboBox1.setEditable(false);

        AttrPane.setLayout(new GridLayout(3, 2, 0, 0));
        JLabel nameLabel = new JLabel("Name");
        JLabel hostLabel = new JLabel("URL");
        JLabel loginLabel = new JLabel("User");
        nameField = new JTextField();
        hostField = new JTextField();
        loginField = new JTextField();
        AttrPane.add(nameLabel);
        AttrPane.add(nameField);
        AttrPane.add(hostLabel);
        AttrPane.add(hostField);
        AttrPane.add(loginLabel);
        AttrPane.add(loginField);
    }

    private void onOK() {
        Provider provider;
        String name = nameField.getText();
        String host = hostField.getText();
        String user = loginField.getText();
        if (name == null || name.isBlank()) {
            JDialog dialogError = new ExceptionDialog(this, "Please, fill name field");
            dialogError.setVisible(true);
            return;
        }
        switch (providerClassName) {
            case "LocalProvider":
                provider = new LocalProvider(name);
                break;
            case "SSHProvider":
                if (host == null || host.isBlank() || user == null || user.isBlank()) {
                    JDialog dialogError = new ExceptionDialog(this, "Please, fill host,and user fields");
                    dialogError.setVisible(true);
                    return;
                }
                provider = new SSHProvider(name, host, user);
                break;
            default:
                return;
        }
        try {
            provider.open();
            provider.ping();
        } catch (ProviderException e) {
            Slf4fLogger.error(this, e.getMessage());
            JDialog dialogError = new ExceptionDialog(this, e.getMessage());
            dialogError.setVisible(true);
            return;
        }
        if (originProvider != null) {
            ResourceController.getInstance().getTransfersByProvider().remove(originName).forEach(transfer -> {
                TransferScheduler.getInstance().deleteFromScheduling(transfer);
                ResourceController.getInstance().getTransferMap().remove(transfer.getName());
            });
            try {
                ResourceController.getInstance().getLinuxProviderMap().remove(originName).close();
            } catch (IOException e) {
                Slf4fLogger.error(this, e.getMessage());
            }
        }
        ResourceController.getInstance().getLinuxProviderMap().put(name, provider);
        ResourceController.getInstance().getTransfersByProvider().put(name, new HashSet<>());
        updateAction.doAction();
        dispose();
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
        buttonOK.setText("Save");
        panel2.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new BorderLayout(0, 0));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        comboBox1 = new JComboBox();
        comboBox1.putClientProperty("html.disable", Boolean.FALSE);
        panel3.add(comboBox1, BorderLayout.NORTH);
        AttrPane = new JPanel();
        AttrPane.setLayout(new GridBagLayout());
        panel3.add(AttrPane, BorderLayout.CENTER);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
