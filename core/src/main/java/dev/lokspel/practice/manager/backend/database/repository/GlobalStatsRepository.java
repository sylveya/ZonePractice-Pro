package dev.lokspel.practice.manager.backend.database.repository;

import dev.lokspel.practice.manager.backend.database.model.GlobalStatsRow;
import dev.lokspel.practice.manager.profile.Profile;
import dev.lokspel.practice.manager.profile.statistics.ProfileStat;
import dev.lokspel.practice.util.Common;
import lombok.Getter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Getter
public class GlobalStatsRepository {

    private static final String SELECT_STATS = "SELECT * FROM global_stats WHERE uuid = ?";

    private static final String UPSERT_STATS =
            "INSERT INTO global_stats (username, uuid, firstJoin, lastJoin, unrankedWins, unrankedLosses, "
                    + "rankedWins, rankedLosses, globalElo, globalRank, experience, winStreak, bestWinStreak, "
                    + "loseStreak, bestLoseStreak) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE username = VALUES(username), firstJoin = VALUES(firstJoin), "
                    + "lastJoin = VALUES(lastJoin), unrankedWins = VALUES(unrankedWins), "
                    + "unrankedLosses = VALUES(unrankedLosses), rankedWins = VALUES(rankedWins), "
                    + "rankedLosses = VALUES(rankedLosses), globalElo = VALUES(globalElo), "
                    + "globalRank = VALUES(globalRank), experience = VALUES(experience), "
                    + "winStreak = VALUES(winStreak), bestWinStreak = VALUES(bestWinStreak), "
                    + "loseStreak = VALUES(loseStreak), bestLoseStreak = VALUES(bestLoseStreak)";

    private static final String DELETE_STATS = "DELETE FROM global_stats WHERE uuid = ?";

    private final ExecutorService executor;
    private final DataSource dataSource;

    public GlobalStatsRepository(DataSource dataSource, ExecutorService executor) {
        this.dataSource = dataSource;
        this.executor = executor;
    }

    public CompletableFuture<GlobalStatsRow> getStats(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(SELECT_STATS)) {

                statement.setString(1, uuid.toString());

                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next() ? read(resultSet) : null;
                }
            } catch (SQLException e) {
                Common.sendConsoleMMMessage("<red>[Database] get global stats error: " + e.getMessage());
                return null;
            }
        }, executor);
    }

    public CompletableFuture<Void> save(Profile profile) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection()) {
                write(connection, profile);
            } catch (SQLException e) {
                Common.sendConsoleMMMessage("<red>[Database] save global stats error: " + e.getMessage());
            }
        }, executor);
    }

    public CompletableFuture<Void> saveAll(Collection<Profile> profiles) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection()) {
                boolean previousAutoCommit = connection.getAutoCommit();
                connection.setAutoCommit(false);

                try {
                    for (Profile profile : profiles) {
                        write(connection, profile);
                    }
                    connection.commit();
                } catch (SQLException e) {
                    connection.rollback();
                    throw e;
                } finally {
                    connection.setAutoCommit(previousAutoCommit);
                }
            } catch (SQLException e) {
                Common.sendConsoleMMMessage("<red>[Database] save global stats error: " + e.getMessage());
            }
        }, executor);
    }

    public CompletableFuture<Void> delete(UUID uuid) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(DELETE_STATS)) {

                statement.setString(1, uuid.toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                Common.sendConsoleMMMessage("<red>[Database] delete global stats error: " + e.getMessage());
            }
        }, executor);
    }

    private void write(Connection connection, Profile profile) throws SQLException {
        ProfileStat stats = profile.getStats();

        try (PreparedStatement statement = connection.prepareStatement(UPSERT_STATS)) {
            statement.setString(1, profile.getStoredName());
            statement.setString(2, profile.getUuid().toString());
            statement.setLong(3, profile.getFirstJoin());
            statement.setLong(4, profile.getLastJoin());
            statement.setInt(5, stats.getWins(false));
            statement.setInt(6, stats.getLosses(false));
            statement.setInt(7, stats.getWins(true));
            statement.setInt(8, stats.getLosses(true));
            statement.setInt(9, stats.getGlobalElo());
            statement.setString(10, stats.getDivision() == null
                    ? null
                    : Common.stripLegacyColor(stats.getDivision().getFullName()));
            statement.setInt(11, stats.getExperience());
            statement.setInt(12, stats.getWinStreak());
            statement.setInt(13, stats.getBestWinStreak());
            statement.setInt(14, stats.getLoseStreak());
            statement.setInt(15, stats.getBestLoseStreak());
            statement.executeUpdate();
        }
    }

    private static GlobalStatsRow read(ResultSet resultSet) throws SQLException {
        GlobalStatsRow row = new GlobalStatsRow();
        row.setId(resultSet.getInt("id"));
        row.setUuid(resultSet.getString("uuid"));
        row.setUsername(resultSet.getString("username"));
        row.setFirstJoin(resultSet.getLong("firstJoin"));
        row.setLastJoin(resultSet.getLong("lastJoin"));
        row.setUnrankedWins(resultSet.getInt("unrankedWins"));
        row.setUnrankedLosses(resultSet.getInt("unrankedLosses"));
        row.setRankedWins(resultSet.getInt("rankedWins"));
        row.setRankedLosses(resultSet.getInt("rankedLosses"));
        row.setGlobalElo(resultSet.getInt("globalElo"));
        row.setGlobalRank(resultSet.getString("globalRank"));
        row.setExperience(resultSet.getInt("experience"));
        row.setWinStreak(resultSet.getInt("winStreak"));
        row.setBestWinStreak(resultSet.getInt("bestWinStreak"));
        row.setLoseStreak(resultSet.getInt("loseStreak"));
        row.setBestLoseStreak(resultSet.getInt("bestLoseStreak"));
        return row;
    }
}
