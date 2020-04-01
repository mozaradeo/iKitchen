package freaktemplate.kingburger.observableLayer;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "orderCart")
public class Order {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "orderId")
    private int orderId;



    @ColumnInfo(name = "userId")
    private int userId;
    @ColumnInfo(name = "orderPrice")
    private double orderPrice;

    @ColumnInfo(name = "timestamp")
    private String timestamp;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @ColumnInfo(name = "address")
    private String address;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public double getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
