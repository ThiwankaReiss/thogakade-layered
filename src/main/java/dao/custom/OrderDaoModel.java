package dao.custom;

import dto.OrderDetailsDto;

import java.sql.SQLException;
import java.util.List;

public interface OrderDaoModel {
    boolean saveOrderDetails(List<OrderDetailsDto> list) throws SQLException, ClassNotFoundException;

    List<OrderDetailsDto> getOrderDetails(String orderId) throws SQLException, ClassNotFoundException;
}
