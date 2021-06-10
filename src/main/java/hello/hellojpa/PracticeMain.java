package hello.hellojpa;

import hello.hellojpa.entity.Item;
import hello.hellojpa.entity.Member;
import hello.hellojpa.entity.Order;
import hello.hellojpa.entity.OrderItem;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class PracticeMain {
    public static void main(String[] args) {

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hellojpa");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            logic(entityManager);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            transaction.rollback();
        } finally {
            entityManager.close();
        }

        entityManagerFactory.close();
    }

    private static void logic(EntityManager entityManager) {
        Member member = new Member();
        Order order = new Order();

        order.setMember(member);

        entityManager.persist(member);
        entityManager.persist(order);

        Item item = new Item();
        OrderItem orderItem = new OrderItem();

        orderItem.setItem(item);
        orderItem.setOrder(order);

        entityManager.persist(orderItem);

        entityManager.clear();

        Order order1 = entityManager.find(Order.class, order.getId());
        Member member1 = order1.getMember();

        entityManager.clear();

        Order order2 = entityManager.find(Order.class, order.getId());
        OrderItem orderItem1 = order2.getOrderItems().get(0);
        Item item1 = orderItem1.getItem();
    }
}
