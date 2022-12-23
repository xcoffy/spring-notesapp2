package com.coffy.notesapp.Note;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface NoteRepository extends JpaRepository<Note, Integer> {
    Page<Note> findAll(Pageable pageable);

    List<Note> findAll();

    Optional<Note> findById(Integer id);

    Note save(Note entity);
}
