package practice4;

import java.util.ArrayList;
import java.util.Objects;

public class ProductGroup {

    private Integer id;
    private String name;
    private String description;

    public ProductGroup(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public ProductGroup(String name, String description) {
        this.name = name;
        this.description = description;

    }

    ProductGroup(){

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductGroup that = (ProductGroup) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }

    @Override
    public String toString() {
        return "ProductGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
