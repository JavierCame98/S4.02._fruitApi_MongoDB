package cat.itacademy.s04.t02.n03.fruit.respositories;

import cat.itacademy.s04.t02.n03.fruit.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
}
