package controller;

import bo.custom.CustomerBo;
import bo.custom.ItemBo;
import bo.custom.OrderBo;
import bo.custom.impl.CustomerBoImpl;
import bo.custom.impl.ItemBoImpl;
import bo.custom.impl.OrderBoImpl;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dto.CustomerDto;
import dto.ItemDto;
import dto.OrderDetailsDto;
import dto.OrderDto;
import dto.tm.OrderTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import dao.custom.CustomerDao;
import dao.custom.ItemDao;
import dao.custom.OrderDao;
import dao.custom.impl.CustomerDaoImpl;
import dao.custom.impl.ItemDaoImpl;
import dao.custom.impl.OrderDaoImpl;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PlaceOrderFormController {
    public JFXComboBox cmbCustId;
    public JFXComboBox cmbItemCode;
    public JFXTextField txtCustName;
    public JFXTextField txtDesc;
    public JFXTextField txtUnitPrice;
    public JFXTextField txtQty;
    public JFXTreeTableView<OrderTm> tblOrder;
    public TreeTableColumn colCode;
    public TreeTableColumn colDesc;
    public TreeTableColumn colQty;
    public TreeTableColumn colAmount;
    public TreeTableColumn colOption;
    public Label lblTotal;
    public Label lblOrderId;
    public AnchorPane pane;

    private List<CustomerDto> customers;
    private List<ItemDto> items;
    private double tot = 0;

    private CustomerBo customerBo = new CustomerBoImpl();
    private ItemBo itemBo = new ItemBoImpl();
//    private OrderDao orderDao = new OrderDaoImpl();
    private OrderBo orderBo=new OrderBoImpl();

    private ObservableList<OrderTm> tmList = FXCollections.observableArrayList();

    public PlaceOrderFormController() throws SQLException, ClassNotFoundException {
    }

    public void initialize(){
        colCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("code"));
        colDesc.setCellValueFactory(new TreeItemPropertyValueFactory<>("desc"));
        colQty.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
        colAmount.setCellValueFactory(new TreeItemPropertyValueFactory<>("amount"));
        colOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));

        generateId();


        loadCustomerIds();
        loadItemCodes();

        cmbCustId.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, id) -> {
            for (CustomerDto dto:customers) {
                if (dto.getId().equals(id)){
                    txtCustName.setText(dto.getName());
                }
            }
        });

        cmbItemCode.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, code) -> {
            for (ItemDto dto:items) {
                if (dto.getCode().equals(code)){
                    txtDesc.setText(dto.getDesc());
                    txtUnitPrice.setText(String.format("%.2f",dto.getUnitPrice()));

                }
            }
        });
    }

    private void loadItemCodes() {
        try {
            items = itemBo.allItems();
            ObservableList list = FXCollections.observableArrayList();
            for (ItemDto dto:items) {
                list.add(dto.getCode());
            }
            cmbItemCode.setItems(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadCustomerIds() {
        try {
            customers = customerBo.allCustomers();
            ObservableList list = FXCollections.observableArrayList();
            for (CustomerDto dto:customers) {
                list.add(dto.getId());
            }
            cmbCustId.setItems(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void backButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) pane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addToCartButtonOnAction(ActionEvent actionEvent) {
        if(txtQty.getText().equals(null)||txtQty.getText().equals("")){
            new Alert(Alert.AlertType.ERROR, "Enter a Buying Quantity").show();
        }else if (Integer.parseInt(txtQty.getText())>0) {
            try {

                double amount = itemBo.getAmount(cmbItemCode.getValue().toString(), Integer.parseInt(txtQty.getText()));

                JFXButton btn = new JFXButton("Delete");

                OrderTm tm = new OrderTm(
                        cmbItemCode.getValue().toString(),
                        txtDesc.getText(),
                        Integer.parseInt(txtQty.getText()),
                        amount,
                        btn
                );

                btn.setOnAction(actionEvent1 -> {
                    tmList.remove(tm);
                    tot -= tm.getAmount();
                    tblOrder.refresh();
                    lblTotal.setText(String.format("%.2f",tot));
                });

                boolean isExist = false;

                for (OrderTm order:tmList) {
                    if (order.getCode().equals(tm.getCode())){
                        order.setQty(order.getQty()+tm.getQty());
                        order.setAmount(order.getAmount()+tm.getAmount());
                        isExist = true;
                        tot+=tm.getAmount();
                    }
                }

                if (!isExist){
                    tmList.add(tm);
                    tot+= tm.getAmount();
                }

                TreeItem<OrderTm> treeObject = new RecursiveTreeItem<OrderTm>(tmList, RecursiveTreeObject::getChildren);
                tblOrder.setRoot(treeObject);
                tblOrder.setShowRoot(false);

                lblTotal.setText(String.format("%.2f",tot));
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            new Alert(Alert.AlertType.ERROR, "Enter Positive Buying Quantity").show();
        }

    }

    public void generateId(){
        try {
            lblOrderId.setText(orderBo.generateId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void placeOrderButtonOnAction(ActionEvent actionEvent) {
        List<OrderDetailsDto> list = new ArrayList<>();
        for (OrderTm tm:tmList) {
            list.add(new OrderDetailsDto(
                    lblOrderId.getText(),
                    tm.getCode(),
                    tm.getQty(),
                    tm.getAmount()/tm.getQty()
            ));
        }
//        if (!tmList.isEmpty()){
            boolean isSaved = false;
            try {
                isSaved = orderBo.saveOrder(new OrderDto(
                        lblOrderId.getText(),
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd")),
                        cmbCustId.getValue().toString(),
                        list
                ));
                if (isSaved){
                    new Alert(Alert.AlertType.INFORMATION,"Order Saved!").show();
                }else{
                    new Alert(Alert.AlertType.ERROR,"Something went wrong!").show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


//        }
    }
}
