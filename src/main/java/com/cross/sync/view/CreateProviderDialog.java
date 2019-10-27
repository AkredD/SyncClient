package com.cross.sync.view;

import com.cross.sync.exception.ProviderException;
import com.cross.sync.provider.LinuxProvider;
import com.cross.sync.provider.impl.LocalProvider;
import com.cross.sync.provider.impl.SSHProvider;

import javax.swing.*;
import java.awt.*;

public class CreateProviderDialog extends JPanel {
    private final String[] activeProviders = {"LocalProvider", "SSHProvider"};
    private JDialog dialog;
    private JButton saveButton;
    private LinuxProvider provider;
    private JPanel providerAttributePanel;
    private String name;

    public CreateProviderDialog(boolean newProvider, LinuxProvider provider) {
        JPanel central = new JPanel();
        central.setLayout(new GridLayout(2, 1));
        providerAttributePanel = new JPanel();
        JComboBox selectProvidersBox = new JComboBox(activeProviders);
        selectProvidersBox.setEditable(newProvider);
        if (!newProvider) {
            int providerIndex = 0;
            switch (provider.getClass().getSimpleName()) {
                case "LocalProvider":
                    providerIndex = 0;
                    break;
                case "SSHProvider":
                    providerIndex = 1;
                    break;
            }
            selectProvidersBox.setSelectedIndex(providerIndex);
        } else {
            updateProviderInput("LocalProvider");
        }
        selectProvidersBox.addActionListener(e -> {
            updateProviderInput((String) selectProvidersBox.getItemAt(selectProvidersBox.getSelectedIndex()));
        });
        central.add(selectProvidersBox);
        central.add(providerAttributePanel);
        saveButton = new JButton((newProvider) ? "Add" : "Edit");
        JButton cancelButton = new JButton("Cancel");
        saveButton.addActionListener(ev -> {
            try {
                if (provider.ping()) {
                    // TODO
                }
            } catch (ProviderException e) {
                e.printStackTrace();
            }
        });
        cancelButton.addActionListener(ev -> dialog.setVisible(false));

        JPanel jpButton = new JPanel();
        jpButton.setLayout(new FlowLayout());
        jpButton.add(saveButton);
        jpButton.add(new JLabel("          "));//dummy Label
        jpButton.add(cancelButton);
        jpButton.setSize(350, 40);

        add(new JLabel("  "), BorderLayout.EAST);
        add(new JLabel("  "), BorderLayout.WEST);
        add(central, BorderLayout.CENTER);
        add(jpButton, BorderLayout.SOUTH);
    }

    public void updateProviderInput(String providerName) {
        providerAttributePanel.removeAll();
        switch (providerName) {
            case "LocalProvider":
                provider = new LocalProvider();
                providerAttributePanel.setLayout(new GridLayout(1, 2));
                break;
            case "SSHProvider":
                provider = new SSHProvider(null, null);
                providerAttributePanel.add(new JLabel("Host"));
                JTextField inputHost = new JTextField();
                inputHost.addActionListener(al -> {
                    ((SSHProvider) provider).setHost(inputHost.getText());
                });
                providerAttributePanel.add(new JLabel("Login"));
                JTextField inputKey = new JTextField();
                inputKey.addActionListener(al -> {
                    ((SSHProvider) provider).setPublicKey(inputKey.getText());
                });
                providerAttributePanel.setLayout(new GridLayout(3, 2));
        }
        providerAttributePanel.add(new JLabel("Name"));
        JTextField inputName = new JTextField();
        inputName.addActionListener(al -> {
            name = inputName.getText();
        });
        providerAttributePanel.add(inputName);
    }

    public void showDialog(Component parent, String title) {
        Frame owner = null;
        if (parent instanceof Frame)
            owner = (Frame) parent;
        else
            owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
        if (dialog == null || dialog.getOwner() != owner) {
            dialog = new JDialog(owner, true);
            dialog.add(this);
            dialog.setResizable(false);
            dialog.getRootPane().setDefaultButton(saveButton);
            dialog.setSize(400, 100);
        }

        dialog.setTitle(title);
        dialog.setVisible(true);
//System.out.println(dialog.getWidth()+" "+dialog.getHeight());
    }
}
