package practice4;

import java.util.Objects;

public class Product {

    private Integer id;
    private String name;
    private Integer groupId;
    private String description;
    private String maker;
    private double price;
    private double amount;

    public Product(Integer id, String name, Integer groupId, String description, String maker, double price, double amount) {
        this.id = id;
        this.name = name;
        this.groupId = groupId;
        this.description = description;
        this.maker = maker;
        this.price = price;
        this.amount = amount;
    }

    public Product(String name, Integer groupId, String description, String maker, double price, double amount) {
        this.name = name;
        this.groupId = groupId;
        this.description = description;
        this.maker = maker;
        this.price = price;
        this.amount = amount;
    }

    public Product() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Double.compare(product.price, price) == 0 &&
                Double.compare(product.amount, amount) == 0 &&
                Objects.equals(id, product.id) &&
                Objects.equals(name, product.name) &&
                Objects.equals(groupId, product.groupId) &&
                Objects.equals(description, product.description) &&
                Objects.equals(maker, product.maker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, groupId, description, maker, price, amount);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMaker() {
        return maker;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", groupId=" + groupId +
                ", description='" + description + '\'' +
                ", maker='" + maker + '\'' +
                ", price=" + price +
                ", amount=" + amount +
                '}';
    }
}


