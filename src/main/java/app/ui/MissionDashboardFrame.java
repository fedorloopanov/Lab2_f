package app.ui;

import app.config.ApplicationConfig;
import app.exception.MissionParseException;
import app.model.Mission;
import app.processor.MissionProcessor;
import app.processor.ProcessedMission;
import app.report.MissionReportRegistry;
import app.service.MissionStatistics;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;

public class MissionDashboardFrame extends JFrame {
    private final MissionProcessor missionProcessor;
    private final MissionReportRegistry reportRegistry;

    private final JLabel fileNameValue = new JLabel("Файл не выбран");
    private final JLabel formatValue = new JLabel("—");
    private final JLabel missionIdValue = new JLabel("—");
    private final JLabel participantsValue = new JLabel("0");
    private final JLabel techniquesValue = new JLabel("0");
    private final JLabel damageValue = new JLabel("0");
    private final JLabel statusBar;

    private final JTextArea overviewArea = createTextArea();
    private final JComboBox<String> reportSelector;

    private Mission currentMission;
    private File currentFile;
    private String currentFormatName = "—";

    public MissionDashboardFrame() {
        this(ApplicationConfig.createMissionProcessor(), ApplicationConfig.createReportRegistry());
    }

    public MissionDashboardFrame(MissionProcessor missionProcessor, MissionReportRegistry reportRegistry) {
        super("Просмотр миссии");
        this.missionProcessor = missionProcessor;
        this.reportRegistry = reportRegistry;
        this.reportSelector = new JComboBox<>(reportRegistry.reportNames().toArray(new String[0]));
        this.statusBar = new JLabel("Готово. Поддерживаются форматы: " + missionProcessor.supportedFormats());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(920, 620);
        setMinimumSize(new Dimension(780, 520));
        setLocationRelativeTo(null);
        setContentPane(buildContent());

        reportSelector.addActionListener(e -> rerenderCurrentMission());
    }

    private JComponent buildContent() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));

        root.add(buildTopPanel(), BorderLayout.NORTH);
        root.add(buildCenterPanel(), BorderLayout.CENTER);

        statusBar.setBorder(new EmptyBorder(4, 2, 2, 2));
        root.add(statusBar, BorderLayout.SOUTH);
        return root;
    }

    private JComponent buildTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(buildButtonsPanel(), BorderLayout.WEST);
        panel.add(buildInfoPanel(), BorderLayout.CENTER);
        panel.add(buildReportPanel(), BorderLayout.EAST);
        return panel;
    }

    private JComponent buildButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

        JButton openButton = new JButton("Открыть файл");
        JButton clearButton = new JButton("Очистить");

        openButton.addActionListener(e -> openMissionFile());
        clearButton.addActionListener(e -> resetView());

        panel.add(openButton);
        panel.add(clearButton);
        return panel;
    }

    private JComponent buildInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 8, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Информация"));
        panel.add(createInfoLabel("Файл:", fileNameValue));
        panel.add(createInfoLabel("Формат:", formatValue));
        panel.add(createInfoLabel("ID миссии:", missionIdValue));
        panel.add(createInfoLabel("Участников:", participantsValue));
        panel.add(createInfoLabel("Техник:", techniquesValue));
        panel.add(createInfoLabel("Мат. ущерб:", damageValue));
        return panel;
    }

    private JComponent buildReportPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panel.setBorder(BorderFactory.createTitledBorder("Отчет"));
        panel.add(new JLabel("Тип:"));
        panel.add(reportSelector);
        return panel;
    }

    private JPanel createInfoLabel(String title, JLabel valueLabel) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        panel.add(new JLabel(title));
        panel.add(valueLabel);
        return panel;
    }

    private JComponent buildCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Содержимое отчета"));
        panel.add(new JScrollPane(overviewArea), BorderLayout.CENTER);
        return panel;
    }

    private JTextArea createTextArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setMargin(new Insets(8, 8, 8, 8));
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        return area;
    }

    private void openMissionFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Открыть файл миссии");
        chooser.setFileFilter(new FileNameExtensionFilter("Поддерживаемые форматы", "txt", "json", "xml"));
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("TXT", "txt"));
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("JSON", "json"));
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("XML", "xml"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            loadMission(chooser.getSelectedFile());
        }
    }

    private void loadMission(File file) {
        try {
            String selectedReport = (String) reportSelector.getSelectedItem();
            ProcessedMission processedMission = missionProcessor.process(file, selectedReport);

            currentMission = processedMission.mission();
            currentFile = file;
            currentFormatName = processedMission.formatName();

            renderMission(processedMission.reportText());
            statusBar.setText("Файл загружен: " + file.getName());
        } catch (MissionParseException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Ошибка чтения", JOptionPane.ERROR_MESSAGE);
            statusBar.setText("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.toString(), "Неожиданная ошибка", JOptionPane.ERROR_MESSAGE);
            statusBar.setText("Неожиданная ошибка при загрузке файла.");
        }
    }

    private void rerenderCurrentMission() {
        if (currentMission == null) {
            return;
        }

        String reportName = (String) reportSelector.getSelectedItem();
        overviewArea.setText(reportRegistry.generate(reportName, currentMission));
        overviewArea.setCaretPosition(0);
        statusBar.setText("Отчет обновлен: " + reportName);
    }

    private void renderMission(String reportText) {
        fileNameValue.setText(currentFile == null ? "Файл не выбран" : currentFile.getName());
        formatValue.setText(currentFormatName);
        missionIdValue.setText(valueOrDash(currentMission.getMissionId()));
        participantsValue.setText(String.valueOf(MissionStatistics.participantCount(currentMission)));
        techniquesValue.setText(String.valueOf(MissionStatistics.techniqueCount(currentMission)));
        damageValue.setText(String.valueOf(currentMission.getDamageCost()));
        overviewArea.setText(reportText);
        overviewArea.setCaretPosition(0);
    }

    private void resetView() {
        currentMission = null;
        currentFile = null;
        currentFormatName = "—";
        fileNameValue.setText("Файл не выбран");
        formatValue.setText("—");
        missionIdValue.setText("—");
        participantsValue.setText("0");
        techniquesValue.setText("0");
        damageValue.setText("0");
        overviewArea.setText("");
        statusBar.setText("Окно очищено.");
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }
}
