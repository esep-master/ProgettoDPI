import { Injectable } from '@angular/core';
import { observe, Observables } from "rxjs-observe";
import { Observable } from 'rxjs';

/****
 * 
 * REQUIRES "npm install rxjs-observe"
 * 
*****/

@Injectable({
  providedIn: 'root'
})
export class StateTreeService {

  // OGGETTO OBSERVER:
  // observables: contiene le proprietà osservabili
  // proxy: permette di scrivere nelle proprietà e far osservare i cambiamenti

  private observer: { 
    observables: Observables<any, object>,
    proxy: any 
  }

  /**
   * Costruttore
   */
  constructor() {

    // Se esiste uno State Tree nella localStorage, impostalo come oggetto di partenza
    // altrimenti imposta un oggetto vuoto

    let stateTree = JSON.parse(localStorage.getItem("state-tree")) || {}

    // Istanzia l'Observer con lo State Tree

    this.observer = observe(stateTree)

  }

  /**
   * Osserva una proprietà dello State Tree
   * @param prop Proprietà da osservare
   */
  public read<T>(prop: string): Observable<T> {

    // Restituisci Observable della proprietà

    return this.observer.observables[prop] as Observable<T>

  }

  /**
   * Aggiungi o aggiorna una proprietà dello State Tree
   * @param prop Proprietà da aggiungere o aggiornare
   * @param value Valore da scrivere
   * @param persistence Persistenza nella localStorage
   */
  public write(prop: string, value: any, persistence: boolean = false) {

    // Se la Persistenza è true, aggiorna lo State Tree nella localStorage

    if (persistence) this.updateInStateTree(prop, value)

    // Aggiungi o aggiorna la proprietà dello State Tree

    this.observer.proxy[prop] = value

  }

  /**
   * Rimuovi una proprietà dallo State Tree
   * @param prop Proprietà da rimuovere
   */
  public clear(prop: string) {

    // Rimuovi proprietà dallo State Tree nella localStorage

    this.removeInStateTree(prop)

    // Rimuovi proprietà dallo State Tree

    this.observer.proxy[prop] = undefined

  }

  /**
   * Aggiorna proprietà dallo State Tree nella localStorage
   * @param prop Proprietà da aggiornare
   * @param value Valore da scrivere
   */
  private updateInStateTree(prop: string, value: any) {

    let stateTree = JSON.parse(localStorage.getItem("state-tree")) || {}

    stateTree[prop] = value

    localStorage.setItem("state-tree", JSON.stringify(stateTree))

  }

  /**
   * Rimuovi proprietà dallo State Tree nella localStorage
   * @param prop Properietà da rimuovere
   */
  private removeInStateTree(prop: string) {

    let stateTree = JSON.parse(localStorage.getItem("state-tree")) || {}

    delete stateTree[prop]

    localStorage.setItem("state-tree", JSON.stringify(stateTree))

  }
}