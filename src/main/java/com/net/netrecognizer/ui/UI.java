package com.net.netrecognizer.ui;


import com.net.netrecognizer.net.CSVNetTrain;
import com.net.netrecognizer.net.ImagesTrains;
import math.Vec;
import network.NeuralNetwork;
import network.Result;
import utils.ImageUtils;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.concurrent.Executors;

public class UI {
    private static final int FRAME_WIDTH = 620;
    private static final int FRAME_HEIGHT = 550;
    private static final Font FONT = new Font("Dialog", Font.BOLD, 18);
    private static final Font ITALIC = new Font("Dialog", Font.ITALIC, 18);
    private static final String AUTONOMOUS_DRIVING = "Распознование";
    private JFrame mainFrame;
    private JPanel mainPanel;
    private File selectedFile = new File("");
    private ProgressBar progressBar;
    private JLabel resultSymbollabel = new JLabel("АААА");
    private NeuralNetwork imageNetwork;
    private NeuralNetwork irisNetwork;

    public void initUi() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        adjustLookAndFeel();
        mainFrame = createMainFrame();
        mainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        createImageDetectorPanel();
        createDataPanel();

        addSignature();

        mainFrame.add(mainPanel, BorderLayout.CENTER);
        mainFrame.setVisible(true);
        startLearn();
    }

    private void createImageDetectorPanel() {
        JPanel imageFontDetector = new JPanel();
        imageFontDetector.setBorder(BorderFactory.createTitledBorder("Распознавание шрифтов"));
        JButton chooseFont = new JButton("Выбрать символ");
        chooseFont.setBackground(Color.ORANGE.darker());
        chooseFont.setForeground(Color.ORANGE.darker());
        chooseFont.addActionListener(e -> chooseFileAction());

        imageFontDetector.add(chooseFont);

        JButton start = new JButton("Распознать символ");

        start.setBackground(Color.GREEN.darker());
        start.setForeground(Color.GREEN.darker());
        start.addActionListener(e -> {
            Executors.newCachedThreadPool().submit(() -> {
                try {
                    Runnable runnable = () -> {
                        try {
                            Result result = imageNetwork.evaluate(new Vec(ImageUtils.convertToMatrix(new File(selectedFile.getPath()))));
                            resultSymbollabel.setText(result.getResult(imageNetwork.getTitles()));
                            progressBar.getProgressBar().setString(result.getResult(imageNetwork.getTitles()));
                        } catch (Exception e2) {
                            System.out.println(e2.getMessage());
                            progressBar.getProgressBar().setString(e2.getMessage());
                        }
                    };

                    new Thread(runnable).start();

                } catch (Exception e1) {
                    progressBar.getProgressBar().setString(e1.getMessage());

                }
            });
        });

        imageFontDetector.add(start);

        mainPanel.add(imageFontDetector);

        JPanel resultPanel = new JPanel();
        JLabel label = new JLabel("Результат");
        label.setForeground(Color.DARK_GRAY);
        resultPanel.add(label);
        resultSymbollabel.setForeground(Color.DARK_GRAY);
        resultPanel.add(resultSymbollabel);
        mainPanel.add(resultPanel);
    }

    private void createDataPanel() {
        JPanel dataIrisPanel = new JPanel();
        dataIrisPanel.setBorder(BorderFactory.createTitledBorder("Распознавание цветков Iris"));
        JLabel pesticLabel = new JLabel("Размеры пестики");
        JTextField pesticSizeFirst = new JTextField(2);
        JTextField pesticSizeSecond = new JTextField(2);
        dataIrisPanel.add(pesticLabel);
        dataIrisPanel.add(pesticSizeFirst);
        dataIrisPanel.add(pesticSizeSecond);
        JLabel tLabel = new JLabel("Размеры тычинки");
        JTextField tSizeFirst = new JTextField(2);
        JTextField tSizeSecond = new JTextField(2);
        dataIrisPanel.add(tLabel);
        dataIrisPanel.add(tSizeFirst);
        dataIrisPanel.add(tSizeSecond);

        JButton start = new JButton("Распознать цветок");

        start.setBackground(Color.GREEN.darker());
        start.setForeground(Color.GREEN.darker());
        start.addActionListener(e -> {
            progressBar.getProgressBar().setString("Распознаю цветок ...");
            Executors.newCachedThreadPool().submit(() -> {
                try {
                    Runnable runnable = () -> {
                        try {
                            Result result = irisNetwork.evaluate(new Vec(Double.parseDouble(pesticSizeFirst.getText()), Double.parseDouble(pesticSizeSecond.getText()), Double.parseDouble(tSizeFirst.getText()), Double.parseDouble(tSizeSecond.getText())));
                            resultSymbollabel.setText(result.getResult(irisNetwork.getTitles()));
                            progressBar.getProgressBar().setString(result.getResult(irisNetwork.getTitles()));
                        } catch (Exception e2) {
                            System.out.println(e2.getMessage());
                            progressBar.getProgressBar().setString(e2.getMessage());
                        }
                    };

                    new Thread(runnable).start();

                } catch (Exception e1) {
                    progressBar.getProgressBar().setString(e1.getMessage());
                    throw new RuntimeException(e1);
                }
            });
        });
        dataIrisPanel.add(start, BorderLayout.SOUTH);
        mainFrame.add(dataIrisPanel, BorderLayout.SOUTH);
    }

    private void chooseFileAction() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(new File("./src/main/resources").getAbsolutePath()));
        int action = chooser.showOpenDialog(null);
        if (action == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
        }
    }

    private void adjustLookAndFeel() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        UIManager.put("Button.font", new FontUIResource(FONT));
        UIManager.put("Label.font", new FontUIResource(ITALIC));
        UIManager.put("Combobox.font", new FontUIResource(ITALIC));
        UIManager.put("ProgressBar.font", new FontUIResource(FONT));
    }

    private JFrame createMainFrame() {
        JFrame mainFrame = new JFrame();
        mainFrame.setTitle(AUTONOMOUS_DRIVING);
        mainFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        mainFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        mainFrame.setMaximumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        mainFrame.setLocationRelativeTo(null);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });
        ImageIcon icon = new ImageIcon("icon.png");
        mainFrame.setIconImage(icon.getImage());
        return mainFrame;
    }

    private void addSignature() {
        JLabel signature = new JLabel("Р.М.Шайхисламов, студент гр. НТм(до)-22", SwingConstants.CENTER);
        signature.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 10));
        signature.setForeground(Color.BLACK);
        mainFrame.add(signature, BorderLayout.EAST);
    }

    private void startLearn() {
        progressBar = new ProgressBar(mainFrame);
        SwingUtilities.invokeLater(() -> progressBar.showProgressBar("Обучаю сеть. Подождите"));
        Executors.newCachedThreadPool().submit(() -> {
            try {
                Runnable runnable = () -> {
                    try {
                        imageNetwork = ImagesTrains.train(progressBar);
                        irisNetwork = CSVNetTrain.train();
                        progressBar.getProgressBar().setString("Готово");

                    } catch (Exception e2) {
                    }
                };

                new Thread(runnable).start();

            } catch (Exception e1) {
                progressBar.getProgressBar().setString(e1.getMessage());
            }
        });
    }

}
