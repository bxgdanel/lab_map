package Repo;

import Domain.Entity;
import Exceptions.RepoException;
import Validari.Validator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class AbstractFileRepository<E extends Entity> extends MemoryRepo<E>{
    String filename;
    public AbstractFileRepository(Validator valid,String filename) {
        this.filename = filename;
        super(valid);
        loadFile();
    }
    public void loadFile(){
        Path path = Path.of(filename);
    if (!Files.exists(path)) {
        return;
    }
        try(Stream<String> lines = Files.lines(path)){
            lines.filter(line -> !line.trim().isEmpty()).
                    forEach(line -> {
                        try {
                            E entity = createEntity(line);
                            super.saveExisting(entity);
                         } catch (Exception e) {
                            throw new RepoException("Eroare din fisierul " + filename + ": " + e.getMessage(), e);
                        }
                    });
        }catch(IOException e){
            throw new RepoException(e.getMessage(),e);
        }
    }

    /**
     * Functie helper care scrie in fisier datele din memorie
     */
    private void writeToFile() {
        Path path = Path.of(filename);
        List<String> lines = super.getAll().values().stream()
                .map(this::createLine).collect(Collectors.toList());
        try{
            Files.write(path,lines);
        }catch(IOException e){
            throw new RuntimeException("Eroare la scrierea in fisier",e);
        }
    }
    @Override
    public E add(E obj){
        E obj1 = (E) super.add(obj);
        writeToFile();
        return obj1;
    }
    @Override
    public E remove(Integer id){
        E clone = super.remove(id);
        writeToFile();
        return clone;
    }
    protected abstract E createEntity(String line);
    protected abstract String createLine(E obj);
}
