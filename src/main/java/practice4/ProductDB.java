package practice4;

import practice5.User;

import java.sql.*;
import java.util.ArrayList;

public class ProductDB {

     private Connection connection;
     private String product = "product";
     private String group = "groupTable";


    public void initDB(String name){
         try {
             Class.forName("org.sqlite.JDBC");
             connection = DriverManager.getConnection("jdbc:sqlite::memory:");


             PreparedStatement st2 = connection.prepareStatement("create table if not exists '" + group + "' ("+
                     "'id' INTEGER PRIMARY KEY AUTOINCREMENT, 'groupName' text UNIQUE, 'descriptionGroup' text);");

             st2.executeUpdate();

             st2.close();
             PreparedStatement st = connection.prepareStatement("create table if not exists '" + product + "' ("+
                     "'id' INTEGER PRIMARY KEY AUTOINCREMENT, 'productName' text UNIQUE, 'groupID' INTEGER,'description' text, 'maker' text, 'price' double, 'amount' double, FOREIGN KEY('groupID') REFERENCES groupTable(id));");

             st.executeUpdate();

             st = connection.prepareStatement("create table if not exists 'users' ("+
                     "'id' INTEGER PRIMARY KEY AUTOINCREMENT, 'login' text UNIQUE, 'password' text UNIQUE);");

             st.executeUpdate();
             st.close();


         } catch (ClassNotFoundException e) {
             e.printStackTrace();
         } catch (SQLException throwables) {
             throwables.printStackTrace();
         }

     }

     /////insertion

    //test ifInsertToDBCorrect
    public Product insertProductToDB(Product product){

         try {
             PreparedStatement ps = connection.prepareStatement("INSERT INTO " + this.product + "(productName, groupID, description, maker, price, amount) VALUES (?,?,?,?,?,?)");
                 if(ifGroupIdExists(product.getGroupId())==true) {
                     ps.setString(1, product.getName());
                     ps.setInt(2, product.getGroupId());
                     ps.setString(3, product.getDescription());
                     ps.setString(4, product.getMaker());
                     ps.setDouble(5, product.getPrice());
                     ps.setDouble(6, product.getAmount());

                     ps.executeUpdate();
                     ResultSet resSet = ps.getGeneratedKeys();

                     product.setId(resSet.getInt("last_insert_rowid()"));
                 }
                 ps.close();

             return product;
         } catch (SQLException throwables) {
             throwables.printStackTrace();
         }
         return null;
     }

    public ProductGroup insertGroupToDB(ProductGroup group){


        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO groupTable(groupName, descriptionGroup) VALUES(?,?)");
            ps.setString(1, group.getName());
            ps.setString(2, group.getDescription());
            ps.executeUpdate();

            ResultSet resSet = ps.getGeneratedKeys();

            group.setId(resSet.getInt("last_insert_rowid()"));
            ps.close();

            return group;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public User insertUserToDB(User user){

        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO users(login, password) VALUES (?,?)");

                ps.setString(1, user.getLogin());
                ps.setString(2, user.getPassword());


                ps.executeUpdate();
                ResultSet resSet = ps.getGeneratedKeys();

                user.setId(resSet.getInt("last_insert_rowid()"));

            ps.close();

            return user;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public User getByLogin(String login){
        try{
            PreparedStatement st = connection.prepareStatement("SELECT * FROM users WHERE login = ?");

            st.setString(1,login);

            ResultSet res = st.executeQuery();

            if(res.next()) {
                Integer id = res.getInt("id");
                String login1 = res.getString("login");
                String password = res.getString("password");

                st.close();
                res.close();

                return new User(id, login1, password);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }
    //get

    public Integer getGroupId(String name){
        try {
            PreparedStatement st = connection.prepareStatement( "SELECT * FROM " +group+ " WHERE groupName=?");
            st.setString(1, name);

            ResultSet rs = st.executeQuery();
            return rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Product getProductByName(String name){
        try{
            PreparedStatement st = connection.prepareStatement("SELECT * FROM "+ product + " WHERE productName = ?");
            st.setString(1,name);
            ResultSet res = st.executeQuery();

            if(res.next()) {
                Integer groupId = res.getInt("groupId");
                String descr = res.getString("description");
                String maker = res.getString("maker");
                double price = res.getDouble("price");
                double amount = res.getDouble("amount");

                return new Product(res.getInt("id"), name, groupId, descr, maker, price, amount);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    public Product getProductByID(Integer id){
        try{
            PreparedStatement st = connection.prepareStatement("SELECT * FROM "+ product + " WHERE id = ?");
            st.setInt(1,id);
            ResultSet res = st.executeQuery();

            if(res.next()) {
                Integer groupId = res.getInt("groupId");
                String name = res.getString("productName");
                String descr = res.getString("description");
                String maker = res.getString("maker");
                double price = res.getDouble("price");
                double amount = res.getDouble("amount");

                return new Product(res.getInt("id"), name, groupId, descr, maker, price, amount);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    public ProductGroup getGroupByID(Integer id){
        try{
            PreparedStatement st = connection.prepareStatement("SELECT * FROM "+ group + " WHERE id = ?");
            st.setInt(1,id);
            ResultSet res = st.executeQuery();

            if(res.next()) {
                String name = res.getString("groupName");
                String descr = res.getString("descriptionGroup");

                return new ProductGroup(res.getInt("id"), name, descr);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    public boolean ifGroupIdExists(Integer i){
        try {
            PreparedStatement st = connection.prepareStatement( "SELECT * FROM " +group+ " WHERE id=?");
            st.setInt(1, i);

            ResultSet rs = st.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //////////////
    //delete
    //test ifAllDelete
    public void deleteAllProducts(){

        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM " + product);
            ps.executeUpdate();
            ps.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    //test ifProductDelete
    public void deleteProductByName(String name){

        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM "+ product +" WHERE productName = ?");
            ps.setString(1,name);
            ps.executeUpdate();
            ps.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void deleteProductByGroupId(Integer id){

        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM "+ product +" WHERE groupID = ?");
            ps.setInt(1,id);
            ps.executeUpdate();
            ps.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    //test testAllGroupDelete
    public void deleteAllGroups(){
        deleteAllProducts();
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM " + group);
            ps.executeUpdate();
            ps.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void deleteGroupByName(String name){
        try {

            Integer groupId = getGroupId(name);
            deleteProductByGroupId(groupId);
            PreparedStatement ps = connection.prepareStatement("DELETE FROM "+ group +" WHERE groupName = ?");
            ps.setString(1,name);

            ps.executeUpdate();
            ps.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    ///update
    //test ifProductChanged
    public void updateProductName (String oldName, String newName){
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE "+ product +" SET productName = ? WHERE productName = ?");

            ps.setString(1,newName);
            ps.setString(2,oldName);

            ps.executeUpdate();
            ps.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void updateProductPrice (String name, double newPrice){
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE "+ product +" SET price = ? WHERE productName = ?");

            ps.setDouble(1,newPrice);
            ps.setString(2,name);

            ps.executeUpdate();
            ps.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void updateProductAmount (String name, double newAmount){
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE "+ product +" SET amount = ? WHERE productName = ?");

            ps.setDouble(1,newAmount);
            ps.setString(2,name);

            ps.executeUpdate();
            ps.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void updateProductGroup (String name, Integer newGroupID){
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE "+ product +" SET groupID = ? WHERE productName = ?");

            if(ifGroupIdExists(newGroupID)==true) {

                ps.setInt(1, newGroupID);
                ps.setString(2, name);

                ps.executeUpdate();
            }
            ps.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void updateProductDescription(String name, String newDescription){
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE "+ product +" SET description = ? WHERE productName = ?");

            ps.setString(1,newDescription);
            ps.setString(2,name);

            ps.executeUpdate();
            ps.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void updateProductMaker(String name, String newMaker){
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE "+ product +" SET maker = ? WHERE productName = ?");

            ps.setString(1,newMaker);
            ps.setString(2,name);

            ps.executeUpdate();
            ps.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void updateGroupName (String oldName, String newName){
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE "+ group +" SET groupName = ? WHERE groupName = ?");

            ps.setString(1,newName);
            ps.setString(2,oldName);

            ps.executeUpdate();
            ps.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void updateGroupDescription (String name, String newDescription){
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE "+ group +" SET descriptionGroup = ? WHERE groupName = ?");

            ps.setString(1,newDescription);
            ps.setString(2,name);

            ps.executeUpdate();
            ps.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
    //show all

     public ArrayList<Product> showAllProducts(){
         try{
             Statement st = connection.createStatement();
             ResultSet res = st.executeQuery("SELECT * FROM "+ product);
             ArrayList<Product> products = new ArrayList<>();

             while (res.next()) {

                 String name = res.getString("productName");
                 Integer groupId = res.getInt("groupId");
                 String descr = res.getString("description");
                 String maker = res.getString("maker");
                 double price = res.getDouble("price");
                 double amount = res.getDouble("amount");

                 Product fromDB = new Product(res.getInt("id"),name,groupId,descr,maker,price,amount);
                 products.add(fromDB);
                 System.out.println (fromDB.toString());
             }
             res.close();
             st.close();

             return products;
         }catch(SQLException e){
             e.printStackTrace();
         }

         return null;
     }

    public ArrayList<ProductGroup> showAllGroups(){
        try{
            Statement st = connection.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM "+ group);
            ArrayList<ProductGroup> groups = new ArrayList<>();

            while (res.next()) {

                String name = res.getString("groupName");
                String desc = res.getString("descriptionGroup");

                ProductGroup fromDB = new ProductGroup(res.getInt("id"),name,desc);
                groups.add(fromDB);
                System.out.println (fromDB.toString());
            }
            res.close();
            st.close();

            return groups;
        }catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    //test testShowAllProductsInGroup
    public ArrayList<Product> showAllProductsInGroup(String groupName){
        try{
            PreparedStatement st = connection.prepareStatement("SELECT * FROM "+ product +" WHERE groupID = ?");

            st.setInt(1,getGroupId(groupName));

            ResultSet res = st.executeQuery();
            ArrayList<Product> products = new ArrayList<>();

            while (res.next()) {

                String name = res.getString("productName");
                Integer groupId = res.getInt("groupId");
                String descr = res.getString("description");
                String maker = res.getString("maker");
                double price = res.getDouble("price");
                double amount = res.getDouble("amount");

                Product fromDB = new Product(res.getInt("id"),name,groupId,descr,maker,price,amount);

                products.add(fromDB);
                System.out.println (fromDB.toString());
            }
            res.close();
            st.close();

            return products;
        }catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }


////////
    ////get by criteria



    public ArrayList<Product> getByPrice(double min, double max){
        if(max<min){
            double temp = min;
            min = max;
            max = temp;
        }
        try{
            PreparedStatement st = connection.prepareStatement("SELECT * FROM product WHERE price BETWEEN ? AND ?");
            st.setDouble(1,min);
            st.setDouble(2,max);
            ResultSet res = st.executeQuery();
            ArrayList<Product> products = new ArrayList<>();

            while (res.next()) {

                String name = res.getString("productName");
                Integer groupId = res.getInt("groupId");
                String descr = res.getString("description");
                String maker = res.getString("maker");
                double price = res.getDouble("price");
                double amount = res.getDouble("amount");

                Product fromDB = new Product(res.getInt("id"),name,groupId,descr,maker,price,amount);
                products.add(fromDB);
                System.out.println (fromDB.toString());
            }
            res.close();
            st.close();

            return products;
        }catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Product> getByAmount(double min, double max){
        if(max<min){
            double temp = min;
            min = max;
            max = temp;
        }
        try{
            PreparedStatement st = connection.prepareStatement("SELECT * FROM product WHERE amount BETWEEN ? AND ?");
            st.setDouble(1,min);
            st.setDouble(2,max);
            ResultSet res = st.executeQuery();
            ArrayList<Product> products = new ArrayList<>();

            while (res.next()) {

                String name = res.getString("productName");
                Integer groupId = res.getInt("groupId");
                String descr = res.getString("description");
                String maker = res.getString("maker");
                double price = res.getDouble("price");
                double amount = res.getDouble("amount");

                Product fromDB = new Product(res.getInt("id"),name,groupId,descr,maker,price,amount);
                products.add(fromDB);
                System.out.println (fromDB.toString());
            }
            res.close();
            st.close();

            return products;
        }catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Product> getByName(String likeName){

        try{
            PreparedStatement st = connection.prepareStatement("SELECT * FROM product WHERE productName LIKE ?");
            st.setString(1,likeName);
            ResultSet res = st.executeQuery();
            ArrayList<Product> products = new ArrayList<>();

            while (res.next()) {

                String name = res.getString("productName");
                Integer groupId = res.getInt("groupId");
                String descr = res.getString("description");
                String maker = res.getString("maker");
                double price = res.getDouble("price");
                double amount = res.getDouble("amount");

                Product fromDB = new Product(res.getInt("id"),name,groupId,descr,maker,price,amount);
                products.add(fromDB);
                System.out.println (fromDB.toString());
            }
            res.close();
            st.close();

            return products;
        }catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Product> getByDescription(String likeDescription){

        try{
            PreparedStatement st = connection.prepareStatement("SELECT * FROM product WHERE description LIKE ?");
            st.setString(1,likeDescription);
            ResultSet res = st.executeQuery();
            ArrayList<Product> products = new ArrayList<>();

            while (res.next()) {

                String name = res.getString("productName");
                Integer groupId = res.getInt("groupId");
                String descr = res.getString("description");
                String maker = res.getString("maker");
                double price = res.getDouble("price");
                double amount = res.getDouble("amount");

                Product fromDB = new Product(res.getInt("id"),name,groupId,descr,maker,price,amount);
                products.add(fromDB);
                System.out.println (fromDB.toString());
            }
            res.close();
            st.close();

            return products;
        }catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Product> getByMaker(String likeMaker){

        try{
            PreparedStatement st = connection.prepareStatement("SELECT * FROM product WHERE maker LIKE ?");
            st.setString(1,likeMaker);
            ResultSet res = st.executeQuery();
            ArrayList<Product> products = new ArrayList<>();

            while (res.next()) {

                String name = res.getString("productName");
                Integer groupId = res.getInt("groupId");
                String descr = res.getString("description");
                String maker = res.getString("maker");
                double price = res.getDouble("price");
                double amount = res.getDouble("amount");

                Product fromDB = new Product(res.getInt("id"),name,groupId,descr,maker,price,amount);
                products.add(fromDB);
                System.out.println (fromDB.toString());
            }
            res.close();
            st.close();

            return products;
        }catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<ProductGroup> getByGroupName(String likeName){

        try{
            PreparedStatement st = connection.prepareStatement("SELECT * FROM groupTable WHERE groupName LIKE ?");
            st.setString(1,likeName);
            ResultSet res = st.executeQuery();
            ArrayList<ProductGroup> groups = new ArrayList<>();

            while (res.next()) {

                String name = res.getString("groupName");
                Integer id = res.getInt("id");
                String descr = res.getString("descriptionGroup");


                ProductGroup fromDB = new ProductGroup(id,name,descr);
                groups.add(fromDB);
                System.out.println (fromDB.toString());
            }
            res.close();
            st.close();

            return groups;
        }catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<ProductGroup> getByGroupDescription(String likeDescription){

        try{
            PreparedStatement st = connection.prepareStatement("SELECT * FROM groupTable WHERE descriptionGroup LIKE ?");
            st.setString(1,likeDescription);
            ResultSet res = st.executeQuery();
            ArrayList<ProductGroup> groups = new ArrayList<>();

            while (res.next()) {

                String name = res.getString("groupName");
                Integer id = res.getInt("id");
                String descr = res.getString("descriptionGroup");


                ProductGroup fromDB = new ProductGroup(id,name,descr);
                groups.add(fromDB);
                System.out.println (fromDB.toString());
            }
            res.close();
            st.close();

            return groups;
        }catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        ProductDB test = new ProductDB();
        test.initDB("db5");
        test.insertGroupToDB(new ProductGroup("крупи","desc"));
        test.showAllGroups();

        test.insertProductToDB(new Product("гречка",1,"desc","maker",10,10));
        test.insertProductToDB(new Product("рис",2,"desc","maker",20,3));
        test.insertProductToDB(new Product("пшоно",1,"desc","maker",5,40));

        test.showAllProducts();

    }

}
