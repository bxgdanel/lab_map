import java.io.*;
import java.util.HashMap;

public class CsvRepo<E extends Entity> implements Repo<E>{
    private final String filename;
    private final EntityParser<E> parser;
    Validator valid;
    HashMap<Integer,E> mem;
    private int highestKey;

    public CsvRepo(String filename, EntityParser<E> parser, Validator valid) {
        this.filename = filename;
        this.parser = parser;
        mem = new HashMap<Integer, E>();
        highestKey = 0;
        this.valid = valid;
        loadFile();
    }
    public void loadFile(){
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while((line = br.readLine()) != null){
                if(!line.trim().isEmpty())
                {
                    E entity = parser.createEntity(line);
                    mem.put(entity.getId(),entity);
                }
            }
        }catch(IOException e){
            throw new RepoException();
        }
    }
    private void writeToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            for(var entity:mem.values()) {
                bw.write(parser.toCsv(entity));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RepoException();
        }
    }
    public E add(E obj){
        if(valid.valid(obj)){
            obj.setId(highestKey);
            mem.putIfAbsent(highestKey,obj);
            highestKey++;
            writeToFile();
            return obj;
        }
        throw new RepoException("Datele invalide");
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
        writeToFile();
        return obj;
    }

}
