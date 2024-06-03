package com.gridhub.utilities;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class RepositoryConnection implements AutoCloseable {
    private final HikariDataSource dataSource;

    public RepositoryConnection(ConnectionProperties connectionProperties) throws SQLException {
        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(connectionProperties.getUrl());
        dataSource.setUsername(connectionProperties.getUser());
        dataSource.setPassword(connectionProperties.getPassword());
    }

    @Override
    public void close() {
        dataSource.close();
    }


    public void execute(String query, Object... args) {
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(query)) {
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void execute(String query, Consumer<PreparedStatement> consumer) {
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(query)) {
            consumer.accept(preparedStatement);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    public <T> T findOne(String query, Function<ResultSet, T> mapper, Object... args) {
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(query)) {
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                T result = null;
                int resultSetSize = 0;
                while (resultSet.next()) {
                    resultSetSize++;
                    if (resultSetSize > 1) {
                        throw new IllegalStateException("More than one result found");
                    }
                    result = mapper.apply(resultSet);
                }
                return result;
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public <T> List<T> findMany(String query, Function<ResultSet, T> mapper, Object... args) {
        List<T> results = new ArrayList<>();
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(query)) {
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    T result = mapper.apply(rs);
                    results.add(result);
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return results;
    }

}