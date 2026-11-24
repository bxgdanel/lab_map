package Ui;

import Domain.User;
import Exceptions.RepoException;
import Exceptions.ValidationException;
import Service.Service;
import paging.Page;

import java.util.Scanner;
import java.util.function.IntFunction;

public class Ui {
    private final Service service;
    private final Scanner scanner;
    private final int pageSize;
    public Ui(Service s,int pageSize){
        this.service = s;
        this.pageSize = pageSize;
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
                9.Filtrare dupa un nume care are si prieteni
                10.Prietenii unui user
                11.Afisare useri cu paging
                12.Afisare prietenii unui user cu paging
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
                    case "10" : ui_priteni();break;
                    case "11" : printUsers_paging();break;
                    case "12" : ui_priteni_paging();break;
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
    private void ui_priteni(){
        System.out.println("Introduceti id:");
        Long id = Long.parseLong(scanner.nextLine().trim());
        service.getFriendsOfUser(id).forEach(System.out::println);
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
        Long id = Long.parseLong(scanner.nextLine().trim());
        service.RemoveUser(id);
    }
    private void ui_addFriendship(){
        System.out.println("Introduceti id-urile pe randuri diferite: ");
        Long id1 = Long.parseLong(scanner.nextLine().trim());
        Long id2 = Long.parseLong(scanner.nextLine().trim());
        service.AddFriendship(id1,id2);
    }
    private void ui_removeFriendship(){
        System.out.println("Introduceti id-ul dumneavoastra, apoi al prietenului:");
        Long id = Long.parseLong(scanner.nextLine().trim());
        Long id2 = Long.parseLong(scanner.nextLine().trim());
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
    /**
     * Displays pages of users one at a time, with next / previous navigation.
     * @param fetch loads the page with the given 0-based number
     */
    private void browse(IntFunction<Page<User>> fetch) {
        int p = 0;
        while (true) {
            Page<User> page = fetch.apply(p);
            int pages = page.totalPages(pageSize);
            if (p >= pages) {
                p = pages - 1;
                continue;
            }
            if (page.items().isEmpty())
                System.out.println("(nimic de afisat)");
            else
                page.items().forEach(System.out::println);
            System.out.println("Pagina " + (p + 1) + " din " + pages);

            System.out.print("[n] urmatoarea / [p] anterioara / [q] iesire: ");
            switch (scanner.nextLine().trim().toLowerCase()) {
                case "n" -> {
                    if (p + 1 < pages) p++;
                    else System.out.println("Ai ajuns la ultima pagina.");
                }
                case "p" -> {
                    if (p > 0) p--;
                    else System.out.println("Esti deja la prima pagina.");
                }
                case "q" -> { return; }
                default -> System.out.println("Optiune invalida.");
            }
        }
    }

    /**
     * Prints users using paging
     */
    private void printUsers_paging() {
        try {
            browse(p -> service.getUsersPage(p, pageSize));
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Prints friends of users using paging
     */
    private void ui_priteni_paging() {
        System.out.println("Introduceti id:");
        Long id = Long.parseLong(scanner.nextLine().trim());
        try {
            browse(p -> service.getFriendsPage(id, p, pageSize));
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
