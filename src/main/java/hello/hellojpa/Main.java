package hello.hellojpa;

import hello.hellojpa.entity.Member;
import hello.hellojpa.entity.Role;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Optional;

public class Main {

    private static EntityManager em;

    public static void main(String[] args) {
        System.out.println("hello jpa");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hellojpa");
        em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
//            test_logic();
//            not_null_example();
            cascade_example();
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }

    private static void cascade_example() {
        /*
         Role: 부모 엔티티
         Member: 자식 엔티티
         */

        Role role = new Role();

        Member member1 = new Member();
        // 양방향 연관관계를 추가
        member1.setRole(role);
        role.getMembers().add(member1);

        Member member2 = new Member();
        member2.setRole(role);
        role.getMembers().add(member2);

        // 부모 저장, 연관된 자식들 저장
        em.persist(role);

        em.clear();

        Role role1 = em.find(Role.class, role.getId());
        role1.getMembers().remove(0);   // 자식 엔티티의 참조를 컬렉션에서 제거
        em.flush();
        System.out.println("=== 부모 삭제 ===");
        em.remove(role1);
    }

    private static void not_null_example() {
        Member member = new Member();
        member.setName("홍길동");

        Role role = new Role();
        role.setDescription("개발자");
        em.persist(role);

        member.setRole(role);
        em.persist(member);

        em.clear();

        Member member1 = em.find(Member.class, member.getId()); // inner join

        em.clear();

        Role role1 = em.find(Role.class, role.getId());         // left outer join
    }

    private static void proxy_association_logic(Long memberId, Long roleId) {
        Member member = em.find(Member.class, memberId);    // join
        Role role = em.getReference(Role.class, roleId);
        member.setRole(role);   // update

        em.flush();
        em.clear();

        System.out.println("find member");
        Member member1 = em.find(Member.class, memberId);
        Role role1 = member1.getRole();
        System.out.println("=== 객체 실제 사용 ===");
        role1.getDescription();

        em.clear();

        System.out.println("=========================");

        Role role2 = em.find(Role.class, roleId);
        List<Member> members = role2.getMembers();
        // 컬렉션 래퍼: 컬렉션에 대한 프록시 역할
        System.out.println("members = " + members.getClass().getName());
        System.out.println("=== 실제 데이터 조회 ===");
        System.out.println(members.get(0));
    }

    private static void proxy_logic(Long memberId) {

        // == 실제 엔티티 참조 == //
        System.out.println("before find");
        Member member = em.find(Member.class, memberId);
        System.out.println("after find");
        Role role = member.getRole();
        System.out.println("회원 이름: " + member.getName());
        System.out.println(role);

        // - 영속성 컨텍스트에 찾는 엔티티가 이미 있으면
        // 데이터베이스를 조회할 필요가 없으므로
        // 실제 엔티티를 반환한다.
        em.clear();

        // == 프록시 객체 == //
        System.out.println("before getReference");
        Member member1 = em.getReference(Member.class, memberId);
        System.out.println("after getReference");

        // - 준영속 상태의 프록시를 초기화하면
        // org.hibernate.LazyInitializationException 예외 발생
//        em.clear(); // 영속성 컨텍스트 초기화

        // - 프록시 객체는 식별자 값을 가지고 있으므로
        // 프록시를 초기화하지 않는다.
        System.out.println(member1.getId());

        // == 초기화 여부 확인 == //
        boolean isLoad = em.getEntityManagerFactory()
                .getPersistenceUnitUtil().isLoaded(member1);
        System.out.println("isLoad = " + isLoad);

        System.out.println(member1.getName());  // 초기화 - 영속성 컨텍스트의 도움을 받아

        // - 처음 사용할 때 한 번만 초기화된다.
        System.out.println(member1.getPhone());
        isLoad = em.getEntityManagerFactory()
                .getPersistenceUnitUtil().isLoaded(member1);
        System.out.println("isLoad = " + isLoad);

        // - 프록시 객체 타입: Member$HibernateProxy$ZWxqur1r
        System.out.println(member1.getClass().getName());
    }

    private static void test_logic() {
        Member member = new Member();
        member.setName("jpa");
        member.setPhone("010-0000-0001");

        save(member);   // IDENTITY: persist()를 호출하는 즉시 INSERT

        em.clear();
        proxy_logic(member.getId());
        if (false) {
            return;
        }

        Member result = findById(member.getId()).get();
        System.out.println(result.getName());

        Member member1 = new Member();
        member1.setName("jpa1");
        save(member1);

        Member member2 = new Member();
        member2.setName("jpa2");
        save(member2);

        Member result1 = findByName("jpa1").get();

        List<Member> result2 = findAll();

        System.out.println(result2.size());

        System.out.println("=========================");

        Role role = new Role();
        role.setDescription("CTO");
        em.persist(role);   // insert hello.hellojpa.entity.Role

        em.clear();
        proxy_association_logic(member.getId(), role.getId());
    }

    private static Member save(Member member) {
        em.persist(member);
        return member;
    }

    private static Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    private static Optional<Member> findByName(String name) {
        List<Member> result = em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
        return result.stream().findAny();
    }

    private static List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }
}
