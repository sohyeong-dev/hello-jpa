package hello.hellojpa;

import hello.hellojpa.entity.item.Album;
import hello.hellojpa.entity.item.Book;
import hello.hellojpa.entity.item.Item;
import hello.hellojpa.entity.Member;
import hello.hellojpa.entity.Order;
import hello.hellojpa.entity.OrderItem;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

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

//        Item item = new Item();
        Album album = new Album();
        OrderItem orderItem = new OrderItem();

        orderItem.setItem(album);
//        orderItem.setOrder(order);
        order.addOrderItem(orderItem);

        entityManager.persist(album);
//        entityManager.persist(orderItem);

        Book book = new Book();
        OrderItem orderItem1 = new OrderItem();

        orderItem1.setItem(book);
//        orderItem1.setOrder(order);
        order.addOrderItem(orderItem1);

        entityManager.persist(book);
//        entityManager.persist(orderItem1);

        System.out.println("=== order persist ===");
        entityManager.persist(order);
        System.out.println("=========================");

        entityManager.clear();

        Order order1 = entityManager.find(Order.class, order.getId());
        List<OrderItem> orderItemList = order1.getOrderItems();
        System.out.println("=== delete order item ===");
        for (OrderItem o:orderItemList
             ) {
//            entityManager.remove(o);
        }
        order1.getOrderItems().clear();
        entityManager.flush();
        System.out.println("=========================");

        entityManager.clear();

        Order order2 = entityManager.find(Order.class, order.getId());
        Member member1 = order2.getMember();

        entityManager.clear();

        Order order3 = entityManager.find(Order.class, order.getId());
        OrderItem orderItem3 = order3.getOrderItems().get(0);
        Item item1 = orderItem3.getItem();

        entityManager.clear();

        System.out.println("=========================");

        Album album1 = entityManager.find(Album.class, album.getId());
        album1.setArtist("supreme");

        Album album2 = new Album();
        entityManager.persist(album2);
        album2.setArtist("supreme");

        System.out.println("=== persist vs merge ===");
        Album album3 = new Album();
        album3.setId(album2.getId());
//        entityManager.persist(album3);
        // Caused by: org.hibernate.PersistentObjectException: detached entity passed to persist
        Album album4 = entityManager.merge(album3);
        album4.setArtist("o-ban");

        entityManager.flush();
    }
}
