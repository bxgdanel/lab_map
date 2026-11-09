package Repo;

import Domain.Entity;
import Exceptions.RepoException;
import Validari.Validator;
import Repo.EntityParser;
import java.io.*;
import java.util.HashMap;

public class CsvRepo<E extends Entity> implements Repo<E>{
    private final String filename;
    private final EntityParser<E> parser;
    Validator<E> valid;
    HashMap<Integer,E> mem;
    private int highestKey;

    public CsvRepo(String filename, EntityParser<E> parser, Validator<E> valid) {
        this.filename = filename;
        this.parser = parser;
        mem = new HashMap<Integer, E>();
        highestKey = 0;
        this.valid = valid;
        loadFile();
    }

    /**
     * Functie helper care incarca in memorie datele din fisier
     */
    public void loadFile(){
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while((line = br.readLine()) != null){
                if(!line.trim().isEmpty())
                {
                    E entity = parser.createEntity(line);
                    mem.put(entity.getId(),entity);
                    if (entity.getId() >= highestKey) {
                        highestKey = entity.getId() + 1;
                    }
                }
            }
        }catch(IOException e){
            throw new RepoException();
        }
    }

    /**
     * Functie helper care scrie in fisier datele din memorie
     */
    private void writeToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for(var entity:mem.values()) {
                bw.write(parser.toCsv(entity));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RepoException();
        }
    }
    public E add(E obj){
        valid.valid(obj);
            obj.setId(highestKey);
            mem.putIfAbsent(highestKey,obj);
            highestKey++;
            writeToFile();
            return obj;
    }

    public HashMap<Integer,E> getAll(){
        return mem;
    }
    public E getById(Integer id){
        E aux = mem.get(id);
        if(aux != null)
            return aux;
        throw new RepoException("Nu exista un utilizator cu id ul dat");
    }
    public E remove(Integer id){
        E obj =  mem.remove(id);
        if(obj == null)
            throw new RepoException("Id-ul nu exista");
        writeToFile();
        return obj;
    }

}
