package controller;

import bo.custom.CustomerBo;
import bo.custom.ItemBo;
import bo.custom.OrderBo;
import bo.custom.impl.CustomerBoImpl;
import bo.custom.impl.ItemBoImpl;
import bo.custom.impl.OrderBoImpl;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import db.DBConnection;
import dto.CustomerDto;
import dto.ItemDto;
import dto.OrderDetailsDto;
import dto.OrderDto;
import dto.tm.OrderDetailsTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class OrderDetailsFormController {
    public AnchorPane pane;
    public JFXComboBox cmbOrderID;

    public Label orderDateFillLabel;
    public Label lblTotal;
    public JFXTreeTableView tblOrder;
    public TreeTableColumn colCode;
    public TreeTableColumn colDescription;
    public TreeTableColumn colQty;
    public TreeTableColumn colAmount;
    public Label custIdFillLabel;
    public Label custNameFillLabel;
    private ItemBo itemBo=new ItemBoImpl();
    private OrderBo orderBo=new OrderBoImpl();
    private CustomerBo customerBo=new CustomerBoImpl();
    private List<OrderDto> orders;
    private List<ItemDto> items;
    private OrderDto orderDto;
    private CustomerDto customerDto;

    public OrderDetailsFormController() throws SQLException, ClassNotFoundException {
    }

    public void backBtnOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) pane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void loadOrderIds() throws SQLException, ClassNotFoundException {
        items=itemBo.allItems();

        try {
            orders = orderBo.allOrders();
            ObservableList list= FXCollections.observableArrayList();
            for (OrderDto dto: orders) {
                list.add(dto.getOrderId());
            }
            cmbOrderID.setItems(list);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public void initialize() throws SQLException, ClassNotFoundException {
        colCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new TreeItemPropertyValueFactory<>("description"));
        colQty.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
        colAmount.setCellValueFactory(new TreeItemPropertyValueFactory<>("amount"));

        loadOrderIds();

        cmbOrderID.getSelectionModel().selectedItemProperty().addListener((ObservableValue,oldValue,orderId)->{

            for (OrderDto dto: orders) {
                if(dto.getOrderId().equals(orderId)){
                    orderDto=dto;
                    orderDateFillLabel.setText(orderDto.getDate());
                    custIdFillLabel.setText(dto.getCustId());
                    loadTable(orderDto.getList());
                    try {
                        setCustomerName(dto.getCustId());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

    }

    private void loadTable(List<OrderDetailsDto> list) {
        ObservableList<OrderDetailsTm> tmList = FXCollections.observableArrayList();
        int total=0;
        for (OrderDetailsDto dto : list) {
            ItemDto item = selectItem(dto.getItemCode());
            OrderDetailsTm tm = new OrderDetailsTm(
                    dto.getItemCode(),
                    item.getDesc(),
                    dto.getQty(),
                    dto.getQty() * item.getUnitPrice()

            );
            total+=dto.getQty()*item.getUnitPrice();
            tmList.add(tm);
        }
        lblTotal.setText(String.valueOf(total));
        TreeItem<OrderDetailsTm> treeItem = new RecursiveTreeItem<OrderDetailsTm>(tmList, RecursiveTreeObject::getChildren);
        tblOrder.setRoot(treeItem);
        tblOrder.setShowRoot(false);

    }
    public ItemDto selectItem(String code){
        for (ItemDto itm:items) {
            if(itm.getCode().equals(code)){
                return itm;
            }
        }
        return null;
    }

    public void setCustomerName(String custId) throws SQLException, ClassNotFoundException {
        customerDto=customerBo.getCustomer(custId);
        custNameFillLabel.setText(customerDto.getName());
    }

    public void reportBtnOnAction(ActionEvent actionEvent) throws JRException, SQLException, ClassNotFoundException {
        String orderId= cmbOrderID.getValue().toString();
        JasperDesign design= JRXmlLoader.load("src/main/resources/reports/orderDetail.jrxml");

        JRDesignQuery query=new JRDesignQuery();
        query.setText("SELECT * FROM orderDetail WHERE orderId='"+orderId+"'");
        design.setQuery(query);

        JasperReport jasperReport= JasperCompileManager.compileReport(design);
        JasperPrint jasperPrint= JasperFillManager.fillReport(jasperReport,null, DBConnection.getInstance().getConnection());
        JasperViewer.viewReport(jasperPrint,false);

    }
}
