package com.techacademy.srr.model;

public class Utilisateur {
    private int id;
    private String login;
    private String motDePasse; // hash BCrypt
    private Role role;

    public Utilisateur() {}

    // Utilisateur de l'application
    public Utilisateur(int id, String login, String motDePasse, Role role){
        this.id = id;
        this.login = login;
        this.motDePasse = motDePasse;
        this.role = role;
    }
    
    /* Get */
    public int getId(){
        return id;
    }

    public String login(){
        return login;
    }

    public String motDePasse(){
        return motDePasse;
    }

    public Role role(){
        return role;
    }

    /* Set */
    
    public void setId(int id){
        this.id = id;
    }

    public void setLogin(String login){
        this.login = login;
    }

    public void setMotDePasse(String motDePasse){
        this.motDePasse = motDePasse;
    }

    public void setRole(Role role){
        this.role = role;
    }

    /* Retourne true si l'utilisateur est admin */
    public boolean isAdmin(){
        return Role.ADMIN.equals(this.role);
    }

    @Override
    public String toString(){
        return login + "(" + role + ")";
    }
}
