package Ui;

import Domain.User;
import Exceptions.RepoException;
import Exceptions.ValidationException;
import Service.Service;

import java.util.Scanner;

public class Ui {
    private final Service service;
    private final Scanner scanner;
    public Ui(Service s){
        this.service = s;
        scanner = new Scanner(System.in);
    }
    private void printMenu(){
        System.out.println("""
                1.Adauga User
                2.Adauga Prietenie
                3.Sterge User
                4.Sterge Prietenie
                5.Printeaza useri
                6.Printeaza prietenii
                7.Numar Comunitati
                8.Cea mai sociabila comunitate
                9.Filtrare dupa un nume
                0.Iesire
                """);
    }
    public void run() {
        while (true) {
            printMenu();
            System.out.print("Alege o optiune: ");
            String optiune = scanner.nextLine().trim();

            if (optiune.equals("0")) {
                System.out.println("La revedere!");
                break;
            }

            try {
                switch (optiune) {
                    case "1" : {ui_addUser();break;}
                    case "2" : {ui_addFriendship();break;}
                    case "3" : {ui_removeUser();break;}
                    case "4" : {ui_removeFriendship();break;}
                    case "5" : {printUsers();break;}
                    case "6" : {printFriendships();break;}
                    case "7" : ui_numarComunitati();break;
                    case "8" : ui_ceaMaiSociabilaComunitate();break;
                    case "9" : ui_filtrare_nume();break;
                    default : System.out.println("Optiune invalida! Incearca din nou.");break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Eroare: ID-ul introdus trebuie sa fie un numar intreg!");
            } catch (RepoException e) {
                System.out.println("Eroare: " + e.getMessage());
            }
            catch(ValidationException e){
                System.out.println(e.getMessage());
            }
        }
        scanner.close();
    }
    private void ui_filtrare_nume(){
        System.out.println("Introduceti numele:");
        String nume = scanner.nextLine().trim();
        service.get_by_name(nume).stream().forEach(System.out::println);
    }
    private void ui_addUser(){
        System.out.println("Introduceti numele:");
        String nume = scanner.nextLine().trim();
        service.AddUser(nume);
    }
    private void ui_removeUser(){
        System.out.println("Introduceti id-ul:");
        Integer id = Integer.parseInt(scanner.nextLine().trim());
        service.RemoveUser(id);
    }
    private void ui_addFriendship(){
        System.out.println("Introduceti id-urile pe randuri diferite: ");
        Integer id1 = Integer.parseInt(scanner.nextLine().trim());
        Integer id2 = Integer.parseInt(scanner.nextLine().trim());
        service.AddFriendship(id1,id2);
    }
    private void ui_removeFriendship(){
        System.out.println("Introduceti id-ul dumneavoastra, apoi al prietenului:");
        Integer id = Integer.parseInt(scanner.nextLine().trim());
        Integer id2 = Integer.parseInt(scanner.nextLine().trim());
        service.RemoveFriendship(id,id2);
    }
    private void printUsers(){
        service.get_all_users().stream().forEach(System.out::println);
    }
    private void printFriendships(){
        service.get_all_friendships().stream().forEach(System.out::println);

    }
    private void ui_numarComunitati() {
        System.out.println("Numarul de comunitati este: " + service.getNumberOfCommunities());
    }

    private void ui_ceaMaiSociabilaComunitate() {
        var community = service.getMostSociableCommunity();
        if (community.isEmpty()) {
            System.out.println("Nu exista utilizatori in retea.");
        } else {
            System.out.println("Cea mai sociabila comunitate este formata din:");
            community.stream().forEach(System.out::println);
        }
    }
}
