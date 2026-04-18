package app.report;

public class MissionReportComposer {
    public MissionReport create(String reportType) {
        MissionReport base = new SummaryReport();
        return switch (reportType) {
            case "Детализированный" -> new DetailedReportDecorator(base);
            case "По рискам" -> new RiskReportDecorator(base);
            case "Статистический" -> new StatisticsReportDecorator(base);
            case "Полный" -> new StatisticsReportDecorator(new RiskReportDecorator(new DetailedReportDecorator(base)));
            default -> base;
        };
    }
}
