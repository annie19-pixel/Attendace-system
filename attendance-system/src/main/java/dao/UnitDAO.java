package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import database.DatabaseConnection;
import Model.Unit;

public class UnitDAO {

    // Add unit
    public boolean addUnit(Unit unit) {

        String sql =
                "INSERT INTO units (unit_code, unit_name) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, unit.getUnitCode());
            stmt.setString(2, unit.getUnitName());

            int rows = stmt.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete unit
    public boolean deleteUnit(String unitCode) {

        String sql = "DELETE FROM units WHERE unit_code = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, unitCode);

            int rows = stmt.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get unit by code
    public Unit getUnitByCode(String unitCode) {

        String sql = "SELECT * FROM units WHERE unit_code = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, unitCode);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                return new Unit(
                        rs.getInt("unit_id"),
                        rs.getString("unit_code"),
                        rs.getString("unit_name")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Get all units
    public List<Unit> getAllUnits() {

        List<Unit> units = new ArrayList<>();

        String sql = "SELECT * FROM units";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                units.add(new Unit(
                        rs.getInt("unit_id"),
                        rs.getString("unit_code"),
                        rs.getString("unit_name")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return units;
    }
}