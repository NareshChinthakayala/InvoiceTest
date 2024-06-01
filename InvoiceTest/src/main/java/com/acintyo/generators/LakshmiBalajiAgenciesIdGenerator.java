package com.acintyo.generators;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("serial")
@Component
public class LakshmiBalajiAgenciesIdGenerator implements IdentifierGenerator{

	@Autowired
    private DataSource dataSource; // Inject DataSource bean configured with HikariCP

//    @Value("${invoice.lakshmiBalaji-id.prefix}")
//    private String prefix;

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT next_val FROM lakshmi_balaji_id");
            if (rs.next()) {
                int id = rs.getInt(1);
                statement.executeUpdate("UPDATE lakshmi_balaji_id SET next_val = next_val + 1");
//                return prefix + "-" + id;
                return  ""+id;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
