import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.PieChart;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import java.util.Calendar;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import javafx.geometry.Pos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import java.sql.*;
import javafx.stage.StageStyle;
import javafx.geometry.Insets;
import org.controlsfx.control.Notifications;
import javafx.application.Platform;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.itextpdf.text.*;
import java.net.MalformedURLException;

public class MainInterface extends Application {

    private double xOffset = 0;
    private double yOffset = 0;
    private TableView<ReportData> reportTable;
    private Map<String, List<TableColumn<ReportData, ?>>> reportTypeColumns;
    
    @Override
    public void start(Stage primaryStage) {
        // Main layout container
        primaryStage.initStyle(StageStyle.UNDECORATED);
        BorderPane root = new BorderPane();

        // Add buttons to the menu bar
        MenuBar menuBar = new MenuBar();
        
        menuBar.setOnMousePressed(event -> {
            xOffset = primaryStage.getX() - event.getScreenX();
            yOffset = primaryStage.getY() - event.getScreenY();
        });

        // Mouse dragged event handler for moving the window
        menuBar.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() + xOffset);
            primaryStage.setY(event.getScreenY() + yOffset);
        });
        
        ImageView appIcon = new ImageView(new Image(getClass().getResourceAsStream("icon/madagascar-icon.png")));
        appIcon.setFitHeight(30);
        appIcon.setFitWidth(30);
        
        Menu windowMenu = new Menu();
        // File menu with sub-options
        Menu fileMenu = new Menu("File");
        fileMenu.getStyleClass().add("file-menu");
        MenuItem newFile = new MenuItem("New");
        MenuItem openFile = new MenuItem("Open");
        MenuItem saveFile = new MenuItem("Save");
        MenuItem exit = new MenuItem("Exit");
        fileMenu.getItems().addAll(newFile, openFile, saveFile, new SeparatorMenuItem(), exit);
        
        // Edit menu with sub-options
        Menu editMenu = new Menu("Edit");
        editMenu.getStyleClass().add("edit-menu");
        MenuItem undo = new MenuItem("Undo");
        MenuItem redo = new MenuItem("Redo");
        MenuItem cut = new MenuItem("Cut");
        MenuItem copy = new MenuItem("Copy");
        MenuItem paste = new MenuItem("Paste");
        editMenu.getItems().addAll(undo, redo, cut, copy, paste);
        
        // Help menu with sub-options
        Menu helpMenu = new Menu("Help");
        helpMenu.getStyleClass().add("help-menu");
        MenuItem about = new MenuItem("About");
        MenuItem documentation = new MenuItem("Documentation");
        helpMenu.getItems().addAll(about, documentation);
        
        // Adding all menus to the menu bar
        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);
        // Search bar in the menu bar
        TextField searchBar = new TextField();
        searchBar.getStyleClass().add("search-bar");
        searchBar.setPromptText("Search...");
        Menu searchMenu = new Menu();
        searchMenu.setGraphic(searchBar);
        menuBar.getMenus().add(searchMenu);
        
        menuBar.getMenus().add(windowMenu);
        
        // Minimize button
        ImageView minimizeIconView = new ImageView(new Image(getClass().getResourceAsStream("icon/minimize-icon.png")));
        minimizeIconView.setFitWidth(18); // Set the width of the icon
        minimizeIconView.setFitHeight(18);
        Button minimizeButton = new Button();
        minimizeButton.setGraphic(minimizeIconView);
        minimizeButton.setOnAction(event -> primaryStage.setIconified(true));

        // Maximize/Restore button
        ImageView maximizeIconView = new ImageView(new Image(getClass().getResourceAsStream("icon/maximize-icon.png")));
        maximizeIconView.setFitWidth(18); // Set the width of the icon
        maximizeIconView.setFitHeight(18);
        Button maximizeButton = new Button();
        maximizeButton.setGraphic(maximizeIconView);
        maximizeButton.setOnAction(event -> {
            if (primaryStage.isMaximized()) {
                primaryStage.setMaximized(false);
            } else {
                primaryStage.setMaximized(true);
            }
        });

        // Close button
        ImageView closeIconView = new ImageView(new Image(getClass().getResourceAsStream("icon/close-icon.png")));
        closeIconView.setFitWidth(18); // Set the width of the icon
        closeIconView.setFitHeight(18);
        Button closeButton = new Button();
        closeButton.setGraphic(closeIconView);
        closeButton.setOnAction(event -> primaryStage.close());

        // Container for the window control buttons
        HBox windowControls = new HBox(minimizeButton, maximizeButton, closeButton);
        windowControls.getStyleClass().add("window-controls");
        windowControls.setAlignment(Pos.CENTER_RIGHT);

        // Container for the menu bar and window controls
        HBox topContainer = new HBox(appIcon, menuBar, windowControls);
        topContainer.getStyleClass().add("top-container");
        HBox.setHgrow(menuBar, Priority.ALWAYS); // Menu bar takes up all available horizontal space

        // Set the top region of the BorderPane to the HBox
        root.setTop(topContainer);

        // Left navigation panel
        VBox navigationPanel = new VBox();
        navigationPanel.getStyleClass().add("navigation-panel");
        navigationPanel.setSpacing(10);

        Button dashboardButton = new Button("Dashboard");
        dashboardButton.setOnAction(event -> {
            // Load the dashboard interface
            root.setCenter(createDashboardInterface());
        });

        // Data Entry button
        Button dataEntryButton = new Button("Data Entry");
        dataEntryButton.setOnAction(event -> {
            // Load the data entry interface
            root.setCenter(createDataEntryInterface(primaryStage));
        });

        // Analysis button
        Button analysisButton = new Button("Analysis");
        analysisButton.setOnAction(event -> {
            // Load the analysis interface
            root.setCenter(createAnalysisInterface());
        });
        
        initializeReportTypeColumns();
        
        // Reports button
        Button reportsButton = new Button("Reports");
        reportsButton.setOnAction(event -> {
            // Load the reports interface
            root.setCenter(createReportsInterface(primaryStage));
        });
        
        navigationPanel.getChildren().addAll(dashboardButton, dataEntryButton, analysisButton, reportsButton);
        root.setLeft(navigationPanel);

        // Center dashboard area
        root.setCenter(createDashboardInterface());

        // Setting the scene
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("css/MainInterface.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public void showNotification(String title, String text, boolean success, Stage ownerStage) {
        Notifications notificationBuilder = Notifications.create()
            .title(title)
            .text(text)
            .hideAfter(Duration.seconds(5))
            .position(Pos.BOTTOM_RIGHT)
            .owner(ownerStage); // Set the owner of the notification

        Platform.runLater(() -> {
            //notificationBuilder.getStyleClass().add("notifications");
            if (success) {
                notificationBuilder.showConfirm();
            } else {
                notificationBuilder.showError();
            }
        });
    }
    private VBox createCard(String title, String value, String styleClass) {
        VBox card = new VBox();
        card.getStyleClass().add("card");
        card.getStyleClass().add(styleClass); // Add the specific style class for color

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("card-title");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("card-value");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }
    // DemographicData class to represent the data in the table
    public static class DemographicData {
        private final SimpleStringProperty region;
        private final SimpleIntegerProperty population;
        private final SimpleIntegerProperty malePopulation;
        private final SimpleIntegerProperty femalePopulation;
        private final SimpleIntegerProperty ageGroup1;
        private final SimpleIntegerProperty ageGroup2;
        private final SimpleIntegerProperty ageGroup3;
        private final SimpleIntegerProperty ageGroup4;
        private final SimpleIntegerProperty ageGroup5;

        public DemographicData(String region, Integer population, Integer malePopulation, Integer femalePopulation, Integer ageGroup1, Integer ageGroup2, Integer ageGroup3, Integer ageGroup4, Integer ageGroup5) {
            this.region = new SimpleStringProperty(region);
            this.population = new SimpleIntegerProperty(population);
            this.malePopulation = new SimpleIntegerProperty(malePopulation);
            this.femalePopulation = new SimpleIntegerProperty(femalePopulation);
            this.ageGroup1 = new SimpleIntegerProperty(ageGroup1);
            this.ageGroup2 = new SimpleIntegerProperty(ageGroup2);
            this.ageGroup3 = new SimpleIntegerProperty(ageGroup3);
            this.ageGroup4 = new SimpleIntegerProperty(ageGroup4);
            this.ageGroup5 = new SimpleIntegerProperty(ageGroup5);
        }

        // Getters (and setters if needed)
        public String getRegion() { return region.get(); }
        public Integer getPopulation() { return population.get(); }
        public Integer getMalePopulation() { return malePopulation.get(); }
        public Integer getFemalePopulation() { return femalePopulation.get(); }
        public Integer getAgeGroup1() { return ageGroup1.get(); }
        public Integer getAgeGroup2() { return ageGroup2.get(); }
        public Integer getAgeGroup3() { return ageGroup3.get(); }
        public Integer getAgeGroup4() { return ageGroup4.get(); }
        public Integer getAgeGroup5() { return ageGroup5.get(); }
        
    }
    public class DatabaseOperations {
        private Connection connection;
        private final String nationalSummaryTable = "nationalsummary";
    
        // Constructor to establish database connection
        public DatabaseOperations() {
            try {
            // Attempt to establish a database connection
            this.connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3307/expldemo", "root", "");
            } catch (SQLException e) {
                System.out.println(e);
                // Handle any SQL exceptions here
            }
        }
        public Map<String, Integer> getRegions() throws SQLException {
            Map<String, Integer> regionsMap = new LinkedHashMap<>();
            String query = "SELECT region_id, region_name FROM regions ORDER BY region_name ASC";
            try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    int regionId = rs.getInt("region_id");
                    String regionName = rs.getString("region_name");
                    regionsMap.put(regionName, regionId);
                }
            }
            return regionsMap;
        }
        public void insertPopulationData(RegionItem selectedRegion, String totalPopulation, String totalMalePopulation, String totalFemalePopulation, String ageGroup0to4, String ageGroup5to14, String ageGroup15to24, String ageGroup25to64, String ageGroup65Plus) throws SQLException {
            String query = "INSERT INTO population (region_id, total_population, male_population, female_population, age_group_0_4, age_group_5_14, age_group_15_24, age_group_25_64, age_group_65_plus) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, selectedRegion.getId());
                pstmt.setInt(2, Integer.parseInt(totalPopulation));
                pstmt.setInt(3, Integer.parseInt(totalMalePopulation));
                pstmt.setInt(4, Integer.parseInt(totalFemalePopulation));
                pstmt.setInt(5, Integer.parseInt(ageGroup0to4));
                pstmt.setInt(6, Integer.parseInt(ageGroup5to14));
                pstmt.setInt(7, Integer.parseInt(ageGroup15to24));
                pstmt.setInt(8, Integer.parseInt(ageGroup25to64));
                pstmt.setInt(9, Integer.parseInt(ageGroup65Plus));
                pstmt.executeUpdate();
            }
        }
    // Function to retrieve the population table
        public ResultSet getPopulationTable() throws SQLException {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery("SELECT p1.*, r.region_name FROM population p1 INNER JOIN (SELECT region_id, MAX(created_at) AS latest_entry FROM population GROUP BY region_id) p2 ON p1.region_id = p2.region_id AND p1.created_at = p2.latest_entry INNER JOIN regions r ON p1.region_id = r.region_id");
        }
        // Assuming birthsNumber and deathsNumber methods return an int directly
        public int birthsNumber() throws SQLException {
            try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM births")) {
                rs.next(); // Move the cursor to the first row
                return rs.getInt(1); // Retrieve the count
            }
        }

        public int deathsNumber() throws SQLException {
            try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM deaths")) {
                rs.next(); // Move the cursor to the first row
                return rs.getInt(1); // Retrieve the count
            }
        }
        // Function to retrieve the latest record of the national summary table
        public ResultSet getLatestNationalSummaryRecord() throws SQLException {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery("SELECT * FROM " + nationalSummaryTable + " ORDER BY created_at DESC LIMIT 1");
        }
        // Function to retrieve the latest records of each year starting from four years ago
        public ResultSet getRecordsFromPastYears(int years) throws SQLException {
            PreparedStatement pstmt = connection.prepareStatement(
                "SELECT * FROM " + nationalSummaryTable + " ns INNER JOIN " +
               "(SELECT YEAR(created_at) AS year, MAX(created_at) AS max_created_at " +
               "FROM " + nationalSummaryTable +
               " GROUP BY YEAR(created_at) " +
               "HAVING year >= ?) AS yearly_max " +
               "ON ns.created_at = yearly_max.max_created_at " +
               "ORDER BY ns.created_at DESC");
            pstmt.setInt(1, getCurrentYear() - years);
            return pstmt.executeQuery();
        }
        // Function to calculate the growth rate of the population
        public double calculateGrowthRate(int initialPopulation, int finalPopulation) {
            if (initialPopulation == 0) {
                throw new IllegalArgumentException("Initial population cannot be zero.");
            }
            return ((double) (finalPopulation - initialPopulation) / initialPopulation) * 100;
        }
    
        // Helper function to get the current year
        private int getCurrentYear() {
            return Calendar.getInstance().get(Calendar.YEAR);
        }
        public String getQueryForReportType(String reportType) {
            return switch (reportType) {
                case "Births Report" -> 
                    "SELECT r.region_name, "
                    + "b.birth_date, b.gender, b.mother_age, b.father_age "
                    + "FROM births b "
                    + "JOIN regions r ON b.region_id = r.region_id "
                    + "WHERE b.birth_date BETWEEN ? AND ?;";
                case "Deaths Report" -> 
                    "SELECT r.region_name, "
                    + "d.death_date, d.age_at_death, d.cause_of_death "
                    + "FROM deaths d "
                    + "JOIN regions r ON d.region_id = r.region_id "
                    + "WHERE d.death_date BETWEEN ? AND ?;";
                case "Population Report" -> 
                    "SELECT r.region_name, p1.total_population, p1.male_population, p1.female_population, " +
                    "p1.age_group_0_4, " +
                    "p1.age_group_5_14, " +
                    "p1.age_group_15_24, " +
                    "p1.age_group_25_64, " +
                    "p1.age_group_65_plus " +
                    "FROM population p1 " +
                    "INNER JOIN regions r ON p1.region_id = r.region_id " +
                    "JOIN (SELECT region_id, MAX(created_at) AS latest_created_at FROM population " +
                    "WHERE created_at BETWEEN ? AND ? " +
                    "GROUP BY region_id " +
                    ") p2 ON p1.region_id = p2.region_id AND p1.created_at = p2.latest_created_at;";
                case "National Summary Report" -> 
                    "SELECT s.total_population, s.total_male_population, " +
                    "s.total_female_population, s.average_age, s.median_age, " +
                    "s.total_births, s.total_deaths, s.natural_growth_rate, " +
                    "s.total_migration_in, s.total_migration_out, s.net_migration_rate " +
                    "FROM nationalsummary s " +
                    "WHERE s.created_at IN (" +
                    "SELECT created_at " +
                    "FROM nationalsummary " +
                    "WHERE created_at BETWEEN ? AND ?" +
                    ") " +
                    "ORDER BY s.created_at DESC " +
                    "LIMIT 1;";
                case "Migration Report" -> 
                    "SELECT "
                    + "m.nation_from_id, "
                    + "m.nation_to_id, "
                    + "m.migration_date, "
                    + "m.reason "
                    + "FROM migration m "
                    + "WHERE m.migration_date BETWEEN ? AND ?;";
                default -> throw new IllegalArgumentException("Invalid report type: " + reportType);
            };
        }
     
    }
    // Method to create the dashboard interface
    private Node createDashboardInterface() {
        GridPane dashboard = new GridPane();
        dashboard.getStyleClass().add("dashboard");
        HBox chartContainer = new HBox(10); // 10 is the spacing between charts
        chartContainer.setAlignment(Pos.CENTER);
        
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), dashboard);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        dashboard.setHgap(10);
        dashboard.setVgap(10);
        // Add dashboard widgets here
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS); // Allow column to grow
        dashboard.getColumnConstraints().add(columnConstraints); // Apply to the gridPane
        
        HBox headerContainer = new HBox();
        headerContainer.getStyleClass().add("header-container");
        headerContainer.setSpacing(10); // Set spacing between icon and label
        ImageView dashboardIcon = new ImageView(new Image(getClass().getResourceAsStream("icon/dashboard-icon-white.png")));
        dashboardIcon.setFitHeight(30);
        dashboardIcon.setFitWidth(30);
        Label headerLabel = new Label("My Dashboard");
        headerContainer.setMaxWidth(Double.MAX_VALUE); // Allow the label to grow
        headerContainer.setAlignment(Pos.CENTER_LEFT); // Center the label tex
        headerContainer.getChildren().addAll(dashboardIcon, headerLabel);
        dashboard.add(headerContainer, 0, 0);
        // Example widgets for the dashboard
        // Inside the start method, after initializing the GridPane dashboard
        FlowPane cardsContainer = new FlowPane();
        cardsContainer.setAlignment(Pos.CENTER);
        cardsContainer.setHgap(10); // Horizontal spacing between cards
        cardsContainer.setVgap(10); // Vertical spacing between cards
        cardsContainer.setPadding(new Insets(0,0, 0, 0)); // Padding around the container
        
        DatabaseOperations dbOps = new DatabaseOperations();
        try(ResultSet latestSummary = dbOps.getRecordsFromPastYears(1)){
            int initialPopulation = 0;
            int finalPopulation = 0;
            float averageAge = 0;
            float medianAge = 0;
            int migrationRate = 0;
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            int year;

            while (latestSummary.next()) {
                year = latestSummary.getInt("year"); // Assuming there's a 'year' column in your result set
                if (year == currentYear - 1) { // Two years ago
                    initialPopulation = latestSummary.getInt("total_population"); // Assuming there's a 'population' column
                } else if (year == currentYear) { // Last year
                    finalPopulation = latestSummary.getInt("total_population");
                    averageAge = latestSummary.getFloat("average_age");
                    medianAge = latestSummary.getFloat("median_age");
                    migrationRate = latestSummary.getInt("net_migration_rate");
                }
            }
            VBox totalPopulationCard = createCard("Total Population", Integer.toString(finalPopulation), "total-population-card");
            VBox growthRateCard = createCard("Growth Rate", String.format("%.2f",dbOps.calculateGrowthRate(initialPopulation, finalPopulation))+"%", "growth-rate-card");
            VBox averageAgeCard = createCard("AverageAge", String.valueOf(averageAge), "average-age-card");
            VBox medianAgeCard = createCard("Median Age", String.valueOf(medianAge), "mediane-age-card");
            VBox migrationRateCard = createCard("Migration Rate", Integer.toString(migrationRate), "migration-rate-card");
            // Add the cards to the cards container or directly to the dashboard
            cardsContainer.getChildren().addAll(totalPopulationCard, growthRateCard, averageAgeCard, medianAgeCard, migrationRateCard);
            dashboard.add(cardsContainer, 0, 3, 2, 1); // Adjust grid position as needed
        } catch (SQLException e) {
            System.out.println(e);
            // Handle exceptions
        }
        LineChart<String, Number> demographicChart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        demographicChart.getStyleClass().add("demographic-chart");
            // Inside the start method, after initializing the LineChart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Population Over Time");

        try(ResultSet latestSummary = dbOps.getRecordsFromPastYears(3)){
            TreeMap<String, Number> sortedData = new TreeMap<>();
            while (latestSummary.next()) {
                sortedData.put(latestSummary.getString("year"), latestSummary.getInt("total_population"));
            }
        
            for(Map.Entry<String, Number> entry : sortedData.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            demographicChart.getData().add(series);
            demographicChart.setTitle("Demographic Trends");
        } catch (SQLException e) {
            System.out.println(e);
            // Handle exceptions
        }
        ObservableList<PieChart.Data> pieChartData =
            FXCollections.observableArrayList(
                new PieChart.Data("High School Diploma", 40),
                new PieChart.Data("Bachelor's Degree", 35),
                new PieChart.Data("Master's Degree", 15),
                new PieChart.Data("Ph.D.", 10)
            );
           
        PieChart pieChart = new PieChart(pieChartData);
        pieChart.getStyleClass().add("pie-chart");
        pieChart.setTitle("Education Level Distribution");
        
        chartContainer.getChildren().addAll(demographicChart, pieChart);
        dashboard.add(chartContainer, 0, 6, 2, 1); // Adjust grid position as needed
        
        HBox listContainer = new HBox(10); // 10 is the spacing between charts
        listContainer.setAlignment(Pos.CENTER);
        
        ListView<String> listView = new ListView<>();
        listView.getItems().addAll("Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7");
        listView.getStyleClass().add("list-view");
        
        try(ResultSet latestSummary = dbOps.getLatestNationalSummaryRecord()){
            if(latestSummary.next()){
                
                ObservableList<String> items = FXCollections.observableArrayList(
                    "Natural Growth Rate: "+ Integer.toString(latestSummary.getInt("natural_growth_rate")), 
                    "Male population: " + Integer.toString(latestSummary.getInt("total_male_population")), 
                    "Female population: " + Integer.toString(latestSummary.getInt("total_male_population")),
                    "Births: " + Integer.toString(dbOps.birthsNumber()),
                    "Deaths: " + Integer.toString(dbOps.deathsNumber()),
                    "migration in: " + Integer.toString(latestSummary.getInt("total_migration_in")),
                    "migration out: " + Integer.toString(latestSummary.getInt("total_migration_out"))
                );
                listView.setItems(items);

                // Customize the appearance of the ListView (optional)
                listView.setPrefSize(200, 250); // Set preferred size
            }
        } catch (SQLException e) {
            System.out.println(e);
            // Handle exceptions
        }

        TableView<DemographicData> tableView = new TableView<>();
        tableView.getStyleClass().add("table-view");
        tableView.setPrefSize(800, 250); // Set preferred size

        TableColumn<DemographicData, String> RegionColumn = new TableColumn<>("Region");
        RegionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));

        TableColumn<DemographicData, Number> populationColumn = new TableColumn<>("Population");
        populationColumn.setCellValueFactory(new PropertyValueFactory<>("population"));

        TableColumn<DemographicData, Number> malePopulationColumn = new TableColumn<>("Male Population");
        malePopulationColumn.setCellValueFactory(new PropertyValueFactory<>("malePopulation"));
        
        TableColumn<DemographicData, Number> femalePopulationColumn = new TableColumn<>("Female Population");
        femalePopulationColumn.setCellValueFactory(new PropertyValueFactory<>("femalePopulation"));

        TableColumn<DemographicData, Number> AgeGroup1Column = new TableColumn<>("Age: 0-4");
        AgeGroup1Column.setCellValueFactory(new PropertyValueFactory<>("ageGroup1"));

        TableColumn<DemographicData, Number> AgeGroup2Column = new TableColumn<>("Age: 5-14");
        AgeGroup2Column.setCellValueFactory(new PropertyValueFactory<>("ageGroup2"));
        
        TableColumn<DemographicData, Number> AgeGroup3Column = new TableColumn<>("Age: 15-24");
        AgeGroup3Column.setCellValueFactory(new PropertyValueFactory<>("ageGroup3"));
        
        TableColumn<DemographicData, Number> AgeGroup4Column = new TableColumn<>("Age: 25-64");
        AgeGroup4Column.setCellValueFactory(new PropertyValueFactory<>("ageGroup4"));
        
        TableColumn<DemographicData, Number> AgeGroup5Column = new TableColumn<>("Age: 65+");
        AgeGroup5Column.setCellValueFactory(new PropertyValueFactory<>("ageGroup5"));
        tableView.getColumns().addAll(RegionColumn, populationColumn, malePopulationColumn, femalePopulationColumn, AgeGroup1Column, AgeGroup2Column, AgeGroup3Column, AgeGroup4Column, AgeGroup5Column);

        try(ResultSet populationTable = dbOps.getPopulationTable()){
            ObservableList<DemographicData> data = FXCollections.observableArrayList();
            
            while (populationTable.next()) {
                String region = populationTable.getString("region_name");
                int population = populationTable.getInt("total_population");
                int malePopulation = populationTable.getInt("male_population");
                int femalePopulation = populationTable.getInt("female_population");
                int ageGroup1 = populationTable.getInt("age_group_0_4");
                int ageGroup2 = populationTable.getInt("age_group_5_14");
                int ageGroup3 = populationTable.getInt("age_group_15_24");
                int ageGroup4 = populationTable.getInt("age_group_25_64");
                int ageGroup5 = populationTable.getInt("age_group_65_plus");
                data.add(new DemographicData(region, population, malePopulation, femalePopulation, ageGroup1, ageGroup2, ageGroup3, ageGroup4, ageGroup5));
            }

            tableView.setItems(data);
        } catch (SQLException e) {
            System.out.println(e);
            // Handle exceptions
        }
        
        listContainer.getChildren().addAll(listView, tableView);
        dashboard.add(listContainer, 0, 8, 2, 1); // Adjust grid position as needed

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(dashboard);
        scrollPane.setFitToWidth(true); // Ensures the scroll pane is only vertical
        scrollPane.getStyleClass().add("scroll-pane");
        return scrollPane;
    }
    public class RegionItem {
        private final String name;
        private final int id;
    
         public RegionItem(String name, int id) {
            this.name = name;
            this.id = id;
        }

        @Override
        public String toString() {
            return name; // This is what will be displayed in the ComboBox
        }

        public int getId() {
            return id; // This can be used to get the ID when an item is selected
        }
    }

    private Node createDataEntryInterface(Stage primaryStage) {
        // Create the GridPane layout
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("grid-pane");
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10); // Horizontal gap between columns
        gridPane.setVgap(10); // Vertical gap between rows
        gridPane.setPadding(new Insets(20)); // Padding around the grid
    
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), gridPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
    
        VBox dataEntryLayout = new VBox(10); // 10 is the spacing between form elements
        dataEntryLayout.setAlignment(Pos.CENTER);
        dataEntryLayout.setPadding(new Insets(30, 40, 30, 60)); // Top, Right, Bottom, Left padding

        dataEntryLayout.getStyleClass().add("data-entry-layout");

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS); // Allow column to grow
        gridPane.getColumnConstraints().add(columnConstraints); // Apply to the gridPane
        
        HBox headerContainer = new HBox();
        headerContainer.getStyleClass().add("header-container");
        headerContainer.setSpacing(10); // Set spacing between icon and label
        ImageView dashboardIcon = new ImageView(new Image(getClass().getResourceAsStream("icon/population-icon.png")));
        dashboardIcon.setFitHeight(30);
        dashboardIcon.setFitWidth(30);
        Label headerLabel = new Label("Population Form");
        headerContainer.setMaxWidth(Double.MAX_VALUE); // Allow the label to grow
        headerContainer.setAlignment(Pos.CENTER_LEFT); // Center the label tex
        headerContainer.getChildren().addAll(dashboardIcon, headerLabel);
    
        gridPane.add(headerContainer, 0, 0);
        gridPane.add(dataEntryLayout, 0, 1);

        // Dropdown for region_id foreign key
        Label regionLabel = new Label("Region:");
        ComboBox<RegionItem> regionDropdown = new ComboBox<>();
        regionDropdown.getStyleClass().add("region-dropdown");
        DatabaseOperations dbOps = new DatabaseOperations();
        try {
            Map<String, Integer> regionsMap = dbOps.getRegions();
            for (Map.Entry<String, Integer> entry : regionsMap.entrySet()) {
                regionDropdown.getItems().add(new RegionItem(entry.getKey(), entry.getValue()));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        regionDropdown.setPromptText("Select Region");

        // Input fields for population data
        Label totalPopulationLabel = new Label("Total Population:");
        TextField totalPopulationField = new TextField();
        totalPopulationField.getStyleClass().add("population-field");
        totalPopulationField.setPromptText("Enter total population");

        Label totalMalePopulationLabel = new Label("Total Male Population:");
        TextField totalMalePopulationField = new TextField();
        totalMalePopulationField.getStyleClass().add("population-field");
        totalMalePopulationField.setPromptText("Enter total male population");

        Label totalFemalePopulationLabel = new Label("Total Female Population:");
        TextField totalFemalePopulationField = new TextField();
        totalFemalePopulationField.getStyleClass().add("population-field");
        totalFemalePopulationField.setPromptText("Enter total female population");
    
        Label ageGroup0to4Label = new Label("Age 0-4:");
        TextField ageGroup0to4Field = new TextField();
        ageGroup0to4Field.getStyleClass().add("age-group-field");
        ageGroup0to4Field.setPromptText("Enter population for age 0-4");

        Label ageGroup5to14Label = new Label("Age 5-14:");
        TextField ageGroup5to14Field = new TextField();
        ageGroup5to14Field.getStyleClass().add("age-group-field");
        ageGroup5to14Field.setPromptText("Enter population for age 5-14");

        Label ageGroup15to24Label = new Label("Age 15-24:");
        TextField ageGroup15to24Field = new TextField();
        ageGroup15to24Field.getStyleClass().add("age-group-field");
        ageGroup15to24Field.setPromptText("Enter population for age 15-24");

        Label ageGroup25to64Label = new Label("Age 25-64:");
        TextField ageGroup25to64Field = new TextField();
        ageGroup25to64Field.getStyleClass().add("age-group-field");
        ageGroup25to64Field.setPromptText("Enter population for age 25-64");

        Label ageGroup65PlusLabel = new Label("Age 65+:");
        TextField ageGroup65PlusField = new TextField();
        ageGroup65PlusField.getStyleClass().add("age-group-field");
        ageGroup65PlusField.setPromptText("Enter population for age 65+");
        // Submit button
        Button submitButton = new Button("Submit");
        submitButton.getStyleClass().add("submit-button");

        // Add all elements to the layout
        dataEntryLayout.getChildren().addAll(
            regionLabel, regionDropdown,
            totalPopulationLabel, totalPopulationField,
            totalMalePopulationLabel, totalMalePopulationField,
            totalFemalePopulationLabel, totalFemalePopulationField,
            ageGroup0to4Label, ageGroup0to4Field,
            ageGroup5to14Label, ageGroup5to14Field,
            ageGroup15to24Label, ageGroup15to24Field,
            ageGroup25to64Label, ageGroup25to64Field,
            ageGroup65PlusLabel, ageGroup65PlusField,
            submitButton
        );

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(gridPane);
        scrollPane.setFitToWidth(true); // Ensures the scroll pane is only vertical
        scrollPane.getStyleClass().add("scroll-pane");
        // Return the ScrollPane instead of the VBox
        submitButton.setOnAction(event -> {
        // Check if any of the fields are empty
        if (regionDropdown.getValue() == null ||
            totalPopulationField.getText().trim().isEmpty() ||
            totalMalePopulationField.getText().trim().isEmpty() ||
            totalFemalePopulationField.getText().trim().isEmpty() ||
            ageGroup0to4Field.getText().trim().isEmpty() ||
            ageGroup5to14Field.getText().trim().isEmpty() ||
            ageGroup15to24Field.getText().trim().isEmpty() ||
            ageGroup25to64Field.getText().trim().isEmpty() ||
            ageGroup65PlusField.getText().trim().isEmpty()) {
        
            System.out.println("Please fill in all fields before submitting.");
            showNotification("Submission Error", "Please fill in all fields before submitting.", false, primaryStage);
            return; // Exit the method without processing the form
        }

        // Proceed with form submission if all fields are filled
        try {
            RegionItem selectedRegion = regionDropdown.getValue();
            String totalPopulation = totalPopulationField.getText();
            String totalMalePopulation = totalMalePopulationField.getText();
            String totalFemalePopulation = totalFemalePopulationField.getText();
            String ageGroup0to4 = ageGroup0to4Field.getText();
            String ageGroup5to14 = ageGroup5to14Field.getText();
            String ageGroup15to24 = ageGroup15to24Field.getText();
            String ageGroup25to64 = ageGroup25to64Field.getText();
            String ageGroup65Plus = ageGroup65PlusField.getText();

            DatabaseOperations dbOps1 = new DatabaseOperations(); // Make sure to have a valid connection object
            dbOps1.insertPopulationData(selectedRegion, totalPopulation, totalMalePopulation, totalFemalePopulation, ageGroup0to4, ageGroup5to14, ageGroup15to24, ageGroup25to64, ageGroup65Plus);

            System.out.println("Data inserted successfully.");
            NationalSummaryUpdater.main(new String[0]);
            showNotification("Success", "Data inserted successfully.", true, primaryStage);
        } catch (SQLException e) {
            System.out.println("Error inserting data: " + e.getMessage());
            showNotification("Error", "An error occurred: " + e.getMessage(), false, primaryStage);
        } catch (NumberFormatException e) {
            System.out.println("Please enter valid numbers for population fields.");
            showNotification("Error", "An error occurred: " + e.getMessage(), false, primaryStage);
        }
        });
        return scrollPane;
    }

    // Method to create the analysis interface
    private Node createAnalysisInterface() {
        // Create and return the analysis interface node
        Label analysisLabel = new Label("Analysis Interface");
        return analysisLabel;
    }
    private void initializeReportTypeColumns() {
        reportTypeColumns = new HashMap<>();

        // Example columns for the "National Summary Report"
        List<TableColumn<ReportData, ?>> nationalSummaryColumns = new ArrayList<>();
        nationalSummaryColumns.add(createColumn("Total Population", "total_population"));
        nationalSummaryColumns.add(createColumn("Total Male Population", "total_male_population"));
        nationalSummaryColumns.add(createColumn("Total Female Population", "total_female_population"));
        nationalSummaryColumns.add(createColumn("Average Age", "average_age"));
        nationalSummaryColumns.add(createColumn("Median Age", "median_age"));
        nationalSummaryColumns.add(createColumn("Total Births", "total_births"));
        nationalSummaryColumns.add(createColumn("Total Deaths", "total_deaths"));
        nationalSummaryColumns.add(createColumn("Natural Growth Rate", "natural_growth_rate"));
        nationalSummaryColumns.add(createColumn("Total Migration In", "total_migration_in"));
        nationalSummaryColumns.add(createColumn("Total Migration Out", "total_migration_out"));
        
        List<TableColumn<ReportData, ?>> populationReportColumns = new ArrayList<>();
        populationReportColumns.add(createColumn("Region Name", "region_name"));
        populationReportColumns.add(createColumn("Total Population", "total_population"));
        populationReportColumns.add(createColumn("Total Male Population", "male_population"));
        populationReportColumns.add(createColumn("Total Female Population", "female_population"));
        populationReportColumns.add(createColumn("Age Group 0-4", "age_group_0_4"));
        populationReportColumns.add(createColumn("Age Group 5-14", "age_group_5_14"));
        populationReportColumns.add(createColumn("Age Group 15-24", "age_group_15_24"));
        populationReportColumns.add(createColumn("Age Group 25-64", "age_group_25_64"));
        populationReportColumns.add(createColumn("Age Group 65+", "age_group_65_plus"));

        // Example columns for the "Migration Report"
        List<TableColumn<ReportData, ?>> migrationReportColumns = new ArrayList<>();
        migrationReportColumns.add(createColumn("Former Nation", "nation_from_id"));
        migrationReportColumns.add(createColumn("Destinations", "nation_to_id"));
        migrationReportColumns.add(createColumn("Migration Date", "migration_date"));
        migrationReportColumns.add(createColumn("Reasons", "reason"));

        // Example columns for the "Deaths Report"
        List<TableColumn<ReportData, ?>> deathsReportColumns = new ArrayList<>();
        deathsReportColumns.add(createColumn("Region Name", "region_name"));
        deathsReportColumns.add(createColumn("Date Of Death", "death_date"));
        deathsReportColumns.add(createColumn("Age At Death", "age_at_death"));
        deathsReportColumns.add(createColumn("Cause Of death", "cause_of_death"));

        // Example columns for the "Births Report"
        List<TableColumn<ReportData, ?>> birthsReportColumns = new ArrayList<>();
        birthsReportColumns.add(createColumn("Region Name", "region_name"));
        birthsReportColumns.add(createColumn("Birth Date", "birth_date"));
        birthsReportColumns.add(createColumn("Gender", "gender"));
        birthsReportColumns.add(createColumn("Mother Age", "mother_age"));
        birthsReportColumns.add(createColumn("Father Age", "father_age"));

        // Map the report types to their respective columns
        reportTypeColumns.put("National Summary Report", nationalSummaryColumns);
        reportTypeColumns.put("Population Report", populationReportColumns);
        reportTypeColumns.put("Migration Report", migrationReportColumns);
        reportTypeColumns.put("Deaths Report", deathsReportColumns);
        reportTypeColumns.put("Births Report", birthsReportColumns);
    }

    private TableColumn<ReportData, ?> createColumn(String title, String propertyName) {
        TableColumn<ReportData, ?> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        return column;
    }

    public static class ReportData {
        private int total_population;
        private int total_male_population;
        private int total_female_population;
        private int male_population;
        private int female_population;
        private double average_age;
        private double median_age;
        private int total_births;
        private int total_deaths;
        private double natural_growth_rate;
        // Fields for the "Population Report"
        private String region_name;
        private int age_group_0_4;
        private int age_group_5_14;
        private int age_group_15_24;
        private int age_group_25_64;
        private int age_group_65_plus;

        private String nation_from_id;
        private String nation_to_id;
        private LocalDate migration_date;
        private int total_migration_in;
        private int total_migration_out;
        private String reason;

        // Fields for the "Deaths Report"
        private LocalDate death_date;
        private int age_at_death;
        private String cause_of_death;
        
        private LocalDate birth_date;
        private String gender;
        private int mother_age;
        private int father_age;

        public ReportData() {
            this.total_population = 0;
            this.total_male_population = 0;
            this.total_female_population = 0;
            this.male_population = 0;
            this.female_population = 0;
            this.average_age = 0;
            this.median_age = 0;
            this.total_births = 0;
            this.total_deaths = 0;
            this.natural_growth_rate = 0;
            this.region_name = "";
            this.age_group_0_4 = 0;
            this.age_group_5_14 = 0;
            this.age_group_15_24 = 0;
            this.age_group_25_64 = 0;
            this.age_group_65_plus = 0;
            this.nation_from_id = "";
            this.nation_to_id = "";
            this.migration_date = LocalDate.now();
            this.total_migration_in = 0;
            this.total_migration_out = 0;
            this.reason = "";
            this.death_date = LocalDate.now();
            this.age_at_death = 0;
            this.cause_of_death = "";
            this.birth_date = LocalDate.now();
            this.gender = "";
            this.mother_age = 0;
            this.father_age = 0;
        }
                
        // Constructor
        public ReportData(
            int total_population,
            int total_male_population,
            int total_female_population,
            int male_population,
            int female_population,
            double average_age,
            double median_age,
            int total_births,
            int total_deaths,
            double natural_growth_rate,
            int total_migration_in,
            int total_migration_out,
            String region_name,
            int ageGroup_0_4,
            int ageGroup_5_14,
            int ageGroup_15_24,
            int ageGroup_25_64,
            int ageGroup_65_plus,
            String nation_from_id,
            String nation_to_id,
            LocalDate migration_date,
            String reason,
            LocalDate death_date,
            int age_at_death,
            String cause_of_death,
            LocalDate birth_date,
            String gender,
            int mother_age,
            int father_age
        ) {
            this.total_population = total_population;
            this.total_male_population = total_male_population;
            this.total_female_population = total_female_population;
            this.male_population = male_population;
            this.female_population = female_population;
            this.average_age = average_age;
            this.median_age = median_age;
            this.total_births = total_births;
            this.total_deaths = total_deaths;
            this.natural_growth_rate = natural_growth_rate;
            this.region_name = region_name;
            this.age_group_0_4 = ageGroup_0_4;
            this.age_group_5_14 = ageGroup_5_14;
            this.age_group_15_24 = ageGroup_15_24;
            this.age_group_25_64 = ageGroup_25_64;
            this.age_group_65_plus = ageGroup_65_plus;
            this.nation_from_id = nation_from_id;
            this.nation_to_id = nation_to_id;
            this.migration_date = migration_date;
            this.total_migration_in = total_migration_in;
            this.total_migration_out = total_migration_out;
            this.reason = reason;
            this.death_date = death_date;
            this.age_at_death = age_at_death;
            this.cause_of_death = cause_of_death;
            this.birth_date = birth_date;
            this.gender = gender;
            this.mother_age = mother_age;
            this.father_age = father_age;
        }
        // Getters and setters
        public int getTotal_population() {return total_population;}
        public void setTotal_population(int total_population) {this.total_population = total_population;}
        public int getTotal_male_population() {return total_male_population;}
        public void setTotal_male_population(int total_male_population) {this.total_male_population = total_male_population;}
        public int getTotal_female_population() {return total_female_population;}
        public void setTotal_female_population(int total_female_population) {this.total_female_population = total_female_population;}
        public int getMale_population() {return male_population;}
        public void setMale_population(int male_population) {this.male_population = male_population;}
        public int getFemale_population() {return female_population;}
        public void setFemale_population(int female_population) {this.female_population = female_population;}
        public double getAverage_age() {return average_age;}
        public void setAverage_age(double average_age) {this.average_age = average_age;}
        public double getMedian_age() {return median_age;}
        public void setMedian_age(double median_age) {this.median_age = median_age;}
        public int getTotal_births() {return total_births;}
        public void setTotal_births(int total_births) {this.total_births = total_births;}
        public int getTotal_deaths() {return total_deaths;}
        public void setTotal_deaths(int total_deaths) {this.total_deaths = total_deaths;}
        public double getNatural_growth_rate() {return natural_growth_rate;}
        public void setNatural_growth_rate(double natural_growth_rate) {this.natural_growth_rate = natural_growth_rate;}
        public String getRegion_name() {return region_name;}
        public void setRegion_name(String region_name) { this.region_name = region_name;}
        public int getAge_group_0_4() {return age_group_0_4;}
        public void setAge_group_0_4(int ageGroup_0_4) {this.age_group_0_4 = ageGroup_0_4;}
        public int getAge_group_5_14() {return age_group_5_14;}
        public void setAge_group_5_14(int ageGroup_5_14) {this.age_group_5_14 = ageGroup_5_14;}
        public int getAge_group_15_24() {return age_group_15_24;}
        public void setAge_group_15_24(int ageGroup_15_24) {this.age_group_15_24 = ageGroup_15_24;}
        public int getAge_group_25_64() {return age_group_25_64;}
        public void setAge_group_25_64(int ageGroup_25_64) {this.age_group_25_64 = ageGroup_25_64;}
        public int getAge_group_65_plus() {return age_group_65_plus;}
        public void setAge_group_65_plus(int ageGroup_65_plus) {this.age_group_65_plus = ageGroup_65_plus;}
        public String getNation_from_id() {return nation_from_id;}
        public void setNation_from_id(String nation_from_id) {this.nation_from_id = nation_from_id;}
        public String getNation_to_id() {return nation_to_id;}
        public void setNation_to_id(String nation_to_id) {this.nation_to_id = nation_to_id;}
        public LocalDate getMigration_date() {return migration_date;}
        public void setMigration_date(LocalDate migration_date) {this.migration_date = migration_date;}
        public int getTotal_migration_in(){return total_migration_in;}
        public void setTotal_migration_in(int total_migration_in){this.total_migration_in = total_migration_in;}
        public int getTotal_migration_out(){return total_migration_out;}
        public void setTotal_migration_out(int total_migration_out){this.total_migration_out = total_migration_out;}
        public String getReason() {return reason;}
        public void setReason(String reason) {this.reason = reason;}
        public LocalDate getDeath_date() {return death_date;}
        public void setDeath_date(LocalDate death_date) {this.death_date = death_date;}
        public int getAge_at_death() {return age_at_death;}
        public void setAge_at_death(int age_at_death) {this.age_at_death = age_at_death;}
        public String getCause_of_death() {return cause_of_death;}
        public void setCause_of_death(String cause_of_death) {this.cause_of_death = cause_of_death;}
        public LocalDate getBirth_date() {return birth_date;}
        public void setBirth_date(LocalDate birth_date) {this.birth_date = birth_date;}
        public String getGender() {return gender;}
        public void setGender(String gender) {this.gender = gender;}
        public int getMother_age() {return mother_age;}
        public void setMother_age(int mother_age) {this.mother_age = mother_age;}
        public int getFather_age() {return father_age;}
        public void setFather_age(int father_age) {this.father_age = father_age;}  
        public void setValue(Field field, Object value) throws IllegalAccessException {
            field.setAccessible(true);
            field.set(this, value);
        }
    }
    public void generateReport(String reportType, LocalDate startDate, LocalDate endDate) {
        ObservableList<ReportData> reportData = FXCollections.observableArrayList();

        DatabaseOperations dbOps = new DatabaseOperations();
        try (Connection connection = dbOps.connection) {
            String query = dbOps.getQueryForReportType(reportType);
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();

            // Clear previous columns
            reportTable.getColumns().clear();

            // Add columns for the specific report type
            List<TableColumn<ReportData, ?>> columnsForReportType = reportTypeColumns.get(reportType);
            if (columnsForReportType != null) {
                reportTable.getColumns().addAll(columnsForReportType);
            } 
            while (rs.next()) {
                ReportData data = new ReportData();
                if (columnsForReportType != null) {
                    for (TableColumn<ReportData, ?> col : columnsForReportType) {
                        try {
                            String propertyName = ((PropertyValueFactory) col.getCellValueFactory()).getProperty();
                            Field field = ReportData.class.getDeclaredField(propertyName);
                            field.setAccessible(true); // Make the field accessible if it's private

                            Object value = rs.getObject(propertyName);
                            switch (value) {
                                case BigDecimal bd -> value = bd.doubleValue();
                                case java.sql.Date jsd -> value = jsd.toLocalDate();
                                // You can add more cases for other types if needed
                                default -> {
                                }
                            }

                            field.set(data, value); // Set the value directly using reflection
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
                reportData.add(data);
            }
            reportTable.setItems(reportData);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            // Handle exception (e.g., show an error dialog)
        }
    }
    private Node createReportsInterface(Stage primaryStage) {
        if (reportTable == null) {
            reportTable = new TableView<>();

            // Define columns for the TableView based on the ReportData fields
            TableColumn<ReportData, String> regionColumn = new TableColumn<>(" ");
            regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));

            // Add all columns to the TableView
            reportTable.getColumns().addAll(regionColumn);
        }
        
        VBox reportsLayout = new VBox(10); // 10 is the spacing between elements
        reportsLayout.setAlignment(Pos.CENTER);
        reportsLayout.setPadding(new Insets(20)); // Padding around the layout
        reportsLayout.getStyleClass().add("grid-pane");
        
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), reportsLayout);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        
        HBox headerContainer = new HBox();
        headerContainer.getStyleClass().add("header-container");
        headerContainer.setSpacing(10); // Set spacing between icon and label
        ImageView dashboardIcon = new ImageView(new Image(getClass().getResourceAsStream("icon/report-icon.png")));
        dashboardIcon.setFitHeight(30);
        dashboardIcon.setFitWidth(30);
        Label headerLabel = new Label("Generate Report");
        headerContainer.setMaxWidth(Double.MAX_VALUE); // Allow the label to grow
        headerContainer.setAlignment(Pos.CENTER_LEFT); // Center the label tex
        headerContainer.getChildren().addAll(dashboardIcon, headerLabel);
        // Dropdown for selecting report type
        ComboBox<String> reportTypeDropdown = new ComboBox<>();
        reportTypeDropdown.getItems().addAll("National Summary Report", "Population Report", "Migration Report", "Deaths Report", "Births Report");
        reportTypeDropdown.setPromptText("Select Report Type");
        reportTypeDropdown.getStyleClass().add("region-dropdown");
        
        // Date range picker
        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");
        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");
        // Generate report button
        Button generateReportButton = new Button("Generate Report");
        generateReportButton.setOnAction(event -> {
        // Logic to generate the report based on selected type and date range
            String reportType = reportTypeDropdown.getValue();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            if (reportType != null && startDate != null && endDate != null) {
                generateReport(reportType, startDate, endDate);
            } else {
                showNotification("Error", "Please select report type and date range.", false, primaryStage);
            }
        });
        
        javafx.scene.control.Button exportButton = new javafx.scene.control.Button("Export to PDF");
        exportButton.setOnAction(event -> {
            String reportType = reportTypeDropdown.getValue();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            if (reportType != null && startDate != null && endDate != null) {
                javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
                // Set extension filter for text files
                javafx.stage.FileChooser.ExtensionFilter extFilter = new javafx.stage.FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
                fileChooser.getExtensionFilters().add(extFilter);
    
                // Show save file dialog
                java.io.File file = fileChooser.showSaveDialog(primaryStage);
    
                if (file != null) {
                    exportReportToPDF(file, reportTypeDropdown.getValue(), primaryStage);
                }
            } else {
                showNotification("Error", "Please select report type and date range.", false, primaryStage);
            }
        });
        
        reportsLayout.getChildren().addAll(headerContainer, reportTypeDropdown, startDatePicker, endDatePicker, generateReportButton, reportTable, exportButton);

        return reportsLayout;
    }
    public void exportReportToPDF(File file, String reportType, Stage primaryStage) {
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        try {
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            document.add(new com.itextpdf.text.Chunk(""));
            
            try{
                com.itextpdf.text.Image emblem = com.itextpdf.text.Image.getInstance("C:/Users/Lalaina/JavaProject/ExplDemo/src/test/java/expldemo/src/icon/madagascar-republic.jpg");
                emblem.setAlignment(Element.ALIGN_CENTER);
                emblem.scaleAbsolute(200f, 125f);
                document.add(emblem);
            } catch (MalformedURLException e) {
                // Handle the MalformedURLException here
                System.out.println(e);
            } catch (IOException e) {
                // Handle the IOException here
                System.out.println(e);
            }
            
            Paragraph executiveSummary = new Paragraph("Executive Summary, Official " + reportType);
            executiveSummary.setAlignment(Element.ALIGN_CENTER);
            document.add(executiveSummary);            

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate localDate = LocalDate.now();
            Paragraph date = new Paragraph(dtf.format(localDate));
            date.setAlignment(Element.ALIGN_CENTER);
            document.add(date);
            
            document.add(new Paragraph("\n\n\n\n"));
            
            com.itextpdf.text.pdf.PdfPTable pdfTable = new com.itextpdf.text.pdf.PdfPTable(reportTable.getColumns().size());
            pdfTable.setWidthPercentage(100);
            for (javafx.scene.control.TableColumn<ReportData, ?> column : reportTable.getColumns()) {
                pdfTable.addCell(new com.itextpdf.text.Paragraph(column.getText()));
            }
            pdfTable.setHeaderRows(1);

            if (reportTable.getItems().isEmpty()) {
                throw new IllegalStateException("No data available to export.");
            }

            for (int i = 0; i < reportTable.getItems().size(); i++) {
                for (int j = 0; j < reportTable.getColumns().size(); j++) {
                    Object cellData = reportTable.getColumns().get(j).getCellData(i);
                    String cellValue = cellData == null ? " " : cellData.toString(); // Handle null values
                    pdfTable.addCell(new com.itextpdf.text.Paragraph(cellValue));
                }
            }

            if (pdfTable.size() <= 0) {
                throw new IllegalStateException("No data was added to the PDF.");
            }

            document.add(pdfTable);
            Platform.runLater(() -> showNotification("Export Successful", "The report has been exported successfully.", true, primaryStage));
        } catch (com.itextpdf.text.DocumentException | FileNotFoundException e) {
            Platform.runLater(() -> showNotification("Export Error", "Failed to export the report: " + e.getMessage(), false, primaryStage));
        } catch (IllegalStateException e) {
            Platform.runLater(() -> showNotification("Export Error", e.getMessage(), false, primaryStage));
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
