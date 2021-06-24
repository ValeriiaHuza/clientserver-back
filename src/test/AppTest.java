import static org.junit.Assert.assertTrue;

import DBConnection.Product;
import DBConnection.ProductGroup;
import com.sun.net.httpserver.HttpExchange;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
import static org.junit.Assert.assertFalse;

public class AppTest 
{
    private HttpExchange httpExchange;
    private Product product;
    private ProductGroup group;
    @Before
    public void setProduct(){
        product =
                new Product("кофтина",1,
                        "description","maker",
                        200,20);
    }

    @Test
    public void testgetName(){
        String s = product.getName();
        assert (s=="кофтина");
    }

    @Test
    public void testgetGroupId(){
        Integer s = product.getGroupId();
        assert (s==1);
    }

    @Test
    public void testgetMaker(){
        String s = product.getMaker();
        assert (s=="maker");
    }

    @Test
    public void testgetDescription(){
        String s = product.getDescription();
        assert (s=="description");
    }

    @Test
    public void testgetAmount(){
        double s = product.getAmount();
        assert (s!=0);
    }

    @Test
    public void testgetPrice(){
        double s = product.getPrice();
        assert (s!=0);
    }

   @Test
   public void testSetName(){
       String name = "Вишиванка";
       product.setName("Вишиванка");
       assert (product.getName()==name);
   }

    @Test
    public void testSetGroupId(){
        Integer id = 111;
        product.setGroupId(id);
        assert (product.getGroupId()==id);
    }

    @Test
    public void testSetMaker(){
        String name = "maker1111";
        product.setMaker(name);
        assert (product.getMaker()==name);
    }

    @Test
    public void testSetDescription(){
        String description = "description";
        product.setDescription(description);
        assert (product.getDescription()==description);
    }

    @Test
    public void testSetAmount(){
        double amount = 222;
        product.setAmount(amount);
        assert (product.getAmount()==amount);
    }

    public void setGroup(){
        group =
                new ProductGroup(1, "одяг","description");
    }

    @Test
    public void testgetNameG(){
        String s = group.getName();
        assert (s=="одяг");
    }

    @Test
    public void testgetId(){
        Integer s = group.getId();
        assert (s==1);
    }

    @Test
    public void testgetMakerG(){
        String s = group.getDescription();
        assert (s=="description");
    }

}
