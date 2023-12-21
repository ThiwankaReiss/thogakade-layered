package bo.custom.impl;

import bo.custom.ItemBo;
import dao.custom.ItemDao;
import dao.custom.impl.ItemDaoImpl;
import db.DBConnection;
import dto.ItemDto;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemBoImpl implements ItemBo {
    private ItemDao itemDao=new ItemDaoImpl();
    @Override
    public List<ItemDto> allItems() throws SQLException, ClassNotFoundException {

        return itemDao.allItems();
    }
    @Override
    public boolean deleteItem(String code) throws SQLException, ClassNotFoundException {

        return itemDao.deleteItem(code);
    }

    @Override
    public boolean saveItem(ItemDto dto) throws SQLException, ClassNotFoundException {

        return itemDao.saveItem(dto);
    }

    @Override
    public boolean updateItem(ItemDto dto) throws SQLException, ClassNotFoundException {
        return itemDao.updateItem(dto);

    }

    @Override
    public double getAmount(String code, int qty) throws SQLException, ClassNotFoundException {
        return itemDao.getItem(code).getUnitPrice() * qty;
    }
}
