import java.util.*;

class PricingService {

    public double calculateTotal(Order o) {
        
        double total = o.items().stream()
        .mapToDouble(item -> item.category().getDiscountedPrice(item.price()))
        .sum();

        if(total > PricingRules.FLAT_DISCOUNT_THRESHOLD) {
            total -= PricingRules.FLAT_DISCOUNT_AMOUNT;
        }

        return total;
    }
}
final class PricingRules {

    private PricingRules() {};

    public static final double FLAT_DISCOUNT_THRESHOLD = 100.0;
    public static final double FLAT_DISCOUNT_AMOUNT = 10.0;
}

interface OrderRepository {
    void save(Order order);
}

class DatabaseOrderRepository implements OrderRepository {

    @Override
    public void save(Order order) {
        System.out.println("Saving order to database...");
    }
}

interface OrderLogger {
    void log(Order order, double total);
}

class ConsoleLogger implements OrderLogger {

    @Override
    public void log(Order order, double total) {
        System.out.println("Order ID: " + order.id() + ", Total: " + total);
    }
}

public class OrderProcessor {

    private final PricingService pricingService;
    private final OrderRepository orderRepository;
    private final OrderLogger orderLogger;

    public OrderProcessor(
            PricingService pricingService,
            OrderRepository repository,
            OrderLogger logger) {

        this.pricingService = pricingService;
        this.orderRepository = repository;
        this.orderLogger = logger;
    }


    // public void process(Order o) {
    //     if (o != null) {
    //         if (o.items != null && o.items.size() > 0) {
    //             double total = 0;
    //             for (int i = 0; i < o.items.size(); i++) {
    //                 Item item = o.items.get(i);
    //                 total += applyDiscountAndReturn(item);
    //             }

    //             if (shouldApplyFlatDiscount(total)) {
    //                 total = total - 10; // flat discount for orders over 100
    //             }
                
    //             System.out.println("Processing order: " + o.id);
    //             System.out.println("Total Price: " + total);
                
    //             // simulate database saving
    //             System.out.println("Saving order to database...");
    //         } else {
    //             throw new IllegalArgumentException("No items");
    //         }
    //     } else {
    //         throw new IllegalArgumentException("Null order");
    //     }
    // }



    public void process(Order o) {
        
        if(o == null || o.items() == null || o.items().isEmpty()) {
            throw new IllegalArgumentException("Invalid Order");
        }
        
        double total = pricingService.calculateTotal(o);

        orderRepository.save(o);
        orderLogger.log(o, total);
    }
}

record Order(String id, List<Item> items) {

    public Order {
        if(id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be null");
        }
        if(items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order should contain items");
        }
    }
}

record Item(Category category, double price) {
    public Item {
        if(price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }
}


enum Category {
    ELECTRONICS(0.10),
    CLOTHING(0.05),
    OTHER(0.0);

    private final double discountRate;

    private Category(double discountRate) {
        this.discountRate = discountRate;
    }

    public double getDiscountedPrice(double price) {
        return price * (1 - discountRate);
    }
}