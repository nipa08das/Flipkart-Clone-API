package com.electronics.sa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "Customers")
public class Customer extends User{

}
