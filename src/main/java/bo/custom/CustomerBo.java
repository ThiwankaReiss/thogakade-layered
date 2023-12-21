package bo.custom;

import bo.SupperBo;
import dto.CustomerDto;

import java.sql.SQLException;
import java.util.List;

public interface CustomerBo extends SupperBo {
    boolean saveCustomer(CustomerDto dto) throws SQLException, ClassNotFoundException;
    boolean updateCustomer(CustomerDto dto) throws SQLException, ClassNotFoundException;
    boolean deleteCustomer(String id) throws SQLException, ClassNotFoundException;
    List<CustomerDto> allCustomers() throws SQLException, ClassNotFoundException;
    CustomerDto getCustomer(String id) throws SQLException, ClassNotFoundException;
}
