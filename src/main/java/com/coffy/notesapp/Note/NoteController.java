package com.coffy.notesapp.Note;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//HATEOAS SKONCZONE
//
//todo:
// flyway migracje done
// przecwicz thymeleafa -> zakładka baeldung -> sprobuj zrobic dodawanie notatki
// dodawanie notatki => w zakładkach masz nam stackoverflow przykład typa co robił coś z pracownikami - zrób nowy projekt i naucz sie z tego i zobacz czy wszystko działa
// priorytet -- - robisz to: pomysl jak dodac priorytet ladnie w html zeby bylo (powinien dzialac normalnie)
// sortowanie: po nazwach
// sortowanie - od najstarszej/zmodyfikowanej (dodaj date dodania i update do bazy danych + migracje)
// sortowanie działa jak należy w parametrach w linku
// add i delete dziala
// widok skoncz w poradniku, skonczyles na tym ze dodalo zielone kolory na notatki, sprobuj dodac funkcje pod ikonke edytowania i usuwania (mozliwe ze bez javascriptu jest to niemozliwe)
// ---------------------------------alles done -----------------------
// wizualka - przyciski do sortowania na dole, opcjonalnei ten rozwijany przycisk ktory chcesz od dawna zrobic
// grupy - grupy jak spis treści w kompendium -> przenosi do normalnego widoku
// keycloak (na końcu)

@Controller
@RequestMapping("/notes")
public class NoteController {
    private final NoteRepository repository;
    public static final Logger logger = LoggerFactory.getLogger(NoteController.class);

    public NoteController(NoteRepository repository) {
        this.repository = repository;
    }

    @GetMapping(value = "/{id}")
    public String deleteNote(@PathVariable(value = "id") int id, Model model) {
        repository.deleteById(id);
        logger.warn("Note deleted");
        model.addAttribute("notes", repository.findAll(Sort.by("priority")));
        return "newnotes";
    }


    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    String showSortedNotes(Model model, @RequestParam(required = false, defaultValue = "priority", value = "sort") String param) {
        if(param.equals("date")) {
            model.addAttribute("notes", repository.findAll(Sort.by("createdOn")));
        } else if(param.equals("title")) {
            model.addAttribute("notes", repository.findAll(Sort.by("title")));
        } else {
            model.addAttribute("notes", repository.findAll(Sort.by("priority")));
        }
        return "notes";
    }

//    @GetMapping(value = "/notes/{id}", produces = MediaType.TEXT_HTML_VALUE)
//    String showNotes(Model model, @PathVariable int id) {
//        model.addAttribute("note", new Note());
//        return "notes";
//    }

    @ModelAttribute("notes")
    List<Note> getNotes() {
        return repository.findAll(Sort.by("priority"));
    }

    @RequestMapping(method = RequestMethod.POST)
    public String addNote(@Valid @ModelAttribute("note") Note current, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            return "newnotes";
        }
        this.repository.save(current);
        model.addAttribute("notes", repository.findAll(Sort.by("priority")));
        model.addAttribute("message", "Dodano notatke!");
        return "newnotes";
    }

    @GetMapping(value = "/new", produces = MediaType.TEXT_HTML_VALUE)
    String showNewNotes() {
        return "newnotes";
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> editNote(@PathVariable int id, @Valid @RequestBody Note newNote) {
        repository.findById(id).ifPresent(note -> {
            note.updateFrom(newNote);
            repository.save(note);
        });
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    public ResponseEntity<List<Note>> showNotesJSON() {
        return ResponseEntity.ok(repository.findAll());
    }
}
