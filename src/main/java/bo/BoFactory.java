package bo;

import bo.custom.impl.CustomerBoImpl;
import dao.util.BoType;

import java.sql.SQLException;

public class BoFactory  {
    private  static BoFactory boFactory;
    private BoFactory(){

    }
    public static  BoFactory getInstance(){
        return boFactory!=null? boFactory:(boFactory=new BoFactory());
    }
    public <T extends SupperBo>T getBo(BoType type) throws SQLException, ClassNotFoundException {
        switch (type){
            case CUSTOMER:return (T) new CustomerBoImpl();
//            case ITEM:return (T) new CustomerBoImpl();
        }
        return null;
    }
}
