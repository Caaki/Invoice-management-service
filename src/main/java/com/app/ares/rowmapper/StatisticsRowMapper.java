package com.app.ares.rowmapper;

import com.app.ares.domain.Role;
import com.app.ares.domain.Statistics;
import org.springframework.jdbc.core.RowMapper;

import javax.swing.tree.TreePath;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatisticsRowMapper implements RowMapper<Statistics> {
    @Override
    public Statistics mapRow(ResultSet resultSet, int rowNum)throws SQLException {
        return Statistics.builder()
                .totalCustomers(resultSet.getInt("total_customers"))
                .totalInvoices(resultSet.getInt("total_invoices"))
                .totalBill(resultSet.getDouble("total_bill"))
                .build();
    }

}
