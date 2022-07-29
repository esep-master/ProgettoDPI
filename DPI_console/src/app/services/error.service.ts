import { ErrorCodeEnum } from '../utility/enums';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ErrorService {

  /**
   * Costruttore
   */
  constructor() {
  }

  /**
   * Decodifica codice d'errore
   * @param code Codice d'errore
   */
  decode(code: number, msg?: string): string {

    let message: string
    let E = ErrorCodeEnum

    switch (code) {

      // ERRORI APPLICATIVI

      case E.BAD_LOGIN://esempio
        message = "Credenziali errate"
        break
      
      // ERRORI HTTP

      case E.TIMEOUT:
        message = "Timeout error"
        break

      case E.BAD_REQUEST:
        message = "Bad request"
        break

      case E.NOT_FOUND:
        message = "Not found"
        break

      case E.NOT_ALLOWED:
        message = "Not allowed"
        break

      case E.INTERNAL_ERROR:
        message = "Internal error"
        break

      default:
        message = "Errore"

    }

    return message

  }
}