package com.techacademy.srr.dao;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Hashage et vérification des mots de passe avec BCrypt.
 * Utilisation :
 * String hash = PasswordUtil.hasher("monMotDePasse);
 * boolean ok = PasswordUtil.verifier("monMotDePasse", hash);
 * */
public class PasswordUtil {
    // facteur de coût BCrypt
    private static final int COST = 12;

    private PasswordUtil(){
    }

    /**
     * Retourne le hash BCrypt du mot de passe en clair qui a été donné
     * @param motDePasseClair le mot de passe a hasher
     * @return hash BCrypt (60 caractères)
     */
    public static String hasher(String motDePasseClair){
        return BCrypt.hashpw(motDePasseClair, BCrypt.gensalt(COST));
    }

    public static boolean verifier(String motDePasseclair, String hash){
        if (motDePasseclair == null || hash == null){
            return false;
        }
        return BCrypt.checkpw(motDePasseclair, hash);
    }
}
