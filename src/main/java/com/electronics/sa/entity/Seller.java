package com.electronics.sa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Sellers")
public class Seller extends User{

	@OneToOne
	@JoinColumn(name = "StoreId")
	private Store store;
}
