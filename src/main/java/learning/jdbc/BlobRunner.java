package learning.jdbc;

import learning.jdbc.util.ConnectionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.*;

public class BlobRunner {

    public static void main(String[] args) throws SQLException, IOException {
        getImage();
    }

    private static void getImage() throws SQLException, IOException {
        var sql = """
                SELECT image from aircraft WHERE id = ?
                """;
        try (var connection = ConnectionManager.get();
            var prepareStatement = connection.prepareStatement(sql)) {
            prepareStatement.setInt(1, 1);
            ResultSet resultSet = prepareStatement.executeQuery();
            while (resultSet.next()) {
                byte[] image = resultSet.getBytes("image");
                Files.write(Path.of("src/main/resources/boing777_new.jpeg"),
                        image, StandardOpenOption.CREATE);
            }
        }
    }

    private static void sameImageForPostgres() throws SQLException, IOException {
        var sql = """
                UPDATE aircraft
                SET image = ?
                WHERE id = 1
                """;
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setBytes(1, Files.readAllBytes(
                    Path.of("src/main/resources/boing777.jpeg")));
            preparedStatement.executeUpdate();
        }
    }

    // не работает в Postgres - Postgres не поддерживаем Blob
    private static void sameImageWithBlob() throws SQLException, IOException {
        var sql = """
                UPDATE aircraft
                SET image = ?
                WHERE id = 1
                """;
        try (Connection connection = ConnectionManager.get();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            Blob blob = connection.createBlob();
            blob.setBytes(1, Files.readAllBytes(
                    Path.of("resources", "boing777.jpeg")));
            preparedStatement.setBlob(1, blob);
            preparedStatement.executeUpdate();
        }
    }
}
