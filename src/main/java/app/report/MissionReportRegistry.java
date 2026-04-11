package app.report;

import app.model.Mission;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MissionReportRegistry {
    private final List<MissionReportGenerator> generators = new ArrayList<>();

    public MissionReportRegistry register(MissionReportGenerator generator) {
        generators.add(generator);
        return this;
    }

    public List<String> reportNames() {
        return generators.stream()
                .map(MissionReportGenerator::getReportName)
                .collect(Collectors.toList());
    }

    public String generate(String reportName, Mission mission) {
        String resolvedName = resolveReportName(reportName);
        return generators.stream()
                .filter(generator -> generator.getReportName().equals(resolvedName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Не найден генератор отчета: " + reportName))
                .generate(mission);
    }

    public String resolveReportName(String reportName) {
        if (reportName == null || reportName.isBlank()) {
            return generators.isEmpty() ? "" : generators.get(0).getReportName();
        }
        return generators.stream()
                .map(MissionReportGenerator::getReportName)
                .filter(name -> name.equals(reportName))
                .findFirst()
                .orElseGet(() -> generators.isEmpty() ? "" : generators.get(0).getReportName());
    }
}
