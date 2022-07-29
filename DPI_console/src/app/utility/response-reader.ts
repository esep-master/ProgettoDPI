import { IResponseError } from "../interfaces/IResponseError";
import { ErrorService } from "../services/error.service";
import { AppInjector } from './app-injector'
import { ErrorCodeEnum } from "./enums";

export class ResponseReader {

    private errorService: ErrorService

    /**
     * Costruttore
     * @param response Payload della Response
     * @param error Payload dell'errore
     */
    constructor(public response: any, public error?: any) {
        this.errorService = AppInjector.get(ErrorService)
    }

    /**
     * Recupera la response
     * @returns l'oggetto contente i dati o gli errori applicativi
     */
    public getData<T>(): T {
        return this.response["data"] as T
    }

    /**
     * recupera l'oggetto error dalla response
     */
    public getError(): IResponseError {
        let errorObj: IResponseError
        if (this.error) {
            errorObj = {
                code: this.error.status,
                message: this.errorService.decode(this.error.status),
                status: this.error.status
            }
        } else if (this.response.code != 200) {
            errorObj = {
                code: this.response.code,
                message: this.response.message,
                status: 200
            }

        } else {
            errorObj = {
                code: ErrorCodeEnum.NOT_RECOGNIZED,
                message: "Errore",
                status: ErrorCodeEnum.NOT_RECOGNIZED
            }
        }

        return errorObj
    }

    public isSuccess(): boolean {
        let success: boolean = this.response.code == 200
        return success
    }

    public isError(): boolean {
        return !this.isSuccess()
    }
}