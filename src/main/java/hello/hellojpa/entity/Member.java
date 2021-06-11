package hello.hellojpa.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

// 회원 엔티티
@Entity
@Table(name = "MEMBER")
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;        // 회원 ID
    private String name;    // 이름

    private String phone;   // 전화번호

    // 주소 정보
    private String city;
    private String street;
    private String zipcode;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    public List<Order> getOrders() {
        return orders;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @ManyToOne                          // 즉시 로딩
//    @ManyToOne(fetch = FetchType.LAZY)  // 지연 로딩
    @JoinColumn(name = "ROLE_ID")
//    @JoinColumn(name = "ROLE_ID", nullable = false)
    private Role role;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
//        if (this.role != null) {
//            this.role.getMembers().remove(this);
//        }
        this.role = role;
//        if (!this.role.getMembers().contains(this)) {
//            this.role.getMembers().add(this);
//        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
