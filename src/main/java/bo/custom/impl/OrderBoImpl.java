package bo.custom.impl;

import bo.custom.OrderBo;
import dao.custom.OrderDao;
import dao.custom.impl.OrderDaoImpl;
import dto.OrderDto;

import java.sql.SQLException;
import java.util.List;

public class OrderBoImpl implements OrderBo {
    private OrderDao orderDao=new OrderDaoImpl();
    @Override
    public boolean saveOrder(OrderDto dto) throws SQLException, ClassNotFoundException {
        return orderDao.save(dto);
    }

    @Override
    public String generateId() throws SQLException, ClassNotFoundException {
        try {
            String id = orderDao.getlastOrder().getOrderId();
            if (id!=null){

                int num = Integer.parseInt(id.split("[D]")[1]);
                num++;
                return  String.format("D%03d",num);
            }else{
                return "D001";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<OrderDto> allOrders() throws SQLException, ClassNotFoundException {
        return orderDao.getAll();
    }
}
