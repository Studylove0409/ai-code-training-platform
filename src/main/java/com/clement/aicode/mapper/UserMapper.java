package com.clement.aicode.mapper;

import com.clement.aicode.model.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UserMapper {

    private final JdbcTemplate jdbcTemplate;

    public UserMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User findByUsername(String username) {
        String sql = """
                SELECT id, username, password, nickname, email, status, created_at, updated_at
                FROM sys_user
                WHERE username = ?
                """;

        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next()) {
                return null;
            }

            return mapRowToUser(rs);
        }, username);
    }

    public User findById(Long id) {
        String sql = """
                SELECT id, username, password, nickname, email, status, created_at, updated_at
                FROM sys_user
                WHERE id = ?
                """;

        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next()) {
                return null;
            }

            return mapRowToUser(rs);
        }, id);
    }

    public int insert(User user) {
        String sql = """
                INSERT INTO sys_user (username, password, nickname, email, status)
                VALUES (?, ?, ?, ?, ?)
                """;

        return jdbcTemplate.update(
                sql,
                user.getUsername(),
                user.getPassword(),
                user.getNickname(),
                user.getEmail(),
                user.getStatus()
        );
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setNickname(rs.getString("nickname"));
        user.setEmail(rs.getString("email"));
        user.setStatus(rs.getInt("status"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return user;
    }
}
