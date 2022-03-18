package com.web.wordle.dao;

import com.web.wordle.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameDao extends JpaRepository<Word, String> {

}
