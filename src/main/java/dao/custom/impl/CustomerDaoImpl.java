package dao.custom.impl;

import dao.custom.CustomerDao;
import dao.util.CrudUtil;
import db.DBConnection;
import dto.CustomerDto;
import entity.Customer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDaoImpl implements CustomerDao {


    @Override
    public CustomerDto searchCustomer(String id) {
        return null;
    }

    @Override
    public CustomerDto getCustomer(String id) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * from customer WHERE id=?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        pstm.setString(1,id);
        ResultSet resultSet = pstm.executeQuery();

        while (resultSet.next()){
            return new CustomerDto(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getDouble(4)
            );
        }
        return null;
    }

    @Override
    public boolean save(Customer entity) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO customer VALUES(?,?,?,?)";
//        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        return CrudUtil.execte(sql,entity.getId(),entity.getName(),entity.getAddress(),entity.getSalary());
    }

    @Override
    public boolean update(Customer entity) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE customer SET name=?, address=?, salary=? WHERE id=?";
//        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
//        pstm.setString(1,entity.getName());
//        pstm.setString(2,entity.getAddress());
//        pstm.setDouble(3,entity.getSalary());
//        pstm.setString(4,entity.getId());

        return CrudUtil.execte(sql,entity.getName(),entity.getAddress(),entity.getSalary(),entity.getId());
    }

    @Override
    public boolean delete(String value) throws SQLException, ClassNotFoundException {
        String sql = "DELETE from customer WHERE id=?";
//        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
//        pstm.setString(1,value);
        return CrudUtil.execte(sql,value);
    }

    @Override
    public List<Customer> getAll() throws SQLException, ClassNotFoundException {
        List<Customer> list = new ArrayList<>();

        String sql = "SELECT * FROM customer";
//        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        ResultSet resultSet = CrudUtil.execte(sql);
        while (resultSet.next()){
            list.add(new Customer(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getDouble(4)
            ));
        }
        return list;
    }
}
