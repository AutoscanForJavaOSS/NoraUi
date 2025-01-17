/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cli;

import static com.github.noraui.Constants.SCENARIO_FILE;
import static com.github.noraui.exception.TechnicalException.TECHNICAL_IO_EXCEPTION;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;

import com.github.noraui.log.annotation.Loggable;
import com.google.common.io.Files;

@Loggable
public class Scenario extends AbstractNoraUiCli {

    static Logger log;

    private String mainPath;

    public Scenario() {
        this.mainPath = "src" + File.separator + "main";
    }

    protected Scenario(String mainPath) {
        this.mainPath = mainPath;
    }

    /**
     * @return a list of available scenarios (name).
     */
    public List<String> get() {
        List<String> scenarios = new ArrayList<>();
        String propertiesfilePath = mainPath + File.separator + RESOURCES + File.separator + SCENARIO_FILE;
        try (BufferedReader br = new BufferedReader(new FileReader(propertiesfilePath))) {
            String line = br.readLine();
            while (line != null) {
                if (!line.startsWith("#") && !"".equals(line)) {
                    String[] l = line.split("=");
                    scenarios.add(l[0]);
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            log.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
        return scenarios;
    }

    /**
     * Add new scenario to your robot.
     * Sample if you add google: -f 2 -s loginSample -d "Scenario that sample." -a google --verbose
     * 
     * @param scenarioName
     *            name of scenario.
     * @param description
     *            is description of scenario.
     * @param applicationName
     *            name of application.
     * @param noraRobotName
     *            Name of your robot generated by NoraUi Maven archetype.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    public void add(String scenarioName, String description, String applicationName, String noraRobotName, boolean verbose) {
        log.info("Add a new scenario named [{}] on [{}] application with this description: [{}]", scenarioName, applicationName, description);
        addScenarioInData(scenarioName, noraRobotName, verbose);
        addScenarioInEnvPropertiesFile(scenarioName, verbose);
        addScenarioFeature(scenarioName, description, applicationName, verbose);
    }

    /**
     * Remove old scenario to your robot.
     * Sample if you add google: -f 3 -s loginSample -d "Scenario that sample." -a google --verbose
     * 
     * @param scenarioName
     *            name of scenario removed.
     * @param noraRobotName
     *            Name of your robot generated by NoraUi Maven archetype.
     * @param robotCounter
     *            Counter class from robot.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    public void remove(String scenarioName, String noraRobotName, Class<?> robotCounter, boolean verbose) {
        log.info("Remove a scenario named [{}].", scenarioName);
        removeScenarioInData(scenarioName, noraRobotName, verbose);
        removeScenarioInEnvPropertiesFile(scenarioName, verbose);
        removeScenarioFeature(scenarioName, verbose);
        removeScenarioCounter(scenarioName, robotCounter, verbose);
    }

    /**
     * @param scenarioName
     *            name of scenario removed.
     * @param noraRobotName
     *            Name of your robot generated by NoraUi Maven archetype.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    private void removeScenarioInData(String scenarioName, String noraRobotName, boolean verbose) {
        String propertiesfilePath = mainPath + File.separator + RESOURCES + File.separator + noraRobotName + ".properties";

        String dataProviderIn = getDataProvider("in", propertiesfilePath);
        log.info("dataProvider.in.type is [{}]", dataProviderIn);
        removeScenarioInData("in", scenarioName, dataProviderIn, verbose);

        String dataProviderOut = getDataProvider("out", propertiesfilePath);
        log.info("dataProvider.out.type is [{}]", dataProviderOut);
        removeScenarioInData("out", scenarioName, dataProviderOut, verbose);
    }

    /**
     * @param scenarioName
     *            name of scenario added.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    private void addScenarioInData(String scenarioName, String noraRobotName, boolean verbose) {
        String propertiesfilePath = mainPath + File.separator + RESOURCES + File.separator + noraRobotName + ".properties";

        String dataProviderIn = getDataProvider("in", propertiesfilePath);
        log.info("dataProvider.in.type is [{}]", dataProviderIn);
        addScenarioInData("in", scenarioName, dataProviderIn, verbose);

        String dataProviderOut = getDataProvider("out", propertiesfilePath);
        log.info("dataProvider.out.type is [{}]", dataProviderOut);
        addScenarioInData("out", scenarioName, dataProviderOut, verbose);
    }

    /**
     * @param type
     * @param scenarioName
     *            name of scenario added.
     * @param dataProvider
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    private void addScenarioInData(String type, String scenarioName, String dataProvider, boolean verbose) {
        if ("CSV".equals(dataProvider)) {
            String csvPath = "src" + File.separator + "test" + File.separator + RESOURCES + File.separator + "data" + File.separator + type + File.separator + scenarioName + ".csv";
            File newCsvfile = new File(csvPath);
            if (!newCsvfile.exists()) {
                addCsvFile(newCsvfile);
            }
        } else if ("DB".equals(dataProvider)) {
            String excelPath = "src" + File.separator + "test" + File.separator + RESOURCES + File.separator + "data" + File.separator + type + File.separator + scenarioName + ".sql";
            File sqlFile = new File(excelPath);
            if (!sqlFile.exists()) {
                addSqlFile(scenarioName, sqlFile);
            }
        } else if ("EXCEL".equals(dataProvider)) {
            String excelPath = "src" + File.separator + "test" + File.separator + RESOURCES + File.separator + "data" + File.separator + type + File.separator + scenarioName + ".xlsx";
            File newExcelFile = new File(excelPath);
            if (!newExcelFile.exists()) {
                addXlsxFile(scenarioName, excelPath);
            }
        } else if (verbose) {
            log.info("CLI do not add your data provider [{}]. CLI add only CSV, DB and EXCEL.", dataProvider);
        }
    }

    /**
     * @param type
     * @param scenarioName
     *            name of scenario.
     * @param dataProvider
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    private void removeScenarioInData(String type, String scenarioName, String dataProvider, boolean verbose) {
        String datafilePath = "";
        if ("CSV".equals(dataProvider)) {
            datafilePath = "src" + File.separator + "test" + File.separator + RESOURCES + File.separator + "data" + File.separator + type + File.separator + scenarioName + ".csv";
        } else if ("DB".equals(dataProvider)) {
            datafilePath = "src" + File.separator + "test" + File.separator + RESOURCES + File.separator + "data" + File.separator + type + File.separator + scenarioName + ".sql";
        } else if ("EXCEL".equals(dataProvider)) {
            datafilePath = "src" + File.separator + "test" + File.separator + RESOURCES + File.separator + "data" + File.separator + type + File.separator + scenarioName + ".xlsx";
        }
        if (!"".equals(datafilePath)) {
            try {
                FileUtils.forceDelete(new File(datafilePath));
                if (verbose) {
                    log.info("{} removed with success.", datafilePath);
                }
            } catch (IOException e) {
                log.debug("{} not revove because do not exist.", datafilePath);
            }
        } else {
            if (verbose) {
                log.info("CLI do not remove your data provider [{}]. CLI remove only CSV, DB and EXCEL.", dataProvider);
            }
        }

    }

    /**
     * @param scenarioName
     *            name of scenario.
     * @param excelPath
     */
    private void addXlsxFile(String scenarioName, String excelPath) {
        try (FileOutputStream outputStream = new FileOutputStream(excelPath); XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFCellStyle noraUiColumnStyle = workbook.createCellStyle();
            XSSFFont noraUiColumnFont = workbook.createFont();
            noraUiColumnFont.setColor(IndexedColors.BLACK.getIndex());
            noraUiColumnFont.setBold(true);
            noraUiColumnStyle.setFont(noraUiColumnFont);
            noraUiColumnStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(0, 96, 88)));
            noraUiColumnStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            XSSFCellStyle noraUiResultColumnStyle = workbook.createCellStyle();
            XSSFFont noraUiResultColumnFont = workbook.createFont();
            noraUiResultColumnFont.setColor(IndexedColors.WHITE.getIndex());
            noraUiResultColumnFont.setBold(false);
            noraUiResultColumnStyle.setFont(noraUiResultColumnFont);
            noraUiResultColumnStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(128, 128, 128)));
            noraUiResultColumnStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            XSSFSheet sheet = workbook.createSheet("NoraUi-" + scenarioName);
            Object[][] datas = { { "user", "password", "Result" }, { "user1", "password1" }, { "user2", "password2" } };
            int rowNum = 0;
            for (int i = 0; i < datas.length; i++) {
                Object[] data = datas[i];
                Row row = sheet.createRow(rowNum++);
                int colNum = 0;
                for (Object field : data) {
                    Cell cell = row.createCell(colNum++);
                    if (i == 0) {
                        setHeaderStyleInXlsxFile(noraUiColumnStyle, noraUiResultColumnStyle, field, cell);
                    }
                    setRowValueInXlsxFile(field, cell);
                }
            }
            workbook.write(outputStream);
        } catch (IOException e) {
            log.error("IOException {}", e.getMessage(), e);
        }
    }

    private void setRowValueInXlsxFile(Object field, Cell cell) {
        if (field instanceof String) {
            cell.setCellValue((String) field);
        } else if (field instanceof Integer) {
            cell.setCellValue((Integer) field);
        }
    }

    private void setHeaderStyleInXlsxFile(XSSFCellStyle noraUiColumnStyle, XSSFCellStyle noraUiResultColumnStyle, Object field, Cell cell) {
        if ("Result".equals(field)) {
            cell.setCellStyle(noraUiResultColumnStyle);
        } else {
            cell.setCellStyle(noraUiColumnStyle);
        }
    }

    /**
     * @param scenarioName
     *            name of scenario.
     * @param sqlFile
     */
    private void addSqlFile(String scenarioName, File sqlFile) {
        StringBuilder sb = new StringBuilder();
        sb.append("(select t.user as \"user\", t.password1 as \"password1\", '' as Result from " + scenarioName + " t)").append(System.lineSeparator());
        sb.append("ORDER BY \"author\"").append(System.lineSeparator());
        try {
            Files.asCharSink(sqlFile, StandardCharsets.UTF_8).write(sb.toString());
        } catch (IOException e) {
            log.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
    }

    /**
     * @param newCsvfile
     */
    private void addCsvFile(File newCsvfile) {
        StringBuilder sb = new StringBuilder();
        sb.append("user;password;Result").append(System.lineSeparator());
        sb.append("user1;password1;").append(System.lineSeparator());
        sb.append("user2;password2;").append(System.lineSeparator());
        try {
            Files.asCharSink(newCsvfile, StandardCharsets.UTF_8).write(sb.toString());
        } catch (IOException e) {
            log.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
    }

    /**
     * @param type
     * @param propertiesfilePath
     * @return
     */
    private String getDataProvider(String type, String propertiesfilePath) {
        String dataProvider = null;
        try (BufferedReader br = new BufferedReader(new FileReader(propertiesfilePath))) {
            String line = br.readLine();
            while (line != null) {
                if (line.startsWith("dataProvider." + type + ".type=")) {
                    dataProvider = line.replaceAll("dataProvider." + type + ".type=", "");
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            log.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
        return dataProvider;
    }

    /**
     * @param scenarioName
     *            name of scenario.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    private void addScenarioInEnvPropertiesFile(String scenarioName, boolean verbose) {
        String scenarioFilePath = mainPath + File.separator + RESOURCES + File.separator + SCENARIO_FILE;
        if (verbose) {
            log.info("Add scenario named [{}] in scenario.properties.", scenarioName);
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(scenarioFilePath))) {
            String line = br.readLine();
            while (line != null) {
                if (!(scenarioName + "=/steps/scenarios/").equals(line)) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    if ("###############   Scenario in production          ###############".equals(line)) {
                        sb.append("#################################################################");
                        sb.append(System.lineSeparator());
                        sb.append(scenarioName + "=/steps/scenarios/");
                        sb.append(System.lineSeparator());
                        line = br.readLine();
                    }
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            log.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
        updateFile(scenarioFilePath, sb);
    }

    /**
     * @param scenarioName
     *            name of scenario.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    private void removeScenarioInEnvPropertiesFile(String scenarioName, boolean verbose) {
        String scenarioFilePath = mainPath + File.separator + RESOURCES + File.separator + SCENARIO_FILE;
        if (verbose) {
            log.info("Remove scenario named [{}] in scenario.properties.", scenarioName);
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(scenarioFilePath))) {
            String line = br.readLine();
            while (line != null) {
                if (!(scenarioName + "=/steps/scenarios/").equals(line)) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            log.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
        updateFile(scenarioFilePath, sb);
    }

    /**
     * @param scenarioName
     *            name of scenario.
     * @param description
     *            is description of scenario.
     * @param applicationName
     *            name of application.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    private void addScenarioFeature(String scenarioName, String description, String applicationName, boolean verbose) {
        String newFeaturePath = "src" + File.separator + "test" + File.separator + RESOURCES + File.separator + "steps" + File.separator + "scenarios" + File.separator + scenarioName + ".feature";
        StringBuilder sb = new StringBuilder();
        sb.append("@" + scenarioName).append(System.lineSeparator());
        sb.append("Feature: " + scenarioName + " (" + description + ")").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("  Scenario Outline:  " + description + "").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    Given I check that 'user' '<user>' is not empty").append(System.lineSeparator());
        sb.append("    Given I check that 'password' '<password>' is not empty").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    Given '" + applicationName.toUpperCase() + "_HOME' is opened").append(System.lineSeparator());
        sb.append("    Then The " + applicationName.toUpperCase() + " home page is displayed").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    And I go back to '" + applicationName.toUpperCase() + "_HOME'").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("  Examples:").append(System.lineSeparator());
        sb.append("    #DATA").append(System.lineSeparator());
        sb.append("    |id|user|password|").append(System.lineSeparator());
        sb.append("    #END").append(System.lineSeparator());
        try {
            File newFeature = new File(newFeaturePath);
            if (!newFeature.exists()) {
                Files.asCharSink(newFeature, StandardCharsets.UTF_8).write(sb.toString());
                if (verbose) {
                    log.info("File [{}] created with success.", newFeaturePath);
                }
            } else {
                if (verbose) {
                    log.info("File [{}] already exist.", newFeaturePath);
                }
            }
        } catch (Exception e) {
            log.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
    }

    /**
     * @param scenarioName
     *            name of scenario.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    private void removeScenarioFeature(String scenarioName, boolean verbose) {
        String featurePath = "src" + File.separator + "test" + File.separator + RESOURCES + File.separator + "steps" + File.separator + "scenarios" + File.separator + scenarioName + ".feature";
        try {
            FileUtils.forceDelete(new File(featurePath));
            if (verbose) {
                log.info("{} removed with success.", featurePath);
            }
        } catch (Exception e) {
            log.debug("{} not revove because do not exist.", featurePath);
        }
    }

    /**
     * @param scenarioName
     *            name of scenario.
     * @param robotCounter
     *            Counter class from robot.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    private void removeScenarioCounter(String scenarioName, Class<?> robotCounter, boolean verbose) {
        String counterFilePath = this.mainPath + File.separator + "java" + File.separator
                + robotCounter.getCanonicalName().replaceAll("\\.", "/").replace("/", Matcher.quoteReplacement(File.separator)) + ".java";
        if (verbose) {
            log.info("Remove scenario named [{}] in black list of counter [{}].", scenarioName, counterFilePath);
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(counterFilePath))) {
            String line = br.readLine();
            while (line != null) {
                if (!("        scenarioBlacklist.add(\"" + scenarioName + "\");").equals(line)) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            log.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
        updateFile(counterFilePath, sb);
    }

}
