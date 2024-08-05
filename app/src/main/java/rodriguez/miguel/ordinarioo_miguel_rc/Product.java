package rodriguez.miguel.ordinarioo_miguel_rc;

public class Product {
    private int id;
    private String name;
    private double price;
    private String category;
    private byte[] image;

    public Product(int id, String name, double price, String category, byte[] image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public byte[] getImage() {
        return image;
    }

    @Override
    public String toString() {
        return name;
    }
}