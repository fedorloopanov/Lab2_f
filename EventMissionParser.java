package app;

import app.parser.*;
import app.report.MissionReportComposer;
import app.service.*;
import app.ui.MissionDashboardFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        MissionParserRegistry parserFactory = new MissionParserRegistry()
                .register(MissionParserCreator.json())
                .register(MissionParserCreator.xml())
                .register(MissionParserCreator.yaml())
                .register(MissionParserCreator.txt())
                .register(MissionParserCreator.event());

        MissionParserContext parserContext = new MissionParserContext(new JsonMissionParser());
        MissionModelFactory elementFactory = MissionModelFactory.createDefault();

        MissionLoaderService loaderService = new MissionLoaderService(
                parserFactory,
                parserContext,
                new MissionDirector(new DefaultMissionBuilder(elementFactory), elementFactory)
        );

        MissionReportComposer reportFactory = new MissionReportComposer();

        SwingUtilities.invokeLater(() ->
                new MissionDashboardFrame(loaderService, reportFactory).setVisible(true)
        );
    }
}
