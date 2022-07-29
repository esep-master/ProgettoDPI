import { Injectable } from "@angular/core"
import { CanActivate, Router, ActivatedRouteSnapshot } from "@angular/router"

@Injectable()
export class AuthGuard implements CanActivate {

    /**
     * Costruttore
     * @param route Servizio di routing
     */
    constructor(private route: Router) {
    }

    /**
     * Determina se l'utente può accedere a determinate routes 
     * in base alla presenza del token di autenticazione nella session
     */
    canActivate(route: ActivatedRouteSnapshot): boolean {
    
        let isLogged = !!localStorage.getItem("token")
        if (isLogged) return true

        // Se il token non è presente,
        // Redirecta al login

        this.route.navigate(["login"])

        return false
    }
}