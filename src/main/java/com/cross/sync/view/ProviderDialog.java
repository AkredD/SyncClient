package com.cross.sync.view;

import com.cross.sync.view.controller.ResourceController;

import javax.swing.*;
import java.awt.*;

public class ProviderDialog extends JPanel {
    private boolean ok;
    private JDialog dialog;
    private JButton closeButton;
    private JList<Object> jProviders;
    private DefaultListModel providersModel;
    private int selectedProviderIndex = -1;

    public ProviderDialog() {
        /*DefaultListModel listModel = new DefaultListModel();
        listModel.addAll(ResourceController.getInstance().getLinuxProviderMap().keySet()
                .stream()
                .map(providerName -> {
                    JLabel label = new JLabel(providerName, JLabel.CENTER);
                    label.setSize(350, 20);
                    return label;
                })
                .collect(Collectors.toList()));
        jProviders = new JList(listModel);*/
        JPanel jpList = new JPanel();
        providersModel = new DefaultListModel();
        refreshProviders();
        jProviders = new JList<>(providersModel);
        jProviders.setPreferredSize(new Dimension(350, 200));
        jProviders.addListSelectionListener(ev -> {
            selectedProviderIndex = jProviders.getSelectedIndex();
        });
        JScrollPane scrollPane = new JScrollPane(jProviders);
        jpList.add(scrollPane);

        JButton addButton = new JButton("Add");
        JButton removeButton = new JButton("Remove");
        closeButton = new JButton("Close");

        addButton.addActionListener(ev -> {
            CreateProviderDialog createProviderDialog = new CreateProviderDialog(true, null);
            createProviderDialog.showDialog(this, "Add provider");
        });
        removeButton.addActionListener(ev -> {
            if (selectedProviderIndex != -1) {
                String providerName = (String) providersModel.get(selectedProviderIndex);
                ResourceController.getInstance().getLinuxProviderMap().remove(providerName);
                providersModel.remove(selectedProviderIndex);
            }
        });
        closeButton.addActionListener(ev -> dialog.setVisible(false));
        JPanel jpButton = new JPanel();
        jpButton.setLayout(new FlowLayout());
        jpButton.add(addButton);
        jpButton.add(new JLabel("          "));//dummy Label
        jpButton.add(removeButton);
        jpButton.add(new JLabel("          "));//dummy Label
        jpButton.add(closeButton);
        jpButton.setSize(350, 40);

        setSize(400, 325);
        add(new JLabel("  "), BorderLayout.EAST);
        add(new JLabel("  "), BorderLayout.WEST);
        add(jpList, BorderLayout.CENTER);
        add(jpButton, BorderLayout.SOUTH);
    }

    public boolean showDialog(Component parent, String title) {
        ok = false;
        Frame owner = null;
        if (parent instanceof Frame)
            owner = (Frame) parent;
        else
            owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
        if (dialog == null || dialog.getOwner() != owner) {
            dialog = new JDialog(owner, true);
            dialog.add(this);
            dialog.setResizable(false);
            dialog.getRootPane().setDefaultButton(closeButton);
            dialog.setSize(400, 325);
        }

        dialog.setTitle(title);
        dialog.setVisible(true);
//System.out.println(dialog.getWidth()+" "+dialog.getHeight());
        return ok;
    }

    public void refreshProviders() {
        providersModel.clear();
        providersModel.addAll(ResourceController.getInstance().getLinuxProviderMap().keySet());
    }
}
