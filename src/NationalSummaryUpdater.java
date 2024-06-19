import java.sql.*;
import java.math.BigDecimal;

public class NationalSummaryUpdater {
    public static void main(String[] args) {
        // Database connection details
        String url = "jdbc:mysql://127.0.0.1:3307/expldemo";
        String username = "root";
        String password = "";

        try(Connection connection = DriverManager.getConnection(url, username, password)) {

            // Calculate summary values
            int totalPopulation = calculateTotalPopulation(connection);
            int totalMalePopulation = calculateTotalMalePopulation(connection);
            int totalFemalePopulation = calculateTotalFemalePopulation(connection);
            BigDecimal averageAge = calculateAverageAge(connection);
            double medianAge = calculateMedianAge(connection);
            int totalBirths = calculateTotalBirths(connection);
            int totalDeaths = calculateTotalDeaths(connection);
            int naturalGrowthRate = totalBirths - totalDeaths;
            int totalMigrationIn = calculateTotalMigrationIn(connection);
            int totalMigrationOut = calculateTotalMigrationOut(connection);
            int netMigrationRate = totalMigrationIn - totalMigrationOut;

            // Insert summary values into nationalsummary table
            insertSummaryValues(connection, totalPopulation, totalMalePopulation, totalFemalePopulation,
                    averageAge, medianAge, totalBirths, totalDeaths, naturalGrowthRate,
                    totalMigrationIn, totalMigrationOut, netMigrationRate);

            // Close the connection
            connection.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    // Implement methods to calculate summary values (e.g., calculateTotalPopulation, calculateAverageAge, etc.)

    // Implement method to insert summary values into nationalsummary table
    private static void insertSummaryValues(Connection connection, int totalPopulation, int totalMalePopulation,
                                            int totalFemalePopulation, BigDecimal averageAge, double medianAge,
                                            int totalBirths, int totalDeaths, int naturalGrowthRate,
                                            int totalMigrationIn, int totalMigrationOut, int netMigrationRate) {
        try {
            String insertQuery = "INSERT INTO nationalsummary (total_population, total_male_population, " +
                    "total_female_population, average_age, median_age, total_births, total_deaths, " +
                    "natural_growth_rate, total_migration_in, total_migration_out, net_migration_rate) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setInt(1, totalPopulation);
            preparedStatement.setInt(2, totalMalePopulation);
            preparedStatement.setInt(3, totalFemalePopulation);
            preparedStatement.setBigDecimal(4, averageAge);
            preparedStatement.setDouble(5, medianAge);
            preparedStatement.setInt(6, totalBirths);
            preparedStatement.setInt(7, totalDeaths);
            preparedStatement.setInt(8, naturalGrowthRate);
            preparedStatement.setInt(9, totalMigrationIn);
            preparedStatement.setInt(10, totalMigrationOut);
            preparedStatement.setInt(11, netMigrationRate);

            preparedStatement.executeUpdate();
            System.out.println("Summary values inserted successfully!");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public static int calculateTotalPopulation(Connection connection) {
        int totalPopulation = 0;
        String query = "SELECT SUM(total_population) " +
               "FROM (" +
               "    SELECT p1.total_population " +
               "    FROM population p1 " +
               "    INNER JOIN (" +
               "        SELECT region_id, MAX(created_at) AS latest_entry " +
               "        FROM population " +
               "        GROUP BY region_id " +
               "        ORDER BY latest_entry DESC " +
               "        LIMIT 22" +
               "    ) p2 ON p1.region_id = p2.region_id AND p1.created_at = p2.latest_entry" +
               ") AS latest_population;";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query);ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                totalPopulation = resultSet.getInt(1);
            }
            resultSet.close();
            preparedStatement.close();
            } catch (SQLException e) {
                System.out.println(e);
            }
        return totalPopulation;
    }

    public static int calculateTotalMalePopulation(Connection connection) {
        int totalMalePopulation = 0;
        String query = "SELECT SUM(male_population) " +
            "FROM ( " +
            "SELECT p1.male_population " +
            "FROM population p1 " +
            "INNER JOIN ( " +
            "SELECT region_id, MAX(created_at) AS latest_entry " +
            "FROM population " +
            "GROUP BY region_id " +
            ") p2 ON p1.region_id = p2.region_id AND p1.created_at = p2.latest_entry " +
            ") AS latest_population";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query);ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                totalMalePopulation = resultSet.getInt(1);
            }
        resultSet.close();
        preparedStatement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return totalMalePopulation;
    }

    public static int calculateTotalFemalePopulation(Connection connection) {
        int totalFemalePopulation = 0;
        String query = "SELECT SUM(female_population) " +
            "FROM ( " +
            "SELECT p1.female_population " +
            "FROM population p1 " +
            "INNER JOIN ( " +
            "SELECT region_id, MAX(created_at) AS latest_entry " +
            "FROM population " +
            "GROUP BY region_id " +
            ") p2 ON p1.region_id = p2.region_id AND p1.created_at = p2.latest_entry " +
            ") AS latest_population";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query);ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                totalFemalePopulation = resultSet.getInt(1);
            }
        resultSet.close();
        preparedStatement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return totalFemalePopulation;
    }
    public static BigDecimal calculateAverageAge(Connection connection) {
        BigDecimal totalWeightedAge = BigDecimal.ZERO;
        int totalPopulation = 0;
        String query = "SELECT total_population, age_group_0_4, age_group_5_14, age_group_15_24, age_group_25_64, age_group_65_plus FROM (SELECT p1.* FROM population p1 INNER JOIN (SELECT region_id, MAX(created_at) AS latest_entry FROM population GROUP BY region_id) p2 ON p1.region_id = p2.region_id AND p1.created_at = p2.latest_entry) AS latest_population";

        try(PreparedStatement preparedStatement = connection.prepareStatement(query);ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int population = resultSet.getInt("total_population");
                int age0_4 = resultSet.getInt("age_group_0_4");
                int age5_14 = resultSet.getInt("age_group_5_14");
                int age15_24 = resultSet.getInt("age_group_15_24");
                int age25_64 = resultSet.getInt("age_group_25_64");
                int age65_plus = resultSet.getInt("age_group_65_plus");

                int totalAge = 2 * age0_4 + 9 * age5_14 + 19 * age15_24 + 44 * age25_64 + 70 * age65_plus;
                totalWeightedAge = totalWeightedAge.add(BigDecimal.valueOf(totalAge));
                totalPopulation += population;
            }

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }

        if (totalPopulation > 0) {
            return totalWeightedAge.divide(BigDecimal.valueOf(totalPopulation), 2, BigDecimal.ROUND_HALF_UP);
        } else {
            return BigDecimal.ZERO;
        }
    }
    private static int getTotalPopulation(Connection connection) throws SQLException {
        String totalPopulationQuery = "SELECT SUM(age_group_0_4 + age_group_5_14 + age_group_15_24 + age_group_25_64 + age_group_65_plus) AS total_population "
                + "FROM ( "
                + "    SELECT p.* "
                + "    FROM population p "
                + "    INNER JOIN ( "
                + "        SELECT region_id, MAX(created_at) AS latest_entry "
                + "        FROM population "
                + "        GROUP BY region_id "
                + "    ) latest_per_region ON p.region_id = latest_per_region.region_id AND p.created_at = latest_per_region.latest_entry "
                + ") AS latest_population";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(totalPopulationQuery)) {
            if (resultSet.next()) {
                return resultSet.getInt("total_population");
            }
        }
        return 0;
    }

    private static double calculateMedianAge(Connection connection) throws SQLException {
        String medianAgeQuery = "SELECT AVG(age_group) AS median_age "
                + "FROM ( "
                + "    SELECT age_group, @cumulative_population := @cumulative_population + total_in_age_group AS cumulative_population "
                + "    FROM ( "
                + "        SELECT 2 AS age_group, SUM(age_group_0_4) AS total_in_age_group FROM population "
                + "        UNION ALL "
                + "        SELECT 9, SUM(age_group_5_14) FROM population "
                + "        UNION ALL "
                + "        SELECT 19, SUM(age_group_15_24) FROM population "
                + "        UNION ALL "
                + "        SELECT 44, SUM(age_group_25_64) FROM population "
                + "        UNION ALL "
                + "        SELECT 70, SUM(age_group_65_plus) FROM population "
                + "    ) AS age_distribution "
                + "    ORDER BY age_group "
                + ") AS cumulative_distribution "
                + "WHERE cumulative_population >= ? / 2 "
                + "LIMIT 2";

        try (PreparedStatement preparedStatement = connection.prepareStatement(medianAgeQuery)) {
            // Initialize session variables
            try (Statement initStatement = connection.createStatement()) {
                initStatement.execute("SET @cumulative_population := 0");
            }

            preparedStatement.setInt(1, getTotalPopulation(connection));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("median_age");
                }
            }
        }
        return 0.0;
    }
    public static int calculateTotalDeaths(Connection connection) {
        int totalDeaths = 0;
        String query = "SELECT COUNT(*) FROM deaths";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query);ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                totalDeaths = resultSet.getInt(1);
            }
        resultSet.close();
        preparedStatement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return totalDeaths;
    }
    public static int calculateTotalBirths(Connection connection) {
        int totalBirths = 0;
        String query = "SELECT COUNT(*) FROM births";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query);ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                totalBirths = resultSet.getInt(1);
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return totalBirths;
    }
    public static int calculateTotalMigrationIn(Connection connection) {
        int totalMigrationIn = 0;
        String query = "SELECT COUNT(*) FROM migration WHERE nation_to_id = 'Madagascar'";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query);ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                totalMigrationIn = resultSet.getInt(1);
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return totalMigrationIn;
    }
    public static int calculateTotalMigrationOut(Connection connection) {
    int totalMigrationOut = 0;
    String query = "SELECT COUNT(*) FROM migration WHERE nation_from_id = 'Madagascar'";
    try(PreparedStatement preparedStatement = connection.prepareStatement(query);ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
            totalMigrationOut = resultSet.getInt(1);
        }
        resultSet.close();
        preparedStatement.close();
    } catch (SQLException e) {
        System.out.println(e);
    }
    return totalMigrationOut;
    }
}