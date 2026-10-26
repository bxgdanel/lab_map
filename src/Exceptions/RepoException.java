package Exceptions;

public class RepoException extends RuntimeException{
    public RepoException(String message){
        super(message);
    }
    public RepoException(){}
}
