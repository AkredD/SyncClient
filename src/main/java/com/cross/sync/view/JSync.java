package com.cross.sync.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class JSync implements ActionListener, MenuConstants {
    final String applicationName = "JSync";
    private JFrame f;
    private JTextArea ta;
    private boolean saved = true;
    private ProviderDialog providerDialog = new ProviderDialog();

    public JSync() {
        f = new JFrame(applicationName);
        ta = new JTextArea(30, 60);
        f.add(new JScrollPane(ta), BorderLayout.CENTER);
        f.add(new JLabel("  "), BorderLayout.SOUTH);
        f.add(new JLabel("  "), BorderLayout.EAST);
        f.add(new JLabel("  "), BorderLayout.WEST);
        createMenuBar(f);
//f.setSize(350,350);
        f.pack();
        f.setLocation(100, 50);
        f.setVisible(true);
        f.setLocation(150, 50);
        f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    JMenu createMenu(String s, int key, JMenuBar toMenuBar) {
        JMenu temp = new JMenu(s);
        temp.setMnemonic(key);
        toMenuBar.add(temp);
        return temp;
    }

    JMenuItem createMenuItem(String s, int key, JMenu toMenu, ActionListener al) {
        JMenuItem temp = new JMenuItem(s, key);
        temp.addActionListener(al);
        toMenu.add(temp);

        return temp;
    }

    JMenuItem createMenuItem(String s, int key, JMenu toMenu, int aclKey, ActionListener al) {
        JMenuItem temp = new JMenuItem(s, key);
        temp.addActionListener(al);
        temp.setAccelerator(KeyStroke.getKeyStroke(aclKey, ActionEvent.CTRL_MASK));
        toMenu.add(temp);

        return temp;
    }

    void createMenuBar(JFrame f) {
        JMenuBar mb = new JMenuBar();
        JMenuItem temp;

        JMenu jobMenu = createMenu(syncTransferJob, KeyEvent.VK_T, mb);
        JMenu providerMenu = createMenu(providerText, KeyEvent.VK_P, mb);
        JMenu helpMenu = createMenu(helpHelpTopic, KeyEvent.VK_H, mb);


        createMenuItem(jobActionNew, KeyEvent.VK_N, jobMenu, KeyEvent.VK_N, this);
        createMenuItem(jobActionDelete, KeyEvent.VK_D, jobMenu, KeyEvent.VK_D, this);


        createMenuItem(providerActionCreate, KeyEvent.VK_S, providerMenu, this);
        createMenuItem(providerActionRemove, KeyEvent.VK_A, providerMenu, this);

        createMenuItem(helpAboutNotepad, KeyEvent.VK_H, helpMenu, this);

        f.setJMenuBar(mb);
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String cmdText = actionEvent.getActionCommand();
        switch (cmdText) {
            case providerActionCreate: {
                providerDialog.refreshProviders();
                providerDialog.showDialog(f, "Create provider");
                break;
            }
        }
        System.out.println(cmdText);
    }
}
