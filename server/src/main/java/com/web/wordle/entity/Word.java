package com.web.wordle.entity;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
public class Word {

    @Id
    private int id;
    private String word;

}
