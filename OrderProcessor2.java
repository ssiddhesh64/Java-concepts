import java.util.List;

interface DatabaseOrderRepository {
    void save(Order order, double total);
}

class MongoDBRepo implements DatabaseOrderRepository {

    @Override
    public void save(Order order, double total) {
        System.out.println("Saving order to MongoDB with Total " + total);
    }
}

class PostgresRepo implements DatabaseOrderRepository {

    @Override
    public void save(Order order, double total) {
        System.out.println("Saving order to PostgreSQL with Total " + total);
    }
}

interface NotificationService {
    void send(String email, double total);
}

class EmailNotificationService implements NotificationService {
    @Override
    public void send(String email, double total) {
        System.out.println("Sending email to " + email + ": Your order of $" + total + " has been processed.");
    }
}

public class OrderProcessor2 {

    private final DatabaseOrderRepository databaseRepository;
    private final NotificationService notificationService;

    public OrderProcessor2(DatabaseOrderRepository databaseRepository, NotificationService notificationService) {
        this.databaseRepository = databaseRepository;
        this.notificationService = notificationService;
    }
    
    public void process(Order order) {
        if(order == null) {
            throw new IllegalArgumentException("Order is null");    
        }
        if(order.items() == null) {
            throw new IllegalArgumentException("No items in order");
        }
        if(order.type() == null) {
            throw new IllegalArgumentException("Order type cannot be null");
        }

        double total = 0.0;
        for(Item item : order.items()) {
            total += item.price() * item.quantity();
        }

        total = order.type().getDiscountedPrice(total);
        System.out.println("Order total calculated: " + total);

        // String sql = "INSERT INTO orders (id, total) VALUES (" + order.id() + ", " + total + ")";
        // System.out.println("Executing SQL in database: " + sql);

        databaseRepository.save(order, total);

        String email = order.email();
        // if(email != null && email.contains("@")) {
        //     System.out.println("Sending email to " + email + ": Your order of $" + total + " has been processed.");
        // }
        notificationService.send(email, total);
    }
    // public void process(Order order) {
    //     if (order != null) {
    //         if (order.getItems() != null && order.getItems().size() > 0) {
    //             double total = 0;
    //             for (Item item : order.getItems()) {
    //                 if (item.getPrice() > 0) {
    //                     total += item.getPrice() * item.getQuantity();
    //                 }
    //             }
                
    //             if (order.getType().equals("VIP")) {
    //                 total = total * 0.9;
    //             } else if (order.getType().equals("REGULAR")) {
    //                 if (total > 100) {
    //                     total = total * 0.95;
    //                 }
    //             }
                
    //             System.out.println("Order total calculated: " + total);
                
    //             // Save order to DB (Hardcoded SQL creation)
    //             String sql = "INSERT INTO orders (id, total) VALUES (" + order.getId() + ", " + total + ")";
    //             System.out.println("Executing SQL in database: " + sql);
                
    //             // Send email notification
    //             String email = order.getCustomerEmail();
    //             if (email != null && email.contains("@")) {
    //                 System.out.println("Sending email to " + email + ": Your order of $" + total + " has been processed.");
    //             }
    //         } else {
    //             throw new IllegalArgumentException("No items in order");
    //         }
    //     } else {
    //         throw new IllegalArgumentException("Order is null");
    //     }
    // }
}

record Order(String id, List<Item> items, OrderType type, String email) {
    
    public Order {
        if(id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be null");
        }
        if(items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order should contain items");
        } 
        if(type == null) {
            throw new IllegalArgumentException("Order type cannot be null");
        }

        if(email != null && !email.contains("@")) {
            throw new IllegalArgumentException("Invalid Email");
        }
    }
}

enum OrderType {

    VIP(0.10),
    REGULAR(0.05);

    private final double discountRate;

    private OrderType(double discountRate) {
        this.discountRate = discountRate;
    }

    public double getDiscountedPrice(double price) {
        return price * (1 - discountRate);
    }
}

// Supporting classes for reference
// class Order {
//     private String id;
//     private List<Item> items;
//     private String type; // VIP, REGULAR
//     private String customerEmail;

//     public String getId() { return id; }
//     public List<Item> getItems() { return items; }
//     public String getType() { return type; }
//     public String getCustomerEmail() { return customerEmail; }
// }

// class Item {
//     private double price;
//     private int quantity;

//     public double getPrice() { return price; }
//     public int getQuantity() { return quantity; }
// }

record Item(double price, int quantity) {
    public Item {
        if(price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if(quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
    }
}
