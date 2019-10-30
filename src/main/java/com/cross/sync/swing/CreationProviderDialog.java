package com.cross.sync.swing;

import com.cross.sync.exception.ProviderException;
import com.cross.sync.provider.LinuxProvider;
import com.cross.sync.provider.impl.LocalProvider;
import com.cross.sync.provider.impl.SSHProvider;
import com.cross.sync.swing.controller.ResourceController;
import com.cross.sync.util.Slf4fLogger;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;

public class CreationProviderDialog extends JDialog {
    private final JDialog parent;
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void onOK() {
        LinuxProvider provider;
        String name = nameField.getText();
        String host = hostField.getText();
        String user = loginField.getText();
        if (name == null || name.isBlank()) {
            return;
        }
        switch (providerClassName) {
            case "LocalProvider":
                provider = new LocalProvider();
                break;
            case "SSHProvider":
                if (host == null || host.isBlank() || user == null || user.isBlank()) {
                    return;
                }
                provider = new SSHProvider(host, user);
                try {
                    provider.ping();
                } catch (ProviderException e) {
                    Slf4fLogger.error(this, e.getMessage());
                    e.printStackTrace();
                    return;
                }
            default:
                return;
        }
        ResourceController.getInstance().getLinuxProviderMap().put(name, provider);
        ResourceController.getInstance().getTransfersByProvider().put(name, new HashSet<>());
        if (parent instanceof ProviderDialog) {
            ((ProviderDialog) parent).updateProviderList();
        }
        dispose();
    }

    CreationProviderDialog(JDialog parent, Boolean modal) {
        super(parent, modal);
        this.parent = parent;
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
