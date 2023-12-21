package bo.custom;

import bo.SupperBo;
import dto.OrderDto;

import java.sql.SQLException;
import java.util.List;

public interface OrderBo extends SupperBo {
    boolean saveOrder(OrderDto dto) throws SQLException,ClassNotFoundException;
    String generateId() throws SQLException, ClassNotFoundException;

    List<OrderDto> allOrders() throws SQLException, ClassNotFoundException;
}
