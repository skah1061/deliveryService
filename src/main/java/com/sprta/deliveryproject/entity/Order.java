package com.sprta.deliveryproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="orders")
public class Order extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="order_id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name="payment_method")
    private String paymentMethod;

    @Column(name="is_reviewed")
    private Boolean isReviewed; //리뷰 작성 여부

    @Column(name="request")
    private String request;

    @Column(name = "total_price")
    private Integer totalPrice;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public void setOrder(String paymentMethod, String request, Integer totalPrice, Shop shop, Member member){
        this.paymentMethod = paymentMethod;
        this.request = request;
        this.totalPrice = totalPrice;
        this.shop = shop;
        this.member = member;
        this.isReviewed = false;
    }
    public void makeIsReviewedTrue(){
        this.isReviewed=true;
    }
    public void makeIsReviewedFalse(){
        this.isReviewed=false;
    }
}
