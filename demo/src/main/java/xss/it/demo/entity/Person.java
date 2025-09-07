/*
 * Copyright © 2025. XTREME SOFTWARE SOLUTIONS
 *
 * All rights reserved. Unauthorized use, reproduction, or distribution
 * of this software or any portion of it is strictly prohibited and may
 * result in severe civil and criminal penalties. This code is the sole
 * proprietary of XTREME SOFTWARE SOLUTIONS.
 *
 * Commercialization, redistribution, and use without explicit permission
 * from XTREME SOFTWARE SOLUTIONS, are expressly forbidden.
 */

package xss.it.demo.entity;

import java.util.Objects;

/**
 * nfx-responsive
 * <p>
 * Description:
 * This class is part of the xss.it.demo.entity package.
 *
 * <p>
 *
 * @author XDSSWAR
 * @version 1.0
 * @since September 06, 2025
 * <p>
 * Created on 09/06/2025 at 20:56
 */
public class Person {
    private String name;
    private String email;
    private String city;
    private String mac;
    private String timestamp;
    private String creditCard;

    public Person(String name, String email, String city, String mac, String timestamp, String creditCard) {
        this.name = name;
        this.email = email;
        this.city = city;
        this.mac = mac;
        this.timestamp = timestamp;
        this.creditCard = creditCard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person person)) return false;
        return Objects.equals(name, person.name)
                && Objects.equals(email, person.email)
                && Objects.equals(city, person.city)
                && Objects.equals(mac, person.mac)
                && Objects.equals(timestamp, person.timestamp)
                && Objects.equals(creditCard, person.creditCard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, city, mac, timestamp, creditCard);
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", city='" + city + '\'' +
                ", mac='" + mac + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", creditCard='" + creditCard + '\'' +
                '}';
    }
}
