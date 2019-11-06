package com.cross.sync;


import com.cross.sync.swing.JSync;

class SynchronizationClient {
    public static void main(String... args) {
        com.cross.sync.swing.JSync dialog = new JSync();
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
