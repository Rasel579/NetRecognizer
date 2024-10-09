package com.net.netrecognizer;

import com.net.netrecognizer.ui.ProgressBar;
import com.net.netrecognizer.ui.UI;

import javax.swing.*;
import java.util.concurrent.Executors;

public class Main {
    private static final JFrame mainFrame = new JFrame();
    public static void main(String[] args) {
        ProgressBar progressBar = new ProgressBar(mainFrame, true);
        progressBar.showProgressBar("Loading models");
        UI ui = new UI();
        Executors.newCachedThreadPool().submit(() -> {
            try {
                ui.initUi();
            } catch (Exception e){
                throw new RuntimeException(e);
            } finally {
                progressBar.setVisible(true);
                mainFrame.dispose();
            }
        });
    }
}
