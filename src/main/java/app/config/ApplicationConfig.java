package app.config;

import app.factory.MissionFactory;
import app.factory.ParserRegistry;
import app.parser.JsonMissionParser;
import app.parser.TxtMissionParser;
import app.parser.XmlMissionParser;
import app.processor.MissionProcessor;
import app.report.DetailedReportGenerator;
import app.report.MissionReportRegistry;
import app.report.RiskReportGenerator;
import app.report.StatisticalReportGenerator;
import app.report.SummaryReportGenerator;
import app.service.MissionLoaderService;
import app.validator.MissionValidator;

public final class ApplicationConfig {
    private ApplicationConfig() {
    }

    public static MissionProcessor createMissionProcessor() {
        ParserRegistry parserRegistry = new ParserRegistry()
                .register(new TxtMissionParser())
                .register(new JsonMissionParser())
                .register(new XmlMissionParser());

        MissionLoaderService loaderService = new MissionLoaderService(
                parserRegistry,
                new MissionFactory(),
                new MissionValidator()
        );

        MissionReportRegistry reportRegistry = new MissionReportRegistry()
                .register(new SummaryReportGenerator())
                .register(new DetailedReportGenerator())
                .register(new RiskReportGenerator())
                .register(new StatisticalReportGenerator());

        return new MissionProcessor(loaderService, reportRegistry);
    }

    public static MissionReportRegistry createReportRegistry() {
        return new MissionReportRegistry()
                .register(new SummaryReportGenerator())
                .register(new DetailedReportGenerator())
                .register(new RiskReportGenerator())
                .register(new StatisticalReportGenerator());
    }
}
