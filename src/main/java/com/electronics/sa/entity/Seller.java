package com.electronics.sa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Sellers")
public class Seller extends User{

	@OneToOne
	@JoinColumn(name = "StoreId")
	private Store store;
	
}
