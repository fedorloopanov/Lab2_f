package app.processor;

import app.exception.MissionParseException;
import app.report.MissionReportRegistry;
import app.service.LoadedMission;
import app.service.MissionLoaderService;

import java.io.File;

public class MissionProcessor {
    private final MissionLoaderService missionLoaderService;
    private final MissionReportRegistry reportRegistry;

    public MissionProcessor(MissionLoaderService missionLoaderService,
                            MissionReportRegistry reportRegistry) {
        this.missionLoaderService = missionLoaderService;
        this.reportRegistry = reportRegistry;
    }

    public ProcessedMission process(File file, String reportName) throws MissionParseException {
        LoadedMission loadedMission = missionLoaderService.load(file);
        String resolvedReport = reportRegistry.resolveReportName(reportName);
        String reportText = reportRegistry.generate(resolvedReport, loadedMission.mission());
        return new ProcessedMission(loadedMission.mission(), loadedMission.formatName(), resolvedReport, reportText);
    }

    public String supportedFormats() {
        return missionLoaderService.supportedFormats();
    }
}
