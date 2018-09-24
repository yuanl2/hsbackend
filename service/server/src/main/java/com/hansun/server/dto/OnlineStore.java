package com.hansun.server.dto;

import javax.persistence.*;

/**
 * @author yuanl2
 */
@Entity
public class OnlineStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private short id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "link", nullable = false)
    private String link;

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
